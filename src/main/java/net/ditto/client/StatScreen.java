package net.ditto.client;

import net.ditto.levelling.PlayerLevelData;
import net.ditto.networking.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class StatScreen extends Screen {
    private int animationTick = 0;

    public StatScreen() {
        super(Text.literal("Stat Interface"));
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;

        // Add Upgrade Buttons (Only enabled if points > 0)
        // IDs: 0=Phys, 1=Fin, 2=Vit, 3=Bond

        // Physique
        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> sendUpgradePacket(0))
                .dimensions(centerX + 60, centerY - 45, 20, 20).build());

        // Finesse
        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> sendUpgradePacket(1))
                .dimensions(centerX + 60, centerY - 15, 20, 20).build());

        // Vitality
        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> sendUpgradePacket(2))
                .dimensions(centerX + 60, centerY + 15, 20, 20).build());

        // Bond
        this.addDrawableChild(ButtonWidget.builder(Text.literal("+"), button -> sendUpgradePacket(3))
                .dimensions(centerX + 60, centerY + 45, 20, 20).build());
    }

    private void sendUpgradePacket(int statId) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(statId);
        ClientPlayNetworking.send(ModNetworking.UPGRADE_STAT_ID, buf);
    }

    @Override
    public void tick() {
        super.tick();
        animationTick++; // For animation math
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // High Quality Dark Transparent Background
        this.renderBackground(context);

        int centerX = width / 2;
        int centerY = height / 2;

        // Draw the "Window" background
        // Animated slide-in effect: The Y position interpolates based on tick
        float slideIn = Math.min(1.0f, (animationTick + delta) / 10.0f);
        int animatedY = (int) (centerY * slideIn);

        // Main Box
        context.fill(centerX - 100, centerY - 80, centerX + 100, centerY + 80, 0xDD000000); // 87% Opacity Black
        context.drawBorder(centerX - 100, centerY - 80, 200, 160, 0xFF404040); // Dark Grey Border

        if (client.player instanceof PlayerLevelData data) {
            // Header
            context.drawCenteredTextWithShadow(textRenderer, "Character Stats", centerX, centerY - 70, 0xFFD700); // Gold

            // Available Points
            // Pulse animation for points if > 0
            int color = 0xFFFFFF;
            if (data.ditto$getStatPoints() > 0) {
                float pulse = (float) Math.sin((animationTick + delta) * 0.2) * 0.5f + 0.5f; // 0.0 to 1.0
                // Interpolate between White and Green
                int green = (int) (255 * pulse);
                color = (0xFF << 24) | (0x55 << 16) | (255 << 8) | 0x55; // Simple bright green tint
            }
            context.drawCenteredTextWithShadow(textRenderer, "Points Available: " + data.ditto$getStatPoints(), centerX, centerY - 95, color);

            // Stats Listing
            // Left align text, buttons are on right
            int startX = centerX - 80;

            drawStatRow(context, "Physique", data.ditto$getPhysique(), startX, centerY - 40, 0xFF5555); // Red
            drawStatRow(context, "Finesse", data.ditto$getFinesse(), startX, centerY - 10, 0x55FF55);  // Green
            drawStatRow(context, "Vitality", data.ditto$getVitality(), startX, centerY + 20, 0x5555FF); // Blue
            drawStatRow(context, "Bond", data.ditto$getBond(), startX, centerY + 50, 0xFF55FF);     // Purple
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawStatRow(DrawContext context, String name, int value, int x, int y, int color) {
        context.drawTextWithShadow(textRenderer, name, x, y, color);
        // Draw bars or value
        context.drawTextWithShadow(textRenderer, String.valueOf(value), x + 80, y, 0xFFFFFF);

        // Visual Bar (Background)
        context.fill(x, y + 10, x + 130, y + 12, 0xFF202020);
        // Visual Bar (Foreground - max out at lvl 100 visual for scaling)
        int fillWidth = Math.min(130, (int)((value / 50.0f) * 130));
        context.fill(x, y + 10, x + fillWidth, y + 12, color);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
