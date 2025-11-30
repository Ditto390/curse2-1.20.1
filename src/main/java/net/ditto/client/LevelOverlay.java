package net.ditto.client;

import net.ditto.ability.Ability;
import net.ditto.ability.ShikaiType;
import net.ditto.levelling.LevelSystem;
import net.ditto.levelling.PlayerLevelData;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import java.util.List;

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

            // --- 1. EXISTING XP BAR LOGIC ---
            int barWidth = 50;
            int barHeight = 3;
            int x = windowWidth - barWidth - 2;
            int y = windowHeight - 30;

            context.fill(x, y, x + barWidth, y + barHeight, 0xFF202020);

            float progress = (float) currentXp / nextLevelXp;
            int filledWidth = (int) (barWidth * progress);

            if (currentXp > 0 && filledWidth == 0) filledWidth = 1;
            if (filledWidth > barWidth) filledWidth = barWidth;

            if (filledWidth > 0) {
                context.fill(x, y, x + filledWidth, y + barHeight, 0xFF80FF20);
            }

            // Draw Border
            int borderColor = 0xFF000000;
            context.fill(x - 1, y - 1, x + barWidth + 1, y, borderColor);
            context.fill(x - 1, y + barHeight, x + barWidth + 1, y + barHeight + 1, borderColor);
            context.fill(x - 1, y, x, y + barHeight, borderColor);
            context.fill(x + barWidth, y, x + barWidth + 1, y + barHeight, borderColor);

            // Draw Level Text
            TextRenderer font = client.textRenderer;
            String lvlText = "Lvl " + level;
            int lvlTextWidth = font.getWidth(lvlText);
            context.drawTextWithShadow(font, lvlText, x + (barWidth / 2) - (lvlTextWidth / 2), y - 10, 0xFF80FF20);


            // --- 2. UPDATED COMPACT SHIKAI HUD ---
            ShikaiType shikai = stats.ditto$getShikaiType();

            if (shikai != ShikaiType.NONE) {
                ShikaiType.Form form = stats.ditto$getForm();
                int abilityIndex = stats.ditto$getSelectedAbilityIndex();

                // PUSH MATRIX to Scale Down
                context.getMatrices().push();

                // Scale to 0.75x (Smaller)
                float scale = 0.75f;
                context.getMatrices().scale(scale, scale, 1.0f);

                // Re-calculate coordinates because scaling affects the grid
                // We want it anchored to the right side, so we divide coordinates by scale
                int scaledWidth = (int) (windowWidth / scale);
                int scaledY = (int) (y / scale);

                // --- Draw Form Name ---
                String formText = shikai.name() + " [" + form.name() + "]";
                int formColor = (form == ShikaiType.Form.SEALED) ? 0xAAAAAA : 0xFFD700;
                int formWidth = font.getWidth(formText);

                // Position: Right side, slightly above XP bar area
                context.drawTextWithShadow(font, formText, scaledWidth - formWidth - 5, scaledY - 25, formColor);

                // --- Draw Selected Ability ---
                List<Ability> abilities = shikai.getAbilitiesForForm(form);
                String abilityName = "No Ability";

                if (!abilities.isEmpty()) {
                    if (abilityIndex >= 0 && abilityIndex < abilities.size()) {
                        abilityName = abilities.get(abilityIndex).getName();
                    }
                    abilityName = "« " + abilityName + " »";
                } else {
                    abilityName = "---";
                }

                int abilityWidth = font.getWidth(abilityName);
                // Position: Above Form Name
                context.drawTextWithShadow(font, abilityName, scaledWidth - abilityWidth - 5, scaledY - 35, 0xFF55FFFF);

                // POP MATRIX to return to normal scaling for other elements
                context.getMatrices().pop();
            }
        }
    }
}