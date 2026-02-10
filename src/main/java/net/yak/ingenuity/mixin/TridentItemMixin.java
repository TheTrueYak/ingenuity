package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TridentItem;
import net.yak.ingenuity.Ingenuity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"))
    private boolean ingenuity$aileronCompatOnUse(Player instance, Operation<Boolean> original) {
        boolean value = original.call(instance);
        return Ingenuity.isAileronLoaded ? (value && !instance.isFallFlying()) : value;
    }

    @WrapOperation(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"))
    private boolean ingenuity$aileronCompatReleaseUsing(Player instance, Operation<Boolean> original) {
        boolean value = original.call(instance);
        return Ingenuity.isAileronLoaded ? (value && !instance.isFallFlying()) : value;
    }
}
