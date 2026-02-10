package net.yak.ingenuity.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.item.ItemStack;
import net.yak.ingenuity.IngenuityClient;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(BundleTooltipComponent.class)
public abstract class ClientBundleTooltipMixin<E> {

    @WrapOperation(method = "drawItem", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private E ingenuity$renderFakeItem(List<ItemStack> instance, int index, Operation<E> original) {
        ItemStack resultStack = (ItemStack) original.call(instance, index);
        if (resultStack.getItem() instanceof PipeBombItem) {
            int count = resultStack.getCount();
            int playerAge = MinecraftClient.getInstance().player.age;
            if ((IngenuityClient.trackedTick != playerAge && IngenuityClient.trackedTick + 1 != playerAge) || (IngenuityClient.stackList == null || IngenuityClient.stackList.size() < instance.size())) {
                IngenuityClient.generateArray(instance.size());
            }
            IngenuityClient.trackedTick = playerAge;
            resultStack = IngenuityClient.stackList.get(index);
            resultStack.setCount(resultStack.isStackable() ? count : 1);
        }
        return (E) resultStack;
    }

}
