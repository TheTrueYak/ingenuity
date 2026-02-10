package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.yak.ingenuity.Ingenuity;
import net.yak.ingenuity.item.PipeBombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void ingenuity$explode(CallbackInfo ci) {
        if (this.age % 2 == 0) {
            ItemStack stack = this.getStack();
            if (stack.getItem() instanceof PipeBombItem) {
                PipeBombItem.primedEntityExplosion(stack, this, true);
                if (stack.contains(Ingenuity.PIPE_BOMB_PRIMED)) {
                    if (this.getEntityWorld().isClient()) {
                        this.getEntityWorld().addParticleClient(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5f, this.getZ(), 0, 0, 0);
                    }
                }
            }
        }
    }

    @ModifyReturnValue(method = "isImmuneToExplosion", at = @At("RETURN"))
    private boolean ingenuity$explosiveImmune(boolean original) {
        return original || this.getStack().getItem() instanceof PipeBombItem;
    }

    @ModifyReturnValue(method = "canMerge()Z", at = @At("RETURN"))
    private boolean ingenuity$preventPipeBombMerging(boolean original) {
        ItemStack stack = this.getStack();
        return original && !(stack.getItem() instanceof PipeBombItem && stack.contains(Ingenuity.PIPE_BOMB_PRIMED));
    }

}
