package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClickType;
import net.yak.ingenuity.Ingenuity;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin extends Item {

    public BundleItemMixin(Settings settings) {
        super(settings);
    }

    @WrapOperation(method = "onStackClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/BundleContentsComponent$Builder;removeSelected()Lnet/minecraft/item/ItemStack;"))
    private ItemStack ingenuity$primePipeBombFromBundle(BundleContentsComponent.Builder instance, Operation<ItemStack> original, ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, @Cancellable CallbackInfoReturnable<ItemStack> cir) {
        ItemStack resultStack = original.call(instance);
        if (resultStack != null && resultStack.getItem() instanceof PipeBombItem && !resultStack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
            PipeBombItem.blockPrimePipeBombOnInteract(resultStack, player.getEntityWorld(), player.getBlockPos(), false, -1, true);
            stack.set(DataComponentTypes.BUNDLE_CONTENTS, instance.build());
            cir.cancel();
            return null;
            //cir.cancel();
            //resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(player.level(), true));
            //PipeBombItem.playIgniteSound(player);
        }
        return resultStack;
    }

    @WrapOperation(method = "onClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/component/type/BundleContentsComponent$Builder;removeSelected()Lnet/minecraft/item/ItemStack;"))
    private ItemStack ingenuity$primePipeBombFromBundle2(BundleContentsComponent.Builder instance, Operation<ItemStack> original, ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, @Cancellable CallbackInfoReturnable<ItemStack> cir) {
        ItemStack resultStack = original.call(instance);
        if (resultStack != null && resultStack.getItem() instanceof PipeBombItem && !resultStack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
            PipeBombItem.blockPrimePipeBombOnInteract(resultStack, player.getEntityWorld(), player.getBlockPos(), false, -1, true);
            stack.set(DataComponentTypes.BUNDLE_CONTENTS, instance.build());
            //playRemoveOneSound(player);
            player.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.getEntityWorld().getRandom().nextFloat() * 0.4F);
            cir.cancel();
            return null;
            //cir.cancel();
            //resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(player.level(), true));
            //PipeBombItem.playIgniteSound(player);
        }
        return resultStack;
    }

    @WrapMethod(method = "onStackClicked")
    private boolean ingenuity$otherStackedReturnTrueWhenCancelled(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, Operation<Boolean> original) {
        boolean value = original.call(stack, slot, clickType, player);
        if (stack.getCount() == 1 && clickType == ClickType.RIGHT) {
            BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
            ItemStack removedStack = builder.removeSelected();
            if (removedStack != null && removedStack.getItem() instanceof PipeBombItem && !removedStack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
                return true; // TODO: probably rewrite all
            }
        }
        return value;
    }

    @WrapMethod(method = "onClicked")
    private boolean ingenuity$meStackedReturnTrueWhenCancelled(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, Operation<Boolean> original) {
        boolean value = original.call(stack, otherStack, slot, clickType, player, cursorStackReference);
        if (stack.getCount() == 1 && clickType == ClickType.RIGHT && slot.canTakeItems(player)) {
            BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
            BundleContentsComponent.Builder builder = new BundleContentsComponent.Builder(bundleContentsComponent);
            ItemStack removedStack = builder.removeSelected();
            if (removedStack != null && removedStack.getItem() instanceof PipeBombItem && !removedStack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
                return true;
            }
        }
        return value;
    }

    /*@WrapOperation(method = "overrideOtherStackedOnMe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/SlotAccess;set(Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean ingenuity$primePipeBombFromBundle3(SlotAccess instance, ItemStack resultStack, Operation<Boolean> original, ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (resultStack != null && resultStack.getItem() instanceof PipeBombItem && !resultStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            PipeBombItem.blockPrimePipeBombOnInteract(resultStack, player.level(), player.blockPosition(), false, 40);
            //resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(player.level(), true));
            //PipeBombItem.playIgniteSound(player);
            return false;
        }
        return original.call(instance, stack);
    }*/

    @WrapOperation(method = "dropFirstBundledStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;dropItem(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/ItemEntity;"))
    private ItemEntity ingenuity$primePipeBombFromBundleEmpty(PlayerEntity instance, ItemStack stack, boolean retainOwnership, Operation<ItemEntity> original) {
        if (stack != null && stack.getItem() instanceof PipeBombItem && !stack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
            stack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(instance.getEntityWorld(), true));
            PipeBombItem.playIgniteSound(instance);
        }
        return original.call(instance, stack, retainOwnership);
    }

}
