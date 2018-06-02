package p455w0rd.danknull.network;

import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.SlotExtractionMode;

/**
 * @author p455w0rd
 *
 */
public class PacketSyncDankDock implements IMessage {

	private NonNullList<ItemStack> itemStacks;
	private int[] stackSizes;
	private Map<ItemStack, SlotExtractionMode> extractionModes = Maps.<ItemStack, SlotExtractionMode>newHashMap();
	private Map<ItemStack, Boolean> oreDictModes = Maps.<ItemStack, Boolean>newHashMap();
	private BlockPos pos = null;
	private static final String TEMP_EXTRACT_TAG = "ExtractionMode";
	private static final String TEMP_OREDICT_TAG = "OreDictMode";
	private boolean isLocked;
	private ItemStack dankNull = ItemStack.EMPTY;

	public PacketSyncDankDock() {
	}

	public PacketSyncDankDock(@Nonnull ItemStack dankNullIn, @Nonnull BlockPos posIn) {
		this(DankNullUtils.getNewDankNullInventory(dankNullIn), posIn);
	}

	public PacketSyncDankDock(@Nonnull InventoryDankNull inv, BlockPos posIn) {
		itemStacks = NonNullList.<ItemStack>withSize(inv.getStacks().size(), ItemStack.EMPTY);
		stackSizes = new int[inv.getStacks().size()];
		for (int i = 0; i < itemStacks.size(); i++) {
			ItemStack itemstack = inv.getStacks().get(i);
			itemStacks.set(i, itemstack.copy());
			stackSizes[i] = itemstack.getCount();
		}
		if (!DankNullUtils.getExtractionModes(inv.getDankNull()).isEmpty()) {
			extractionModes = DankNullUtils.getExtractionModes(inv.getDankNull());
		}
		if (!DankNullUtils.getOreDictModes(inv.getDankNull()).isEmpty()) {
			oreDictModes = DankNullUtils.getOreDictModes(inv.getDankNull());
		}
		if (posIn != null) {
			pos = posIn;
		}
		isLocked = DankNullUtils.isCreativeDankNullLocked(inv.getDankNull());
		dankNull = inv.getDankNull();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int i = buf.readShort();
		itemStacks = NonNullList.<ItemStack>withSize(i, ItemStack.EMPTY);
		stackSizes = new int[i];
		for (int j = 0; j < i; ++j) {
			itemStacks.set(j, ByteBufUtils.readItemStack(buf));
		}
		for (int j = 0; j < i; ++j) {
			stackSizes[j] = buf.readInt();
		}
		int extractionModesSize = buf.readInt();
		if (extractionModesSize > 0) {
			for (int k = 0; k < extractionModesSize; k++) {
				ItemStack currentStack = ByteBufUtils.readItemStack(buf);
				currentStack.setCount(1);
				int tmpExtractMode = 0;
				if (currentStack.hasTagCompound() && currentStack.getTagCompound().hasKey(TEMP_EXTRACT_TAG, Constants.NBT.TAG_INT)) {
					tmpExtractMode = currentStack.getTagCompound().getInteger(TEMP_EXTRACT_TAG);
				}
				SlotExtractionMode mode = SlotExtractionMode.values()[tmpExtractMode];
				extractionModes.put(currentStack, mode);
			}
		}
		int oreDictModesSize = buf.readInt();
		if (oreDictModesSize > 0) {
			for (int k = 0; k < oreDictModesSize; k++) {
				ItemStack currentStack = ByteBufUtils.readItemStack(buf);
				currentStack.setCount(1);
				boolean tmoOreDictMode = false;
				if (currentStack.hasTagCompound() && currentStack.getTagCompound().hasKey(TEMP_OREDICT_TAG)) {
					tmoOreDictMode = currentStack.getTagCompound().getBoolean(TEMP_OREDICT_TAG);
				}
				oreDictModes.put(currentStack, tmoOreDictMode);
			}
		}
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		pos = new BlockPos(x, y, z);
		isLocked = buf.readBoolean();
		dankNull = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeShort(itemStacks.size());
		for (ItemStack itemstack : itemStacks) {
			ByteBufUtils.writeItemStack(buf, itemstack);
		}
		for (int i = 0; i < itemStacks.size(); i++) {
			buf.writeInt(stackSizes[i]);
		}
		buf.writeInt(extractionModes.size());
		if (!extractionModes.isEmpty()) {
			for (ItemStack stack : extractionModes.keySet()) {
				if (!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
				}
				stack.getTagCompound().setInteger(TEMP_EXTRACT_TAG, extractionModes.get(stack).ordinal());
				ByteBufUtils.writeItemStack(buf, stack);
			}
		}
		buf.writeInt(oreDictModes.size());
		if (!oreDictModes.isEmpty()) {
			for (ItemStack stack : oreDictModes.keySet()) {
				if (!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
				}
				stack.getTagCompound().setBoolean(TEMP_OREDICT_TAG, oreDictModes.get(stack));
				ByteBufUtils.writeItemStack(buf, stack);
			}
		}
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		buf.writeBoolean(isLocked);
		ByteBufUtils.writeItemStack(buf, dankNull);
	}

