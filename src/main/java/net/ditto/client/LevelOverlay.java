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


            // --- 2. NEW SHIKAI HUD LOGIC ---
            ShikaiType shikai = stats.ditto$getShikaiType();

            // Only show if the player actually has a Shikai assigned
            if (shikai != ShikaiType.NONE) {
                ShikaiType.Form form = stats.ditto$getForm();
                int abilityIndex = stats.ditto$getSelectedAbilityIndex();

                // --- Draw Form Name (e.g., "ZANGETSU - SHIKAI") ---
                // Position: Above the Level Text
                String formText = shikai.name() + " [" + form.name() + "]";
                int formColor = (form == ShikaiType.Form.SEALED) ? 0xAAAAAA : 0xFFD700; // Grey for Sealed, Gold for Shikai/Bankai
                int formWidth = font.getWidth(formText);

                // Align to the right side
                context.drawTextWithShadow(font, formText, windowWidth - formWidth - 5, y - 35, formColor);

                // --- Draw Selected Ability (e.g., ">> Getsuga Tenshou <<") ---
                // Position: Above the Form Name
                List<Ability> abilities = shikai.getAbilitiesForForm(form);
                String abilityName = "No Ability";

                if (!abilities.isEmpty()) {
                    if (abilityIndex >= 0 && abilityIndex < abilities.size()) {
                        abilityName = abilities.get(abilityIndex).getName();
                    }
                    // Add arrows to indicate it's a selection
                    abilityName = "« " + abilityName + " »";
                } else {
                    abilityName = "---";
                }

                int abilityWidth = font.getWidth(abilityName);
                context.drawTextWithShadow(font, abilityName, windowWidth - abilityWidth - 5, y - 45, 0xFF55FFFF); // Light Blue
            }
        }
    }
}