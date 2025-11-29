package net.ditto.levelling;

public interface PlayerLevelData {
    // Leveling
    int ditto$getLevel();
    void ditto$setLevel(int level);
    int ditto$getCurrentXp();
    void ditto$setCurrentXp(int xp);

    // Stats
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

    // Core
    void ditto$syncLevel();
    void ditto$copyFrom(PlayerLevelData old); // New method for death persistence
}
