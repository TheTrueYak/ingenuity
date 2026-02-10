package net.yak.ingenuity.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.yak.ingenuity.Ingenuity;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBoat.class)
public abstract class ChestBoatMixin extends Boat {

    @Shadow private NonNullList<ItemStack> itemStacks;

    public ChestBoatMixin(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/ChestBoat;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/Entity;)V"))
    private void ingenuity$primePipeBombChestBoat(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        PipeBombItem.primePipeBombFromContainer(this.level(), this.blockPosition(), this.itemStacks, true);
        /*int limit = 0;
        for (ItemStack stack : this.itemStacks) {
            if (stack.getItem() instanceof PipeBombItem) {
                PipeBombItem.entityPrimePipeBombOnInteract(stack, this, true);
                limit++;
                if (limit >= Ingenuity.pipeBombContainerLimit) {
                    break;
                }
            }
        }*/
    }

}
