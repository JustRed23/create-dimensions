package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import dev.JustRed23.createdimensions.DimensionsAddon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemTransporterEntity extends TransporterEntity implements IHaveGoggleInformation {

    private final SmartInventory upgradeInventory;
    private final SmartInventory syncInventory;

    protected LazyOptional<IItemHandlerModifiable> itemCapability;

    public ItemTransporterEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        upgradeInventory = new SmartInventory(1, this).withMaxStackSize(1);
        syncInventory = new SmartInventory(4, this).whenContentsChanged($ -> this.syncWithConnected());

        itemCapability = LazyOptional.of(() -> syncInventory);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this)
                .allowingBeltFunnels()
                .onlyInsertWhen(side -> side != Direction.DOWN && getMode() == TransportationMode.INSERT && isConnected())
        );
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && isConnected())
            return itemCapability.cast();
        return super.getCapability(cap, side);
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        IItemHandlerModifiable items = itemCapability.orElse(new ItemStackHandler());

        Lang.builder(DimensionsAddon.MODID).translate("gui.item_transporter.title")
                .forGoggles(tooltip);

        boolean empty = true;

        for (int slot = 0; slot < items.getSlots(); slot++) {
            ItemStack stackInSlot = items.getStackInSlot(slot);
            if (stackInSlot.isEmpty())
                continue;
            Lang.text("")
                    .add(Components.translatable(stackInSlot.getDescriptionId())
                            .withStyle(ChatFormatting.GRAY))
                    .add(Lang.text(" x" + stackInSlot.getCount())
                            .style(ChatFormatting.GREEN))
                    .forGoggles(tooltip, 1);
            empty = false;
        }

        if (empty)
            tooltip.clear();

        return true;
    }

    protected boolean tryConnect(TransporterEntity blockEntity) {
        if (!(blockEntity instanceof ItemTransporterEntity other)) // Cannot connect if the block entity is not of the same type
            return false;

        if (this.isVirtual() || other.isVirtual()) // Cannot connect if either of the blocks are virtual
            return false;

        if (this.isConnected() || other.isConnected()) // Cannot connect if either of the blocks are already connected
            return false;

        if (!this.isEmpty() && !other.isEmpty()) {
            final int slots = this.syncInventory.getSlots();
            final int slots1 = other.syncInventory.getSlots();
            if (slots != slots1) return false;

            for (int i = 0; i < slots; i++) {
                final ItemStack stack = this.syncInventory.getStackInSlot(i);
                final ItemStack stack1 = other.syncInventory.getStackInSlot(i);
                if (!ItemStack.isSame(stack, stack1)) return false;
            }
        }

        return true;
    }

    protected void trySyncContents(TransporterEntity blockEntity) {
        if (!(blockEntity instanceof ItemTransporterEntity other)) return;

        other.syncInventory.clearContent();

        for (int i = 0; i < this.syncInventory.getSlots(); i++) {
            final ItemStack stack = this.syncInventory.getStackInSlot(i);
            other.syncInventory.setStackInSlot(i, stack.copy());
        }

        other.setChanged();
    }

    protected void onConnectionRemoved(boolean keepContents) {
        if (!keepContents) {
            syncInventory.clearContent();

            if (!upgradeInventory.isEmpty())
                ItemHelper.dropContents(getLevel(), getBlockPos(), upgradeInventory);
        }
        setChanged();
    }

    protected void dropContents() {
        if (!upgradeInventory.isEmpty())
            ItemHelper.dropContents(getLevel(), getBlockPos(), upgradeInventory);

        if (!syncInventory.isEmpty())
            ItemHelper.dropContents(getLevel(), getBlockPos(), syncInventory);
    }

    protected void onModeChanged(TransportationMode mode) {
        switch (mode) {
            case INSERT -> {
                syncInventory.allowInsertion();
                syncInventory.forbidExtraction();
            }
            case EXTRACT -> {
                syncInventory.forbidInsertion();
                syncInventory.allowExtraction();
            }
        }
        setChanged();
    }

    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        syncInventory.deserializeNBT(tag.getCompound("SyncInventory"));
        upgradeInventory.deserializeNBT(tag.getCompound("UpgradeInventory"));
    }

    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("SyncInventory", syncInventory.serializeNBT());
        tag.put("UpgradeInventory", upgradeInventory.serializeNBT());
    }

    public void invalidate() {
        super.invalidate();
        itemCapability.invalidate();
    }

    public boolean isEmpty() {
        return syncInventory.isEmpty();
    }

    public SmartInventory getSyncInventory() {
        return syncInventory;
    }
}
