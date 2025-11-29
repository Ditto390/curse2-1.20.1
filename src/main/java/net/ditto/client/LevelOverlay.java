package net.ditto.client;

import net.ditto.levelling.LevelSystem;
import net.ditto.levelling.PlayerLevelData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class LevelOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (client.player instanceof PlayerLevelData stats) {
            int level = stats.ditto$getLevel();
            int currentXp = stats.ditto$getCurrentXp();
            int nextLevelXp = LevelSystem.getXpForNextLevel(level);

            int windowWidth = client.getWindow().getScaledWidth();
            int windowHeight = client.getWindow().getScaledHeight();

            // Dimensions: Thinner (5px) and shorter (70px) to look more like a vanilla HUD element
            int barWidth = 70;
            int barHeight = 5;

            // Position: Move to the right side of the screen
            // 15 pixels padding from the right edge, 30 pixels up from bottom
            int x = windowWidth - barWidth - 15;
            int y = windowHeight - 30;

            // Draw Background (Dark Grey/Black to resemble empty XP bar slot)
            context.fill(x, y, x + barWidth, y + barHeight, 0xFF202020);

            // Draw Progress (Vanilla XP Green: 0xFF80FF20)
            float progress = (float) currentXp / nextLevelXp;
            int filledWidth = (int) (barWidth * progress);

            // Only draw the green fill if we have XP
            if (filledWidth > 0) {
                context.fill(x, y, x + filledWidth, y + barHeight, 0xFF80FF20);
            }

            // Draw Border (Manual fills for crisp 1px black outline)
            int borderColor = 0xFF000000;
            context.fill(x - 1, y - 1, x + barWidth + 1, y, borderColor); // Top
            context.fill(x - 1, y + barHeight, x + barWidth + 1, y + barHeight + 1, borderColor); // Bottom
            context.fill(x - 1, y, x, y + barHeight, borderColor); // Left
            context.fill(x + barWidth, y, x + barWidth + 1, y + barHeight, borderColor); // Right

            // Draw Level Text
            // Centered above the bar, using the same Green color for style
            TextRenderer font = client.textRenderer;
            String text = "Lvl " + level;
            int textWidth = font.getWidth(text);

            // Position text centered relative to the bar, and 10 pixels above it
            context.drawTextWithShadow(font, text, x + (barWidth / 2) - (textWidth / 2), y - 10, 0xFF80FF20);
        }
    }
}
