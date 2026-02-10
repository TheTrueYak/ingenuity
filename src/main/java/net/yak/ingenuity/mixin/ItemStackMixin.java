package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.yak.ingenuity.Ingenuity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow @Nullable public abstract <T> T remove(DataComponentType<? extends T> component);

    @WrapOperation(method = "split", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copyWithCount(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack ingenuity$splitPipeBombComponent(ItemStack instance, int i, Operation<ItemStack> original) {
        ItemStack resultStack = original.call(instance, i);
        if (resultStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            this.remove(Ingenuity.PIPE_BOMB_PRIMED);
        }
        return resultStack;
    }

}
