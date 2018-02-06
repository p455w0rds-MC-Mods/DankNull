package p455w0rd.danknull.blocks.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import p455w0rd.danknull.api.IRedstoneControllable;
import p455w0rd.danknull.api.ITOPBlockDisplayOverride;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.network.PacketSyncDankNull;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.SlotExtractionMode;

/**
 * @author p455w0rd
 *
 */
public class TileDankNullDock extends TileEntity implements IRedstoneControllable, ITOPBlockDisplayOverride, ISidedInventory {

	//private static final String TAG_REDSTONEMODE = "RedstoneMode";
	//private static final String TAG_HAS_REDSTONE_SIGNAL = "HasRSSignal";
	public static final String TAG_ITEMSTACK = "DankNullStack";
	public static final String TAG_NAME = "PWDock";
	private static final String TAG_SELECTEDSTACK = "SelectedStack";
	private static ItemStack selectedStack = ItemStack.EMPTY;

	private RedstoneMode redstoneMode = RedstoneMode.REQUIRED;
	private boolean hasRedstoneSignal = false;
	private ItemStack dankNullStack = ItemStack.EMPTY;
	private NonNullList<ItemStack> slots = NonNullList.create();
	InventoryDankNull inventory;

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return (!getStack().isEmpty() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN) || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (!getStack().isEmpty() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
		if (state.getBlock() == ModBlocks.DANKNULL_DOCK) {
			ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
			TileEntity tile = world.getTileEntity(data.getPos());
			if (tile != null && tile instanceof TileDankNullDock) {
				TileDankNullDock te = (TileDankNullDock) tile;
				stack.setTagInfo("BlockEntityTag", te.writeToNBT(new NBTTagCompound()));
				String dankNull = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
				String msg = DankNullUtils.translate("dn.right_click_with.desc") + (te.getStack().isEmpty() ? " " + dankNull : " " + DankNullUtils.translate("dn.empty_hand_open.desc"));
				ItemStack dockedDankNull = te.getStack().isEmpty() ? ItemStack.EMPTY : te.getStack();
				IProbeInfo topTip = probeInfo.horizontal().item(stack).vertical().itemLabel(stack);
				if (!dockedDankNull.isEmpty()) {
					String dockedMsg = ModGlobals.Rarities.getRarityFromMeta(dockedDankNull.getItemDamage()).rarityColor + "" + dockedDankNull.getDisplayName() + "" + TextFormatting.WHITE + " " + DankNullUtils.translate("dn.docked.desc");
					topTip.text(dockedMsg);
					ItemStack selectedStack = DankNullUtils.getSelectedStack(getInventory());
					if (!selectedStack.isEmpty()) {
						ItemStack tmpStack = selectedStack.copy();
						tmpStack.setCount(1);
						topTip.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(tmpStack).text(" " + DankNullUtils.translate("dn.selected.desc") + ", " + DankNullUtils.translate("dn.count.desc") + ": " + (DankNullUtils.isCreativeDankNull(dockedDankNull) ? DankNullUtils.translate("dn.infinite.desc") : selectedStack.getCount()));
						topTip.text(DankNullUtils.translate("dn.extract_mode.desc") + ": " + DankNullUtils.getExtractionModeForStack(dockedDankNull, selectedStack).getTooltip());
					}
				}
				topTip.text(msg).text(TextStyleClass.MODNAME.toString() + Tools.getModName(state.getBlock()));
				return true;
			}
		}
		return false;
	}

	public InventoryDankNull getInventory() {
		if (inventory == null && !getStack().isEmpty()) {
			inventory = DankNullUtils.getNewDankNullInventory(getStack());
		}
		return inventory;
	}

	public void setInventory(InventoryDankNull inv) {
		inventory = inv;
	}

	public void resetInventory() {
		inventory = null;
	}

	public void setStack(@Nonnull ItemStack stack) {
		dankNullStack = stack;
		//markDirty();
	}

	public int slotCount() {
		return !getStack().isEmpty() ? getStack().getItemDamage() + 1 * 9 : 0;
	}

	public ItemStack getSelectedStack() {
		return selectedStack;
	}

	public void setSelectedStack(ItemStack stack) {
		selectedStack = stack;
	}

