package net.ditto.ability;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public enum ShikaiType {
    NONE,
    ZANGETSU, // Melee/Energy Archetype
    RYUJIN_JAKKA; // Fire Archetype

    // Modular Ability Fetcher
    public List<Ability> getAbilitiesForForm(Form form) {
        List<Ability> abilities = new ArrayList<>();

        switch (this) {
            case ZANGETSU -> {
                if (form == Form.SEALED) {
                    abilities.add(new Ability("Heavy Slash", 40) {
                        @Override public void onActivate(PlayerEntity p) {
                            p.sendMessage(Text.literal("Used Heavy Slash!"), true);
                            // Add logic: p.addVelocity(p.getRotationVector().multiply(2));
                        }
                    });
                } else if (form == Form.SHIKAI) {
                    abilities.add(new Ability("Getsuga Tenshou", 100) {
                        @Override public void onActivate(PlayerEntity p) {
                            p.sendMessage(Text.literal("GETSUGA TENSHOU!"), true);
                            // Add logic: Spawn projectile
                        }
                    });
                    abilities.add(new Ability("Flash Step", 20) {
                        @Override public void onActivate(PlayerEntity p) {
                            p.sendMessage(Text.literal("Whoosh!"), true);
                        }
                    });
                }
            }
            case RYUJIN_JAKKA -> {
                if (form == Form.SHIKAI) {
                    abilities.add(new Ability("Jokaku Enjo", 200) {
                        @Override public void onActivate(PlayerEntity p) {
                            p.setOnFireFor(10);
                            p.sendMessage(Text.literal("Wall of Flames!"), true);
                        }
                    });
                }
            }
        }
        return abilities;
    }

    public enum Form {
        SEALED,
        SHIKAI,
        BANKAI
    }
}
