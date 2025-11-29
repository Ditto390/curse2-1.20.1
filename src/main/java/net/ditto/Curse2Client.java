package net.ditto;

import net.ditto.client.LevelOverlay;
import net.ditto.networking.ModNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class Curse2Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register Networking
        ModNetworking.registerClientPackets();

        // Register Overlay
        HudRenderCallback.EVENT.register(new LevelOverlay());
    }
}