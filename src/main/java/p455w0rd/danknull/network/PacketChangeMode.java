package p455w0rd.danknull.network;

import net.minecraft.inventory.Container;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author BrockWS
 */
public class PacketChangeMode implements IMessage {

    private ChangeType changeType;
    private int slot;

    public PacketChangeMode() {}

    public PacketChangeMode(ChangeType changeType) {
        this.changeType = changeType;
        this.slot = -1;
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

    @Override
    public void fromBytes(ByteBuf buf) {
        this.changeType = ChangeType.values()[buf.readInt()];
        this.slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.changeType.ordinal());
        buf.writeInt(this.slot);
    }

    public static class Handler implements IMessageHandler<PacketChangeMode, IMessage> {

        @Override
        public IMessage onMessage(PacketChangeMode message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
                Container container = ctx.getServerHandler().player.openContainer;
                if (container instanceof ContainerDankNull) {
                    ((ContainerDankNull) container).handleModeUpdate(message.changeType, message.slot);
                } else if (container instanceof ContainerDankNullDock) {
                    ((ContainerDankNullDock) container).handleModeUpdate(message.changeType, message.slot);
                }
            });
            return null;
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
