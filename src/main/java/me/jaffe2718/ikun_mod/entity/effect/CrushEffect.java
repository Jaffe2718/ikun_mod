package me.jaffe2718.ikun_mod.entity.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class CrushEffect extends StatusEffect {
    public CrushEffect() {
        super(StatusEffectCategory.HARMFUL, 0x8C8981);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
