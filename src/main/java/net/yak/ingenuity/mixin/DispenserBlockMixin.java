package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.yak.ingenuity.Ingenuity;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {

    @WrapOperation(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/DispenserBlockEntity;getItem(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack ingenuity$dispensePrimedPipeBombs(DispenserBlockEntity instance, int i, Operation<ItemStack> original) {
        ItemStack resultStack = original.call(instance, i);
        if (resultStack.getItem() instanceof PipeBombItem) {
            Level level = instance.getLevel();
            if (!resultStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
                resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(level, false));
                PipeBombItem.playIgniteSound(level, instance.getBlockPos());
            }
        }
        return resultStack;
    }

}
