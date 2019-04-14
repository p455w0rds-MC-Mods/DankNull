package p455w0rd.danknull.util;

import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.inventory.DankNullItemHandler;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.PacketConfigSync;
import p455w0rd.danknull.network.PacketSetSelectedItem;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;
import p455w0rdslib.util.GuiUtils;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class DankNullUtils {

	@Nonnull
	public static ItemStack getFirstDankNull(final EntityPlayer player) {
		final InventoryPlayer playerInv = player.inventory;
		ItemStack dankNullItem = ItemStack.EMPTY;
		if (!player.getHeldItemMainhand().isEmpty()) {
			if (player.getHeldItemMainhand().getItem() instanceof ItemDankNull) {
				dankNullItem = player.getHeldItem(EnumHand.MAIN_HAND);
			}
			else if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemDankNull) {
				dankNullItem = player.getHeldItem(EnumHand.OFF_HAND);
			}
		}
		else if (!player.getHeldItemOffhand().isEmpty()) {
			if (player.getHeldItemOffhand().getItem() instanceof ItemDankNull) {
				dankNullItem = player.getHeldItem(EnumHand.OFF_HAND);
			}
			else if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemDankNull) {
				dankNullItem = player.getHeldItem(EnumHand.MAIN_HAND);
			}
		}
		if (dankNullItem.isEmpty()) {
			final int invSize = playerInv.getSizeInventory();
			if (invSize <= 0) {
				return ItemStack.EMPTY;
			}
			for (int i = 0; i < invSize; i++) {
				final ItemStack itemStack = playerInv.getStackInSlot(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.getItem() instanceof ItemDankNull) {
						dankNullItem = itemStack;
						break;
					}
				}
			}
		}
		return dankNullItem;
	}

	public static ItemStack getDankNullFromPlayerInvSlot(final int slot, final EntityPlayer player) {
		final ItemStack slotStack = player.inventory.getStackInSlot(slot);
		if (isDankNull(slotStack)) {
			return slotStack;
		}
		return ItemStack.EMPTY;
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	public static Pair<Integer, ItemStack> getDankNullFromCurrentScreen(final EntityPlayer player) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiDankNull) {
			final GuiDankNull gui = (GuiDankNull) Minecraft.getMinecraft().currentScreen;
			if (gui.inventorySlots instanceof ContainerDankNull) {
				final ContainerDankNull c = (ContainerDankNull) gui.inventorySlots;
				return Pair.of(c.getPlayerInvSlot(), c.getDankNull());
			}
		}
		return Pair.of(-1, ItemStack.EMPTY);
	}

	public static Map<Integer, ItemStack> getAllDankNulls(final EntityPlayer player) {
		final InventoryPlayer playerInv = player.inventory;
		final Map<Integer, ItemStack> dankNullList = new HashMap<>();
		for (int i = 0; i < playerInv.getSizeInventory(); i++) {
			final ItemStack stack = playerInv.getStackInSlot(i);
			if (isDankNull(stack)) {
				dankNullList.put(i, stack);
			}
		}
		return dankNullList;
	}

	@Nonnull
	public static Pair<Integer, ItemStack> getFirstDankNullForStack(final EntityPlayer player, final ItemStack stack) {
		final Map<Integer, ItemStack> dankNulls = getAllDankNulls(player);
		for (final Map.Entry<Integer, ItemStack> dankNull : dankNulls.entrySet()) {
			if (dankNull.getValue().hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				if (isFiltered(getNewDankNullInventory(dankNull.getValue()), stack)) {
					return Pair.of(dankNull.getKey(), dankNull.getValue());
				}
			}
		}
		return Pair.of(-1, ItemStack.EMPTY);
	}

	public static Pair<Integer, ItemStack> getDankNullForStack(final EntityPlayer player, final ItemStack stack) {
		final InventoryPlayer playerInv = player.inventory;
		ItemStack dankNullItem = ItemStack.EMPTY;
		int dnSlot = -1;
		final int invSize = playerInv.getSizeInventory();
		if (invSize <= 0) {
			return Pair.of(dnSlot, dankNullItem);
		}
		for (int i = 0; i < invSize; i++) {
			final ItemStack itemStack = playerInv.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if (isDankNull(itemStack)) {
					final InventoryDankNull inv = getNewDankNullInventory(itemStack);
					if (isFiltered(inv, stack)) {
						dankNullItem = itemStack;
						dnSlot = i;
						break;
					}
					if (isFilteredOreDict(inv, stack)) {
						dankNullItem = itemStack;
						dnSlot = i;
						break;
					}
				}
			}
		}
		return Pair.of(dnSlot, dankNullItem);
	}

	public static void reArrangeStacks(final InventoryDankNull inventory) {
		if (inventory != null) {
			int count = 0;
			final NonNullList<ItemStack> stackList = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				final ItemStack stack = inventory.getStackInSlot(i);
				if (!stack.isEmpty()) {
					stackList.set(count, inventory.getStackInSlot(i));
					count++;
				}
			}
			if (stackList.size() == 0) {
				setSelectedStackIndex(inventory, -1);
			}
			else {
				for (int i = 0; i < stackList.size(); i++) {
					inventory.setInventorySlotContents(i, stackList.get(i));
				}
				for (int i = stackList.size(); i < inventory.getSizeInventory(); i++) {
					inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				}
			}
			setSelectedIndexApplicable(inventory);
		}
	}

	public static NonNullList<ItemStack> getInventoryListArray(final InventoryDankNull inventory) {
		if (inventory != null) {
			return inventory.getStacks();
		}
		return NonNullList.<ItemStack>create();
	}

	public static int getSelectedStackIndex(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			final ItemStack dankNull = inventory.getDankNull();
			if (!dankNull.hasTagCompound()) {
				dankNull.setTagCompound(new NBTTagCompound());
			}
			if (!dankNull.getTagCompound().hasKey("selectedIndex")) {
				dankNull.getTagCompound().setInteger("selectedIndex", 0);
			}
			return dankNull.getTagCompound().getInteger("selectedIndex");
		}
		return -1;
	}

	public static boolean isDankNull(final ItemStack stack) {
		return stack.getItem() instanceof ItemDankNull;
	}

	public static ItemStack makeDankNullCreative(final ItemStack dankNull) {
		final ItemStack creativeDankNull = dankNull.copy();
		if (isDankNull(creativeDankNull) && !isCreativeDankNull(creativeDankNull)) {
			if (creativeDankNull.getItemDamage() != 6) {
				creativeDankNull.setItemDamage(6);
			}
		}
		return creativeDankNull;
	}

	public static boolean isCreativeDankNull(final ItemStack stack) {
		return isDankNull(stack) && stack.getItemDamage() == 6;
	}

	public static boolean isCreativeDankNullLocked(final ItemStack dankNull) {
		if (isCreativeDankNull(dankNull)) {
			return dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(NBT.LOCKED_KEY, Constants.NBT.TAG_BYTE) && dankNull.getTagCompound().getBoolean(NBT.LOCKED_KEY);
		}
		return false;
	}

	public static void setLocked(final ItemStack creativeDankNull, final boolean locked) {
		if (isCreativeDankNull(creativeDankNull)) {
			if (!creativeDankNull.hasTagCompound()) {
				creativeDankNull.setTagCompound(new NBTTagCompound());
			}
			creativeDankNull.getTagCompound().setBoolean(NBT.LOCKED_KEY, locked);
		}
	}

	public static void setSelectedStackIndex(final InventoryDankNull inventory, final int index) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			final ItemStack dankNull = inventory.getDankNull();
			if (!dankNull.hasTagCompound()) {
				dankNull.setTagCompound(new NBTTagCompound());
			}
			dankNull.getTagCompound().setInteger("selectedIndex", index);
		}
	}

	/*
	public static void setSelectedStackIndex(InventoryDankNull inventory, int index, World world, BlockPos dockPos) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			ItemStack dankNull = inventory.getDankNull();
			if (!dankNull.hasTagCompound()) {
				dankNull.setTagCompound(new NBTTagCompound());
			}
			dankNull.getTagCompound().setInteger("selectedIndex", index);
			if (dockPos != null && world != null && !world.isRemote) {
				TileEntity te = world.getTileEntity(dockPos);
				if (te instanceof TileDankNullDock) {
					TileDankNullDock dankDock = (TileDankNullDock) te;
					dankDock.setStack(dankNull);
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(dankDock);
					dankDock.markDirty();
					world.notifyBlockUpdate(dockPos, world.getBlockState(dockPos), world.getBlockState(dockPos), 3);
				}
			}
		}
	}
	*/

	public static void setNextSelectedStack(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			setNextSelectedStack(inventory, null);
		}
	}

	public static void setNextSelectedStack(final InventoryDankNull inventory, final EntityPlayer player) {
		final int currentIndex = getSelectedStackIndex(inventory);
		final int totalSize = getItemCount(inventory);
		final int maxIndex = totalSize - 1;
		int newIndex = 0;
		if (totalSize > 1) {
			if (currentIndex == maxIndex) {
				newIndex = 0;
				ModNetworking.getInstance().sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
			else {
				newIndex = currentIndex + 1;
				ModNetworking.getInstance().sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
		}
	}

	public static void setPreviousSelectedStack(final InventoryDankNull inventory, final EntityPlayer player) {
		final int currentIndex = getSelectedStackIndex(inventory);
		final int totalSize = getItemCount(inventory);
		final int maxIndex = totalSize - 1;
		int newIndex = 0;
		if (totalSize > 1) {
			if (currentIndex == 0) {
				newIndex = maxIndex;
				ModNetworking.getInstance().sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
			else {
				newIndex = currentIndex - 1;
				ModNetworking.getInstance().sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
		}
	}

	public static int getItemCount(final InventoryDankNull inventory) {
		int count = 0;
		if (inventory != null) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					count++;
				}
			}
		}
		return count;
	}

	public static void decrSelectedStackSize(final InventoryDankNull inventory, final int amount) {
		if (inventory == null || inventory.getDankNull().isEmpty()) {
			return;
		}
		getSelectedStack(inventory).shrink(amount);
		reArrangeStacks(inventory);
	}

	public static int getSelectedStackSize(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			final ItemStack selectedStack = getSelectedStack(inventory);
			if (!selectedStack.isEmpty()) {
				return selectedStack.getCount();
			}
		}
		return 0;
	}

	public static InventoryDankNull getInventoryFromHeld(final EntityPlayer player) {
		ItemStack dankNull = ItemStack.EMPTY;
		if (player != null) {
			if (player.getHeldItemMainhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemMainhand();
			}
			else if (player.getHeldItemOffhand().getItem() == ModItems.DANK_NULL) {
				dankNull = player.getHeldItemOffhand();
			}
			if (!dankNull.isEmpty() && DankNullUtils.isDankNull(dankNull)) {
				return getNewDankNullInventory(dankNull);
			}
		}
		return null;
	}

	public static ItemStack getSelectedStack(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			final ItemStack dankNull = inventory.getDankNull();
			if (!dankNull.hasTagCompound()) {
				setSelectedStackIndex(inventory, isEmpty(inventory) ? 1 : 0);
			}

			final NBTTagCompound nbtTC = dankNull.getTagCompound();
			if (!nbtTC.hasKey("selectedIndex")) {
				nbtTC.setInteger("selectedIndex", 0);
			}
			final int selectedIndex = nbtTC.getInteger("selectedIndex");
			if (selectedIndex > -1) {
				return inventory.getStackInSlot(selectedIndex);
			}
			//}
		}
		return ItemStack.EMPTY;
	}

	public static boolean isEmpty(final InventoryDankNull inventory) {
		return inventory.isEmpty();
	}

	public static boolean isFiltered(final InventoryDankNull inventory, final ItemStack filteredStack) {
		if (inventory != null) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					if (areStacksEqual(inventory.getStackInSlot(i), filteredStack)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isFilteredOreDict(final InventoryDankNull inventory, final ItemStack filteredStack) {
		if (DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isItemOreDictBlacklisted(filteredStack) || DankNullUtils.isOreDictWhitelistEnabled() && DankNullUtils.isItemOreDictWhitelisted(filteredStack) || !DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isOreDictWhitelistEnabled()) {
			final int[] ids = OreDictionary.getOreIDs(filteredStack);
			if (inventory != null && ids.length > 0) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (!inventory.getStackInSlot(i).isEmpty() && isItemOreDicted(inventory.getStackInSlot(i)) && isItemOreDicted(filteredStack) && getOreDictModeForStack(inventory.getDankNull(), inventory.getStackInSlot(i))) {
						final int[] ids2 = OreDictionary.getOreIDs(inventory.getStackInSlot(i));
						for (final int id : ids) {
							final String name = OreDictionary.getOreName(id);
							for (final int id2 : ids2) {
								if (name.equals(OreDictionary.getOreName(id2))) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean isOreDictBlacklistEnabled() {
		return !Options.getOreBlacklist().isEmpty() && !isOreDictWhitelistEnabled();
	}

	public static boolean isOreDictWhitelistEnabled() {
		return !Options.getOreWhitelist().isEmpty();
	}

	public static boolean isItemOreDictBlacklisted(final ItemStack stack) {
		if (isOreDictBlacklistEnabled()) {
			for (final int id : OreDictionary.getOreIDs(stack)) {
				if (Options.getOreBlacklist().contains(OreDictionary.getOreName(id))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isItemOreDictWhitelisted(final ItemStack stack) {
		if (isOreDictWhitelistEnabled()) {
			for (final int id : OreDictionary.getOreIDs(stack)) {
				if (Options.getOreWhitelist().contains(OreDictionary.getOreName(id))) {
					return true;
				}
			}
		}
		return false;
	}

	public static DankNullItemHandler getHandler(final ItemStack dankNull) {
		if (hasDankNullHandler(dankNull)) {
			return (DankNullItemHandler) dankNull.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		}
		return null;
	}

	public static boolean hasDankNullHandler(final ItemStack dankNull) {
		return dankNull.getItem() == ModItems.DANK_NULL && dankNull.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	public static boolean canStackBeAdded(final InventoryDankNull inventory, final ItemStack stack) {
		if (!inventory.getDankNull().isEmpty()) {
			final ItemStack dankNull = inventory.getDankNull();
			if (DankNullUtils.isCreativeDankNull(dankNull)) {
				NonNullList<ItemStack> whiteList = null;
				try {
					whiteList = Options.getCreativeWhitelistedItems();
				}
				catch (final Exception e) {
				}
				if (whiteList != null && !whiteList.isEmpty()) {
					for (final ItemStack whiteListedStack : whiteList) {
						if (areStacksEqual(stack, whiteListedStack)) {
							return true;
						}
					}
					return false;
				}
				NonNullList<ItemStack> blackList = null;
				try {
					blackList = Options.getCreativeBlacklistedItems();
				}
				catch (final Exception e) {
				}
				if (blackList != null && !blackList.isEmpty()) {
					for (final ItemStack blackListedStack : blackList) {
						if (areStacksEqual(stack, blackListedStack)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static boolean addUnfiliteredStackToDankNull(final ContainerDankNull container, final ItemStack stack) {
		if (container.getDankNullInventory() != null && canStackBeAdded(container.getDankNullInventory(), stack)) {
			container.inventorySlots.get(getNextAvailableSlot(container)).putStack(stack);
			return true;
		}
		return false;
	}

	//TODO remove when these classes get merged
	public static boolean addUnfiliteredStackToDankNull(final ContainerDankNullDock container, final ItemStack stack) {
		if (container.getDankNullInventory() != null && canStackBeAdded(container.getDankNullInventory(), stack)) {
			container.inventorySlots.get(getNextAvailableSlot(container)).putStack(stack);
			return true;
		}
		return false;
	}

	public static int getNextAvailableSlot(final ContainerDankNull container) {
		if (isCreativeDankNull(container.getDankNull()) && isCreativeDankNullLocked(container.getDankNull())) {
			return -1;
		}
		for (int i = 36; i < container.inventorySlots.size(); i++) {
			final Slot s = container.inventorySlots.get(i);
			if (s != null && s.getStack().isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	//TODO remove when these classes get merged
	public static int getNextAvailableSlot(final ContainerDankNullDock container) {
		if (isCreativeDankNull(container.getDankNull()) && isCreativeDankNullLocked(container.getDankNull())) {
			return -1;
		}
		for (int i = 36; i < container.inventorySlots.size(); i++) {
			final Slot s = container.inventorySlots.get(i);
			if (s != null && s.getStack().isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	public static boolean addFilteredStackToDankNull(final InventoryDankNull inventory, ItemStack filteredStack) {
		if (canStackBeAdded(inventory, filteredStack)) {
			if (getIndexForStack(inventory, filteredStack) >= 0) {
				final ItemStack currentStack = getFilteredStack(inventory, filteredStack);
				if (!currentStack.isEmpty() && !filteredStack.isEmpty() && !areStacksEqual(currentStack, filteredStack)) {
					filteredStack = convertToOreDictedStack(filteredStack, currentStack);
				}
				if (filteredStack.getCount() < Integer.MAX_VALUE) {
					final long currentSize = currentStack.getCount();
					final long maxDankNullStackSize = getDankNullMaxStackSize(inventory);
					if (currentSize + filteredStack.getCount() > maxDankNullStackSize) {
						currentStack.setCount((int) maxDankNullStackSize);
					}
					else {
						currentStack.setCount((int) currentSize + filteredStack.getCount());
					}
					inventory.setInventorySlotContents(getIndexForStack(inventory, filteredStack), currentStack);
					return true;
				}
			}
		}
		return false;
	}

	public static ItemStack getFilteredStack(final InventoryDankNull inventory, final ItemStack stack) {
		if (isFiltered(inventory, stack) || isFilteredOreDict(inventory, stack)) {
			return getItemByIndex(inventory, getIndexForStack(inventory, stack));
		}
		return ItemStack.EMPTY;
	}

	public static boolean areStacksEqual(final ItemStack stack1, final ItemStack stack2) {
		final ItemStack tmpStack1 = stack1.copy();
		tmpStack1.setCount(1);
		final ItemStack tmpStack2 = stack2.copy();
		tmpStack2.setCount(1);
		return ItemStack.areItemStacksEqual(tmpStack1, tmpStack2);
	}

	public static int getIndexForStack(final InventoryDankNull inventory, final ItemStack filteredStack) {
		if (!filteredStack.isEmpty()) {
			if (isFiltered(inventory, filteredStack)) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (!inventory.getStackInSlot(i).isEmpty()) {
						if (areStacksEqual(inventory.getStackInSlot(i), filteredStack)) {
							return i;
						}
					}
				}
			}
			else if (isFilteredOreDict(inventory, filteredStack)) {
				final int[] ids = OreDictionary.getOreIDs(filteredStack);
				if (inventory != null && ids.length > 0) {
					for (int i = 0; i < inventory.getSizeInventory(); i++) {
						if (!inventory.getStackInSlot(i).isEmpty() && isItemOreDicted(inventory.getStackInSlot(i)) && isItemOreDicted(filteredStack) && getOreDictModeForStack(inventory.getDankNull(), inventory.getStackInSlot(i))) {
							final int[] ids2 = OreDictionary.getOreIDs(inventory.getStackInSlot(i));
							for (final int id : ids) {
								final String name = OreDictionary.getOreName(id);
								for (final int id2 : ids2) {
									if (name.equals(OreDictionary.getOreName(id2))) {
										return i;
									}
								}
							}
						}
					}
				}
			}
		}
		return -1;
	}

	public static ItemStack getItemByIndex(final InventoryDankNull inventory, final int index) {
		if (inventory != null && index >= 0) {
			return inventory.getStackInSlot(index);
		}
		return ItemStack.EMPTY;
	}

	public static void decrDankNullStackSize(@Nonnull final InventoryDankNull inventory, @Nonnull final ItemStack stack, final int amount) {
		if (inventory == null || stack.isEmpty()) {
			return;
		}
		if (isFiltered(inventory, stack)) {
			ItemStack currentStack = getFilteredStack(inventory, stack);
			currentStack.setCount(currentStack.getCount() - amount);
			if (currentStack.getCount() <= 0) {
				currentStack = ItemStack.EMPTY;
			}
			inventory.markDirty();
		}
	}

	public static InventoryDankNull getNewDankNullInventory(@Nonnull final ItemStack stack) {
		return stack.getItem() instanceof ItemDankNull ? new InventoryDankNull(stack) : null;
	}

	public static int getDankNullMaxStackSize(@Nonnull final ItemStack itemStackIn) {
		final int level = itemStackIn.getItemDamage() + 1;
		if (level >= 6) {
			return Integer.MAX_VALUE;
		}
		return level * 128 * level;
	}

	public static int getDankNullMaxStackSize(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			final int level = inventory.getDankNull().getItemDamage() + 1;
			if (level == 6) {
				return Integer.MAX_VALUE;
			}
			return level * 128 * level;
		}
		return 0;
	}

	public static int getColor(final int damage, final boolean opaque) {
		switch (damage) {
		case 0:
			return opaque ? 0xFFEC4848 : 0x99EC4848;
		case 1:
			return opaque ? 0xFF4885EC : 0x994885EC;
		default:
		case 2:
			return opaque ? 0xFFFFFFFF : 0x99FFFFFF;
		case 3:
			return opaque ? 0xFFFFFF00 : 0x99FFFF00;
		case 4:
			return opaque ? 0xFF00FFFF : 0x9900FFFF;
		case 5:
			return opaque ? 0xFF17FF6D : 0x9917FF6D;
		case 6:
			return opaque ? 0xFF8F15D4 : 0x998F15D4;
		}
	}

	public static int getSlotCount(final ItemStack stack) {
		return (stack.getItemDamage() + 1) * 9;
	}

	public static int getSlotCount(final InventoryDankNull inventory) {
		return inventory.getSizeInventory();
	}

	public static void setSelectedIndexApplicable(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			if (getSelectedStackIndex(inventory) >= 0 && !getItemByIndex(inventory, getSelectedStackIndex(inventory)).isEmpty()) {
				return;
			}
			boolean indexFound = false;
			for (int i = getSelectedStackIndex(inventory); i > -1; i--) {
				if (!getItemByIndex(inventory, i).isEmpty()) {
					indexFound = true;
					setSelectedStackIndex(inventory, i);
					return;
				}
			}
			for (int i = getSlotCount(inventory) - 1; i > -1; i--) {
				if (!getItemByIndex(inventory, i).isEmpty()) {
					indexFound = true;
					setSelectedStackIndex(inventory, i);
					return;
				}
			}
			if (!indexFound) {
				setSelectedStackIndex(inventory, -1);
			}
		}
	}

	public static EnumActionResult placeBlock(@Nonnull final IBlockState state, final World world, final BlockPos pos) {
		return world.setBlockState(pos, state, 2) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}

	public static IRecipe addDankNullUpgradeRecipe(final String recipeName, final Object... params) {
		final ShapedPrimer primer = CraftingHelper.parseShaped(params);
		final IRecipe recipe = new RecipeDankNullUpgrade(primer.input).setRegistryName(new ResourceLocation(ModGlobals.MODID, recipeName));
		return recipe;
	}

	// ======
	// OreDict Mode
	// ======
	public static Map<ItemStack, Boolean> getOreDictModes(final ItemStack dankNull) {
		final Map<ItemStack, Boolean> modes = Maps.<ItemStack, Boolean>newHashMap();
		if (!Options.disableOreDictMode && dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(NBT.OREDICT_MODES_KEY, Constants.NBT.TAG_LIST)) {
			final NBTTagList extractionList = dankNull.getTagCompound().getTagList(NBT.OREDICT_MODES_KEY, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				final NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag("Stack")), tempNBT.getBoolean("OreDict"));
			}
		}
		return modes;
	}

	public static void setOreDictModes(final ItemStack dankNull, final Map<ItemStack, Boolean> modes) {
		if (Options.disableOreDictMode || modes.isEmpty()) {
			return;
		}
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound dankNullNBT = dankNull.getTagCompound();
		final NBTTagList oreDictList = new NBTTagList();
		for (final ItemStack stack : modes.keySet()) {
			final NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag("Stack", stack.serializeNBT());
			tempNBT.setBoolean("OreDict", modes.get(stack));
			oreDictList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(NBT.OREDICT_MODES_KEY, oreDictList);
	}

	public static boolean getOreDictModeForStack(final ItemStack dankNull, final ItemStack stack) {
		if (!Options.disableOreDictMode) {
			final Map<ItemStack, Boolean> modes = getOreDictModes(dankNull);
			if (!modes.isEmpty()) {
				for (final ItemStack currentStack : modes.keySet()) {
					if (areStacksEqual(currentStack, stack)) {
						return modes.get(currentStack);
					}
				}
			}
		}
		return false;
	}

	public static void setOreDictModeForStack(final ItemStack dankNull, final ItemStack stack, final boolean mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		final ItemStack tempStack = stack.copy();
		tempStack.setCount(1);
		final Map<ItemStack, Boolean> currentModes = getOreDictModes(dankNull);
		for (final ItemStack currentStack : currentModes.keySet()) {
			if (areStacksEqual(tempStack, currentStack)) {
				currentModes.put(currentStack, mode);
				alreadyAdded = true;
			}
		}
		if (!alreadyAdded) {
			currentModes.put(tempStack, mode);
		}
		setOreDictModes(dankNull, currentModes);
	}

	public static void cycleOreDictModeForStack(final ItemStack dankNull, final ItemStack stack) {
		setOreDictModeForStack(dankNull, stack, !getOreDictModeForStack(dankNull, stack));
	}

	// ======
	// Extraction Mode
	// ======

	public static Map<ItemStack, SlotExtractionMode> getExtractionModes(final ItemStack dankNull) {
		final Map<ItemStack, SlotExtractionMode> modes = Maps.<ItemStack, SlotExtractionMode>newHashMap();
		if (dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(NBT.EXTRACTION_MODES_KEY, Constants.NBT.TAG_LIST)) {
			final NBTTagList extractionList = dankNull.getTagCompound().getTagList(NBT.EXTRACTION_MODES_KEY, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				final NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag("Stack")), SlotExtractionMode.values()[tempNBT.getInteger("Mode")]);
			}
		}
		return modes;
	}

	public static void setExtractionModes(final ItemStack dankNull, final Map<ItemStack, SlotExtractionMode> modes) {
		if (modes.isEmpty()) {
			return;
		}
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound dankNullNBT = dankNull.getTagCompound();
		final NBTTagList extractionList = new NBTTagList();
		for (final ItemStack stack : modes.keySet()) {
			final NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag("Stack", stack.serializeNBT());
			tempNBT.setInteger("Mode", modes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(NBT.EXTRACTION_MODES_KEY, extractionList);
	}

	public static SlotExtractionMode getExtractionModeForStack(final ItemStack dankNull, final ItemStack stack) {
		final Map<ItemStack, SlotExtractionMode> modes = getExtractionModes(dankNull);
		if (!modes.isEmpty()) {
			for (final ItemStack currentStack : modes.keySet()) {
				if (areStacksEqual(stack, currentStack)) {
					return modes.get(currentStack);
				}
			}
		}
		return SlotExtractionMode.KEEP_ALL;
	}

	public static void setExtractionModeForStack(final ItemStack dankNull, final ItemStack stack, final SlotExtractionMode mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		final Map<ItemStack, SlotExtractionMode> currentModes = getExtractionModes(dankNull);
		for (final ItemStack currentStack : currentModes.keySet()) {
			if (areStacksEqual(stack, currentStack)) {
				currentModes.put(currentStack, mode);
				alreadyAdded = true;
			}
		}
		if (!alreadyAdded) {
			currentModes.put(stack, mode);
		}
		setExtractionModes(dankNull, currentModes);
	}

	public static void cycleExtractionMode(final ItemStack dankNull, final ItemStack stack) {
		final SlotExtractionMode current = getExtractionModeForStack(dankNull, stack);
		if (current.ordinal() >= SlotExtractionMode.values().length - 1) {
			setExtractionModeForStack(dankNull, stack, SlotExtractionMode.values()[0]);
		}
		else {
			setExtractionModeForStack(dankNull, stack, SlotExtractionMode.values()[current.ordinal() + 1]);
		}
		if (FMLCommonHandler.instance().getSide().isClient()) {
			if (getExtractionModeForStack(dankNull, stack) == null) {
				ModLogger.warn("no extraction mode found");
				return;
			}
		}
	}

	// ======
	// Placement Mode
	// ======

	public static Map<ItemStack, SlotExtractionMode> getPlacementModes(final ItemStack dankNull) {
		final Map<ItemStack, SlotExtractionMode> modes = Maps.<ItemStack, SlotExtractionMode>newHashMap();
		if (dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(NBT.PLACEMENT_MODES_KEY, Constants.NBT.TAG_LIST)) {
			final NBTTagList extractionList = dankNull.getTagCompound().getTagList(NBT.PLACEMENT_MODES_KEY, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				final NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag("Stack")), SlotExtractionMode.values()[tempNBT.getInteger("Mode")]);
			}
		}
		return modes;
	}

	public static void setPlacementModes(final ItemStack dankNull, final Map<ItemStack, SlotExtractionMode> modes) {
		if (modes.isEmpty()) {
			return;
		}
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		final NBTTagCompound dankNullNBT = dankNull.getTagCompound();
		final NBTTagList extractionList = new NBTTagList();
		for (final ItemStack stack : modes.keySet()) {
			final NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag("Stack", stack.serializeNBT());
			tempNBT.setInteger("Mode", modes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(NBT.PLACEMENT_MODES_KEY, extractionList);
	}

	public static SlotExtractionMode getPlacementModeForStack(final ItemStack dankNull, final ItemStack stack) {
		final Map<ItemStack, SlotExtractionMode> modes = getPlacementModes(dankNull);
		if (!modes.isEmpty()) {
			for (final ItemStack currentStack : modes.keySet()) {
				if (areStacksEqual(stack, currentStack)) {
					return modes.get(currentStack);
				}
			}
		}
		return SlotExtractionMode.KEEP_1;
	}

	public static void setPlacementModeForStack(final ItemStack dankNull, final ItemStack stack, final SlotExtractionMode mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		final Map<ItemStack, SlotExtractionMode> currentModes = getPlacementModes(dankNull);
		for (final ItemStack currentStack : currentModes.keySet()) {
			if (areStacksEqual(stack, currentStack)) {
				currentModes.put(currentStack, mode);
				alreadyAdded = true;
			}
		}
		if (!alreadyAdded) {
			currentModes.put(stack, mode);
		}
		setPlacementModes(dankNull, currentModes);
	}

	public static void cyclePlacementMode(final ItemStack dankNull, final ItemStack stack) {
		final SlotExtractionMode current = getPlacementModeForStack(dankNull, stack);
		if (current.ordinal() >= SlotExtractionMode.values().length - 1) {
			setPlacementModeForStack(dankNull, stack, SlotExtractionMode.values()[0]);
		}
		else {
			setPlacementModeForStack(dankNull, stack, SlotExtractionMode.values()[current.ordinal() + 1]);
		}
		if (FMLCommonHandler.instance().getSide().isClient()) {
			if (getPlacementModeForStack(dankNull, stack) == null) {
				ModLogger.warn("no extraction mode found");
				return;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void renderHUD(final Minecraft mc, final ScaledResolution scaledRes) {
		if (!mc.playerController.shouldDrawHUD() && !mc.player.capabilities.isCreativeMode) {
			return;
		}
		ItemStack currentItem = mc.player.inventory.getCurrentItem();
		if (currentItem.isEmpty() || currentItem.getItem() != ModItems.DANK_NULL) {
			currentItem = mc.player.getHeldItemOffhand();
		}
		if (!currentItem.isEmpty() && currentItem.getItem() == ModItems.DANK_NULL) {
			final ItemStack selectedStack = DankNullUtils.getSelectedStack(DankNullUtils.getInventoryFromHeld(mc.player));
			if (!selectedStack.isEmpty()) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen0.png"));
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GuiUtils.drawTexturedModalRect(scaledRes.getScaledWidth() - 106, scaledRes.getScaledHeight() - 45, 0, 210, 106, 45, 0);
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				mc.fontRenderer.drawStringWithShadow(currentItem.getDisplayName(), scaledRes.getScaledWidth() * 2 - 212 + 55, scaledRes.getScaledHeight() * 2 - 83, DankNullUtils.getColor(currentItem.getItemDamage(), true));
				String selectedStackName = selectedStack.getDisplayName();
				final int itemNameWidth = mc.fontRenderer.getStringWidth(selectedStackName);
				if (itemNameWidth >= 88) {
					selectedStackName = selectedStackName.substring(0, 14).trim() + "...";
				}
				final SlotExtractionMode placementMode = DankNullUtils.getPlacementModeForStack(currentItem, selectedStack);
				mc.fontRenderer.drawStringWithShadow(translate("dn.selected_item.desc") + ": " + selectedStackName, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 72, 16777215);
				mc.fontRenderer.drawStringWithShadow(translate("dn.count.desc") + ": " + (isCreativeDankNull(currentItem) ? "Infinite" : selectedStack.getCount()), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 61, 16777215);
				mc.fontRenderer.drawStringWithShadow(translate("dn.place.desc") + ": " + placementMode.getTooltip().replace(DankNullUtils.translate("dn.extract.desc").toLowerCase(Locale.ENGLISH), DankNullUtils.translate("dn.place.desc").toLowerCase(Locale.ENGLISH)).replace(DankNullUtils.translate("dn.extract.desc"), DankNullUtils.translate("dn.place.desc")), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 50, 16777215);
				mc.fontRenderer.drawStringWithShadow(translate("dn.extract.desc") + ": " + getExtractionModeForStack(currentItem, selectedStack).getTooltip(), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 40, 16777215);

				final String keyBind = ModKeyBindings.getOpenDankNullKeyBind().getDisplayName();
				mc.fontRenderer.drawStringWithShadow(keyBind.equalsIgnoreCase("none") ? translate("dn.no_open_keybind.desc") : translate("dn.open_with.desc") + " " + keyBind, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 29, 16777215);
				String oreDictMode = translate("dn.ore_dictionary.desc") + ": " + (getOreDictModeForStack(currentItem, selectedStack) ? translate("dn.enabled.desc") : translate("dn.disabled.desc"));
				if (!isItemOreDicted(selectedStack)) {
					oreDictMode = translate("dn.not_oredicted.desc");
				}
				mc.fontRenderer.drawStringWithShadow(oreDictMode, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 18, 16777215);
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				RenderUtils.getRenderItem().renderItemAndEffectIntoGUI(currentItem, scaledRes.getScaledWidth() - 106 + 5, scaledRes.getScaledHeight() - 20);
				GlStateManager.popMatrix();
				String oreNames = "";
				if (isItemOreDicted(selectedStack)) {
					for (int i = 0; i < OreDictionary.getOreIDs(selectedStack).length; i++) {
						oreNames += OreDictionary.getOreName(OreDictionary.getOreIDs(selectedStack)[i]) + " ";
					}
				}
			}
		}
	}

	public static void emptyDankNullDock(@Nonnull final TileDankNullDock dock) {
		if (dock != null && dock.getWorld() != null) {
			dock.setInventory(null);
		}
	}

	public static String translate(final String key) {
		return I18n.translateToLocal(key);
	}

	public static boolean isItemOreDicted(final ItemStack stack) {
		return OreDictionary.getOreIDs(stack).length > 0;
	}

	public static ItemStack convertToOreDictedStack(final ItemStack toBeConverted, final ItemStack toConvertTo) {
		if (!isItemOreDicted(toBeConverted) || !isItemOreDicted(toConvertTo) || Options.disableOreDictMode) {
			return ItemStack.EMPTY;
		}
		final ItemStack newStack = toConvertTo.copy();
		newStack.setCount(toBeConverted.getCount());
		return newStack;
	}

	@SideOnly(Side.SERVER)
	public static void sendConfigsToClient(final EntityPlayerMP player) {
		final WeakHashMapSerializable<String, Object> map = new WeakHashMapSerializable<>();
		map.put(ModConfig.CONST_CREATIVE_BLACKLIST, Options.creativeBlacklist);
		map.put(ModConfig.CONST_CREATIVE_WHITELIST, Options.creativeWhitelist);
		map.put(ModConfig.CONST_OREDICT_BLACKLIST, Options.oreBlacklist);
		map.put(ModConfig.CONST_OREDICT_WHITELIST, Options.oreWhitelist);
		map.put(ModConfig.CONST_DISABLE_OREDICT, Options.disableOreDictMode);
		ModNetworking.getInstance().sendTo(new PacketConfigSync(map), player);
	}

	public static enum SlotExtractionMode {

			KEEP_ALL(Integer.MAX_VALUE, DankNullUtils.translate("dn.not_extract.desc")),
			KEEP_1(1, DankNullUtils.translate("dn.extract_all_but.desc") + " 1"),
			KEEP_16(16, DankNullUtils.translate("dn.extract_all_but.desc") + " 16"),
			KEEP_64(64, DankNullUtils.translate("dn.extract_all_but.desc") + " 64"),
			KEEP_NONE(0, DankNullUtils.translate("dn.extract_all.desc"));

		int number = 0;
		String msg;

		SlotExtractionMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return DankNullUtils.translate("dn.will.desc") + " " + msg + " " + DankNullUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return DankNullUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

	public static enum SlotPlacementMode {

			KEEP_ALL(Integer.MAX_VALUE, DankNullUtils.translate("dn.not_extract.desc")),
			KEEP_1(1, DankNullUtils.translate("dn.extract_all_but.desc") + " 1"),
			KEEP_16(16, DankNullUtils.translate("dn.extract_all_but.desc") + " 16"),
			KEEP_64(64, DankNullUtils.translate("dn.extract_all_but.desc") + " 64"),
			KEEP_NONE(0, DankNullUtils.translate("dn.extract_all.desc"));

		int number = 0;
		String msg;

		SlotPlacementMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return DankNullUtils.translate("dn.will.desc") + " " + msg + " " + DankNullUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return DankNullUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

}