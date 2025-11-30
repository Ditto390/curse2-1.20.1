package net.ditto;

import net.ditto.client.LevelOverlay;
import net.ditto.client.StatScreen;
import net.ditto.networking.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

public class Curse2Client implements ClientModInitializer {

    public static KeyBinding statMenuKey;
    public static KeyBinding abilityKey; // Z
    public static KeyBinding formKey;    // J
    public static KeyBinding modifierKey; // R (For scrolling)

    @Override
    public void onInitializeClient() {
        ModNetworking.registerClientPackets();
        HudRenderCallback.EVENT.register(new LevelOverlay());

        // 1. Register Keys
        statMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.curse2.stats", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.curse2.general"));
        abilityKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.curse2.use_ability", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.curse2.combat"));
        formKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.curse2.switch_form", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "category.curse2.combat"));
        modifierKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.curse2.modifier", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.curse2.combat"));

        // 2. Handle Key Inputs (Ticks)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (statMenuKey.wasPressed()) {
                client.setScreen(new StatScreen());
            }

            while (abilityKey.wasPressed()) {
                ClientPlayNetworking.send(ModNetworking.USE_ABILITY_ID, PacketByteBufs.empty());
            }

            while (formKey.wasPressed()) {
                ClientPlayNetworking.send(ModNetworking.TOGGLE_FORM_ID, PacketByteBufs.empty());
            }
        });
    }
}