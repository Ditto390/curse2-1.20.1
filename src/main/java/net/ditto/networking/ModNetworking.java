package net.ditto.networking;

import net.ditto.Curse2;
import net.ditto.ability.ShikaiType;
import net.ditto.item.ModItems;
import net.ditto.levelling.PlayerLevelData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier LEVEL_SYNC_ID = new Identifier(Curse2.MOD_ID, "level_sync");
    public static final Identifier UPGRADE_STAT_ID = new Identifier(Curse2.MOD_ID, "upgrade_stat");

    public static final Identifier ABILITY_SYNC_ID = new Identifier(Curse2.MOD_ID, "ability_sync");
    public static final Identifier CYCLE_ABILITY_ID = new Identifier(Curse2.MOD_ID, "cycle_ability");
    public static final Identifier USE_ABILITY_ID = new Identifier(Curse2.MOD_ID, "use_ability");
    public static final Identifier TOGGLE_FORM_ID = new Identifier(Curse2.MOD_ID, "toggle_form");

    private static final int STAT_CAP = 100;

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(UPGRADE_STAT_ID, (server, player, handler, buf, responseSender) -> {
            int statId = buf.readInt();

            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    if (data.ditto$getStatPoints() > 0) {
                        boolean success = false;
                        switch (statId) {
                            case 0 -> { if (data.ditto$getPhysique() < STAT_CAP) { data.ditto$increasePhysique(); success = true; } }
                            case 1 -> { if (data.ditto$getFinesse() < STAT_CAP) { data.ditto$increaseFinesse(); success = true; } }
                            case 2 -> { if (data.ditto$getVitality() < STAT_CAP) { data.ditto$increaseVitality(); success = true; } }
                            case 3 -> { if (data.ditto$getBond() < STAT_CAP) { data.ditto$increaseBond(); success = true; } }
                        }
                        if (success) {
                            data.ditto$addStatPoints(-1);
                            data.ditto$syncLevel();
                        }
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CYCLE_ABILITY_ID, (server, player, handler, buf, responseSender) -> {
            int direction = buf.readInt();
            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    data.ditto$cycleAbility(direction);
                    data.ditto$syncAbilities();

                    var abilities = data.ditto$getShikaiType().getAbilitiesForForm(data.ditto$getForm());
                    if (!abilities.isEmpty()) {
                        String abilityName = abilities.get(data.ditto$getSelectedAbilityIndex()).getName();
                        player.sendMessage(Text.literal("Selected: " + abilityName), true);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(USE_ABILITY_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    var abilities = data.ditto$getShikaiType().getAbilitiesForForm(data.ditto$getForm());
                    int index = data.ditto$getSelectedAbilityIndex();

                    if (index >= 0 && index < abilities.size()) {
                        abilities.get(index).onActivate(player);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_FORM_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player instanceof PlayerLevelData data) {
                    // Check if player is trying to enter Shikai
                    if (data.ditto$getForm() == ShikaiType.Form.SEALED) {

                        // REQUIREMENT 1: Bond >= 25
                        if (data.ditto$getBond() < 25) {
                            player.sendMessage(Text.literal("§cYour Bond is too weak (" + data.ditto$getBond() + "/25)"), true);
                            return;
                        }

                        // REQUIREMENT 2: Holding Branded Zanpakuto
                        ItemStack stack = player.getMainHandStack();
                        boolean isZanpakuto = stack.isOf(ModItems.ZAMPATO);
                        boolean isBranded = stack.hasNbt() && stack.getNbt().getBoolean("Branded");

                        if (!isZanpakuto || !isBranded) {
                            player.sendMessage(Text.literal("§cYou must hold your Zanpakutō to release it."), true);
                            return;
                        }

                        // Switch to SHIKAI
                        data.ditto$setForm(ShikaiType.Form.SHIKAI);
                        player.sendMessage(Text.literal("§b" + data.ditto$getShikaiType().name() + ", Shikai!"), true);

                        // --- TEXTURE CHANGE: Set CustomModelData to 1 ---
                        stack.getOrCreateNbt().putInt("CustomModelData", 1);

                    } else {
                        // Switch back to SEALED (Always allowed)
                        data.ditto$setForm(ShikaiType.Form.SEALED);
                        player.sendMessage(Text.literal("§7Sealed."), true);

                        // --- TEXTURE CHANGE: Remove CustomModelData (Reset) ---
                        ItemStack stack = player.getMainHandStack();
                        if (stack.isOf(ModItems.ZAMPATO)) {
                            stack.getOrCreateNbt().remove("CustomModelData");
                        }
                    }

                    data.ditto$syncAbilities();
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

        ClientPlayNetworking.registerGlobalReceiver(ABILITY_SYNC_ID, (client, handler, buf, responseSender) -> {
            int shikaiOrd = buf.readInt();
            int formOrd = buf.readInt();
            int selectedIndex = buf.readInt();

            client.execute(() -> {
                if (client.player instanceof PlayerLevelData data) {
                    data.ditto$setShikaiType(ShikaiType.values()[shikaiOrd]);
                    data.ditto$setForm(ShikaiType.Form.values()[formOrd]);
                    data.ditto$setSelectedAbilityIndex(selectedIndex); // NEW: Update client side index
                }
            });
        });
    }
}