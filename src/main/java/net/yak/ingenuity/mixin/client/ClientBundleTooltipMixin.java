package net.yak.ingenuity.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.yak.ingenuity.IngenuityClient;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientBundleTooltip.class)
public abstract class ClientBundleTooltipMixin {

    @WrapOperation(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BundleContents;getItemUnsafe(I)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack ingenuity$renderFakeItem(BundleContents instance, int index, Operation<ItemStack> original) {
        ItemStack resultStack = original.call(instance, index);
        if (resultStack.getItem() instanceof PipeBombItem) {
            int count = resultStack.getCount();
            int playerAge = Minecraft.getInstance().player.tickCount;
            if ((IngenuityClient.trackedTick != playerAge && IngenuityClient.trackedTick + 1 != playerAge) || (IngenuityClient.arrayList == null || IngenuityClient.arrayList.size() < instance.size())) {
                IngenuityClient.generateArray(instance.size());
            }
            IngenuityClient.trackedTick = playerAge;
            resultStack = new ItemStack(BuiltInRegistries.ITEM.byId(IngenuityClient.arrayList.get(index)));
            resultStack.setCount(resultStack.isStackable() ? count : 1);
        }
        return resultStack;
    }

}