	public ItemStack getStack() {
		return dankNullStack;
	}

	public NonNullList<ItemStack> getSlots() {
		return slots;
	}

	@Override
	public RedstoneMode getRedstoneMode() {
		return redstoneMode;
	}

	@Override
	public void setRedstoneMode(RedstoneMode mode) {
		redstoneMode = mode;
		markDirty();
	}

	@Override
	public boolean isRedstoneRequirementMet() {
		switch (getRedstoneMode()) {
		default:
		case IGNORED:
			return true;
		case REQUIRED:
			return hasRSSignal();
		case REQUIRE_NONE:
			return !hasRSSignal();
		}
	}

	@Override
	public boolean hasRSSignal() {
		return hasRedstoneSignal;
	}

	@Override
	public void setRSSignal(boolean isPowered) {
		hasRedstoneSignal = isPowered;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 255, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (getWorld() != null) {
			IBlockState state = getWorld().getBlockState(pos);
			if (state != null) {
				getWorld().notifyBlockUpdate(pos, state, state, 3);
				if (!getStack().isEmpty()) {
					ModNetworking.getInstance().sendToDimension(new PacketSyncDankNull(getStack(), getPos()), getWorld().provider.getDimension());
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		//NBTTagCompound nbt = compound.getCompoundTag(TAG_NAME);
		//setRedstoneMode(RedstoneMode.values()[nbt.getInteger(TAG_REDSTONEMODE)]);
		//setRSSignal(nbt.getBoolean(TAG_HAS_REDSTONE_SIGNAL));
		NBTTagCompound itemNBT = compound.getCompoundTag(TAG_ITEMSTACK);
		ItemStack newStack = itemNBT == null ? ItemStack.EMPTY : new ItemStack(itemNBT);
		setStack(newStack);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		NBTTagCompound itemNBT = new NBTTagCompound();
		//compound.setInteger(TAG_REDSTONEMODE, redstoneMode.ordinal());
		//compound.setBoolean(TAG_HAS_REDSTONE_SIGNAL, hasRSSignal());
		if (!getSelectedStack().isEmpty()) {
			NBTTagCompound selectedItemNBT = new NBTTagCompound();
			getSelectedStack().writeToNBT(selectedItemNBT);
			compound.setTag(TAG_SELECTEDSTACK, selectedItemNBT);
		}
		if (!getStack().isEmpty()) {
			getStack().writeToNBT(itemNBT);
			compound.setTag(TAG_ITEMSTACK, itemNBT);
		}
		return compound;
	}

	@Override
	public int getSizeInventory() {
		return !getStack().isEmpty() ? getInventory().getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		ItemStack stack = getInventory().getStackInSlot(index);
		if (!stack.isEmpty()) {
			int amountToBeKept = DankNullUtils.getExtractionModeForStack(getStack(), stack).getNumberToKeep();
			if (stack.getCount() > amountToBeKept) {
				ItemStack availableStack = stack.copy();
				availableStack.setCount(stack.getCount() - amountToBeKept);
				return availableStack;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack ret = !getStack().isEmpty() ? getInventory().decrStackSize(index, count, this) : ItemStack.EMPTY;
		DankNullUtils.reArrangeStacks(getInventory());
		return ret;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (!getStack().isEmpty()) {
			getInventory().setInventorySlotContents(index, stack, this);
			return;
		}
		getInventory().setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return !getStack().isEmpty() ? Integer.MAX_VALUE : 0;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return getInventory().isItemValidForSlot(index, stack);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public String getName() {
		return "danknull-inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (!getStack().isEmpty() && side == EnumFacing.DOWN) {
			int[] slots = new int[getSizeInventory()];
			for (int i = 0; i < slots.length; i++) {
				slots[i] = i;
			}
			return slots;
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if (!getStack().isEmpty()) {
			ItemStack tmpStack = getStackInSlot(index);
			if (DankNullUtils.getExtractionModeForStack(getStack(), stack) != SlotExtractionMode.KEEP_ALL) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return !getStack().isEmpty();
	}

	public enum RedstoneMode {
			REQUIRED, REQUIRE_NONE, IGNORED
	}

}
