package net.ditto;

import net.ditto.command.CurseCommand;
import net.ditto.levelling.PlayerLevelData;
import net.ditto.networking.ModNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Curse2 implements ModInitializer {
    public static final String MOD_ID = "curse2";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Hello Fabric world!");

        CommandRegistrationCallback.EVENT.register(CurseCommand::register);

        // 1. Register Packets (Vital for stat menu buttons)
        ModNetworking.registerServerPackets();

        // 2. DATA PERSISTENCE (Fixes Stats Resetting on Death)
        // This runs when the new entity is created. We copy the stats here.
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer instanceof PlayerLevelData newDitto && oldPlayer instanceof PlayerLevelData oldDitto) {
                newDitto.ditto$copyFrom(oldDitto);
            }
        });

        // 3. VISUAL SYNC (Fixes Bar not updating after Respawn)
        // This runs AFTER the player has officially respawned.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer instanceof PlayerLevelData playerData) {
                playerData.ditto$syncLevel();
            }
        });

        // 4. JOIN SYNC (Fixes Bar empty on Login)
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.player instanceof PlayerLevelData playerData) {
                playerData.ditto$syncLevel();
            }
        });
    }
}