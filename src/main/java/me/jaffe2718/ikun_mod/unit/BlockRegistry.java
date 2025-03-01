package me.jaffe2718.ikun_mod.unit;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.block.ChickenCoopBlock;
import me.jaffe2718.ikun_mod.block.entity.ChickenCoopBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;


public abstract class BlockRegistry {

    public static final Block CHICKEN_COOP = Blocks.register(
            RegistryKey.of(RegistryKeys.BLOCK, IKunMod.id("chicken_coop")),
            ChickenCoopBlock::new,
            AbstractBlock.Settings.create()
                    .nonOpaque()
                    .mapColor(MapColor.IRON_GRAY)
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .requiresTool()
                    .strength(5.0F, 6.0F)
                    .sounds(BlockSoundGroup.METAL)
                    .suffocates(Blocks::never)
                    .solidBlock(Blocks::never));

    public static BlockEntityType<ChickenCoopBlockEntity> CHICKEN_COOP_BLOCK_ENTITY;

    public static void register() {
        CHICKEN_COOP_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                IKunMod.id("chicken_coop"),
                FabricBlockEntityTypeBuilder.create(ChickenCoopBlockEntity::new, CHICKEN_COOP).build());
    }
}
