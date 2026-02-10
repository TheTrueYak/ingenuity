package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.yak.ingenuity.Ingenuity;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {

    @WrapOperation(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/DispenserBlockEntity;getStack(I)Lnet/minecraft/item/ItemStack;"))
    private ItemStack ingenuity$dispensePrimedPipeBombs(DispenserBlockEntity instance, int i, Operation<ItemStack> original) {
        ItemStack resultStack = original.call(instance, i);
        if (resultStack.getItem() instanceof PipeBombItem) {
            World world = instance.getWorld();
            if (!resultStack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
                resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(world, false));
                PipeBombItem.playIgniteSound(world, instance.getPos());
            }
        }
        return resultStack;
    }

}
