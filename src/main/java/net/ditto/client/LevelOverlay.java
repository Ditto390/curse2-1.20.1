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

            // Dimensions: Thinner (3px) and smaller (50px)
            int barWidth = 50;
            int barHeight = 3;

            // Position: Move to the very right side of the screen
            // 2 pixels padding from the right edge
            int x = windowWidth - barWidth - 2;
            int y = windowHeight - 30;

            // Draw Background (Dark Grey/Black to resemble empty XP bar slot)
            context.fill(x, y, x + barWidth, y + barHeight, 0xFF202020);

            // Draw Progress (Vanilla XP Green: 0xFF80FF20)
            float progress = (float) currentXp / nextLevelXp;
            int filledWidth = (int) (barWidth * progress);

            // Fix: Ensure at least 1 pixel is shown if player has any XP (visual feedback for small gains)
            if (currentXp > 0 && filledWidth == 0) {
                filledWidth = 1;
            }
            // Cap width to bar size
            if (filledWidth > barWidth) {
                filledWidth = barWidth;
            }

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

            // Position text centered relative to the bar, and 10 pixels above it to avoid cropping
            context.drawTextWithShadow(font, text, x + (barWidth / 2) - (textWidth / 2), y - 10, 0xFF80FF20);
        }
    }
}