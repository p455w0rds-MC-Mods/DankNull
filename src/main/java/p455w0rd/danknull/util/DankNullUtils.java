package p455w0rd.danknull.util;

import static p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory.MAIN;
import static p455w0rd.danknull.inventory.PlayerSlot.EnumInvCategory.OFF_HAND;

import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import p455w0rd.danknull.blocks.BlockDankNullDock;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.client.gui.GuiDankNull;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.items.*;
import p455w0rd.danknull.network.*;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;
import p455w0rdslib.util.*;

/**
 * @author p455w0rd
 */
public class DankNullUtils {

	public static PlayerSlot getDankNullSlot(final EntityPlayer player) {
		final InventoryPlayer playerInv = player.inventory;
		final ItemStack mainHand = player.getHeldItemMainhand();
		final ItemStack offHand = player.getHeldItemOffhand();

		if (mainHand.getItem() instanceof ItemDankNull) {
			return new PlayerSlot(playerInv.currentItem, MAIN);
		}
		else if (offHand.getItem() instanceof ItemDankNull) {
			return new PlayerSlot(0, OFF_HAND);
		}

		for (int i = 0; i < playerInv.mainInventory.size(); i++) {
			final ItemStack stack = playerInv.mainInventory.get(i);
			if (stack.getItem() instanceof ItemDankNull) {
				return new PlayerSlot(i, MAIN);
			}
		}
		return null;
	}

	public static ItemStack getDockedDankNull(final ItemStack dankNullDock) {
		if (dankNullDock.hasTagCompound() && dankNullDock.getTagCompound().hasKey(NBT.BLOCKENTITYTAG, Constants.NBT.TAG_COMPOUND)) {
			final NBTTagCompound nbt = dankNullDock.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
			if (!nbt.hasNoTags()) {
				return new ItemStack(nbt.getCompoundTag(NBT.DOCKEDSTACK));
			}
		}
		return ItemStack.EMPTY;
	}

	public static void setDockedDankNull(final ItemStack dankNullDock, final ItemStack newDankNull) {
		if (!dankNullDock.hasTagCompound()) {
			dankNullDock.setTagCompound(new NBTTagCompound());
		}
		if (dankNullDock.getTagCompound().hasKey(NBT.BLOCKENTITYTAG, Constants.NBT.TAG_COMPOUND)) {
			final NBTTagCompound nbt = dankNullDock.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
			nbt.setTag(NBT.DOCKEDSTACK, newDankNull.serializeNBT());
		}
		else {
			final NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag(NBT.DOCKEDSTACK, newDankNull.serializeNBT());
			dankNullDock.getTagCompound().setTag(NBT.BLOCKENTITYTAG, nbt);
		}
	}

	public static ItemStack getDockedDankNull(final TileEntity dankDock) {
		if (isDankNullDock(dankDock)) {
			return ((TileDankNullDock) dankDock).getDankNull();
		}
		return ItemStack.EMPTY;
	}

	public static List<PlayerSlot> getAllDankNulls(final EntityPlayer player) {
		final InventoryPlayer playerInv = player.inventory;
		final List<PlayerSlot> dankNullList = Lists.newArrayList();
		for (int i = 0; i < playerInv.mainInventory.size(); i++) {
			if (isDankNull(playerInv.mainInventory.get(i))) {
				dankNullList.add(new PlayerSlot(i, MAIN));
			}
		}

		for (int i = 0; i < playerInv.offHandInventory.size(); i++) {
			if (isDankNull(playerInv.offHandInventory.get(i))) {
				dankNullList.add(new PlayerSlot(i, MAIN));
			}
		}

		return dankNullList;
	}

	public static PlayerSlot getDankNullForStack(final EntityPlayer player, final ItemStack stack) {
		final List<PlayerSlot> dankNulls = getAllDankNulls(player);

		for (final PlayerSlot slot : dankNulls) {
			final ItemStack itemStack = slot.getStackInSlot(player);
			if (itemStack.getItem() instanceof ItemDankNull) {
				if (isFiltered(getNewDankNullInventory(slot, player), stack)) {
					return slot;
				}
				if (isFilteredOreDict(getNewDankNullInventory(slot, player), stack)) {
					return slot;
				}
			}
		}

		return null;
	}

