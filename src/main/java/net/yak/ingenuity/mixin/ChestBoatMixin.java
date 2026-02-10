package net.yak.ingenuity.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.entity.vehicle.AbstractChestBoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(AbstractChestBoatEntity.class)
public abstract class ChestBoatMixin extends AbstractBoatEntity implements RideableInventory {

    @Shadow private DefaultedList<ItemStack> inventory;

    public ChestBoatMixin(EntityType<? extends AbstractBoatEntity> type, World world, Supplier<Item> itemSupplier) {
        super(type, world, itemSupplier);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractChestBoatEntity;emitGameEvent(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/Entity;)V"))
    private void ingenuity$primePipeBombChestBoat(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PipeBombItem.primePipeBombFromContainer(this.getEntityWorld(), this.getBlockPos(), this.inventory, true);
    }

}
