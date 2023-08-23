package dev.JustRed23.createdimensions.utils;

import dev.JustRed23.createdimensions.register.CDItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class ItemUtils {

    @OnlyIn(Dist.CLIENT)
    public static boolean isHolding(Item item) {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.isHolding(item);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isHoldingSynchronizerCard() {
        return isHolding(CDItems.TRANSPORTER_SYNCHRONIZER_CARD.get());
    }
}
