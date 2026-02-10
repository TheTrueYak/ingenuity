package net.yak.ingenuity.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.yak.ingenuity.Ingenuity;

public class PipeBombEntity {/*extends ThrowableItemProjectile {

    private long explosionTime;

    public PipeBombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PipeBombEntity(Level level, LivingEntity shooter, long explosionTime) {
        super(Ingenuity.PIPE_BOMB_ENTITY.get(), shooter, level);
        this.explosionTime = explosionTime;
    }

    public PipeBombEntity(Level level, double x, double y, double z, ItemStack stack, long explosionTime) {
        super(Ingenuity.PIPE_BOMB_ENTITY.get(), x, y, z, level);
        this.setItem(stack);
        this.explosionTime = explosionTime;
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.level();
        //if (this.explosionTime <= level.getGameTime()) {
        if (this.getItem().getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0l) <= level.getGameTime()) {
            level.explode(null, this.getX(), this.getY(), this.getZ(), 3f, Level.ExplosionInteraction.TRIGGER);
            this.discard();
        }
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        super.move(type, pos);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putLong("ExplosionTime", this.explosionTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ExplosionTime")) {
            this.explosionTime = compound.getLong("ExplosionTime");
        }
    }

    /*private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return (ParticleOptions)(!itemstack.isEmpty() && !itemstack.is(this.getDefaultItem()) ? new ItemParticleOption(ParticleTypes.ITEM, itemstack) : ParticleTypes.ITEM_SNOWBALL);
    }

    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), (double)0.0F, (double)0.0F, (double)0.0F);
            }
        }

    }*//*

    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        /*if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }*//*
        if (this.tickCount > 5 && this.getDeltaMovement().x > 0 || this.getDeltaMovement().z > 0) {
            this.setDeltaMovement(0, 0.4, 0);
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Ingenuity.PIPE_BOMB.get();
    }*/
}
