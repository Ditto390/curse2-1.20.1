package net.ditto.levelling;

import net.ditto.ability.ShikaiType;

public interface PlayerLevelData {
    // --- Leveling ---
    int ditto$getLevel();
    void ditto$setLevel(int level);

    int ditto$getCurrentXp();
    void ditto$setCurrentXp(int xp);

    // --- Stats Management ---
    int ditto$getStatPoints();
    void ditto$addStatPoints(int points);
    void ditto$setStatPoints(int points);

    // Physique
    int ditto$getPhysique();
    void ditto$setPhysique(int value);
    void ditto$increasePhysique();

    // Finesse
    int ditto$getFinesse();
    void ditto$setFinesse(int value);
    void ditto$increaseFinesse();

    // Vitality
    int ditto$getVitality();
    void ditto$setVitality(int value);
    void ditto$increaseVitality();

    // Bond
    int ditto$getBond();
    void ditto$setBond(int value);
    void ditto$increaseBond();

    // --- Shikai & Abilities (New) ---
    ShikaiType ditto$getShikaiType();
    void ditto$setShikaiType(ShikaiType type);

    ShikaiType.Form ditto$getForm();
    void ditto$setForm(ShikaiType.Form form);

    int ditto$getSelectedAbilityIndex();
    void ditto$cycleAbility(int direction); // Pass 1 for next, -1 for previous

    // --- Sync & Core ---
    void ditto$syncLevel();      // Syncs stats/level
    void ditto$syncAbilities();  // Syncs Shikai/Form/Ability Selection

    void ditto$copyFrom(PlayerLevelData old); // Handles death persistence
}
