package net.yak.ingenuity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestMinecartEntity.class)
public abstract class ChestMinecartEntityMixin extends StorageMinecartEntity {

    protected ChestMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/ChestMinecartEntity;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/Entity;)V"))
    private void ingenuity$primePipeBombChestBoat(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PipeBombItem.primePipeBombFromContainer(this.getEntityWorld(), this.getBlockPos(), this.getInventory(), true);
    }

}
