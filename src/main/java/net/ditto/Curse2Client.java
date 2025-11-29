package net.ditto;

import net.ditto.client.LevelOverlay;
import net.ditto.client.StatScreen;
import net.ditto.networking.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Curse2Client implements ClientModInitializer {

    private static KeyBinding statMenuKey;

    @Override
    public void onInitializeClient() {
        ModNetworking.registerClientPackets();
        HudRenderCallback.EVENT.register(new LevelOverlay());

        // Register Keybind
        statMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.curse2.stats",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V, // V for Vitality/Stats
                "category.curse2.general"
        ));

        // Handle Key Input
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (statMenuKey.wasPressed()) {
                client.setScreen(new StatScreen());
            }
        });
    }
}