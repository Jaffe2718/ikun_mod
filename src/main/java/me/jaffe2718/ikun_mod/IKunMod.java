package me.jaffe2718.ikun_mod;


import me.jaffe2718.ikun_mod.unit.BlockRegistry;
import me.jaffe2718.ikun_mod.unit.EffectRegistry;
import me.jaffe2718.ikun_mod.unit.EntityRegistry;
import me.jaffe2718.ikun_mod.unit.ItemRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class IKunMod implements ModInitializer {

    public static final String MOD_ID = "ikun_mod";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Contract("_ -> new")
    public static @NotNull Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        EffectRegistry.register();
        BlockRegistry.register();
        ItemRegistry.register();
        EntityRegistry.register();
    }
}
