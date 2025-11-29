package net.ditto.networking;

import net.ditto.Curse2;
import net.ditto.levelling.PlayerLevelData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier LEVEL_SYNC_ID = new Identifier(Curse2.MOD_ID, "level_sync");
    public static final Identifier UPGRADE_STAT_ID = new Identifier(Curse2.MOD_ID, "upgrade_stat");

    private static final int STAT_CAP = 100;

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(UPGRADE_STAT_ID, (server, player, handler, buf, responseSender) -> {
            int statId = buf.readInt();

            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    if (data.ditto$getStatPoints() > 0) {
                        boolean success = false;

                        // Check Caps
                        switch (statId) {
                            case 0 -> { if (data.ditto$getPhysique() < STAT_CAP) { data.ditto$increasePhysique(); success = true; } }
                            case 1 -> { if (data.ditto$getFinesse() < STAT_CAP) { data.ditto$increaseFinesse(); success = true; } }
                            case 2 -> { if (data.ditto$getVitality() < STAT_CAP) { data.ditto$increaseVitality(); success = true; } }
                            case 3 -> { if (data.ditto$getBond() < STAT_CAP) { data.ditto$increaseBond(); success = true; } }
                        }

                        if (success) {
                            data.ditto$addStatPoints(-1);
                            data.ditto$syncLevel(); // This triggers the Client Packet below
                        }
                    }
                }
            });
        });
    }

    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(LEVEL_SYNC_ID, (client, handler, buf, responseSender) -> {
            int level = buf.readInt();
            int xp = buf.readInt();
            int points = buf.readInt();
            int physique = buf.readInt();
            int finesse = buf.readInt();
            int vitality = buf.readInt();
            int bond = buf.readInt();

            client.execute(() -> {
                if (client.player instanceof PlayerLevelData playerData) {
                    // This updates the CLIENT SIDE data, making the bar move
                    playerData.ditto$setLevel(level);
                    playerData.ditto$setCurrentXp(xp);
                    playerData.ditto$setStatPoints(points);
                    playerData.ditto$setPhysique(physique);
                    playerData.ditto$setFinesse(finesse);
                    playerData.ditto$setVitality(vitality);
                    playerData.ditto$setBond(bond);
                }
            });
        });
    }
}