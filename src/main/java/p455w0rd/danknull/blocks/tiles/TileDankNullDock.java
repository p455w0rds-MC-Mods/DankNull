package p455w0rd.danknull.blocks.tiles;

import javax.annotation.Nullable;

import mcjty.theoneprobe.Tools;
import mcjty.theoneprobe.api.*;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import p455w0rd.danknull.api.IRedstoneControllable;
import p455w0rd.danknull.api.ITOPBlockDisplayOverride;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.network.VanillaPacketDispatcher;
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

	private RedstoneMode redstoneMode = RedstoneMode.REQUIRED;
	private boolean hasRedstoneSignal = false;
	private final NonNullList<ItemStack> slots = NonNullList.create();
	InventoryDankNull inventory = null;

	@Override
	public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
		return !getDankNull().isEmpty() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
		if (!getDankNull().isEmpty() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == EnumFacing.DOWN) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new SidedInvWrapper(this, facing));
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean overrideStandardInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player, final World world, final IBlockState state, final IProbeHitData data) {
		if (state.getBlock() == ModBlocks.DANKNULL_DOCK) {
			final ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
			final TileEntity tile = world.getTileEntity(data.getPos());
			if (tile != null && tile instanceof TileDankNullDock) {
				final TileDankNullDock te = (TileDankNullDock) tile;
				stack.setTagInfo("BlockEntityTag", te.writeToNBT(new NBTTagCompound()));
				final String dankNull = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
				final String msg = DankNullUtils.translate("dn.right_click_with.desc") + (te.getDankNull().isEmpty() ? " " + dankNull : " " + DankNullUtils.translate("dn.empty_hand_open.desc"));
				final ItemStack dockedDankNull = te.getDankNull().isEmpty() ? ItemStack.EMPTY : te.getDankNull();
				final IProbeInfo topTip = probeInfo.horizontal().item(stack).vertical().itemLabel(stack);
				if (!dockedDankNull.isEmpty()) {
					final String dockedMsg = ModGlobals.Rarities.getRarityFromMeta(dockedDankNull.getItemDamage()).rarityColor + "" + dockedDankNull.getDisplayName() + "" + TextFormatting.WHITE + " " + DankNullUtils.translate("dn.docked.desc");
					topTip.text(dockedMsg);
					final ItemStack selectedStack = DankNullUtils.getSelectedStack(getInventory());
					if (!selectedStack.isEmpty()) {
						final ItemStack tmpStack = selectedStack.copy();
						String countText = "";
						if (selectedStack.getCount() >= 100000) {
							tmpStack.setCount(1);
							countText = ", " + DankNullUtils.translate("dn.count.desc") + ": " + (DankNullUtils.isCreativeDankNull(dockedDankNull) ? DankNullUtils.translate("dn.infinite.desc") : "" + selectedStack.getCount());
						}
						topTip.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(tmpStack).text(" " + DankNullUtils.translate("dn.selected.desc") + "" + countText);
						topTip.text(DankNullUtils.translate("dn.extract_mode.desc") + ": " + DankNullUtils.getExtractionModeForStack(dockedDankNull, selectedStack).getTooltip());
					}
				}
				//topTip.text(msg).text(TextStyleClass.MODNAME.toString() + Tools.getModName(state.getBlock()));
				topTip.text(TextStyleClass.MODNAME.toString() + Tools.getModName(state.getBlock()));
				return true;
			}
		}
		return false;
	}

	public InventoryDankNull getInventory() {
		return inventory;
	}

	public void setInventory(final InventoryDankNull inv) {
		inventory = inv;
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}

	public int slotCount() {
		return !getDankNull().isEmpty() ? getDankNull().getItemDamage() + 1 * 9 : 0;
	}

	public ItemStack getSelectedStack() {
		return getDankNull().isEmpty() ? ItemStack.EMPTY : DankNullUtils.getSelectedStack(getInventory());
	}

	public ItemStack getDankNull() {
		return getInventory() == null ? ItemStack.EMPTY : getInventory().getDankNull();
	}

	public NonNullList<ItemStack> getSlots() {
		return slots;
	}

	@Override
	public RedstoneMode getRedstoneMode() {
		return redstoneMode;
	}

	@Override
	public void setRedstoneMode(final RedstoneMode mode) {
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
	public void setRSSignal(final boolean isPowered) {
		hasRedstoneSignal = isPowered;
	}

	@Override
	public boolean shouldRefresh(final World world, final BlockPos pos, final IBlockState oldState, final IBlockState newSate) {
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
	public void onDataPacket(final NetworkManager net, final SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public void markDirty() {
		if (getWorld() != null && !getWorld().isRemote) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey(TAG_ITEMSTACK, Constants.NBT.TAG_COMPOUND)) {
			setInventory(DankNullUtils.getNewDankNullInventory(new ItemStack(compound.getCompoundTag(TAG_ITEMSTACK))));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setTag(TAG_ITEMSTACK, getDankNull().serializeNBT());
		return compound;
	}

	@Override
	public int getSizeInventory() {
		return !getDankNull().isEmpty() ? getInventory().getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(final int index) {
		if (getInventory() != null) {
			final ItemStack stack = getInventory().getStackInSlot(index);
			if (!stack.isEmpty()) {
				final int amountToBeKept = DankNullUtils.getExtractionModeForStack(getDankNull(), stack).getNumberToKeep();
				if (stack.getCount() > amountToBeKept) {
					final ItemStack availableStack = stack.copy();
					availableStack.setCount(stack.getCount() - amountToBeKept);
					return availableStack;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(final int index, final int count) {
		final ItemStack ret = !getDankNull().isEmpty() ? getInventory().decrStackSize(index, count) : ItemStack.EMPTY;
		if (getInventory() != null) {
			DankNullUtils.reArrangeStacks(getInventory());
		}
		markDirty();
		return ret;
	}

	@Override
	public ItemStack removeStackFromSlot(final int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(final int index, final ItemStack stack) {
		if (!getDankNull().isEmpty()) {
			getInventory().setInventorySlotContents(index, stack);
			return;
		}
		if (getInventory() != null) {
			getInventory().setInventorySlotContents(index, stack);
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return !getDankNull().isEmpty() ? Integer.MAX_VALUE : 0;
	}

	@Override
	public boolean isUsableByPlayer(final EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(final EntityPlayer player) {
	}

	@Override
	public void closeInventory(final EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(final int index, final ItemStack stack) {
		return getInventory() == null ? false : getInventory().isItemValidForSlot(index, stack);
	}

	@Override
	public int getField(final int id) {
		return 0;
	}

	@Override
	public void setField(final int id, final int value) {
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
	public int[] getSlotsForFace(final EnumFacing side) {
		if (!getDankNull().isEmpty() && side == EnumFacing.DOWN) {
			final int[] slots = new int[getSizeInventory()];
			for (int i = 0; i < slots.length; i++) {
				slots[i] = i;
			}
			return slots;
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(final int index, final ItemStack itemStackIn, final EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing direction) {
		if (!getDankNull().isEmpty()) {
			if (DankNullUtils.getExtractionModeForStack(getDankNull(), stack) != SlotExtractionMode.KEEP_ALL) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return !getDankNull().isEmpty();
	}

	public enum RedstoneMode {
			REQUIRED, REQUIRE_NONE, IGNORED
	}

}
