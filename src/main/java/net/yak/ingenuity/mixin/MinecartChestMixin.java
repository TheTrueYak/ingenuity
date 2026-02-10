package net.yak.ingenuity.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.Level;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartChest.class)
public abstract class MinecartChestMixin extends AbstractMinecartContainer {

    protected MinecartChestMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/MinecartChest;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;)V"))
    private void ingenuity$primePipeBombChestBoat(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        PipeBombItem.primePipeBombFromContainer(this.level(), this.blockPosition(), this.getItemStacks(), true);
    }


}
