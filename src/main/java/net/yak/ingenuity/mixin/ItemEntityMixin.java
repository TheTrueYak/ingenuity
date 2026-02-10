package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.yak.ingenuity.Ingenuity;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow public abstract ItemStack getItem();

    @Shadow private int age;

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void ingenuity$explode(CallbackInfo ci) {
        if (this.age % 2 == 0) {
            ItemStack stack = this.getItem();
            if (stack.getItem() instanceof PipeBombItem) {
                PipeBombItem.primedEntityExplosion(stack, this, true);
                if (stack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
                    if (level().isClientSide) {
                        level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5f, this.getZ(), 0, 0, 0);
                    }
                }
            }
        }
    }

    @WrapOperation(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean ingenuity$explosiveImmune(ItemStack instance, Item item, Operation<Boolean> original) {
        return original.call(instance, item) || instance.getItem() instanceof PipeBombItem;
    }

    @ModifyReturnValue(method = "isMergable", at = @At("RETURN"))
    private boolean ingenuity$preventPipeBombMerging(boolean original) {
        ItemStack stack = this.getItem();
        return original && !(stack.getItem() instanceof PipeBombItem && stack.has(Ingenuity.PIPE_BOMB_PRIMED));
    }

}
