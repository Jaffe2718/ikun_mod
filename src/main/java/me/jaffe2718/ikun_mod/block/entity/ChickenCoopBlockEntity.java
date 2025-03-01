package me.jaffe2718.ikun_mod.block.entity;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.client.render.block.ChickenCoopBlockEntityRenderer;
import me.jaffe2718.ikun_mod.unit.BlockRegistry;
import me.jaffe2718.ikun_mod.unit.EntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

public class ChickenCoopBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final String CONTROLLER_NAME = "chicken_coop.block_entity.animation_controller";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public int activeTicks = 0;    // 0 means inactive, >0 means active. if >2500, the blockEntity will explode.
    private boolean isPlayingSound = false;

    public ChickenCoopBlockEntity(BlockPos pos, BlockState state) {
        super(BlockRegistry.CHICKEN_COOP_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.activeTicks = nbt.getInt("ActiveTicks");
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putInt("ActiveTicks", this.activeTicks);
    }

    @Override
    public void registerControllers(AnimatableManager.@NotNull ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, CONTROLLER_NAME, state -> PlayState.STOP)
                .triggerableAnim("static", ChickenCoopBlockEntityRenderer.STATIC)
                .triggerableAnim("active", ChickenCoopBlockEntityRenderer.ACTIVE)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtil.getCurrentTick();
    }

    public void active() {
        this.activeTicks = 1;
        if (this.world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, this.pos, SoundEvent.of(IKunMod.id("block.chicken_coop.active")), SoundCategory.BLOCKS, 1, 1);
            this.isPlayingSound = true;
        }
    }

    public static void tick(@NotNull ServerWorld world, BlockPos blockPos, @NotNull ChickenCoopBlockEntity chickenCoop) {
        if (chickenCoop.activeTicks > 0) {
            chickenCoop.triggerAnim(CONTROLLER_NAME, "active");
            chickenCoop.activeTicks += 1;
            if (!chickenCoop.isPlayingSound) {
                world.playSound(null, blockPos, SoundEvent.of(IKunMod.id("block.chicken_coop.active")), SoundCategory.BLOCKS, 1, 1);
                chickenCoop.isPlayingSound = true;
            }
        } else { // inactive
            chickenCoop.triggerAnim(CONTROLLER_NAME, "static");
        }
        if (chickenCoop.activeTicks > 2500) {
            world.createExplosion(null, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, 5, false, World.ExplosionSourceType.BLOCK);
            EntityRegistry.CHICKEN_KUN.spawn(world, blockPos, SpawnReason.EVENT);
        }
    }
}
