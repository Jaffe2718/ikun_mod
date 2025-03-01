package me.jaffe2718.ikun_mod.entity;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.client.render.entity.ChickenKunEntityRenderer;
import me.jaffe2718.ikun_mod.unit.EffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ChickenKunEntity
        extends HostileEntity
        implements RangedAttackMob, GeoEntity {

    private static final String CONTROLLER_NAME = "chicken_kun.animation_controller";
    private final ServerBossBar bossBar;
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    boolean isHealing = false;

    public static DefaultAttributeContainer.Builder createChickenKunAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 250.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.6)
                .add(EntityAttributes.FOLLOW_RANGE, 24.0)
                .add(EntityAttributes.ARMOR, 4.0)
                .add(EntityAttributes.ATTACK_SPEED, 1.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.ATTACK_KNOCKBACK, 3.0);
    }

    int [] skillTicks = {-1, -1, -1};      // {0: sushan6, 1: kunjump, 2: kunyao} countdown to release skill, -1 means not ready

    List<LivingEntity> explodedEntities = new ArrayList<>();

    public ChickenKunEntity(EntityType<ChickenKunEntity> entityType, World world) {
        super(entityType, world);
        this.bossBar = new ServerBossBar(this.getDisplayName(), ServerBossBar.Color.PURPLE, ServerBossBar.Style.PROGRESS);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putIntArray("SkillTicks", skillTicks);
        NbtCompound explodedEntityUUIDs = new NbtCompound();
        int entityCount = this.explodedEntities.size();
        explodedEntityUUIDs.putInt("EntityCount", entityCount);
        for (int i = 0; i < entityCount; i++) {
            explodedEntityUUIDs.putUuid("e" + i, this.explodedEntities.get(i).getUuid());
        }
        nbt.put("ExplodedEntities", explodedEntityUUIDs);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
        this.skillTicks = nbt.getIntArray("SkillTicks");
        if (this.skillTicks.length != 3) {
            this.skillTicks = new int[]{-1, -1, -1};
        }
        NbtCompound explodedEntityUUIDs = nbt.getCompound("ExplodedEntities");
        int entityCount = explodedEntityUUIDs.getInt("EntityCount");
        this.explodedEntities.clear();
        for (int i = 0; i < entityCount; i++) {
            UUID uuid = explodedEntityUUIDs.getUuid("e" + i);
            if (uuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
                Entity entity = serverWorld.getEntity(uuid);
                if (entity instanceof LivingEntity livingEntity) {
                    this.explodedEntities.add(livingEntity);
                }
            }
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new XiaoHeiZiAttackGoal(this));
        this.goalSelector.add(3, new TrackMobGoal(this));
        this.goalSelector.add(4, new HealGoal(this));
        this.goalSelector.add(5, new LookAtEntityGoal(this, LivingEntity.class, 10.0F));
        this.goalSelector.add(5, new WanderAroundGoal(this, 0.3D, 120, false));
        this.targetSelector.add(1, new RevengeGoal(this, ChickenKunEntity.class));
        this.targetSelector.add(2, new ActiveTargetGoal<>(
                this,
                LivingEntity.class,
                0,
                false,
                false,
                (entity, world) ->
                        !(entity instanceof ChickenKunEntity || entity instanceof ChickenEntity)
                                && this.getHealth() > 75
                                && !this.isHealing
        ));
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(ServerWorld world, @NotNull DamageSource source) {
        if (source.isIn(DamageTypeTags.IS_EXPLOSION) || source.isIn(DamageTypeTags.IS_FALL)) {
            return true;
        } else if (source.getAttacker() instanceof LivingEntity livingEntity
                && !livingEntity.isPlayer()
                && livingEntity.hasStatusEffect(EffectRegistry.CRUSH)) {
            return true;
        }
        return super.isInvulnerableTo(world, source);
    }

    @Override
    protected void applyDamage(ServerWorld world, DamageSource source, float amount) {
        super.applyDamage(world, source, amount);
        this.isHealing = false;
        this.stopPlayingSounds(IKunMod.id("entity.chicken_kun.healing"), 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvent.of(IKunMod.id("entity.chicken_kun.hurt"));
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvent.of(IKunMod.id("entity.chicken_kun.death"));
    }

    public void stopPlayingSounds(Identifier soundID, float volume) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.getServer().getPlayerManager().sendToAround(
                    null,
                    this.getX(), this.getY(), this.getZ(),
                    SoundEvent.of(soundID).getDistanceToTravel(volume),
                    serverWorld.getRegistryKey(),
                    new StopSoundS2CPacket(soundID, this.getSoundCategory())
            );
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.stopPlayingSounds(IKunMod.id("entity.chicken_kun.healing"), 1.0F);
        super.onDeath(damageSource);
    }

    @Override
    protected void mobTick(ServerWorld world) {
        if (this.getTarget() instanceof LivingEntity) {  // apply animations
            if (this.moveControl.isMoving()) {
                this.triggerAnim(CONTROLLER_NAME, "run");
            } else if (this.skillTicks[0] > -1) {
                this.triggerAnim(CONTROLLER_NAME, "sushan6");
            } else if (this.skillTicks[1] > -1) {
                this.triggerAnim(CONTROLLER_NAME, "kunjump");
            } else if (this.skillTicks[2] > -1) {
                this.triggerAnim(CONTROLLER_NAME, "kunyao");
            }
        } else {
            if (this.isHealing) {
                this.triggerAnim(CONTROLLER_NAME, "kunshankao");
            } else if (this.navigation.isIdle()) {
                this.triggerAnim(CONTROLLER_NAME, "idle");
            } else {
                this.triggerAnim(CONTROLLER_NAME, "walk");
            }
        }
        if (this.skillTicks[0] == 78) {
            this.explodedEntities = world.getEntitiesByClass(
                    LivingEntity.class,
                    this.getBoundingBox().expand(4, 1, 4),
                    livingEntity -> !(livingEntity instanceof ChickenKunEntity) && livingEntity.isAlive());
            for (LivingEntity entity : this.explodedEntities) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1), this);
                world.spawnParticles(ParticleTypes.CLOUD,
                        this.getX(), this.getY() + 0.25, this.getZ(),
                        96,
                        4, 0.5, 4, 0.0);
            }
        } else if (this.skillTicks[0] == 21) {
            this.jump();
        } else if (this.skillTicks[0] == 0) {
            this.explodedEntities.forEach(entity -> {
                if (entity.isAlive()) {
                    this.getWorld().createExplosion(this, entity.getX(), entity.getY(), entity.getZ(), 2F, World.ExplosionSourceType.MOB);
                    entity.addStatusEffect(new StatusEffectInstance(EffectRegistry.CRUSH, 180));
                    entity.playSound(SoundEvent.of(IKunMod.id("entity.chicken_kun.baozha")), 1.0F, 1.0F);
                }
            });
            this.explodedEntities.clear();
        } else if (this.skillTicks[1] == 21) {
            this.jump();
        } else if (this.skillTicks[1] == 10) {
            world.getEntitiesByClass(
                    LivingEntity.class,
                    this.getBoundingBox().expand(2.5, 0.5, 2.5),
                    livingEntity ->
                            !(livingEntity instanceof ChickenKunEntity)
                                    && !(livingEntity instanceof ChickenEntity)
                                    && livingEntity.isAlive()).forEach(entity -> {
                                        this.tryAttack(world, entity);
                                        entity.addStatusEffect(new StatusEffectInstance(EffectRegistry.CRUSH, 25));
                                    });
            world.spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1, this.getZ(), 1, 0, 0, 0, 0);
        }
        for (int i = 0; i < 3; i++) {
            if (this.skillTicks[i] >= 0) {
                this.skillTicks[i]--;
            }
        }
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void shootAt(@NotNull LivingEntity target, float pullProgress) {
        float h = target.getDimensions(target.getPose()).height();
        Vec3d bulletVelocity = target.getPos().add(0, h / 2, 0).subtract(this.getEyePos()).normalize();
        XiaoHeiZiEntity.shoot(this.getWorld(), this, bulletVelocity);
        this.useSkills(2);
    }

    public boolean willUsingSkills() {
        return Arrays.stream(this.skillTicks).anyMatch(t -> t > -1);
    }

    public void useSkills(int skillID) {
        switch (skillID) {   // 0 -> sushan6, 1 -> kunjump, 2 -> kunyao
            case 0 -> {
                this.skillTicks[0] = 78;
                this.playSound(SoundEvent.of(IKunMod.id("entity.chicken_kun.sushan6")), 1.0F, 1.0F);
            }
            case 1 -> {
                this.skillTicks[1] = 28;
                this.playSound(SoundEvent.of(IKunMod.id("entity.chicken_kun.kunjump")), 1.0F, 1.0F);
            }
            case 2 -> {
                this.skillTicks[2] = 50;
                this.playSound(SoundEvent.of(IKunMod.id("entity.chicken_kun.shoot")), 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void registerControllers(@NotNull AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>(this, CONTROLLER_NAME, state -> PlayState.STOP)
                        .triggerableAnim("idle", ChickenKunEntityRenderer.IDLE)
                        .triggerableAnim("walk", ChickenKunEntityRenderer.WALK)
                        .triggerableAnim("run", ChickenKunEntityRenderer.RUN)
                        .triggerableAnim("sushan6", ChickenKunEntityRenderer.SUSHAN6)
                        .triggerableAnim("kunshankao", ChickenKunEntityRenderer.KUNSHANKAO)
                        .triggerableAnim("kunjump", ChickenKunEntityRenderer.KUNJUMP)
                        .triggerableAnim("kunyao", ChickenKunEntityRenderer.KUNYAO)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


    static class XiaoHeiZiAttackGoal extends ProjectileAttackGoal {
        private final ChickenKunEntity source;
        private final double squaredMinShootDistance;

        public XiaoHeiZiAttackGoal(ChickenKunEntity chickenKun) {
            super(chickenKun, 0.7, 25, 20);
            this.squaredMinShootDistance = 100;
            this.source = chickenKun;
        }

        @Override
        public boolean canStart() {
            if (!this.source.isHealing
                    && this.mob.getTarget() instanceof LivingEntity livingEntity
                    && this.mob.squaredDistanceTo(livingEntity) >= this.squaredMinShootDistance) {
                return super.canStart();
            } else {
                return false;
            }
        }

    }


    static class TrackMobGoal extends FollowMobGoal {
        ChickenKunEntity source;

        public TrackMobGoal(ChickenKunEntity chickenKun) {
            super(chickenKun, 0.7, 2.5F, 10.0F);
            this.source = chickenKun;
        }

        @Override
        public boolean canStart() {
            if (this.source.willUsingSkills()) {
                return false;
            } else if (this.source.getTarget() instanceof MobEntity mobEntity
                    && this.source.squaredDistanceTo(mobEntity) <= 100) {
                this.target = mobEntity;
                return true;
            }
            return super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && !this.source.willUsingSkills();
        }

        @Override
        public void stop() {
            super.stop();
            this.source.useSkills(Random.create().nextBetween(1, (int)this.source.getMaxHealth()) >= this.source.getHealth() ? 0 : 1);
        }
    }


    static class HealGoal extends Goal {

        private final ChickenKunEntity source;
        private int cooldown;
        private BlockPos initialPos = BlockPos.ORIGIN;

        public HealGoal(ChickenKunEntity chickenKun) {
            this.source = chickenKun;
            this.cooldown = 10;
        }

        @Override
        public void tick() {
            this.source.navigation.stop();
            if (this.cooldown % 2 == 0 && this.source.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.NOTE,
                        this.source.getX(), this.source.getY() + 1, this.source.getZ(),
                        1,
                        0.5, 0.25, 0.5, 0.0);
            }
            this.cooldown--;
            if (this.cooldown <= 0) {
                this.cooldown = 10;
                this.source.heal(1);
            }
        }

        @Override
        public void start() {
            this.source.isHealing = true;
            this.initialPos = this.source.getBlockPos();
            this.source.playSound(SoundEvent.of(IKunMod.id("entity.chicken_kun.healing")), 1.0F, 1.0F);
        }

        @Override
        public void stop() {
            this.source.isHealing = false;
            this.source.stopPlayingSounds(IKunMod.id("entity.chicken_kun.healing"), 1.0F);
        }

        @Override
        public boolean canStop() {
            return this.source.getHealth() == this.source.getMaxHealth()
                    || this.source.getTarget() instanceof LivingEntity;
        }

        @Override
        public boolean canStart() {
            return this.source.getHealth() < this.source.getMaxHealth()
                    && this.source.isOnGround()
                    && this.source.navigation.isIdle()
                    && !this.source.willUsingSkills()
                    && this.source.getTarget() == null;
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && this.source.getBlockPos().equals(this.initialPos);
        }
    }
}
