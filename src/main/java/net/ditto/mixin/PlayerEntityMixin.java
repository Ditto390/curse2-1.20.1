package net.ditto.mixin;

import net.ditto.levelling.PlayerLevelData;
import net.ditto.networking.ModNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
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
    @Unique private int statPoints = 0;
    @Unique private int physique = 1;
    @Unique private int finesse = 1;
    @Unique private int vitality = 1;
    @Unique private int bond = 1;

    @Override public int ditto$getLevel() { return dittoLevel; }
    @Override public int ditto$getCurrentXp() { return dittoCurrentXp; }
    @Override public int ditto$getStatPoints() { return statPoints; }
    @Override public int ditto$getPhysique() { return physique; }
    @Override public int ditto$getFinesse() { return finesse; }
    @Override public int ditto$getVitality() { return vitality; }
    @Override public int ditto$getBond() { return bond; }

    @Override public void ditto$setLevel(int level) { this.dittoLevel = level; }
    @Override public void ditto$setCurrentXp(int xp) { this.dittoCurrentXp = xp; }
    @Override public void ditto$setStatPoints(int points) { this.statPoints = points; }
    @Override public void ditto$addStatPoints(int points) { this.statPoints += points; }

    @Override
    public void ditto$setPhysique(int value) {
        this.physique = value;
        updateAttributes();
    }
    @Override
    public void ditto$increasePhysique() {
        this.physique++;
        updateAttributes();
    }

    @Override
    public void ditto$setFinesse(int value) {
        this.finesse = value;
        updateAttributes();
    }
    @Override
    public void ditto$increaseFinesse() {
        this.finesse++;
        updateAttributes();
    }

    @Override
    public void ditto$setVitality(int value) {
        this.vitality = value;
        updateAttributes();
    }
    @Override
    public void ditto$increaseVitality() {
        this.vitality++;
        updateAttributes();
    }

    @Override public void ditto$setBond(int value) { this.bond = value; }
    @Override public void ditto$increaseBond() { this.bond++; }

    @Unique
    private void updateAttributes() {
        PlayerEntity player = (PlayerEntity) (Object) this;

        EntityAttributeInstance healthAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(20.0 + ((vitality - 1) * 2.0));
        }

        EntityAttributeInstance damageAttr = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (damageAttr != null) {
            damageAttr.setBaseValue(1.0 + ((physique - 1) * 0.5));
        }

        EntityAttributeInstance speedAttr = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(0.1 + ((finesse - 1) * 0.002));
        }
    }

    @Override
    public void ditto$copyFrom(PlayerLevelData old) {
        this.dittoLevel = old.ditto$getLevel();
        this.dittoCurrentXp = old.ditto$getCurrentXp();
        this.statPoints = old.ditto$getStatPoints();
        this.physique = old.ditto$getPhysique();
        this.finesse = old.ditto$getFinesse();
        this.vitality = old.ditto$getVitality();
        this.bond = old.ditto$getBond();
        // Important: Update attributes on the new server entity immediately
        updateAttributes();
    }

    @Override
    public void ditto$syncLevel() {
        if ((Object)this instanceof ServerPlayerEntity serverPlayer) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(dittoLevel);
            buf.writeInt(dittoCurrentXp);
            buf.writeInt(statPoints);
            buf.writeInt(physique);
            buf.writeInt(finesse);
            buf.writeInt(vitality);
            buf.writeInt(bond);
            ServerPlayNetworking.send(serverPlayer, ModNetworking.LEVEL_SYNC_ID, buf);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void writeLevelData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("DittoLevel", dittoLevel);
        nbt.putInt("DittoXP", dittoCurrentXp);
        nbt.putInt("DittoPoints", statPoints);
        nbt.putInt("DittoPhysique", physique);
        nbt.putInt("DittoFinesse", finesse);
        nbt.putInt("DittoVitality", vitality);
        nbt.putInt("DittoBond", bond);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void readLevelData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("DittoLevel")) {
            dittoLevel = nbt.getInt("DittoLevel");
            dittoCurrentXp = nbt.getInt("DittoXP");
            statPoints = nbt.getInt("DittoPoints");
            physique = nbt.getInt("DittoPhysique");
            finesse = nbt.getInt("DittoFinesse");
            vitality = nbt.getInt("DittoVitality");
            bond = nbt.getInt("DittoBond");
            updateAttributes();
        }
    }
}