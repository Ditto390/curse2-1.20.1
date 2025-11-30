package net.ditto.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ZampatoItem extends SwordItem {
    public ZampatoItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // Only run on the server to prevent syncing weirdness
        if (!world.isClient) {
            // Check if the holder is a player
            if (entity instanceof PlayerEntity player) {
                NbtCompound nbt = stack.getOrCreateNbt();

                // Check if we have already branded this specific sword
                if (!nbt.contains("Branded")) {
                    // 1. Mark it as branded so we don't do this every single tick
                    nbt.putBoolean("Branded", true);

                    // 2. Change the name to Zanpakutō
                    String playerName = player.getName().getString();
                    stack.setCustomName(Text.literal("Zanpakutō (" + playerName + ")"));
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}