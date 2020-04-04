package p455w0rd.danknull.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;

/**
 * @author BrockWS
 */
public class PacketChangeMode implements IMessage {

	private ChangeType changeType;
	private int slot = -1;
	private boolean isGui = true;
	private boolean mainHand = true;

	public PacketChangeMode() {
	}

	public PacketChangeMode(final ChangeType changeType) {
		this.changeType = changeType;
	}

	public PacketChangeMode(final ItemPlacementMode mode, final int slot) {
		switch (mode) {
		case KEEP_NONE:
			changeType = ChangeType.PLACE_KEEP_NONE;
			break;
		case KEEP_1:
			changeType = ChangeType.PLACE_KEEP_1;
			break;
		case KEEP_16:
			changeType = ChangeType.PLACE_KEEP_16;
			break;
		case KEEP_64:
			changeType = ChangeType.PLACE_KEEP_64;
			break;
		case KEEP_ALL:
			changeType = ChangeType.PLACE_KEEP_ALL;
			break;
		default:
			throw new RuntimeException("Unknown ItemPlacementMode " + mode.name());
		}
		this.slot = slot;
	}

	public PacketChangeMode(final ItemExtractionMode mode, final int slot) {
		switch (mode) {
		case KEEP_NONE:
			changeType = ChangeType.EXTRACT_KEEP_NONE;
			break;
		case KEEP_1:
			changeType = ChangeType.EXTRACT_KEEP_1;
			break;
		case KEEP_16:
			changeType = ChangeType.EXTRACT_KEEP_16;
			break;
		case KEEP_64:
			changeType = ChangeType.EXTRACT_KEEP_64;
			break;
		case KEEP_ALL:
			changeType = ChangeType.EXTRACT_KEEP_ALL;
			break;
		default:
			throw new RuntimeException("Unknown ItemExtractionMode " + mode.name());
		}
		this.slot = slot;
	}

	public PacketChangeMode(final ChangeType type, final int slot) {
		this(type, slot, true, EnumHand.MAIN_HAND);
	}

	public PacketChangeMode(final ChangeType type, final int slot, final boolean isGui, final EnumHand hand) {
		changeType = type;
		this.slot = slot;
		this.isGui = isGui;
		mainHand = hand == EnumHand.MAIN_HAND;
	}

	@Override
	public void fromBytes(final ByteBuf buf) {
		changeType = ChangeType.VALUES[buf.readInt()];
		slot = buf.readInt();
		isGui = buf.readBoolean();
		if (!isGui) {
			mainHand = buf.readBoolean();
		}
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		buf.writeInt(changeType.ordinal());
		buf.writeInt(slot);
		buf.writeBoolean(isGui);
		if (!isGui) {
			buf.writeBoolean(mainHand);
		}
	}

	public static class Handler implements IMessageHandler<PacketChangeMode, IMessage> {

		private static ItemStack findDankNull(final EntityPlayer player, final boolean mainHand) {
			final ItemStack stack = mainHand ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
			if (stack.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null)) {
				return stack;
			}
			return ItemStack.EMPTY;
		}

		@Override
		public IMessage onMessage(final PacketChangeMode message, final MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				if (message.isGui) {
					final Container container = ctx.getServerHandler().player.openContainer;
					if (container instanceof ContainerDankNull) {
						PacketChangeMode.handleModeUpdate(((ContainerDankNull) container).getHandler(), message.changeType, message.slot);
					}
				}
				else {
					final ItemStack stack = findDankNull(ctx.getServerHandler().player, message.mainHand);
					if (!stack.isEmpty() && stack.hasCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null)) {
						PacketChangeMode.handleModeUpdate(stack.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null), message.changeType, message.slot);
					}
				}
			});
			return null;
		}
	}

	private static void handleModeUpdate(final IDankNullHandler handler, final PacketChangeMode.ChangeType changeType, final int slot) {
		final ItemStack slotStack = slot >= 0 && slot < handler.getSlots() ? handler.getFullStackInSlot(slot) : ItemStack.EMPTY;
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
			handler.setExtractionMode(slotStack, ItemExtractionMode.KEEP_ALL);
			break;
		case EXTRACT_KEEP_1:
			handler.setExtractionMode(slotStack, ItemExtractionMode.KEEP_1);
			break;
		case EXTRACT_KEEP_16:
			handler.setExtractionMode(slotStack, ItemExtractionMode.KEEP_16);
			break;
		case EXTRACT_KEEP_64:
			handler.setExtractionMode(slotStack, ItemExtractionMode.KEEP_64);
			break;
		case EXTRACT_KEEP_NONE:
			handler.setExtractionMode(slotStack, ItemExtractionMode.KEEP_NONE);
			break;

		case PLACE_KEEP_ALL:
			handler.setPlacementMode(slotStack, ItemPlacementMode.KEEP_ALL);
			break;
		case PLACE_KEEP_1:
			handler.setPlacementMode(slotStack, ItemPlacementMode.KEEP_1);
			break;
		case PLACE_KEEP_16:
			handler.setPlacementMode(slotStack, ItemPlacementMode.KEEP_16);
			break;
		case PLACE_KEEP_64:
			handler.setPlacementMode(slotStack, ItemPlacementMode.KEEP_64);
			break;
		case PLACE_KEEP_NONE:
			handler.setPlacementMode(slotStack, ItemPlacementMode.KEEP_NONE);
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
			PLACE_KEEP_NONE;

		public static ChangeType[] VALUES = values(); //Learned this from McJty..if your Enum won't be modified later, cache the values

	}
}
