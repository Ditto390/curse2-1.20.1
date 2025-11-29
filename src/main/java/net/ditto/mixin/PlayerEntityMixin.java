package net.ditto.mixin;

import net.ditto.levelling.PlayerLevelData;
import net.ditto.networking.ModNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerLevelData {

    @Unique private int dittoLevel = 1;
    @Unique private int dittoCurrentXp = 0;

    @Override
    public int ditto$getLevel() { return dittoLevel; }

    @Override
    public void ditto$setLevel(int level) { this.dittoLevel = level; }

    @Override
    public int ditto$getCurrentXp() { return dittoCurrentXp; }

    @Override
    public void ditto$setCurrentXp(int xp) { this.dittoCurrentXp = xp; }

    @Override
    public void ditto$syncLevel() {
        if ((Object)this instanceof ServerPlayerEntity serverPlayer) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(dittoLevel);
            buf.writeInt(dittoCurrentXp);
            ServerPlayNetworking.send(serverPlayer, ModNetworking.LEVEL_SYNC_ID, buf);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeLevelData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("DittoLevel", dittoLevel);
        nbt.putInt("DittoXP", dittoCurrentXp);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readLevelData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("DittoLevel")) {
            dittoLevel = nbt.getInt("DittoLevel");
            dittoCurrentXp = nbt.getInt("DittoXP");
        }
    }
}
