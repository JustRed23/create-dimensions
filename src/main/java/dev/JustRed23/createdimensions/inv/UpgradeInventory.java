package dev.JustRed23.createdimensions.inv;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.item.SmartInventory;
import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.world.item.ItemStack;

public class UpgradeInventory extends SmartInventory {

    public UpgradeInventory(SmartBlockEntity be) {
        super(1, be, 1, false);
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == 0)
            return CDItems.TRANSPORTER_CHUNK_UPGRADE.isIn(stack);
        return false;
    }
}
