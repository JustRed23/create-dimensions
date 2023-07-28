package dev.JustRed23.createdimensions.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ChunkLoaderCard extends Item {

    public ChunkLoaderCard(Properties pProperties) {
        super(pProperties);
    }

    public boolean isFoil(ItemStack pStack) { // Enchantment glint
        return true;
    }
}
