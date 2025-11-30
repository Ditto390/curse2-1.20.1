package net.ditto.ability;

import net.minecraft.entity.player.PlayerEntity;

public abstract class Ability {
    private final String name;
    private final int cooldownTicks;

    public Ability(String name, int cooldownTicks) {
        this.name = name;
        this.cooldownTicks = cooldownTicks;
    }

    public abstract void onActivate(PlayerEntity player);

    public String getName() { return name; }
    public int getCooldown() { return cooldownTicks; }
}
