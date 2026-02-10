package net.yak.ingenuity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Creeper.class)
public abstract class CreeperEntityMixin extends Monster {

    protected CreeperEntityMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(method = "explodeCreeper", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;DDDFLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;"))
    private Explosion ingenuity$preventCreeperGriefing(Level instance, Entity source, double x, double y, double z, float radius, Level.ExplosionInteraction explosionInteraction, Operation<Explosion> original) {
        return original.call(instance, source, x, y, z, radius, Level.ExplosionInteraction.TRIGGER);
    }
}
