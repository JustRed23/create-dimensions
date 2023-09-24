package dev.JustRed23.createdimensions.blocks.blockentities;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.NBTHelper;
import dev.JustRed23.createdimensions.DimensionsAddon;
import dev.JustRed23.createdimensions.behaviour.ISync;
import dev.JustRed23.createdimensions.gui.impl.RotationTransporterMenu;
import dev.JustRed23.createdimensions.inv.UpgradeInventory;
import dev.JustRed23.createdimensions.utils.TransporterUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static dev.JustRed23.createdimensions.utils.ItemUtils.isHoldingSynchronizerCard;

public class RotationTransporterEntity extends KineticBlockEntity implements IHaveGoggleInformation, ISync, MenuProvider {

    private final SmartInventory upgradeInventory;

    public RotationTransporterEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        upgradeInventory = new UpgradeInventory(this);
        upgradeInventory.whenContentsChanged($ -> TransporterUtils.handleChunkLoading(this, getLevel(), upgradeInventory));
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (isHoldingSynchronizerCard())
            return false;

        Lang.builder(DimensionsAddon.MODID).translate("gui.rotation_transporter.title")
                .forGoggles(tooltip);

        if (!hasNetwork()) {
            Lang.builder(DimensionsAddon.MODID).translate("gui.rotation_transporter.no_network")
                    .style(ChatFormatting.DARK_GRAY)
                    .forGoggles(tooltip, 1);
            return true;
        }

        Lang.builder(DimensionsAddon.MODID).translate("gui.rotation_transporter.speed")
                .style(ChatFormatting.GREEN)
                .add(Lang.number(Math.abs(getSpeed())).space().translate("generic.unit.rpm"))
                .forGoggles(tooltip, 1);

        Lang.builder(DimensionsAddon.MODID).translate("gui.rotation_transporter.stress")
                .style(ChatFormatting.GOLD)
                .add(
                        Lang.number(stress)
                                .translate("generic.unit.stress")
                                .space().text("/").space()
                                .add(
                                        Lang.number(capacity)
                                                .translate("generic.unit.stress")
                                )
                ).forGoggles(tooltip, 1);

