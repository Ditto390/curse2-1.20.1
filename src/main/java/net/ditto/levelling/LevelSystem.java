package net.ditto.levelling;

public class LevelSystem {
    // Modular Formula: XP needed for next level = Base * (Level^Multiplier)
    private static final int BASE_XP = 100;
    private static final float MULTIPLIER = 1.5f;

    public static int getXpForNextLevel(int currentLevel) {
        return (int) (BASE_XP * Math.pow(currentLevel, MULTIPLIER));
    }

    // Returns true if player leveled up
    public static boolean addXp(PlayerLevelData data, int amount) {
        int currentXp = data.ditto$getCurrentXp() + amount;
        int currentLevel = data.ditto$getLevel();
        int required = getXpForNextLevel(currentLevel);

        boolean leveledUp = false;
        while (currentXp >= required) {
            currentXp -= required;
            currentLevel++;
            required = getXpForNextLevel(currentLevel);
            leveledUp = true;
        }

        data.ditto$setLevel(currentLevel);
        data.ditto$setCurrentXp(currentXp);
        return leveledUp;
    }
}
