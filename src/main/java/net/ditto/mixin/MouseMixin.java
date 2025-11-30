package net.ditto.mixin;

import net.ditto.Curse2Client;
import net.ditto.networking.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"), cancellable = true)
    private void onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if our Modifier Key (R) is pressed
        if (client.player != null && Curse2Client.modifierKey.isPressed()) {

            // vertical is usually 1.0 (up) or -1.0 (down)
            int direction = (vertical > 0) ? -1 : 1; // Invert if feels wrong

            // Send Packet to Server
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(direction);
            ClientPlayNetworking.send(ModNetworking.CYCLE_ABILITY_ID, buf);

            // Cancel the event so the Hotbar doesn't scroll
            ci.cancel();
        }
    }
}
