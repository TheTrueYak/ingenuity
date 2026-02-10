package net.yak.ingenuity.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.yak.ingenuity.Ingenuity;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class PipeBombItem extends Item {

    public static final int DEFAULT_TIMER = 50;
    private static final int CHARGE_TIME = 20;

    public PipeBombItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return !stack.contains(Ingenuity.PIPE_BOMB_PRIMED);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TRIDENT;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 30000;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (stack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
            onThrow(stack, user, user.getEntityWorld(), 0);
            return ActionResult.SUCCESS;
        }
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
        int timeUsed = getMaxUseTime(stack, user) - remainingUseTicks;
        if (!stack.contains(Ingenuity.PIPE_BOMB_PRIMED) && timeUsed != 0 && timeUsed % CHARGE_TIME == 0 && timeUsed <= 200) {
            world.playSoundFromEntity(null, user, SoundEvents.BLOCK_SCAFFOLDING_HIT, SoundCategory.PLAYERS, 1f, 1f);
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int timeUsed = getMaxUseTime(stack, user) - user.getItemUseTimeLeft();
        onFinishUse(stack, user, timeUsed);
        return super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!stack.contains(Ingenuity.PIPE_BOMB_PRIMED)) { // primed bombs cannot be changed!
            if (!stack.contains(Ingenuity.FIREBOMB)) {
                if (state.isOf(Blocks.LAVA_CAULDRON)) {
                    stack.set(Ingenuity.FIREBOMB, true);
                    world.playSound(null, pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 0.7f, world.getRandom().nextFloat() * 0.4f + 0.8f);
                    return ActionResult.SUCCESS;
                }
            }
            else {
                if (state.isOf(Blocks.WATER_CAULDRON)) {
                    stack.remove(Ingenuity.FIREBOMB);
                    world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7f, world.getRandom().nextFloat() * 0.4f + 0.8f);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        long explosionTime = stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0l);
        if (explosionTime != 0 && explosionTime <= world.getTime()) {
            handleExplosion(entity, stack.contains(Ingenuity.FIREBOMB));
            stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
            if (entity instanceof PlayerEntity playerEntity && !playerEntity.isInCreativeMode()) {
                stack.decrement(1);
            }
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        return stack.contains(Ingenuity.FIREBOMB) ? Text.translatable("item.ingenuity.firebomb") : super.getName(stack);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
        long primeTime = stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0l);
        if (primeTime != 0) {
            long countdown = (primeTime - MinecraftClient.getInstance().player.getEntityWorld().getTime()) / 20;
            MutableText text = Text.translatable("tooltip.ingenuity.pipe_bomb.timer").withColor(0x8C8C8C);
            MutableText number = Text.literal(String.valueOf(countdown)).withColor(0xFFC12D);
            if (countdown < 0) { // if explosion time has already passed, obfuscate
                number.formatted(Formatting.OBFUSCATED);
                number.withColor(0xFF3939);
                countdown = Math.max(countdown, 999); // clamped at 4 digits (3 + negative sign)
            }
            text.append(number);
            text.append(Text.translatable(countdown == 1 ? "tooltip.ingenuity.pipe_bomb.second" : "tooltip.ingenuity.pipe_bomb.seconds")).withColor(0x8C8C8C);
            textConsumer.accept(text);
        }
    }

    private void onFinishUse(ItemStack stack, LivingEntity livingEntity, int useTime) {
        if (livingEntity instanceof PlayerEntity playerEntity) {
            World world = livingEntity.getEntityWorld();
            if (!stack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
                if (useTime >= CHARGE_TIME) {
                    stack.set(Ingenuity.PIPE_BOMB_PRIMED, world.getTime() + (int) ((Math.clamp(useTime / 40, 1, 10) + 1) * 30)); //
                    world.playSoundFromEntity(null, playerEntity, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1f, 1f);
                    playerEntity.getItemCooldownManager().set(this.getDefaultStack(), 5); //TODO CHANGE SOUND
                }
            }
        }
    }

    private void onThrow(ItemStack stack, PlayerEntity playerEntity, World world, int useTime) {
        //float velocity = useTime == 0 ? Math.max(1f, Math.min(2.5f, 3f * (useTime / (float) chargeTime))) : 1f;
        //PipeBombEntity pipeBombEntity = new PipeBombEntity(level, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), stack.copyWithCount(1), stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, level.getGameTime() + defaultTimer));
        //pipeBombEntity.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0, 1.5f, 1f);

        ItemEntity itemEntity = new ItemEntity(world, playerEntity.getX(), playerEntity.getEyeY() - 0.3f, playerEntity.getZ(), stack.copyWithCount(1));
        itemEntity.setPickupDelay(40);
        itemEntity.setThrower(playerEntity);

        float f8 = MathHelper.sin(playerEntity.getPitch() * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(playerEntity.getPitch() * ((float)Math.PI / 180F));
        float f3 = MathHelper.sin(playerEntity.getYaw() * ((float)Math.PI / 180F));
        float f4 = MathHelper.cos(playerEntity.getYaw() * ((float)Math.PI / 180F));
        float f5 = playerEntity.getRandom().nextFloat() * ((float)Math.PI * 2F);
        float f6 = 0.02F * playerEntity.getRandom().nextFloat();
        float boost = 4.5f;
        float yBoost = 1.5f;
        itemEntity.setVelocity((double)(-f3 * f2 * 0.3F * boost) + Math.cos(f5) * (double)f6, (double)(-f8 * 0.3F + 0.1F + (playerEntity.getRandom().nextFloat() - playerEntity.getRandom().nextFloat()) * 0.1F) * yBoost, (double)(f4 * f2 * 0.3F * boost) + Math.sin(f5) * (double)f6);

        world.spawnEntity(itemEntity);
        world.playSoundFromEntity(null, itemEntity, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1f, 1f);
        stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
        if (!playerEntity.isInCreativeMode()) {
            stack.decrement(1);
        }
    }

    public static void playIgniteSound(Entity entity) {
        entity.getEntityWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 0.8f, 0.8f + entity.getRandom().nextFloat() * 0.4F);
    }

    public static void playIgniteSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 0.8f, 0.8f + world.getRandom().nextFloat() * 0.4F);
    }

    public static void entityPrimePipeBombOnInteract(ItemStack stack, Entity sourceEntity, boolean shouldTossUp) {
        World world = sourceEntity.getEntityWorld();
        if (!stack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
            stack.set(Ingenuity.PIPE_BOMB_PRIMED, encodePrimedTime(world, true));
            playIgniteSound(sourceEntity);
        }

        ItemEntity itemEntity = new ItemEntity(world, sourceEntity.getX(), sourceEntity.getEyeY() + 0.3f, sourceEntity.getZ(), stack.copyWithCount(1));
        itemEntity.setPickupDelay(15); // short pickup delay so if you're quick you can grab it muahahahah
        if (shouldTossUp) {
            itemEntity.setVelocity(0, 0.1f, 0);
        }

        world.spawnEntity(itemEntity); //TODO: unique sound
        world.playSoundFromEntity(null, itemEntity, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1f, 1f);
        stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
        stack.decrement(1);
    }

    public static void blockPrimePipeBombOnInteract(ItemStack stack, World world, BlockPos pos, boolean shouldTossUp) {
        blockPrimePipeBombOnInteract(stack, world, pos, shouldTossUp, 15, true);
    }

    public static void blockPrimePipeBombOnInteract(ItemStack stack, World world, BlockPos pos, boolean shouldTossUp, int pickupDelay, boolean honorCount) {
        if (!stack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
            stack.set(Ingenuity.PIPE_BOMB_PRIMED, encodePrimedTime(world, true));
            playIgniteSound(world, pos);
        }

        int delay = Math.clamp((int) (stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, world.getTime()) - world.getTime()), 10, 200);
        int loopCount = honorCount ? Math.max(1, stack.getCount()) : 1;
        for (int i = 0; i < loopCount; i++) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + 0.95f, pos.getZ(), stack.copyWithCount(1));
            itemEntity.setPickupDelay(pickupDelay == -1 ? delay : pickupDelay); // gets pickup delay from component
            itemEntity.setVelocity(0, shouldTossUp ? 0.1f : 0, 0);

            world.spawnEntity(itemEntity); //TODO: unique sound
            world.playSoundFromEntity(null, itemEntity, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1f, 1f);
            if (i >= Ingenuity.pipeBombContainerLimit) {
                break;
            }
        }
        stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
        stack.decrement(loopCount);
    }

    public static void primedEntityExplosion(ItemStack stack, Entity sourceEntity, boolean shouldDiscard) {
        long explosionTime = stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0l);
        if (explosionTime != 0 && explosionTime <= sourceEntity.getEntityWorld().getTime()) {
            handleExplosion(sourceEntity, stack.contains(Ingenuity.FIREBOMB));
            stack.remove(Ingenuity.PIPE_BOMB_PRIMED);
            stack.decrement(1);
            if (shouldDiscard && stack.getCount() == 0) {
                sourceEntity.discard();
            }
        }
    }

    public static void primePipeBombFromContainer(World world, BlockPos pos, DefaultedList<ItemStack> items, boolean shouldTossUp) {
        //int limit = 0;
        //boolean breakAll = false;
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof PipeBombItem) {
                //for (int i = 0; i < stack.getCount(); i++) {
                    PipeBombItem.blockPrimePipeBombOnInteract(stack, world, pos, shouldTossUp, 30, true);
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
        entity.getEntityWorld().createExplosion(null, entity.getX(), entity.getY() + (entity.getHeight() / 2), entity.getZ(), fire ? 2f : 4f, fire, World.ExplosionSourceType.TRIGGER);
    }

    public static long encodePrimedTime(World world, boolean imprecise) {
        int imprecision = imprecise ? (world.getRandom().nextInt(60) - 30) : 0; // up to 1.5 seconds early or late
        return world.getTime() + DEFAULT_TIMER + imprecision;
    }


}
