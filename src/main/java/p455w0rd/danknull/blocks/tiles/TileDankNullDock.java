package p455w0rd.danknull.blocks.tiles;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import p455w0rd.danknull.api.IRedstoneControllable;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModDataFixing.DankNullFixer;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.inventory.DankNullSidedInvWrapper;
import p455w0rd.danknull.items.ItemBlockDankNullDock;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.VanillaPacketDispatcher;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rd.danknull.util.DankNullUtils.ItemExtractionMode;
import p455w0rdslib.api.client.shader.IColoredLightEmitter;
import p455w0rdslib.api.client.shader.Light;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class TileDankNullDock extends TileEntity implements IRedstoneControllable, ISidedInventory, IColoredLightEmitter {

	private RedstoneMode redstoneMode = RedstoneMode.REQUIRED;
	private boolean hasRedstoneSignal = false;
	private final NonNullList<ItemStack> slots = NonNullList.create();
	ItemStack dankNull = ItemStack.EMPTY;

	@Override
	public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
		return !getDankNull().isEmpty() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == EnumFacing.UP) || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
		if (!getDankNull().isEmpty() && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && (facing == EnumFacing.DOWN || facing == EnumFacing.UP)) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new DankNullSidedInvWrapper(this, facing));
		}
		return super.getCapability(capability, facing);
	}

	/*@Override
	public boolean overrideStandardInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player, final World world, final IBlockState state, final IProbeHitData data) {
		if (state.getBlock() == ModBlocks.DANKNULL_DOCK) {
			final ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
			final TileEntity tile = world.getTileEntity(data.getPos());
			if (tile != null && tile instanceof TileDankNullDock) {
				final TileDankNullDock te = (TileDankNullDock) tile;
				stack.setTagInfo(NBT.BLOCKENTITYTAG, te.writeToNBT(new NBTTagCompound()));
				final String dankNull = "/d" + (Options.callItDevNull ? "ev" : "ank") + "/null";
				final String msg = TextUtils.translate("dn.right_click_with.desc") + (te.getDankNull().isEmpty() ? " " + dankNull : " " + TextUtils.translate("dn.empty_hand_open.desc"));
				final ItemStack dockedDankNull = te.getDankNull().isEmpty() ? ItemStack.EMPTY : te.getDankNull();
				final IProbeInfo topTip = probeInfo.horizontal().item(stack).vertical().itemLabel(stack);
				if (!dockedDankNull.isEmpty()) {
					final String dockedMsg = ModGlobals.Rarities.getRarityFromMeta(DankNullUtils.getMeta(dockedDankNull)).getColor() + "" + dockedDankNull.getDisplayName() + "" + TextFormatting.WHITE + " " + TextUtils.translate("dn.docked.desc");
					topTip.text(dockedMsg);
					final ItemStack selectedStack = DankNullUtils.getSelectedStack(getDankNull());
					if (!selectedStack.isEmpty()) {
						final ItemStack tmpStack = selectedStack.copy();
						String countText = "";
						if (selectedStack.getCount() >= 100000) {
							tmpStack.setCount(1);
							countText = ", " + TextUtils.translate("dn.count.desc") + ": " + (DankNullUtils.isCreativeDankNull(dockedDankNull) ? TextUtils.translate("dn.infinite.desc") : "" + selectedStack.getCount());
						}
						topTip.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_CENTER)).item(tmpStack).text(" " + TextUtils.translate("dn.selected.desc") + "" + countText);
						topTip.text(TextUtils.translate("dn.extract_mode.desc") + ": " + DankNullUtils.getExtractionModeForStack(dockedDankNull, selectedStack).getTooltip());
					}
				}
				topTip.text(TextStyleClass.MODNAME.toString() + Tools.getModName(state.getBlock()));
				return true;
			}
		}
		return true;
	}*/

	public void removeDankNull() {
		if (!getDankNull().isEmpty()) {
			setDankNull(ItemStack.EMPTY);
		}
	}

	public void setDankNull(final ItemStack dankNull) {
		this.dankNull = new ItemStack(DankNullFixer.getInstance().fixTagCompound(dankNull.serializeNBT()));
		markDirty();
	}

	public int slotCount() {
		return !getDankNull().isEmpty() ? DankNullUtils.getMeta(getDankNull()) + 1 * 9 : 0;
	}

	public ItemStack getSelectedStack() {
		return getDankNull().isEmpty() ? ItemStack.EMPTY : DankNullUtils.getSelectedStack(DankNullUtils.getNewDankNullInventory(getDankNull()));
	}

	public ItemStack getDankNull() {
		return dankNull;
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
		readNBT(pkt.getNbtCompound());
	}

	@Override
	public void markDirty() {
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		super.markDirty();
	}

	private void readNBT(final NBTTagCompound nbt) {
		if (nbt.hasKey(NBT.DOCKEDSTACK, Constants.NBT.TAG_COMPOUND)) {
			setDankNull(new ItemStack(nbt.getCompoundTag(NBT.DOCKEDSTACK)));
		}
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		readNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		compound.setTag(NBT.DOCKEDSTACK, getDankNull().serializeNBT());
		return compound;
	}

	@Override
	public int getSizeInventory() {
		return !getDankNull().isEmpty() ? DankNullUtils.getSizeInventory(getDankNull()) : 0;
	}

	@Override
	public ItemStack getStackInSlot(final int index) {
		if (!getDankNull().isEmpty()) {
			final ItemStack stack = DankNullUtils.getStackInDankNullSlotWithSize(getDankNull(), index);
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

	public ItemStack getActualStackInSlot(final int index) {
		if (!getDankNull().isEmpty()) {
			return DankNullUtils.getStackInDankNullSlotWithSize(getDankNull(), index);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(final int index, final int count) {
		final ItemStack ret = !getDankNull().isEmpty() ? DankNullUtils.decrStackSize(getDankNull(), index, count) : ItemStack.EMPTY;
		if (!getDankNull().isEmpty()) {
			DankNullUtils.reArrangeStacks(getDankNull());
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
			DankNullUtils.setStackInSlot(getDankNull(), index, stack);
			markDirty();
		}

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
		return getDankNull().isEmpty() ? false : !(stack.getItem() instanceof ItemDankNull);
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
		return NBT.DANKNULL_INVENTORY;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int[] getSlotsForFace(final EnumFacing side) {
		if (!getDankNull().isEmpty() && side == EnumFacing.DOWN || side == EnumFacing.UP) {
			final int[] slots = new int[getSizeInventory()];
			for (int i = 0; i < slots.length; i++) {
				slots[i] = i;
			}
			return slots;
		}
		return new int[0];
	}

	@Override
	public boolean canInsertItem(final int index, final ItemStack stack, final EnumFacing side) {
		return side == EnumFacing.UP && DankNullUtils.isFiltered(getDankNull(), stack);
	}

	@Override
	public boolean canExtractItem(final int index, final ItemStack stack, final EnumFacing side) {
		if (!getDankNull().isEmpty()) {
			if (DankNullUtils.getExtractionModeForStack(getDankNull(), stack) != ItemExtractionMode.KEEP_ALL) {
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

	private int brightness = 0;
	private boolean brightnessDir = false;
	private int step = 0;
	private boolean initLight = false;

	@Override
	public void emitLight(final List<Light> lights, final TileEntity tile) {
		if (!Options.enabledColoredLightShaderSupport) {
			return;
		}
		if (!initLight) {
			step = getWorld().rand.nextInt(4);
			initLight = true;
		}
		final ItemStack lightStack = getDankNull();
		if (getWorld() != null && getWorld().isRemote && !lightStack.isEmpty() && lightStack.hasEffect()) {
			if (brightnessDir) {
				brightness++;
				if (brightness > DankNullUtils.STEPS_MOST[step]) {
					brightnessDir = !brightnessDir;
					step++;
					if (step > 4) {
						step = 0;
					}
				}
			}
			else {
				brightness--;
				if (brightness < DankNullUtils.STEPS_LEAST[step]) {
					brightnessDir = !brightnessDir;
					step++;
					if (step > 4) {
						step = 0;
					}
				}
			}
			final Vec3i c = RenderUtils.hexToRGB(DankNullUtils.getTier(getDankNull()).getHexColor(false));
			lights.add(Light.builder().pos(tile.getPos()).color(c.getX(), c.getY(), c.getZ(), (float) (brightness * 0.001)).radius(2.5f).intensity(5).build());
		}
		else {
			brightnessDir = false;
			brightness = 0;
			step = 0;
		}
	}

	@Override
	public void emitLight(final List<Light> lights, final Entity e) {
		if (!Options.enabledColoredLightShaderSupport) {
			return;
		}
		if (!initLight) {
			step = getWorld().rand.nextInt(4);
			initLight = true;
		}
		if (e instanceof EntityPlayer) {
			final EntityPlayer p = (EntityPlayer) e;
			for (final ItemStack stack : p.getHeldEquipment()) {
				if (stack.getItem() instanceof ItemBlockDankNullDock) {
					final ItemStack dankStack = DankNullUtils.getDockedDankNull(stack);
					if (!dankStack.isEmpty() && dankStack.getItem() instanceof IColoredLightEmitter) {
						((IColoredLightEmitter) dankStack.getItem()).emitLight(lights, e);
					}
				}
			}
		}
		else if (e instanceof EntityItem) {
			if (DankNullUtils.isDankNull(((EntityItem) e).getItem())) {
				((IColoredLightEmitter) ((EntityItem) e).getItem().getItem()).emitLight(lights, e);
			}
			else if (((EntityItem) e).getItem().getItem() instanceof ItemBlockDankNullDock) {
				((IColoredLightEmitter) DankNullUtils.getDockedDankNull(((EntityItem) e).getItem()).getItem()).emitLight(lights, e);
			}
		}
	}

}
