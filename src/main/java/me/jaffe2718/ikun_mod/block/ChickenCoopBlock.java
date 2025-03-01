package me.jaffe2718.ikun_mod.block;


import com.mojang.serialization.MapCodec;
import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.block.entity.ChickenCoopBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChickenCoopBlock extends BlockWithEntity {

    public static final MapCodec<ChickenCoopBlock> CODEC = createCodec(ChickenCoopBlock::new);

    public ChickenCoopBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<ChickenCoopBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return this::ticker;
    }

    private <T extends BlockEntity> void ticker(World world, BlockPos blockPos, BlockState state, T t) {
        if (world instanceof ServerWorld serverWorld && t instanceof ChickenCoopBlockEntity entity) {
            ChickenCoopBlockEntity.tick(serverWorld, blockPos, entity);
        }
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (entity instanceof ChickenEntity chicken && world instanceof ServerWorld serverWorld) {
            if (serverWorld.getBlockEntity(pos) instanceof ChickenCoopBlockEntity coop) {
                coop.active();
                chicken.discard();
            }
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, @NotNull BlockView world, BlockPos pos, ShapeContext context) {
        if (world.getBlockEntity(pos) instanceof ChickenCoopBlockEntity coop) {
            if (coop.activeTicks > 0) {
                return VoxelShapes.fullCube();
            } else {
                return VoxelShapes.empty();
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    public void onBroken(@NotNull WorldAccess world, BlockPos pos, BlockState state) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getServer().getPlayerManager().sendToAround(
                    null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvent.of(IKunMod.id("block.chicken_coop.active")).getDistanceToTravel(1),
                    serverWorld.getRegistryKey(),
                    new StopSoundS2CPacket(IKunMod.id("block.chicken_coop.active"), SoundCategory.BLOCKS)
            );
        }
    }

    @Override
    public void onDestroyedByExplosion(@NotNull ServerWorld world, BlockPos pos, Explosion explosion) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.getServer().getPlayerManager().sendToAround(
                    null,
                    pos.getX(), pos.getY(), pos.getZ(),
                    SoundEvent.of(IKunMod.id("block.chicken_coop.active")).getDistanceToTravel(1),
                    serverWorld.getRegistryKey(),
                    new StopSoundS2CPacket(IKunMod.id("block.chicken_coop.active"), SoundCategory.BLOCKS)
            );
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChickenCoopBlockEntity(pos, state);
    }

}
