package me.jaffe2718.ikun_mod.unit;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.entity.ChickenKunEntity;
import me.jaffe2718.ikun_mod.entity.XiaoHeiZiEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
import net.minecraft.entity.EntityType;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public abstract class EntityRegistry {

    public static final EntityType<ChickenKunEntity> CHICKEN_KUN = Registry.register(
            Registries.ENTITY_TYPE,
            IKunMod.id("chicken_kun"),
            EntityType.Builder.create(ChickenKunEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6F, 1.8F)
                    .eyeHeight(1.62F)
                    .maxTrackingRange(10)
                    .build(RegistryKey.of(Registries.ENTITY_TYPE.getKey(), IKunMod.id("chicken_kun")))
    );

    public static final EntityType<XiaoHeiZiEntity> XIAO_HEI_ZI = Registry.register(
            Registries.ENTITY_TYPE,
            IKunMod.id("xiao_hei_zi"),
            EntityType.Builder.create(XiaoHeiZiEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.4F, 0.4F)
                    .maxTrackingRange(8)
                    .build(RegistryKey.of(Registries.ENTITY_TYPE.getKey(), IKunMod.id("xiao_hei_zi")))
    );


    public static void register() {
        FabricDefaultAttributeRegistry.register(CHICKEN_KUN, ChickenKunEntity.createChickenKunAttributes());
    }
}
