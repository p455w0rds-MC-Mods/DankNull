package p455w0rd.danknull.network;

import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullBase;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.cap.CapabilityDankNull;
import p455w0rd.danknull.util.cap.IDankNullHandler;

/**
 * @author BrockWS
 */
public class PacketChangeMode implements IMessage {

    private ChangeType changeType;
    private int slot = -1;
    private String uuid = "";

    public PacketChangeMode() {}

    public PacketChangeMode(ChangeType changeType) {
        this.changeType = changeType;
    }

    public PacketChangeMode(DankNullUtils.ItemPlacementMode mode, int slot) {
        switch (mode) {
            case KEEP_NONE:
                this.changeType = ChangeType.PLACE_KEEP_NONE;
                break;
            case KEEP_1:
                this.changeType = ChangeType.PLACE_KEEP_1;
                break;
            case KEEP_16:
                this.changeType = ChangeType.PLACE_KEEP_16;
                break;
            case KEEP_64:
                this.changeType = ChangeType.PLACE_KEEP_64;
                break;
            case KEEP_ALL:
                this.changeType = ChangeType.PLACE_KEEP_ALL;
                break;
            default:
                throw new RuntimeException("Unknown ItemPlacementMode " + mode.name());
        }
        this.slot = slot;
    }

    public PacketChangeMode(DankNullUtils.ItemExtractionMode mode, int slot) {
        switch (mode) {
            case KEEP_NONE:
                this.changeType = ChangeType.EXTRACT_KEEP_NONE;
                break;
            case KEEP_1:
                this.changeType = ChangeType.EXTRACT_KEEP_1;
                break;
            case KEEP_16:
                this.changeType = ChangeType.EXTRACT_KEEP_16;
                break;
            case KEEP_64:
                this.changeType = ChangeType.EXTRACT_KEEP_64;
                break;
            case KEEP_ALL:
                this.changeType = ChangeType.EXTRACT_KEEP_ALL;
                break;
            default:
                throw new RuntimeException("Unknown ItemExtractionMode " + mode.name());
        }
        this.slot = slot;
    }

    public PacketChangeMode(ChangeType type, int slot) {
        this.changeType = type;
        this.slot = slot;
    }

    public PacketChangeMode(ChangeType type, int slot, String uuid) {
        this.changeType = type;
        this.slot = slot;
        this.uuid = uuid;
    }

    public PacketChangeMode(ChangeType type, String uuid) {
        this.changeType = type;
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.changeType = ChangeType.values()[buf.readInt()];
        this.slot = buf.readInt();
        this.uuid = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.changeType.ordinal());
        buf.writeInt(this.slot);
        ByteBufUtils.writeUTF8String(buf, this.uuid);
    }

    public static class Handler implements IMessageHandler<PacketChangeMode, IMessage> {

        @Override
        public IMessage onMessage(PacketChangeMode message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                Container container = ctx.getServerHandler().player.openContainer;
                if (container instanceof ContainerDankNullBase) {
                    PacketChangeMode.handleModeUpdate(((ContainerDankNull) container).getHandler(), message.changeType, message.slot);
                } else if (message.uuid != null && !message.uuid.isEmpty()) {
                    ItemStack stack = DankNullUtils.findDankNull(ctx.getServerHandler().player, message.uuid);
                    if (stack != null && stack.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null)) {
                        PacketChangeMode.handleModeUpdate(stack.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null), message.changeType, message.slot);
                    }
                }
            });
            return null;
        }
    }

    private static void handleModeUpdate(IDankNullHandler handler, PacketChangeMode.ChangeType changeType, int slot) {
        ItemStack slotStack = slot >= 0 && slot < handler.getSlots() ? handler.getStackInSlot(slot) : ItemStack.EMPTY;
        switch (changeType) {
            case SELECTED:
                handler.setSelected(slot);
                break;

            case LOCK:
                handler.setLocked(true);
                break;
            case UNLOCK:
                handler.setLocked(false);
                break;

            case ORE_ON:
                handler.setOre(slotStack, true);
                break;
            case ORE_OFF:
                handler.setOre(slotStack, false);
                break;

            case EXTRACT_KEEP_ALL:
                handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_ALL);
                break;
            case EXTRACT_KEEP_1:
                handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_1);
                break;
            case EXTRACT_KEEP_16:
                handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_16);
                break;
            case EXTRACT_KEEP_64:
                handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_64);
                break;
            case EXTRACT_KEEP_NONE:
                handler.setExtractionMode(slotStack, DankNullUtils.ItemExtractionMode.KEEP_NONE);
                break;

            case PLACE_KEEP_ALL:
                handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_ALL);
                break;
            case PLACE_KEEP_1:
                handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_1);
                break;
            case PLACE_KEEP_16:
                handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_16);
                break;
            case PLACE_KEEP_64:
                handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_64);
                break;
            case PLACE_KEEP_NONE:
                handler.setPlacementMode(slotStack, DankNullUtils.ItemPlacementMode.KEEP_NONE);
                break;
        }
    }

    public enum ChangeType {
        LOCK,
        UNLOCK,
        SELECTED,
        ORE_ON,
        ORE_OFF,
        EXTRACT_KEEP_ALL,
        EXTRACT_KEEP_1,
        EXTRACT_KEEP_16,
        EXTRACT_KEEP_64,
        EXTRACT_KEEP_NONE,
        PLACE_KEEP_ALL,
        PLACE_KEEP_1,
        PLACE_KEEP_16,
        PLACE_KEEP_64,
        PLACE_KEEP_NONE
    }
}