	public static void setStackInSlot(final ItemStack dankNull, final int index, final ItemStack stack) {
		final InventoryDankNull inv = getNewDankNullInventory(dankNull);
		inv.setInventorySlotContents(index, stack);
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		inv.saveInventory(dankNull.getTagCompound());
	}

	public static ItemStack decrStackSize(final ItemStack dankNull, final int index, final int amount) {
		final InventoryDankNull inv = getNewDankNullInventory(dankNull);
		final ItemStack ret = inv.decrStackSize(index, amount);
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		inv.saveInventory(dankNull.getTagCompound());
		return ret;
	}

	public static boolean isDankDock(final Object obj) {
		return obj != null && obj instanceof TileDankNullDock || obj instanceof BlockDankNullDock || obj instanceof ItemBlockDankNullDock;
	}

	public static void reArrangeStacks(final TileDankNullDock dankDock) {
		if (isDankDock(dankDock) && !dankDock.getDankNull().isEmpty()) {
			//final InventoryDankNull tmpInv = getNewDankNullInventory(dankDock.getDankNull());
			reArrangeStacks(dankDock.getDankNull());
		}
	}

	public static void reArrangeStacks(final ItemStack dankNull) {
		if (isDankNull(dankNull)) {
			final InventoryDankNull tmpInv = getNewDankNullInventory(dankNull);
			reArrangeStacks(tmpInv);
			tmpInv.markDirty();
		}
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

	public static IMessage getSyncPacket(final GuiDankNull gui) {
		if (gui.isTile()) {
			final ContainerDankNullDock c = (ContainerDankNullDock) gui.inventorySlots;
			return new PacketSyncDankNullDock(c.getTile(), c.getDankNull());
		}
		final ContainerDankNull c = (ContainerDankNull) gui.inventorySlots;
		return new PacketSyncDankNull(Pair.of(c.getPlayerSlot().getSlotIndex(), gui.getDankNull()));
	}

	public static NonNullList<ItemStack> getInventoryListArray(final InventoryDankNull inventory) {
		if (inventory != null) {
			return inventory.getStacks();
		}
		return NonNullList.<ItemStack>create();
	}

	public static int getSelectedStackIndex(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			return getSelectedStackIndex(inventory.getDankNull());
		}
		return -1;
	}

