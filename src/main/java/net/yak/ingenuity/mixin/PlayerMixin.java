package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.yak.ingenuity.Ingenuity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    @Unique private boolean ingenuity$usedRiptide = false;
    @Unique private int ingenuity$riptideTicks = 0;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @WrapMethod(method = "tryToStartFallFlying")
    private boolean ingenuity$preventFallFlyingIfNecessary(Operation<Boolean> original) {
        if (Ingenuity.isAileronLoaded && ingenuity$usedRiptide) {
            if (this.fallFlyTicks > 0) {
                this.fallFlyTicks = 0;
            }
            if (this.getSharedFlag(7)) {
                this.setSharedFlag(7, false);
            }
            return false;
        }
        return original.call();
    }

    @WrapMethod(method = "startAutoSpinAttack")
    private void ingenuity$setUsedRiptide(int ticks, float damage, ItemStack stack, Operation<Void> original) {
        original.call(ticks, damage, stack);
        if (Ingenuity.isAileronLoaded) {
            ingenuity$usedRiptide = true;
            ingenuity$riptideTicks = ticks;
        }
    }

    @WrapMethod(method = "tick")
    private void ingenuity$checkGround(Operation<Void> original) {
        original.call();
        if (Ingenuity.isAileronLoaded) {
            if (ingenuity$riptideTicks > 0) {
                ingenuity$riptideTicks--;
            }
            if (ingenuity$usedRiptide && ingenuity$riptideTicks == 0 && !this.isFallFlying() && !this.isAutoSpinAttack() && (this.onGround() || this.isInWater() || this.onClimbable())) {
                ingenuity$usedRiptide = false;
            }
        }
    }

}
