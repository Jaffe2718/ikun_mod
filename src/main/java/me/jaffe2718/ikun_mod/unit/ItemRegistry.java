package me.jaffe2718.ikun_mod.unit;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.item.CenterPartedWigItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

public abstract class ItemRegistry {

    public static final Item CHICKEN_COOP = Items.register(BlockRegistry.CHICKEN_COOP, new Item.Settings().maxCount(1));
    public static final Item CENTER_PARTED_WIG = Items.register(
            RegistryKey.of(RegistryKeys.ITEM, IKunMod.id("center_parted_wig")),
            CenterPartedWigItem::new,
            new Item.Settings().fireproof().rarity(Rarity.RARE));


    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(group -> group.add(CHICKEN_COOP));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(group -> group.add(CENTER_PARTED_WIG));
    }
}
