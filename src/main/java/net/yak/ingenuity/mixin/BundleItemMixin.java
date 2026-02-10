package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.yak.ingenuity.Ingenuity;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin extends Item {

    @Shadow protected abstract void playRemoveOneSound(Entity entity);

    public BundleItemMixin(Properties properties) {
        super(properties);
    }

    /*@WrapOperation(method = "overrideStackedOnOther", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;safeInsert(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack ingenuity$primePipeBombFromBundle1(Slot instance, ItemStack resultStack, Operation<ItemStack> original, ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (resultStack != null && resultStack.getItem() instanceof PipeBombItem && !resultStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            PipeBombItem.blockPrimePipeBombOnInteract(resultStack, player.level(), player.blockPosition(), false, 40);
            //resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(player.level(), true));
            //PipeBombItem.playIgniteSound(player);
            return resultStack;
        }
        return original.call(instance, stack);
    }*/

    @WrapOperation(method = "overrideStackedOnOther", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BundleContents$Mutable;removeOne()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack ingenuity$primePipeBombFromBundle(BundleContents.Mutable instance, Operation<ItemStack> original, ItemStack stack, Slot slot, ClickAction action, Player player, @Cancellable CallbackInfoReturnable<ItemStack> cir) {
        ItemStack resultStack = original.call(instance);
        if (resultStack != null && resultStack.getItem() instanceof PipeBombItem && !resultStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            PipeBombItem.blockPrimePipeBombOnInteract(resultStack, player.level(), player.blockPosition(), false, -1, true);
            stack.set(DataComponents.BUNDLE_CONTENTS, instance.toImmutable());
            cir.cancel();
            return null;
            //cir.cancel();
            //resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(player.level(), true));
            //PipeBombItem.playIgniteSound(player);
        }
        return resultStack;
    }

    @WrapOperation(method = "overrideOtherStackedOnMe", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/BundleContents$Mutable;removeOne()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack ingenuity$primePipeBombFromBundle2(BundleContents.Mutable instance, Operation<ItemStack> original, ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access, @Cancellable CallbackInfoReturnable<ItemStack> cir) {
        ItemStack resultStack = original.call(instance);
        if (resultStack != null && resultStack.getItem() instanceof PipeBombItem && !resultStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            PipeBombItem.blockPrimePipeBombOnInteract(resultStack, player.level(), player.blockPosition(), false, -1, true);
            stack.set(DataComponents.BUNDLE_CONTENTS, instance.toImmutable());
            playRemoveOneSound(player);
            cir.cancel();
            return null;
            //cir.cancel();
            //resultStack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(player.level(), true));
            //PipeBombItem.playIgniteSound(player);
        }
        return resultStack;
    }

    @WrapMethod(method = "overrideStackedOnOther")
    private boolean ingenuity$otherStackedReturnTrueWhenCancelled(ItemStack stack, Slot slot, ClickAction action, Player player, Operation<Boolean> original) {
        boolean value = original.call(stack, slot, action, player);
        if (stack.getCount() == 1 && action == ClickAction.SECONDARY) {
            BundleContents bundlecontents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            BundleContents.Mutable bundlecontents$mutable = new BundleContents.Mutable(bundlecontents);
            ItemStack removedStack = bundlecontents$mutable.removeOne();
            if (removedStack != null && removedStack.getItem() instanceof PipeBombItem && !removedStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
                return true;
            }
        }
        return value;
    }

    @WrapMethod(method = "overrideOtherStackedOnMe")
    private boolean ingenuity$meStackedReturnTrueWhenCancelled(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access, Operation<Boolean> original) {
        boolean value = original.call(stack, other, slot, action, player, access);
        if (stack.getCount() == 1 && action == ClickAction.SECONDARY && slot.allowModification(player)) {
            BundleContents bundlecontents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
            BundleContents.Mutable bundlecontents$mutable = new BundleContents.Mutable(bundlecontents);
            ItemStack removedStack = bundlecontents$mutable.removeOne();
            if (removedStack != null && removedStack.getItem() instanceof PipeBombItem && !removedStack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
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

    @WrapOperation(method = "lambda$dropContents$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private static ItemEntity ingenuity$primePipeBombFromBundleEmpty(Player instance, ItemStack stack, boolean includeThrowerName, Operation<ItemEntity> original) {
        if (stack != null && stack.getItem() instanceof PipeBombItem && !stack.has(Ingenuity.PIPE_BOMB_PRIMED)) {
            //PipeBombItem.primePipeBombFromContainer(instance.level(), instance.blockPosition(), NonNullList.of(stack), false);
            stack.set(Ingenuity.PIPE_BOMB_PRIMED, PipeBombItem.encodePrimedTime(instance.level(), true));
            PipeBombItem.playIgniteSound(instance);
        }
        return original.call(instance, stack, includeThrowerName);
    }

}
