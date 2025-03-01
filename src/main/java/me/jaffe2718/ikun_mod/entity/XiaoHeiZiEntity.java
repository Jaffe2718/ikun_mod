package me.jaffe2718.ikun_mod.entity;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.unit.EffectRegistry;
import me.jaffe2718.ikun_mod.unit.EntityRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class XiaoHeiZiEntity extends ExplosiveProjectileEntity {

    public static void shoot(World world, LivingEntity owner, Vec3d velocity) {
        XiaoHeiZiEntity entity = new XiaoHeiZiEntity(EntityRegistry.XIAO_HEI_ZI, world);
        entity.setOwner(owner);
        entity.setPosition(owner.getEyePos());
        entity.setRotation(owner.getYaw(), owner.getPitch());
        entity.setVelocity(velocity);
        world.spawnEntity(entity);
    }

    public XiaoHeiZiEntity(EntityType<? extends XiaoHeiZiEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, World.ExplosionSourceType.MOB);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(@NotNull EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(EffectRegistry.CRUSH, 100));
        }
    }

    protected boolean isBurning() {
        return false;
    }
}
