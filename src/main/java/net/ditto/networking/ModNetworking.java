package net.ditto.networking;

import net.ditto.Curse2;
import net.ditto.ability.ShikaiType;
import net.ditto.levelling.PlayerLevelData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModNetworking {
    // Existing IDs
    public static final Identifier LEVEL_SYNC_ID = new Identifier(Curse2.MOD_ID, "level_sync");
    public static final Identifier UPGRADE_STAT_ID = new Identifier(Curse2.MOD_ID, "upgrade_stat");

    // New IDs
    public static final Identifier ABILITY_SYNC_ID = new Identifier(Curse2.MOD_ID, "ability_sync");
    public static final Identifier CYCLE_ABILITY_ID = new Identifier(Curse2.MOD_ID, "cycle_ability");
    public static final Identifier USE_ABILITY_ID = new Identifier(Curse2.MOD_ID, "use_ability");
    public static final Identifier TOGGLE_FORM_ID = new Identifier(Curse2.MOD_ID, "toggle_form");

    public static void registerServerPackets() {
        // ... (Keep existing packet registrations)

        // 1. Cycle Ability (Scroll + R)
        ServerPlayNetworking.registerGlobalReceiver(CYCLE_ABILITY_ID, (server, player, handler, buf, responseSender) -> {
            int direction = buf.readInt(); // 1 or -1
            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    data.ditto$cycleAbility(direction);
                    data.ditto$syncAbilities(); // Sync back to client so UI updates

                    // Optional: Action bar notification
                    var abilities = data.ditto$getShikaiType().getAbilitiesForForm(data.ditto$getForm());
                    if (!abilities.isEmpty()) {
                        String abilityName = abilities.get(data.ditto$getSelectedAbilityIndex()).getName();
                        player.sendMessage(Text.literal("Selected: " + abilityName), true);
                    }
                }
            });
        });

        // 2. Use Ability (Z)
        ServerPlayNetworking.registerGlobalReceiver(USE_ABILITY_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    var abilities = data.ditto$getShikaiType().getAbilitiesForForm(data.ditto$getForm());
                    int index = data.ditto$getSelectedAbilityIndex();

                    if (index >= 0 && index < abilities.size()) {
                        // Activate logic (Add cooldown checks here later)
                        abilities.get(index).onActivate(player);
                    }
                }
            });
        });

        // 3. Toggle Form (J)
        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_FORM_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    // Simple Toggle Framework: Sealed <-> Shikai
                    ShikaiType.Form newForm = (data.ditto$getForm() == ShikaiType.Form.SEALED)
                            ? ShikaiType.Form.SHIKAI
                            : ShikaiType.Form.SEALED;

                    // Add Logic: Check if player HAS Shikai unlocked here

                    data.ditto$setForm(newForm);
                    data.ditto$syncAbilities();
                    player.sendMessage(Text.literal("Form Changed: " + newForm.name()), true);
                }
            });
        });
    }

    public static void registerClientPackets() {
        // ... (Keep existing)

        ClientPlayNetworking.registerGlobalReceiver(ABILITY_SYNC_ID, (client, handler, buf, responseSender) -> {
            int shikaiOrd = buf.readInt();
            int formOrd = buf.readInt();
            int selectedIndex = buf.readInt();

            client.execute(() -> {
                if (client.player instanceof PlayerLevelData data) {
                    data.ditto$setShikaiType(ShikaiType.values()[shikaiOrd]);
                    data.ditto$setForm(ShikaiType.Form.values()[formOrd]);
                    // We need to manually set the index or expose a setter for it on client
                    // For now, let's assume you added ditto$setSelectedAbilityIndex to interface
                }
            });
        });
    }
}