	public static class Handler implements IMessageHandler<PacketSyncDankDock, IMessage> {
		@Override
		public IMessage onMessage(PacketSyncDankDock message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				InventoryDankNull inv = DankNullUtils.getNewDankNullInventory(message.dankNull);
				EntityPlayer player = ctx.side == Side.CLIENT ? DankNull.PROXY.getPlayer() : ctx.getServerHandler().player;
				World world = player.getEntityWorld();
				TileEntity te = world.getTileEntity(message.pos);
				//if (ctx.side == Side.CLIENT) {

				if (player.openContainer instanceof ContainerDankNull) {
					ContainerDankNull container = (ContainerDankNull) player.openContainer;
					if (!message.dankNull.isEmpty()) {
						inv = DankNullUtils.getNewDankNullInventory(message.dankNull);
						//inv = container.getDankNullInventory();
						for (int i = 0; i < message.itemStacks.size(); i++) {
							message.itemStacks.get(i).setCount(DankNullUtils.isCreativeDankNull(message.dankNull) ? Integer.MAX_VALUE : message.stackSizes[i]);
							//inv.setInventorySlotContents(i, message.itemStacks.get(i));
							container.putStackInSlot(i + 36, message.itemStacks.get(i));
						}
						if (!message.extractionModes.isEmpty()) {
							DankNullUtils.setExtractionModes(inv.getDankNull(), message.extractionModes);
						}
						if (!message.oreDictModes.isEmpty()) {
							DankNullUtils.setOreDictModes(inv.getDankNull(), message.oreDictModes);
						}
						if (DankNullUtils.isCreativeDankNull(inv.getDankNull())) {
							DankNullUtils.setLocked(inv.getDankNull(), message.isLocked);
						}
						container.setDankNullInventory(inv);
						//container.detectAndSendChanges();
						if (ctx.side == Side.CLIENT && inv != null) {
							if (DankNull.PROXY.getScreen() != null && DankNull.PROXY.getScreen() instanceof GuiDankNull) {
								GuiDankNull screen = (GuiDankNull) DankNull.PROXY.getScreen();
								screen.setDankNull(inv.getDankNull());
							}
						}
						if (ctx.side == Side.SERVER) {
							//container.sync();
						}
					}
				}
				//}
				if (te != null && te instanceof TileDankNullDock) {
					TileDankNullDock dankDock = (TileDankNullDock) te;
					//if (inv != null) {
					dankDock.setInventory(inv);
					//}
					//else {
					//	inv = dankDock.getInventory();
					//	if (inv == null) {
					//		return;
					//	}
					//}
					if (inv.getDankNull() != null) {
						ItemStack dankNull = inv.getDankNull();
						if (!dankNull.isEmpty()) {
							dankDock.setStack(dankNull);
							for (int i = 0; i < message.itemStacks.size(); i++) {
								message.itemStacks.get(i).setCount(DankNullUtils.isCreativeDankNull(dankNull) ? Integer.MAX_VALUE : message.stackSizes[i]);
								inv.setInventorySlotContents(i, message.itemStacks.get(i));
							}
							if (!message.extractionModes.isEmpty()) {
								DankNullUtils.setExtractionModes(dankNull, message.extractionModes);
							}
							if (!message.oreDictModes.isEmpty()) {
								DankNullUtils.setOreDictModes(inv.getDankNull(), message.oreDictModes);
							}
							if (DankNullUtils.isCreativeDankNull(inv.getDankNull())) {
								DankNullUtils.setLocked(inv.getDankNull(), message.isLocked);
							}
							//dankDock.setInventory(inv);
						}
					}
				}

			});
			return null;
		}

	}

}
