package net.ditto.levelling;

public interface PlayerLevelData {
    int ditto$getLevel();
    void ditto$setLevel(int level);
    int ditto$getCurrentXp();
    void ditto$setCurrentXp(int xp);
    void ditto$syncLevel(); // Method to trigger a sync
}
