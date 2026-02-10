package net.yak.ingenuity.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.yak.ingenuity.Ingenuity;

import java.util.List;

public class PipeBombItem extends Item /*implements ProjectileItem*/ {

    public static final int DEFAULT_TIMER = 50;
    private static final int CHARGE_TIME = 20;

    public PipeBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return !stack.has(Ingenuity.PIPE_BOMB_PRIMED);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        //return stack.has(Ingenuity.PIPE_BOMB_PRIMED) ? 1000 : 10000;
        return 30000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            onThrow(stack, player, level, 0);
            return InteractionResultHolder.success(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
        int timeUsed = getUseDuration(stack, livingEntity) - remainingUseDuration;
        if (!stack.has(Ingenuity.PIPE_BOMB_PRIMED) && timeUsed != 0 && timeUsed % CHARGE_TIME == 0 && timeUsed <= 200) {
            level.playSound(null, livingEntity, SoundEvents.SCAFFOLDING_HIT, SoundSource.PLAYERS, 1f, 1f);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        super.releaseUsing(stack, level, livingEntity, timeCharged);
        int timeUsed = getUseDuration(stack, livingEntity) - livingEntity.getUseItemRemainingTicks();
        onFinishUse(stack, livingEntity, timeUsed);
    }

    /*@Override
    public void onStopUsing(ItemStack stack, LivingEntity livingEntity, int count) {
        super.onStopUsing(stack, livingEntity, count);
        //onFinishUse(stack, livingEntity, count);
    }*/

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!stack.has(Ingenuity.PIPE_BOMB_PRIMED)) { // primed bombs cannot be changed!
            if (!stack.has(Ingenuity.FIREBOMB)) {
                if (state.is(Blocks.LAVA_CAULDRON)) {
                    stack.set(Ingenuity.FIREBOMB, true);
                    level.playSound(null, pos, SoundEvents.BLAZE_SHOOT, SoundSource.BLOCKS, 0.7f, level.getRandom().nextFloat() * 0.4f + 0.8f);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
            else {
                if (state.is(Blocks.WATER_CAULDRON)) {
                    stack.remove(Ingenuity.FIREBOMB);
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.7f, level.getRandom().nextFloat() * 0.4f + 0.8f);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }
        return super.useOn(context);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        long explosionTime = stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0l);
        if (explosionTime != 0 && explosionTime <= level.getGameTime()) {
            handleExplosion(entity, stack.has(Ingenuity.FIREBOMB));
            stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
            if (entity instanceof Player playerEntity && !playerEntity.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return stack.has(Ingenuity.FIREBOMB) ? Component.translatable("item.ingenuity.firebomb") : super.getName(stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        long primeTime = stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0l);
        if (primeTime != 0) {
            long countdown = (primeTime - Minecraft.getInstance().player.level().getGameTime()) / 20;
            MutableComponent text = Component.translatable("tooltip.ingenuity.pipe_bomb.timer").withColor(0x8C8C8C);
            MutableComponent number = Component.literal(String.valueOf(countdown)).withColor(0xFFC12D);
            if (countdown < 0) { // if explosion time has already passed, obfuscate
                number.withStyle(ChatFormatting.OBFUSCATED);
                number.withColor(0xFF3939);
                countdown = Math.max(countdown, 999); // clamped at 4 digits (3 + negative sign)
            }
            text.append(number);
            text.append(Component.translatable(countdown == 1 ? "tooltip.ingenuity.pipe_bomb.second" : "tooltip.ingenuity.pipe_bomb.seconds")).withColor(0x8C8C8C);
            tooltipComponents.add(text);
        }
    }

    /*@Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        //return new PipeBombEntity(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, level.getGameTime() + defaultTimer));
        return new ItemEntity(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1));
    }*/

    private void onFinishUse(ItemStack stack, LivingEntity livingEntity, int useTime) {
        if (livingEntity instanceof Player playerEntity) {
            Level level = livingEntity.level();
            if (!stack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
                if (useTime >= CHARGE_TIME) {
                    stack.set(Ingenuity.PIPE_BOMB_PRIMED, livingEntity.level().getGameTime() + (int) ((Math.clamp(useTime / 40, 1, 10) + 1) * 30)); //
                    level.playSound(null, playerEntity, SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1f, 1f);
                    playerEntity.getCooldowns().addCooldown(this, 5); //TODO CHANGE SOUND
                }
            }
        }
    }

    private void onThrow(ItemStack stack, Player playerEntity, Level level, int useTime) {
        //float velocity = useTime == 0 ? Math.max(1f, Math.min(2.5f, 3f * (useTime / (float) chargeTime))) : 1f;
        //PipeBombEntity pipeBombEntity = new PipeBombEntity(level, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), stack.copyWithCount(1), stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, level.getGameTime() + defaultTimer));
        //pipeBombEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0, 1.5f, 1f);

        ItemEntity itemEntity = new ItemEntity(level, playerEntity.getX(), playerEntity.getEyeY() - 0.3f, playerEntity.getZ(), stack.copyWithCount(1));
        itemEntity.setPickUpDelay(40);
        itemEntity.setThrower(playerEntity);

        float f8 = Mth.sin(playerEntity.getXRot() * ((float)Math.PI / 180F));
        float f2 = Mth.cos(playerEntity.getXRot() * ((float)Math.PI / 180F));
        float f3 = Mth.sin(playerEntity.getYRot() * ((float)Math.PI / 180F));
        float f4 = Mth.cos(playerEntity.getYRot() * ((float)Math.PI / 180F));
        float f5 = playerEntity.getRandom().nextFloat() * ((float)Math.PI * 2F);
        float f6 = 0.02F * playerEntity.getRandom().nextFloat();
        float boost = 4.5f;
        float yBoost = 1.5f;
        itemEntity.setDeltaMovement((double)(-f3 * f2 * 0.3F * boost) + Math.cos((double)f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (playerEntity.getRandom().nextFloat() - playerEntity.getRandom().nextFloat()) * 0.1F) * yBoost, (double)(f4 * f2 * 0.3F * boost) + Math.sin((double)f5) * (double)f6);

        level.addFreshEntity(itemEntity);
        level.playSound(null, itemEntity, SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1f, 1f);
        stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
        if (!playerEntity.hasInfiniteMaterials()) {
            stack.shrink(1);
        }
    }

    public static void playIgniteSound(Entity entity) {
        entity.level().playSound(null, entity.getOnPos(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 0.8f, 0.8f + entity.getRandom().nextFloat() * 0.4F);
    }

    public static void playIgniteSound(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 0.8f, 0.8f + level.getRandom().nextFloat() * 0.4F);
    }

    public static void entityPrimePipeBombOnInteract(ItemStack stack, Entity sourceEntity, boolean shouldTossUp) {
        Level level = sourceEntity.level();
        if (!stack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            stack.set(Ingenuity.PIPE_BOMB_PRIMED, encodePrimedTime(level, true));
            playIgniteSound(sourceEntity);
        }

        ItemEntity itemEntity = new ItemEntity(level, sourceEntity.getX(), sourceEntity.getEyeY() + 0.3f, sourceEntity.getZ(), stack.copyWithCount(1));
        itemEntity.setPickUpDelay(15); // short pickup delay so if you're quick you can grab it muahahahah
        if (shouldTossUp) {
            itemEntity.setDeltaMovement(0, 0.1f, 0);
        }

        level.addFreshEntity(itemEntity); //TODO: unique sound
        level.playSound(null, itemEntity, SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1f, 1f);
        stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
        stack.shrink(1);
    }

    public static void blockPrimePipeBombOnInteract(ItemStack stack, Level level, BlockPos pos, boolean shouldTossUp) {
        blockPrimePipeBombOnInteract(stack, level, pos, shouldTossUp, 15, true);
    }

    public static void blockPrimePipeBombOnInteract(ItemStack stack, Level level, BlockPos pos, boolean shouldTossUp, int pickupDelay, boolean honorCount) {
        if (!stack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            stack.set(Ingenuity.PIPE_BOMB_PRIMED, encodePrimedTime(level, true));
            playIgniteSound(level, pos);
        }

        int delay = Math.clamp((int) (stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, level.getGameTime()) - level.getGameTime()), 10, 200);
        int loopCount = honorCount ? Math.max(1, stack.getCount()) : 1;
        for (int i = 0; i < loopCount; i++) {
            ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY() + 0.95f, pos.getZ(), stack.copyWithCount(1));
            itemEntity.setPickUpDelay(pickupDelay == -1 ? delay : pickupDelay); // gets pickup delay from component
            /*if (shouldTossUp) {
                itemEntity.setDeltaMovement(0, 0.1f, 0);
            }*/
            itemEntity.setDeltaMovement(0, shouldTossUp ? 0.1f : 0, 0);

            level.addFreshEntity(itemEntity); //TODO: unique sound
            level.playSound(null, itemEntity, SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 1f, 1f);
            if (i >= Ingenuity.pipeBombContainerLimit) {
                break;
            }
        }
        stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
        stack.shrink(loopCount);
    }

    public static void primedEntityExplosion(ItemStack stack, Entity sourceEntity, boolean shouldDiscard) {
        long explosionTime = stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0l);
        if (explosionTime != 0 && explosionTime <= sourceEntity.level().getGameTime()) {
            handleExplosion(sourceEntity, stack.has(Ingenuity.FIREBOMB));
            stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
            stack.shrink(1);
            if (shouldDiscard && stack.getCount() == 0) {
                sourceEntity.discard();
            }
        }
    }

    public static void primePipeBombFromContainer(Level level, BlockPos pos, NonNullList<ItemStack> items, boolean shouldTossUp) {
        //int limit = 0;
        //boolean breakAll = false;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof PipeBombItem) {
                //for (int i = 0; i < stack.getCount(); i++) {
                    PipeBombItem.blockPrimePipeBombOnInteract(stack, level, pos, shouldTossUp, 30, true);
                    /*limit++;
                    if (limit >= Ingenuity.pipeBombContainerLimit) {
                        breakAll = true;
                        break;
                    }
                //}
                if (breakAll) {
                    break;
                }*/
            }
        }
    }

    private static void handleExplosion(Entity entity, boolean fire) { // can be normal explosion or fiery, but fiery explosion is weaker
        entity.level().explode(null, entity.getX(), entity.getY() + (entity.getBbHeight() / 2), entity.getZ(), fire ? 2f : 4f, fire, Level.ExplosionInteraction.TRIGGER);
    }

    public static long encodePrimedTime(Level level, boolean imprecise) {
        int imprecision = imprecise ? (level.getRandom().nextInt(60) - 30) : 0; // up to 1.5 seconds early or late
        return level.getGameTime() + DEFAULT_TIMER + imprecision;
    }


}
