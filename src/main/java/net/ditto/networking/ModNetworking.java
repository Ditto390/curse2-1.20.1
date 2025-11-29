package net.ditto.networking;

import net.ditto.Curse2;
import net.ditto.levelling.PlayerLevelData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier LEVEL_SYNC_ID = new Identifier(Curse2.MOD_ID, "level_sync");

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(LEVEL_SYNC_ID, (client, handler, buf, responseSender) -> {
            int level = buf.readInt();
            int xp = buf.readInt();
            client.execute(() -> {
                // Update the client-side player
                if (client.player instanceof PlayerLevelData playerData) {
                    playerData.ditto$setLevel(level);
                    playerData.ditto$setCurrentXp(xp);
                }
            });
        });
    }
}