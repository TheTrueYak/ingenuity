package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.yak.ingenuity.Ingenuity;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract <T> @Nullable T remove(ComponentType<? extends T> type);

    @WrapOperation(method = "split", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copyWithCount(I)Lnet/minecraft/item/ItemStack;"))
    private ItemStack ingenuity$splitPipeBombComponent(ItemStack instance, int i, Operation<ItemStack> original) {
        ItemStack resultStack = original.call(instance, i);
        if (resultStack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
            this.remove(Ingenuity.PIPE_BOMB_PRIMED);
        }
        return resultStack;
    }

}
