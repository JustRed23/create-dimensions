package dev.JustRed23.createdimensions.register;

import com.jozufozu.flywheel.core.PartialModel;
import dev.JustRed23.createdimensions.DimensionsAddon;
import net.minecraft.resources.ResourceLocation;

public final class CDPartialModels {

    // This is just here to make sure the class is loaded
    public static void init() {}

    private static PartialModel block(String path) {
        return new PartialModel(new ResourceLocation(DimensionsAddon.MODID, "block/" + path));
    }

    public static final PartialModel ROTATION_TRANSPORTER_SHAFT = block("transporters/rotation/shaft");
}
