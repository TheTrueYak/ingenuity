package net.yak.ingenuity.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin extends RandomizableContainerBlockEntity {

    @Shadow private NonNullList<ItemStack> items;

    protected BarrelBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "startOpen", at = @At("TAIL"))
    private void ingenuity$primeWhenOpenBarrel(Player player, CallbackInfo ci) {
        PipeBombItem.primePipeBombFromContainer(this.level, this.getBlockPos(), this.items, false);
    }

}
