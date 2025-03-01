package me.jaffe2718.ikun_mod.unit;

import me.jaffe2718.ikun_mod.IKunMod;
import me.jaffe2718.ikun_mod.entity.effect.CrushEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;

public abstract class EffectRegistry {
    public static RegistryEntry<StatusEffect> CRUSH;

    public static void register() {
        CRUSH = Registry.registerReference(Registries.STATUS_EFFECT, IKunMod.id("crush"), new CrushEffect());
    }

}
