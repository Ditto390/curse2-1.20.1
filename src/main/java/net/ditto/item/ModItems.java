package net.ditto.item;

import net.ditto.Curse2;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    // Use our custom ZampatoItem class instead of the generic SwordItem
    public static final Item ZAMPATO = registerItem("zampato",
            new ZampatoItem(ZampatoMaterial.INSTANCE, 3, -2.4f, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Curse2.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Curse2.LOGGER.info("Registering Mod Items for " + Curse2.MOD_ID);

        // Add to the Combat Creative Tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.add(ZAMPATO);
        });
    }
}