	public static int getSelectedStackIndex(final ItemStack dankNull) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		if (!dankNull.getTagCompound().hasKey(NBT.SELECTEDINDEX)) {
			dankNull.getTagCompound().setInteger(NBT.SELECTEDINDEX, 0);
		}
		return dankNull.getTagCompound().getInteger(NBT.SELECTEDINDEX);
	}

	public static boolean isDankNull(final ItemStack stack) {
		return stack.getItem() instanceof ItemDankNull;
	}

	public static ItemStack makeDankNullCreative(final ItemStack dankNull) {
		final ItemStack creativeDankNull = dankNull.copy();
		if (isDankNull(creativeDankNull) && !isCreativeDankNull(creativeDankNull)) {
			if (getMeta(creativeDankNull) != 6) {
				final ItemStack newStack = new ItemStack(ModItems.CREATIVE_DANKNULL);
				if (creativeDankNull.hasTagCompound()) {
					newStack.setTagCompound(creativeDankNull.getTagCompound());
				}
				return newStack;
			}
		}
		return creativeDankNull;
	}

	public static boolean isCreativeDankNull(final ItemStack stack) {
		return isDankNull(stack) && stack.getItem() == ModItems.CREATIVE_DANKNULL;
	}

	public static boolean isDankNullPanel(final ItemStack stack) {
		return stack.getItem() instanceof ItemDankNullPanel;
	}

	public static boolean isDankNullDock(final ItemStack stack) {
		return stack.getItem() instanceof ItemBlockDankNullDock;
	}

	public static boolean isDankNullDock(final TileEntity tile) {
		return tile instanceof TileDankNullDock;
	}

	public static boolean isDankNullDockEmpty(final ItemStack dankDock) {
		return isDankNullDock(dankDock) && getDockedDankNull(dankDock).isEmpty();
	}

	public static int getMeta(final ItemStack stack) {
		if (isDankNull(stack)) {
			return ((ItemDankNull) stack.getItem()).getTier().ordinal();
		}
		else if (isDankNullPanel(stack)) {
			return ((ItemDankNullPanel) stack.getItem()).getTier().ordinal();
		}
		else if (isDankNullDock(stack)) {
			return !isDankNullDockEmpty(stack) ? ((ItemDankNull) getDockedDankNull(stack).getItem()).getTier().ordinal() : -1;
		}
		return -1;
	}

	public static boolean isCreativeDankNullLocked(final ItemStack dankNull) {
		if (isCreativeDankNull(dankNull)) {
			return dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(NBT.LOCKED, Constants.NBT.TAG_BYTE) && dankNull.getTagCompound().getBoolean(NBT.LOCKED);
		}
		return false;
	}

	public static void setLocked(final ItemStack creativeDankNull, final boolean locked) {
		if (isCreativeDankNull(creativeDankNull)) {
			if (!creativeDankNull.hasTagCompound()) {
				creativeDankNull.setTagCompound(new NBTTagCompound());
			}
			creativeDankNull.getTagCompound().setBoolean(NBT.LOCKED, locked);
		}
	}

	public static void setSelectedStackIndex(final InventoryDankNull inventory, final int index) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			final ItemStack dankNull = inventory.getDankNull();
			setSelectedStackIndex(dankNull, index);
		}
	}

	public static void setSelectedStackIndex(final ItemStack dankNull, final int index) {
		ItemNBTUtils.setInteger(dankNull, NBT.SELECTEDINDEX, index);
	}

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
		if (player != null) {
			if (isDankNull(player.getHeldItemMainhand())) {
				return new InventoryDankNull(new PlayerSlot(player.inventory.currentItem, MAIN), player);
			}
			else if (isDankNull(player.getHeldItemOffhand())) {
				return new InventoryDankNull(new PlayerSlot(0, OFF_HAND), player);
			}
		}
		return null;
	}

	public static ItemStack getSelectedStack(final ItemStack dankNull) {
		if (!dankNull.hasTagCompound()) {
			setSelectedStackIndex(dankNull, isEmpty(dankNull) ? -1 : 0);
		}
		final int selectedIndex = ItemNBTUtils.getInt(dankNull, NBT.SELECTEDINDEX);
		if (selectedIndex > -1 && ItemNBTUtils.hasNBTTagList(dankNull, NBT.DANKNULL_INVENTORY)) {
			final NBTTagList itemList = ItemNBTUtils.getNBTTagList(dankNull, NBT.DANKNULL_INVENTORY);
			if (selectedIndex < itemList.tagCount()) {
				final NBTTagCompound currentNBT = itemList.getCompoundTagAt(selectedIndex);
				final ItemStack ret = new ItemStack(currentNBT);
				if (NBTUtils.hasInt(currentNBT, NBT.REALCOUNT)) {
					ret.setCount(NBTUtils.getInt(currentNBT, NBT.REALCOUNT));
				}
				return ret;
			}
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getSelectedStack(final InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			final ItemStack dankNull = inventory.getDankNull();
			if (!dankNull.hasTagCompound()) {
				setSelectedStackIndex(inventory, isEmpty(inventory) ? 1 : 0);
			}
			final int selectedIndex = ItemNBTUtils.getInt(dankNull, NBT.SELECTEDINDEX);
			if (selectedIndex > -1) {
				return inventory.getStackInSlot(selectedIndex);
			}
		}
		return ItemStack.EMPTY;
	}

	public static boolean isEmpty(final InventoryDankNull inventory) {
		return inventory.isEmpty();
	}

	private static boolean isEmpty(final ItemStack dankNull) {
		return isEmpty(getNewDankNullInventory(dankNull));
	}

	public static boolean isFiltered(final ItemStack dankNull, final ItemStack filteredStack) {
		return isFiltered(getNewDankNullInventory(dankNull), filteredStack);
	}

	public static boolean isFiltered(final InventoryDankNull inventory, final ItemStack filteredStack) {
		if (inventory != null) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					if (ItemUtils.areItemStacksEqualIgnoreSize(inventory.getStackInSlot(i), filteredStack)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isFilteredOreDict(final InventoryDankNull inventory, final ItemStack filteredStack) {
		if (isOreDictBlacklistEnabled() && !isItemOreDictBlacklisted(filteredStack) || isOreDictWhitelistEnabled() && isItemOreDictWhitelisted(filteredStack) || !isOreDictBlacklistEnabled() && !isOreDictWhitelistEnabled()) {
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

	public static boolean canStackBeAdded(final InventoryDankNull inventory, final ItemStack stack) {
		if (isDankNull(inventory.getDankNull())) {
			return canStackBeAdded(inventory.getDankNull(), stack);
		}
		return false;
	}

	public static boolean canStackBeAdded(final ItemStack dankNull, final ItemStack stack) {
		if (isDankNull(dankNull)) {
			if (DankNullUtils.isCreativeDankNull(dankNull)) {
				NonNullList<ItemStack> whiteList = null;
				try {
					whiteList = Options.getCreativeWhitelistedItems();
				}
				catch (final Exception e) {
				}
				if (whiteList != null && !whiteList.isEmpty()) {
					for (final ItemStack whiteListedStack : whiteList) {
						if (ItemUtils.areItemStacksEqualIgnoreSize(stack, whiteListedStack)) {
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
						if (ItemUtils.areItemStacksEqualIgnoreSize(stack, blackListedStack)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	/*public static boolean addUnfiliteredStackToDankNull(final ContainerDankNull container, final ItemStack stack) {
		if (container.getDankNullInventory() != null && canStackBeAdded(container.getDankNullInventory(), stack)) {
			container.inventorySlots.get(getNextAvailableSlot(container)).putStack(stack);
			return true;
		}
		return false;
	}*/

	//TODO remove when these classes get merged
	public static boolean addUnfiliteredStackToDankNull(final InventoryDankNull inventory, final ItemStack stack) {
		if (!inventory.getDankNull().isEmpty() && canStackBeAdded(inventory, stack)) {
			inventory.setInventorySlotContents(getNextAvailableSlot(inventory), stack);
			return true;
		}
		return false;
	}

	public static int getNextAvailableSlot(final InventoryDankNull inventory) {
		if (isCreativeDankNull(inventory.getDankNull()) && isCreativeDankNullLocked(inventory.getDankNull())) {
			return -1;
		}
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				return i;
			}
		}
		return -1;
	}

	public static boolean addFilteredStackToDankNull(ItemStack dankNull, ItemStack filteredStack) {
		if (isDankNull(dankNull) && canStackBeAdded(dankNull, filteredStack)) {
			final InventoryDankNull tmpInv = getNewDankNullInventory(dankNull); //we'll only use getters with this inv (no syncing)
			if (getIndexForStack(tmpInv, filteredStack) >= 0) {
				final ItemStack currentStack = getFilteredStack(tmpInv, filteredStack);
				if (!currentStack.isEmpty() && !filteredStack.isEmpty() && !ItemUtils.areItemStacksEqualIgnoreSize(currentStack, filteredStack)) {
					filteredStack = convertToOreDictedStack(filteredStack, currentStack);
				}
				if (filteredStack.getCount() < Integer.MAX_VALUE) {
					final long currentSize = currentStack.getCount();
					final long maxDankNullStackSize = getTier(dankNull).getMaxStackSize();
					if (currentSize + filteredStack.getCount() > maxDankNullStackSize) {
						currentStack.setCount((int) maxDankNullStackSize);
					}
					else {
						currentStack.setCount((int) currentSize + filteredStack.getCount());
					}
					tmpInv.setInventorySlotContents(getIndexForStack(tmpInv, filteredStack), currentStack);
					dankNull = tmpInv.getDankNull();
					return true;
				}
			}
		}
		return false;
	}

	/*public static boolean addFilteredStackToDankNull(final ContainerDankNull container, ItemStack filteredStack) {
		if (container.getDankNullInventory() != null && canStackBeAdded(container.getDankNullInventory(), filteredStack)) {
			final InventoryDankNull inventory = container.getDankNullInventory();
			if (getIndexForStack(inventory, filteredStack) >= 0) {
				final ItemStack currentStack = getFilteredStack(inventory, filteredStack);
				if (!currentStack.isEmpty() && !filteredStack.isEmpty() && !ItemUtils.areItemStacksEqualIgnoreSize(currentStack, filteredStack)) {
					filteredStack = convertToOreDictedStack(filteredStack, currentStack);
				}
				if (filteredStack.getCount() < Integer.MAX_VALUE) {
					final long currentSize = currentStack.getCount();
					final long maxDankNullStackSize = getTier(inventory).getMaxStackSize();
					if (currentSize + filteredStack.getCount() > maxDankNullStackSize) {
						currentStack.setCount((int) maxDankNullStackSize);
					}
					else {
						currentStack.setCount((int) currentSize + filteredStack.getCount());
					}
					inventory.setInventorySlotContents(getIndexForStack(inventory, filteredStack), currentStack);
					container.inventorySlots.get(36 + getIndexForStack(inventory, filteredStack)).putStack(currentStack);
					return true;
				}
			}
		}
		return false;
	}*/

	public static boolean addFilteredStackToDankNull(final InventoryDankNull inventory, ItemStack filteredStack) {
		if (canStackBeAdded(inventory, filteredStack)) {
			if (getIndexForStack(inventory, filteredStack) >= 0) {
				final ItemStack currentStack = getFilteredStack(inventory, filteredStack);
				if (!currentStack.isEmpty() && !filteredStack.isEmpty() && !ItemUtils.areItemStacksEqualIgnoreSize(currentStack, filteredStack)) {
					filteredStack = convertToOreDictedStack(filteredStack, currentStack);
				}
				if (filteredStack.getCount() < Integer.MAX_VALUE) {
					final long currentSize = currentStack.getCount();
					final long maxDankNullStackSize = getTier(inventory).getMaxStackSize();
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

	public static int getIndexForStack(final ItemStack dankNull, final ItemStack filteredStack) {
		final InventoryDankNull inv = getNewDankNullInventory(dankNull);
		return getIndexForStack(inv, filteredStack);
	}

	public static int getIndexForStack(final InventoryDankNull inventory, final ItemStack filteredStack) {
		if (!filteredStack.isEmpty()) {
			if (isFiltered(inventory, filteredStack)) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (!inventory.getStackInSlot(i).isEmpty()) {
						if (ItemUtils.areItemStacksEqualIgnoreSize(inventory.getStackInSlot(i), filteredStack)) {
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

	// for rendering
	public static ItemStack getStackInDankNullSlot(final ItemStack dankNull, final int slot) {
		final NBTTagList itemList = getNBTItemList(dankNull);
		if (!itemList.hasNoTags() && itemList.tagCount() > slot) {
			return new ItemStack(itemList.getCompoundTagAt(slot));
		}
		return ItemStack.EMPTY;
	}

	public static ItemStack getStackInDankNullSlotWithSize(final ItemStack dankNull, final int slot) {
		final NBTTagList itemList = getNBTItemList(dankNull);
		if (!itemList.hasNoTags() && itemList.tagCount() > slot) {
			final NBTTagCompound nbt = itemList.getCompoundTagAt(slot);
			final ItemStack ret = new ItemStack(nbt);
			if (NBTUtils.hasInt(nbt, NBT.REALCOUNT)) {
				ret.setCount(NBTUtils.getInt(nbt, NBT.REALCOUNT));
			}
			return ret;
		}
		return ItemStack.EMPTY;
	}

	public static int getSizeInventory(final ItemStack dankNull) {
		return !getNBTItemList(dankNull).hasNoTags() ? getNBTItemList(dankNull).tagCount() : 0;
	}

	public static NBTTagList getNBTItemList(final ItemStack dankNull) {
		if (dankNull.hasTagCompound()) {
			final NBTTagCompound nbt = dankNull.getTagCompound();
			if (NBTUtils.hasNBTTagList(nbt, NBT.DANKNULL_INVENTORY)) {
				return NBTUtils.getNBTTagList(nbt, NBT.DANKNULL_INVENTORY);
			}
		}
		return new NBTTagList();
	}

	public static void decrDankNullStackSize(@Nonnull final TileDankNullDock dankDock, @Nonnull final ItemStack stack, final int amount) {
		if (dankDock == null || dankDock.getDankNull().isEmpty() || stack.isEmpty()) {
			return;
		}
		final ItemStack dankNull = dankDock.getDankNull();
		if (isFiltered(dankNull, stack)) {
			final InventoryDankNull tmpInv = getNewDankNullInventory(dankNull);
			ItemStack currentStack = getFilteredStack(tmpInv, stack);
			currentStack.setCount(currentStack.getCount() - amount);
			if (currentStack.getCount() <= 0) {
				currentStack = ItemStack.EMPTY;
			}

			final NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag(NBT.DOCKEDSTACK, ItemNBTUtils.getRawStack(tmpInv.getDankNull()));
			dankDock.readFromNBT(nbt);
			dankDock.markDirty();
		}
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

	// For ItemStack access via player inventory (right-click, keybind, etc)
	public static InventoryDankNull getNewDankNullInventory(final PlayerSlot slot, final EntityPlayer player) {
		final ItemStack stack = slot.getStackInSlot(player);
		if (isDankNull(stack)) {
			return new InventoryDankNull(slot, player);
		}
		return null;
	}

	// Use for Tile ONLY!
	public static InventoryDankNull getNewDankNullInventory(final ItemStack stack) {
		return new InventoryDankNull(stack);
	}

	public static DankNullTier getTier(final InventoryDankNull inventory) {
		return getTier(inventory.getDankNull());
	}

	public static DankNullTier getTier(final ItemStack dankNull) {
		return getMeta(dankNull) == -1 ? DankNullTier.NONE : DankNullTier.VALUES[getMeta(dankNull)];
	}

	public static int getSlotCount(final ItemStack stack) {
		return (getMeta(stack) + 1) * 9;
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
		if (!Options.disableOreDictMode && dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(NBT.OREDICT_MODES, Constants.NBT.TAG_LIST)) {
			final NBTTagList extractionList = dankNull.getTagCompound().getTagList(NBT.OREDICT_MODES, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				final NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag(NBT.STACK)), tempNBT.getBoolean(NBT.OREDICT));
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
			tempNBT.setTag(NBT.STACK, stack.serializeNBT());
			tempNBT.setBoolean(NBT.OREDICT, modes.get(stack));
			oreDictList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(NBT.OREDICT_MODES, oreDictList);
	}

	public static boolean getOreDictModeForStack(final ItemStack dankNull, final ItemStack stack) {
		if (!Options.disableOreDictMode) {
			final Map<ItemStack, Boolean> modes = getOreDictModes(dankNull);
			if (!modes.isEmpty()) {
				for (final ItemStack currentStack : modes.keySet()) {
					if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
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
			if (ItemUtils.areItemStacksEqualIgnoreSize(tempStack, currentStack)) {
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

	public static Map<ItemStack, ItemExtractionMode> getExtractionModes(final ItemStack dankNull) {
		final Map<ItemStack, ItemExtractionMode> modes = Maps.<ItemStack, ItemExtractionMode>newHashMap();
		if (ItemNBTUtils.hasTag(dankNull) && ItemNBTUtils.hasNBTTagList(dankNull, NBT.EXTRACTION_MODES)) {
			final NBTTagList extractionList = ItemNBTUtils.getNBTTagList(dankNull, NBT.EXTRACTION_MODES);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				final NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag(NBT.STACK)), ItemExtractionMode.values()[tempNBT.getInteger(NBT.MODE)]);
			}
		}
		return modes;
	}

	public static void setExtractionModes(final ItemStack dankNull, final Map<ItemStack, ItemExtractionMode> modes) {
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
			tempNBT.setTag(NBT.STACK, ItemNBTUtils.getRawStack(stack));
			tempNBT.setInteger(NBT.MODE, modes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(NBT.EXTRACTION_MODES, extractionList);
	}

	public static ItemExtractionMode getExtractionModeForStack(final ItemStack dankNull, final ItemStack stack) {
		final Map<ItemStack, ItemExtractionMode> modes = getExtractionModes(dankNull);
		if (!modes.isEmpty()) {
			for (final ItemStack currentStack : modes.keySet()) {
				if (ItemUtils.areItemStacksEqualIgnoreSize(stack, currentStack)) {
					return modes.get(currentStack);
				}
			}
		}
		return ItemExtractionMode.KEEP_ALL;
	}

	public static void setExtractionModeForStack(final ItemStack dankNull, final ItemStack stack, final ItemExtractionMode mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		final Map<ItemStack, ItemExtractionMode> currentModes = getExtractionModes(dankNull);
		for (final ItemStack currentStack : currentModes.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(stack, currentStack)) {
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
		final ItemExtractionMode current = getExtractionModeForStack(dankNull, stack);
		if (current.ordinal() >= ItemExtractionMode.values().length - 1) {
			setExtractionModeForStack(dankNull, stack, ItemExtractionMode.values()[0]);
		}
		else {
			setExtractionModeForStack(dankNull, stack, ItemExtractionMode.values()[current.ordinal() + 1]);
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

	public static Map<ItemStack, ItemPlacementMode> getPlacementModes(final ItemStack dankNull) {
		final Map<ItemStack, ItemPlacementMode> modes = Maps.<ItemStack, ItemPlacementMode>newHashMap();
		if (ItemNBTUtils.hasTag(dankNull) && ItemNBTUtils.hasNBTTagList(dankNull, NBT.PLACEMENT_MODES)) {
			final NBTTagList extractionList = ItemNBTUtils.getNBTTagList(dankNull, NBT.PLACEMENT_MODES);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				final NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag(NBT.STACK)), ItemPlacementMode.values()[tempNBT.getInteger(NBT.MODE)]);
			}
		}
		return modes;
	}

	public static void setPlacementModes(final ItemStack dankNull, final Map<ItemStack, ItemPlacementMode> modes) {
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
			tempNBT.setTag(NBT.STACK, stack.serializeNBT());
			tempNBT.setInteger(NBT.MODE, modes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(NBT.PLACEMENT_MODES, extractionList);
	}

	public static ItemPlacementMode getPlacementModeForStack(final ItemStack dankNull, final ItemStack stack) {
		final Map<ItemStack, ItemPlacementMode> modes = getPlacementModes(dankNull);
		if (!modes.isEmpty()) {
			for (final ItemStack currentStack : modes.keySet()) {
				if (ItemUtils.areItemStacksEqualIgnoreSize(stack, currentStack)) {
					return modes.get(currentStack);
				}
			}
		}
		return ItemPlacementMode.KEEP_1;
	}

	public static void setPlacementModeForStack(final ItemStack dankNull, final ItemStack stack, final ItemPlacementMode mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		final Map<ItemStack, ItemPlacementMode> currentModes = getPlacementModes(dankNull);
		for (final ItemStack currentStack : currentModes.keySet()) {
			if (ItemUtils.areItemStacksEqualIgnoreSize(stack, currentStack)) {
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
		final ItemPlacementMode current = getPlacementModeForStack(dankNull, stack);
		if (current.ordinal() >= ItemPlacementMode.values().length - 1) {
			setPlacementModeForStack(dankNull, stack, ItemPlacementMode.values()[0]);
		}
		else {
			setPlacementModeForStack(dankNull, stack, ItemPlacementMode.values()[current.ordinal() + 1]);
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
		if (!Options.showHUD || !mc.playerController.shouldDrawHUD() && !mc.player.capabilities.isCreativeMode) {
			return;
		}
		ItemStack currentItem = mc.player.inventory.getCurrentItem();
		if (currentItem.isEmpty() || !isDankNull(currentItem)) {
			currentItem = mc.player.getHeldItemOffhand();
		}
		if (!currentItem.isEmpty() && isDankNull(currentItem)) {
			final ItemStack selectedStack = getSelectedStack(getInventoryFromHeld(mc.player));
			if (!selectedStack.isEmpty()) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen0.png"));
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GuiUtils.drawTexturedModalRect(scaledRes.getScaledWidth() - 106, scaledRes.getScaledHeight() - 45, 0, 210, 106, 45, 0);
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				mc.fontRenderer.drawStringWithShadow(currentItem.getDisplayName(), scaledRes.getScaledWidth() * 2 - 212 + 55, scaledRes.getScaledHeight() * 2 - 83, getTier(currentItem).getHexColor(true));
				String selectedStackName = selectedStack.getDisplayName();
				final int itemNameWidth = mc.fontRenderer.getStringWidth(selectedStackName);
				if (itemNameWidth >= 88 && selectedStackName.length() >= 14) {
					selectedStackName = selectedStackName.substring(0, 14).trim() + "...";
				}
				final ItemPlacementMode placementMode = getPlacementModeForStack(currentItem, selectedStack);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.selected_item.desc") + ": " + selectedStackName, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 72, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.count.desc") + ": " + (isCreativeDankNull(currentItem) ? "Infinite" : selectedStack.getCount()), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 61, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.place.desc") + ": " + placementMode.getTooltip().replace(TextUtils.translate("dn.extract.desc").toLowerCase(Locale.ENGLISH), TextUtils.translate("dn.place.desc").toLowerCase(Locale.ENGLISH)).replace(TextUtils.translate("dn.extract.desc"), TextUtils.translate("dn.place.desc")), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 50, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.extract.desc") + ": " + getExtractionModeForStack(currentItem, selectedStack).getTooltip(), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 40, 16777215);

				final String keyBind = ModKeyBindings.getOpenDankNullKeyBind().getDisplayName();
				mc.fontRenderer.drawStringWithShadow(keyBind.equalsIgnoreCase("none") ? TextUtils.translate("dn.no_open_keybind.desc") : TextUtils.translate("dn.open_with.desc") + " " + keyBind, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 29, 16777215);
				String oreDictMode = TextUtils.translate("dn.ore_dictionary.desc") + ": " + (getOreDictModeForStack(currentItem, selectedStack) ? TextUtils.translate("dn.enabled.desc") : TextUtils.translate("dn.disabled.desc"));
				if (!isItemOreDicted(selectedStack)) {
					oreDictMode = TextUtils.translate("dn.not_oredicted.desc");
				}
				mc.fontRenderer.drawStringWithShadow(oreDictMode, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 18, 16777215);
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				RenderUtils.getRenderItem().renderItemAndEffectIntoGUI(currentItem, scaledRes.getScaledWidth() - 106 + 5, scaledRes.getScaledHeight() - 20);
				GlStateManager.popMatrix();
				//String oreNames = "";
				if (isItemOreDicted(selectedStack)) {
					for (int i = 0; i < OreDictionary.getOreIDs(selectedStack).length; i++) {
						//oreNames += OreDictionary.getOreName(OreDictionary.getOreIDs(selectedStack)[i]) + " ";
					}
				}
			}
		}
	}

	public static void toggleHUD() {
		Options.showHUD = !Options.showHUD;
		ModConfig.CONFIG.save();
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

	public static enum ItemExtractionMode {

			KEEP_ALL(Integer.MAX_VALUE, TextUtils.translate("dn.not_extract.desc")),
			KEEP_1(1, TextUtils.translate("dn.extract_all_but.desc") + " 1"),
			KEEP_16(16, TextUtils.translate("dn.extract_all_but.desc") + " 16"),
			KEEP_64(64, TextUtils.translate("dn.extract_all_but.desc") + " 64"),
			KEEP_NONE(0, TextUtils.translate("dn.extract_all.desc"));

		int number = 0;
		String msg;

		ItemExtractionMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return TextUtils.translate("dn.will.desc") + " " + msg + " " + TextUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return TextUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

	public static enum ItemPlacementMode {

			KEEP_ALL(Integer.MAX_VALUE, TextUtils.translate("dn.not_extract.desc")),
			KEEP_1(1, TextUtils.translate("dn.extract_all_but.desc") + " 1"),
			KEEP_16(16, TextUtils.translate("dn.extract_all_but.desc") + " 16"),
			KEEP_64(64, TextUtils.translate("dn.extract_all_but.desc") + " 64"),
			KEEP_NONE(0, TextUtils.translate("dn.extract_all.desc"));

		int number = 0;
		String msg;

		ItemPlacementMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return TextUtils.translate("dn.will.desc") + " " + msg + " " + TextUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return TextUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

}