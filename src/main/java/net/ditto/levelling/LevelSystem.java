package net.ditto.levelling;

public class LevelSystem {
    private static final int BASE_XP = 100;
    private static final float MULTIPLIER = 1.5f;
    private static final int POINTS_PER_LEVEL = 3; // Configurable: Points gained per level

    public static int getXpForNextLevel(int currentLevel) {
        return (int) (BASE_XP * Math.pow(currentLevel, MULTIPLIER));
    }

    public static boolean addXp(PlayerLevelData data, int amount) {
        int currentXp = data.ditto$getCurrentXp() + amount;
        int currentLevel = data.ditto$getLevel();
        int required = getXpForNextLevel(currentLevel);

        boolean leveledUp = false;
        while (currentXp >= required) {
            currentXp -= required;
            currentLevel++;
            required = getXpForNextLevel(currentLevel);

            // Award Stat Points
            data.ditto$addStatPoints(POINTS_PER_LEVEL);
            leveledUp = true;
        }

        data.ditto$setLevel(currentLevel);
        data.ditto$setCurrentXp(currentXp);
        return leveledUp;
    }
}