        return true;
    }

    protected boolean tryConnect(RotationTransporterEntity other) {
        if (this.isVirtual() || other.isVirtual()) // Cannot connect if either of the blocks are virtual
            return false;

        if (this.isConnected() || other.isConnected()) // Cannot connect if either of the blocks are already connected
            return false;

        if (this.getSpeed() > 0 && other.getSpeed() > 0) // Cannot connect if both blocks are moving
            return this.getSpeed() == other.getSpeed(); // Unless they are moving at the same speed

        return true;
    }

    protected void trySyncContents(RotationTransporterEntity other) {
        other.notifyUpdate();
    }

    protected void onConnectionRemoved(boolean keepContents) {
        detachKinetics();
        removeSource();

        if (!keepContents && !upgradeInventory.isEmpty())
                ItemHelper.dropContents(getLevel(), getBlockPos(), upgradeInventory);
    }

    protected void dropContents() {
        if (!upgradeInventory.isEmpty())
            ItemHelper.dropContents(getLevel(), getBlockPos(), upgradeInventory);
    }

    protected void onModeChanged(TransporterEntity.TransportationMode mode) {
        setChanged();
    }

    public void onRead(CompoundTag tag, boolean clientPacket) {
        upgradeInventory.deserializeNBT(tag.getCompound("UpgradeInventory"));
    }

    public void onWrite(CompoundTag tag, boolean clientPacket) {
        tag.put("UpgradeInventory", upgradeInventory.serializeNBT());
    }

    public @Nullable AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return RotationTransporterMenu.create(pContainerId, pPlayerInventory, this);
    }

    public void setChanged() {
        if (this.isConnected()) syncWithConnected();
        super.setChanged();
    }

    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        final RotationTransporterEntity other = getOther();
        if (isConnected() && other != null && this.getMode().equals(TransporterEntity.TransportationMode.INSERT) && other.getMode().equals(TransporterEntity.TransportationMode.EXTRACT))
            neighbours.add(other.getBlockPos());
        return super.addPropagationLocations(block, state, neighbours);
    }

    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (isConnected() && isCustomConnection(target, stateFrom, stateTo))
            return 1;
        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
    }

    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
        final RotationTransporterEntity other1 = getOther();
        if (other != null && other.equals(other1))
            return true;
        return super.isCustomConnection(other, state, otherState);
    }

    private RotationTransporterEntity getOther() {
        if (!isConnected()) return null;
        BlockEntity blockEntity = level.getBlockEntity(connectedTo);
        if (!(blockEntity instanceof RotationTransporterEntity other)) return null;
        return other;
    }

    public SmartInventory getUpgradeInventory() {
        return upgradeInventory;
    }

    public @NotNull Component getDisplayName() {
        return Lang.builder(DimensionsAddon.MODID).translate("gui.rotation_transporter.title").component();
    }

    public float getStress() {
        return stress;
    }

    public float getMaxStress() {
        return capacity;
    }





    // Code copied from TransporterEntity
    protected BlockPos connectedTo;
    protected ResourceKey<Level> dimension;
    private TransporterEntity.TransportationMode mode = TransporterEntity.TransportationMode.INSERT;
    private boolean chunkLoaded = false;
    private boolean preventSync = false; // Prevents a sync loop when syncing with connected block

    protected void read(CompoundTag tag, boolean clientPacket) {
        onRead(tag, clientPacket);
        super.read(tag, clientPacket);

        if (tag.contains("Mode")) {
            mode = NBTHelper.readEnum(tag, "Mode", TransporterEntity.TransportationMode.class);
            onModeChanged(mode);
        }

        if (!tag.contains("RemoteConnection")) {
            this.connectedTo = null;
            this.dimension = null;
            return;
        }

        CompoundTag connectionTag = tag.getCompound("RemoteConnection");
        if (!connectionTag.contains("pos") || !connectionTag.contains("dimension"))
            return;

        connectedTo = NbtUtils.readBlockPos(connectionTag.getCompound("pos"));
        dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(connectionTag.getString("dimension")));

        chunkLoaded = tag.getBoolean("ChunkLoaded");
        if (chunkLoaded) TransporterUtils.handleChunkLoading(this, getLevel(), getUpgradeInventory());
    }

    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        onWrite(tag, clientPacket);

        NBTHelper.writeEnum(tag, "Mode", mode);
        tag.putBoolean("ChunkLoaded", chunkLoaded);

        if (!isConnected()) return;
        CompoundTag connectionTag = new CompoundTag();
        connectionTag.put("pos", NbtUtils.writeBlockPos(connectedTo));
        connectionTag.putString("dimension", dimension.location().toString());
        tag.put("RemoteConnection", connectionTag);
    }

    public void destroy() {
        TransporterUtils.handleDestroyed(this, getLevel());
        clearConnection(false);
        super.destroy();
    }

    public void syncWithConnected() {
        if (!(level instanceof ServerLevel) || !isConnected()) return;

        ServerLevel dimensionLevel = level.getServer().getLevel(dimension);
        if (dimensionLevel == null)
            return;

        if (!(dimensionLevel.getBlockEntity(connectedTo) instanceof RotationTransporterEntity other)) {
            clearConnection(true);
            return;
        }

        if (this.preventSync || other.preventSync) return;

        other.preventSync = true;
        trySyncContents(other);
        other.preventSync = false;
    }

    public TransporterEntity.ConnectionStatus connectTo(BlockPos pos, ResourceKey<Level> dimension) {
        if (pos == null || dimension == null) return TransporterEntity.ConnectionStatus.INVALID_DATA;
        if (Objects.equals(dimension, this.dimension) && Objects.equals(pos, this.connectedTo)) return TransporterEntity.ConnectionStatus.ALREADY_CONNECTED; // Already connected to this block

        if (Objects.equals(getBlockPos(), pos) && Objects.equals(getLevel().dimension(), dimension))
            return TransporterEntity.ConnectionStatus.CANNOT_CONNECT_TO_SELF;

        ServerLevel dimensionLevel = getLevel().getServer().getLevel(dimension);
        if (dimensionLevel == null) return TransporterEntity.ConnectionStatus.INVALID_LEVEL;

        final BlockEntity blockEntity = dimensionLevel.getBlockEntity(pos);
        if (!(blockEntity instanceof RotationTransporterEntity other))
            return TransporterEntity.ConnectionStatus.INVALID_BLOCK;

        if (!tryConnect(other))
            return TransporterEntity.ConnectionStatus.CONNECT_FAILURE;

        this.connectedTo = pos;
        this.dimension = dimension;

        other.connectedTo = getBlockPos();
        other.dimension = getLevel().dimension();

        this.notifyUpdate();
        other.notifyUpdate();

        syncWithConnected();
        return TransporterEntity.ConnectionStatus.SUCCESS;
    }

    public boolean isConnected() {
        return dimension != null && connectedTo != null;
    }

    private void disconnect(boolean keepContents) {
        this.connectedTo = null;
        this.dimension = null;
        onConnectionRemoved(keepContents);
        notifyUpdate();
    }

    public void clearConnection(boolean keepContents) {
        clearConnection(keepContents, false);
    }

    public void clearConnection(boolean keepContents, boolean keepBoth) {
        if (!isConnected()) {
            dropContents();
            return;
        }

        ServerLevel dimensionLevel = level.getServer().getLevel(this.dimension);
        if (dimensionLevel == null) return;

        if (!(dimensionLevel.getBlockEntity(connectedTo) instanceof RotationTransporterEntity other)) return;

        other.disconnect(keepBoth || !keepContents);
        disconnect(keepBoth || keepContents);
    }

    public BlockPos getConnection() {
        return connectedTo;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setMode(@NotNull TransporterEntity.TransportationMode mode) {
        if (mode.equals(this.mode)) return;
        this.mode = mode;
        onModeChanged(mode);
    }

    public TransporterEntity.TransportationMode getMode() {
        return mode;
    }

    public void switchMode() {
        setMode(getMode() == TransporterEntity.TransportationMode.INSERT ? TransporterEntity.TransportationMode.EXTRACT : TransporterEntity.TransportationMode.INSERT);
    }

    public boolean isChunkLoaded() {
        return chunkLoaded;
    }

    public void setChunkLoaded(boolean chunkLoaded) {
        this.chunkLoaded = chunkLoaded;
    }
}
