package p455w0rd.danknull.util;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.container.ContainerDankNullDock;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.init.ModKeyBindings;
import p455w0rd.danknull.init.ModLogger;
import p455w0rd.danknull.init.ModNetworking;
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

	public static final String TAG_EXTRACTION_MODES = "ExtractionModes";
	public static final String TAG_PLACEMENT_MODES = "PlacementModes";
	private static final String TAG_OREDICT_MODES = "OreDictModes";
	private static final String TAG_LOCKED = "Locked";

	@Nonnull
	public static ItemStack getDankNull(EntityPlayer player) {
		InventoryPlayer playerInv = player.inventory;
		ItemStack dankNullItem = ItemStack.EMPTY;
		if (!player.getHeldItemMainhand().isEmpty()) {
			if ((player.getHeldItemMainhand().getItem() instanceof ItemDankNull)) {
				dankNullItem = player.getHeldItem(EnumHand.MAIN_HAND);
			}
			else if ((!player.getHeldItemOffhand().isEmpty()) && ((player.getHeldItemOffhand().getItem() instanceof ItemDankNull))) {
				dankNullItem = player.getHeldItem(EnumHand.OFF_HAND);
			}
		}
		else if (!player.getHeldItemOffhand().isEmpty()) {
			if ((player.getHeldItemOffhand().getItem() instanceof ItemDankNull)) {
				dankNullItem = player.getHeldItem(EnumHand.OFF_HAND);
			}
			else if ((!player.getHeldItemMainhand().isEmpty()) && ((player.getHeldItemMainhand().getItem() instanceof ItemDankNull))) {
				dankNullItem = player.getHeldItem(EnumHand.MAIN_HAND);
			}
		}
		if (dankNullItem.isEmpty()) {
			int invSize = playerInv.getSizeInventory();
			if (invSize <= 0) {
				return ItemStack.EMPTY;
			}
			for (int i = 0; i < invSize; i++) {
				ItemStack itemStack = playerInv.getStackInSlot(i);
				if (!itemStack.isEmpty()) {
					if ((itemStack.getItem() instanceof ItemDankNull)) {
						dankNullItem = itemStack;
						break;
					}
				}
			}
		}
		return dankNullItem;
	}

	@Nonnull
	public static Pair<Integer, ItemStack> getSyncableDankNull(EntityPlayer player) {
		InventoryPlayer playerInv = player.inventory;
		ItemStack dankNullItem = ItemStack.EMPTY;
		int slot = -1;
		if ((player.getHeldItemMainhand().getItem() instanceof ItemDankNull)) {
			slot = playerInv.currentItem;
			dankNullItem = player.getHeldItem(EnumHand.MAIN_HAND);
		}
		else if ((!player.getHeldItemOffhand().isEmpty()) && ((player.getHeldItemOffhand().getItem() instanceof ItemDankNull))) {
			slot = 40;
			dankNullItem = player.getHeldItem(EnumHand.OFF_HAND);
		}
		if (dankNullItem.isEmpty()) {
			int invSize = playerInv.getSizeInventory();
			if (invSize <= 0) {
				return Pair.of(-1, ItemStack.EMPTY);
			}
			for (int i = 0; i < invSize; i++) {
				ItemStack itemStack = playerInv.getStackInSlot(i);
				if (!itemStack.isEmpty() && (itemStack.getItem() instanceof ItemDankNull)) {
					slot = i;
					dankNullItem = itemStack;
					break;
				}
			}
		}
		return Pair.of(slot, dankNullItem);
	}

	public static List<ItemStack> getAllDankNulls(EntityPlayer player) {
		InventoryPlayer playerInv = player.inventory;
		List<ItemStack> dankNullList = Lists.newArrayList();
		for (ItemStack stack : playerInv.mainInventory) {
			if (isDankNull(stack)) {
				dankNullList.add(stack);
			}
		}
		if (!playerInv.offHandInventory.isEmpty() && playerInv.offHandInventory.get(0).getItem() == ModItems.DANK_NULL) {
			dankNullList.add(playerInv.offHandInventory.get(0));
		}
		return dankNullList;
	}

	@Nonnull
	public static InventoryDankNull getFirstDankNullForStack(EntityPlayer player, ItemStack stack) {
		InventoryDankNull dankNullInv = null;
		List<ItemStack> dankNulls = getAllDankNulls(player);
		for (ItemStack dankNull : dankNulls) {
			InventoryDankNull tmpInv = getNewDankNullInventory(dankNull);
			if (isFiltered(tmpInv, stack)) {
				dankNullInv = tmpInv;
				break;
			}
		}
		return dankNullInv;
	}

	public static ItemStack getDankNullForStack(EntityPlayer player, ItemStack stack) {
		InventoryPlayer playerInv = player.inventory;
		ItemStack dankNullItem = ItemStack.EMPTY;
		int invSize = playerInv.getSizeInventory();
		if (invSize <= 0) {
			return ItemStack.EMPTY;
		}
		for (int i = 0; i < invSize; i++) {
			ItemStack itemStack = playerInv.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if ((itemStack.getItem() instanceof ItemDankNull)) {
					if (isFiltered(getNewDankNullInventory(itemStack), stack)) {
						dankNullItem = itemStack;
						break;
					}
					if (isFilteredOreDict(getNewDankNullInventory(itemStack), stack)) {
						dankNullItem = itemStack;
						break;
					}
				}
			}
		}
		return dankNullItem;
	}

	public static void reArrangeStacks(InventoryDankNull inventory) {
		if (inventory != null) {
			int count = 0;
			NonNullList<ItemStack> stackList = NonNullList.withSize(inventory.getSizeInventory(), ItemStack.EMPTY);
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
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

	public static NonNullList<ItemStack> getInventoryListArray(InventoryDankNull inventory) {
		if (inventory != null) {
			return inventory.getStacks();
		}
		return NonNullList.<ItemStack>create();
	}

	public static int getSelectedStackIndex(InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			ItemStack dankNull = inventory.getDankNull();
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

	public static boolean isDankNull(ItemStack stack) {
		return stack.getItem() instanceof ItemDankNull;
	}

	public static ItemStack makeDankNullCreative(ItemStack dankNull) {
		ItemStack creativeDankNull = dankNull.copy();
		if (isDankNull(creativeDankNull) && !isCreativeDankNull(creativeDankNull)) {
			if (creativeDankNull.getItemDamage() != 6) {
				creativeDankNull.setItemDamage(6);
			}
		}
		return creativeDankNull;
	}

	public static boolean isCreativeDankNull(ItemStack stack) {
		return isDankNull(stack) && stack.getItemDamage() == 6;
	}

	public static boolean isCreativeDankNullLocked(ItemStack dankNull) {
		if (isCreativeDankNull(dankNull)) {
			return dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(TAG_LOCKED, Constants.NBT.TAG_BYTE) && dankNull.getTagCompound().getBoolean(TAG_LOCKED);
		}
		return false;
	}

	public static void setLocked(ItemStack creativeDankNull, boolean locked) {
		if (isCreativeDankNull(creativeDankNull)) {
			if (!creativeDankNull.hasTagCompound()) {
				creativeDankNull.setTagCompound(new NBTTagCompound());
			}
			creativeDankNull.getTagCompound().setBoolean(TAG_LOCKED, locked);
		}
	}

	public static void setSelectedStackIndex(InventoryDankNull inventory, int index) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			ItemStack dankNull = inventory.getDankNull();
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

	public static void setNextSelectedStack(InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			setNextSelectedStack(inventory, null);
		}
	}

	public static void setNextSelectedStack(InventoryDankNull inventory, EntityPlayer player) {
		int currentIndex = getSelectedStackIndex(inventory);
		int totalSize = getItemCount(inventory);
		int maxIndex = totalSize - 1;
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

	public static void setPreviousSelectedStack(InventoryDankNull inventory, EntityPlayer player) {
		int currentIndex = getSelectedStackIndex(inventory);
		int totalSize = getItemCount(inventory);
		int maxIndex = totalSize - 1;
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

	public static int getItemCount(InventoryDankNull inventory) {
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

	public static void decrSelectedStackSize(InventoryDankNull inventory, int amount) {
		if (inventory == null || inventory.getDankNull().isEmpty()) {
			return;
		}
		getSelectedStack(inventory).shrink(amount);
		reArrangeStacks(inventory);
	}

	public static int getSelectedStackSize(InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			ItemStack selectedStack = getSelectedStack(inventory);
			if (!selectedStack.isEmpty()) {
				return selectedStack.getCount();
			}
		}
		return 0;
	}

	public static InventoryDankNull getInventoryFromHeld(EntityPlayer player) {
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

	public static ItemStack getSelectedStack(InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			ItemStack dankNull = inventory.getDankNull();
			if (!dankNull.hasTagCompound()) {
				setSelectedStackIndex(inventory, isEmpty(inventory) ? 1 : 0);
			}

			NBTTagCompound nbtTC = dankNull.getTagCompound();
			if (!nbtTC.hasKey("selectedIndex")) {
				nbtTC.setInteger("selectedIndex", 0);
			}
			int selectedIndex = nbtTC.getInteger("selectedIndex");
			if (selectedIndex > -1) {
				return inventory.getStackInSlot(selectedIndex);
			}
			//}
		}
		return ItemStack.EMPTY;
	}

	public static boolean isEmpty(InventoryDankNull inventory) {
		return inventory.isEmpty();
	}

	public static boolean isFiltered(InventoryDankNull inventory, ItemStack filteredStack) {
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

	public static boolean isFilteredOreDict(InventoryDankNull inventory, ItemStack filteredStack) {
		if ((DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isItemOreDictBlacklisted(filteredStack)) || (DankNullUtils.isOreDictWhitelistEnabled() && DankNullUtils.isItemOreDictWhitelisted(filteredStack)) || !DankNullUtils.isOreDictBlacklistEnabled() && !DankNullUtils.isOreDictWhitelistEnabled()) {
			int[] ids = OreDictionary.getOreIDs(filteredStack);
			if (inventory != null && ids.length > 0) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					if (!inventory.getStackInSlot(i).isEmpty() && isItemOreDicted(inventory.getStackInSlot(i)) && isItemOreDicted(filteredStack) && getOreDictModeForStack(inventory.getDankNull(), inventory.getStackInSlot(i))) {
						int[] ids2 = OreDictionary.getOreIDs(inventory.getStackInSlot(i));
						for (int id : ids) {
							String name = OreDictionary.getOreName(id);
							for (int id2 : ids2) {
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

	public static boolean isItemOreDictBlacklisted(ItemStack stack) {
		if (isOreDictBlacklistEnabled()) {
			for (int id : OreDictionary.getOreIDs(stack)) {
				if (Options.getOreBlacklist().contains(OreDictionary.getOreName(id))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isItemOreDictWhitelisted(ItemStack stack) {
		if (isOreDictWhitelistEnabled()) {
			for (int id : OreDictionary.getOreIDs(stack)) {
				if (Options.getOreWhitelist().contains(OreDictionary.getOreName(id))) {
					return true;
				}
			}
		}
		return false;
	}

	public static DankNullItemHandler getHandler(ItemStack dankNull) {
		if (hasDankNullHandler(dankNull)) {
			return (DankNullItemHandler) dankNull.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		}
		return null;
	}

	public static boolean hasDankNullHandler(ItemStack dankNull) {
		return dankNull.getItem() == ModItems.DANK_NULL && dankNull.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	public static boolean canStackBeAdded(InventoryDankNull inventory, ItemStack stack) {
		if (!inventory.getDankNull().isEmpty()) {
			ItemStack dankNull = inventory.getDankNull();
			if (DankNullUtils.isCreativeDankNull(dankNull)) {
				NonNullList<ItemStack> whiteList = null;
				try {
					whiteList = Options.getCreativeWhitelistedItems();
				}
				catch (Exception e) {
				}
				if (whiteList != null && !whiteList.isEmpty()) {
					for (ItemStack whiteListedStack : whiteList) {
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
				catch (Exception e) {
				}
				if (blackList != null && !blackList.isEmpty()) {
					for (ItemStack blackListedStack : blackList) {
						if (areStacksEqual(stack, blackListedStack)) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	public static boolean addUnfiliteredFilteredStackToDankNull(ContainerDankNull container, ItemStack stack) {
		if (container.getDankNullInventory() != null && canStackBeAdded(container.getDankNullInventory(), stack)) {
			container.inventorySlots.get(getNextAvailableSlot(container)).putStack(stack);
			return true;
		}
		return false;
	}

	//TODO remove when these classes get merged
	public static boolean addUnfiliteredFilteredStackToDankNull(ContainerDankNullDock container, ItemStack stack) {
		if (container.getDankNullInventory() != null && canStackBeAdded(container.getDankNullInventory(), stack)) {
			container.inventorySlots.get(getNextAvailableSlot(container)).putStack(stack);
			return true;
		}
		return false;
	}

	public static int getNextAvailableSlot(ContainerDankNull container) {
		if (isCreativeDankNull(container.getDankNull()) && isCreativeDankNullLocked(container.getDankNull())) {
			return -1;
		}
		for (int i = 36; i < container.inventorySlots.size(); i++) {
			Slot s = container.inventorySlots.get(i);
			if ((s != null) && (s.getStack().isEmpty())) {
				return i;
			}
		}
		return -1;
	}

	//TODO remove when these classes get merged
	public static int getNextAvailableSlot(ContainerDankNullDock container) {
		if (isCreativeDankNull(container.getDankNull()) && isCreativeDankNullLocked(container.getDankNull())) {
			return -1;
		}
		for (int i = 36; i < container.inventorySlots.size(); i++) {
			Slot s = container.inventorySlots.get(i);
			if ((s != null) && (s.getStack().isEmpty())) {
				return i;
			}
		}
		return -1;
	}

	public static boolean addFilteredStackToDankNull(InventoryDankNull inventory, ItemStack filteredStack) {
		if (canStackBeAdded(inventory, filteredStack)) {
			if (getIndexForStack(inventory, filteredStack) >= 0) {
				ItemStack currentStack = getFilteredStack(inventory, filteredStack);
				if (!currentStack.isEmpty() && !filteredStack.isEmpty() && !areStacksEqual(currentStack, filteredStack)) {
					filteredStack = convertToOreDictedStack(filteredStack, currentStack);
				}
				if (filteredStack.getCount() < Integer.MAX_VALUE) {
					long currentSize = currentStack.getCount();
					long maxDankNullStackSize = getDankNullMaxStackSize(inventory);
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

	public static ItemStack getFilteredStack(InventoryDankNull inventory, ItemStack stack) {
		if (isFiltered(inventory, stack) || isFilteredOreDict(inventory, stack)) {
			return getItemByIndex(inventory, getIndexForStack(inventory, stack));
		}
		return ItemStack.EMPTY;
	}

	public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
		ItemStack tmpStack1 = stack1.copy();
		tmpStack1.setCount(1);
		ItemStack tmpStack2 = stack2.copy();
		tmpStack2.setCount(1);
		return ItemStack.areItemStacksEqual(tmpStack1, tmpStack2);
	}

	public static int getIndexForStack(InventoryDankNull inventory, ItemStack filteredStack) {
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
				int[] ids = OreDictionary.getOreIDs(filteredStack);
				if (inventory != null && ids.length > 0) {
					for (int i = 0; i < inventory.getSizeInventory(); i++) {
						if (!inventory.getStackInSlot(i).isEmpty() && isItemOreDicted(inventory.getStackInSlot(i)) && isItemOreDicted(filteredStack) && getOreDictModeForStack(inventory.getDankNull(), inventory.getStackInSlot(i))) {
							int[] ids2 = OreDictionary.getOreIDs(inventory.getStackInSlot(i));
							for (int id : ids) {
								String name = OreDictionary.getOreName(id);
								for (int id2 : ids2) {
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

	public static ItemStack getItemByIndex(InventoryDankNull inventory, int index) {
		if (inventory != null && index >= 0) {
			return inventory.getStackInSlot(index);
		}
		return ItemStack.EMPTY;
	}

	public static void decrDankNullStackSize(@Nonnull InventoryDankNull inventory, @Nonnull ItemStack stack, int amount) {
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

	public static InventoryDankNull getNewDankNullInventory(@Nonnull ItemStack stack) {
		return (stack.getItem() instanceof ItemDankNull) ? new InventoryDankNull(stack) : null;
	}

	public static int getDankNullMaxStackSize(@Nonnull ItemStack itemStackIn) {
		int level = itemStackIn.getItemDamage() + 1;
		if (level >= 6) {
			return Integer.MAX_VALUE;
		}
		return level * (128 * level);
	}

	public static int getDankNullMaxStackSize(InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			int level = inventory.getDankNull().getItemDamage() + 1;
			if (level == 6) {
				return Integer.MAX_VALUE;
			}
			return level * (128 * level);
		}
		return 0;
	}

	public static int getColor(int damage, boolean opaque) {
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

	public static int getSlotCount(ItemStack stack) {
		return (stack.getItemDamage() + 1) * 9;
	}

	public static int getSlotCount(InventoryDankNull inventory) {
		return inventory.getSizeInventory();
	}

	public static void setSelectedIndexApplicable(InventoryDankNull inventory) {
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

	public static EnumActionResult placeBlock(@Nonnull IBlockState state, World world, BlockPos pos) {
		return world.setBlockState(pos, state, 2) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}

	public static IRecipe addDankNullUpgradeRecipe(String recipeName, Object... params) {
		ShapedPrimer primer = CraftingHelper.parseShaped(params);
		IRecipe recipe = new RecipeDankNullUpgrade(primer.input).setRegistryName(new ResourceLocation(ModGlobals.MODID, recipeName));
		return recipe;
	}

	// ======
	// OreDict Mode
	// ======
	public static Map<ItemStack, Boolean> getOreDictModes(ItemStack dankNull) {
		Map<ItemStack, Boolean> modes = Maps.<ItemStack, Boolean>newHashMap();
		if (!Options.disableOreDictMode && dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(TAG_OREDICT_MODES, Constants.NBT.TAG_LIST)) {
			NBTTagList extractionList = dankNull.getTagCompound().getTagList(TAG_OREDICT_MODES, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag("Stack")), tempNBT.getBoolean("OreDict"));
			}
		}
		return modes;
	}

	public static void setOreDictModes(ItemStack dankNull, Map<ItemStack, Boolean> modes) {
		if (Options.disableOreDictMode || modes.isEmpty()) {
			return;
		}
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound dankNullNBT = dankNull.getTagCompound();
		NBTTagList oreDictList = new NBTTagList();
		for (ItemStack stack : modes.keySet()) {
			NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag("Stack", stack.serializeNBT());
			tempNBT.setBoolean("OreDict", modes.get(stack));
			oreDictList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(TAG_OREDICT_MODES, oreDictList);
	}

	public static boolean getOreDictModeForStack(ItemStack dankNull, ItemStack stack) {
		if (!Options.disableOreDictMode) {
			Map<ItemStack, Boolean> modes = getOreDictModes(dankNull);
			if (!modes.isEmpty()) {
				for (ItemStack currentStack : modes.keySet()) {
					if (areStacksEqual(currentStack, stack)) {
						return modes.get(currentStack);
					}
				}
			}
		}
		return false;
	}

	public static void setOreDictModeForStack(ItemStack dankNull, ItemStack stack, boolean mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		ItemStack tempStack = stack.copy();
		tempStack.setCount(1);
		Map<ItemStack, Boolean> currentModes = getOreDictModes(dankNull);
		for (ItemStack currentStack : currentModes.keySet()) {
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

	public static void cycleOreDictModeForStack(ItemStack dankNull, ItemStack stack) {
		setOreDictModeForStack(dankNull, stack, !getOreDictModeForStack(dankNull, stack));
	}

	// ======
	// Extraction Mode
	// ======

	public static Map<ItemStack, SlotExtractionMode> getExtractionModes(ItemStack dankNull) {
		Map<ItemStack, SlotExtractionMode> modes = Maps.<ItemStack, SlotExtractionMode>newHashMap();
		if (dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(TAG_EXTRACTION_MODES, Constants.NBT.TAG_LIST)) {
			NBTTagList extractionList = dankNull.getTagCompound().getTagList(TAG_EXTRACTION_MODES, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag("Stack")), SlotExtractionMode.values()[tempNBT.getInteger("Mode")]);
			}
		}
		return modes;
	}

	public static void setExtractionModes(ItemStack dankNull, Map<ItemStack, SlotExtractionMode> modes) {
		if (modes.isEmpty()) {
			return;
		}
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound dankNullNBT = dankNull.getTagCompound();
		NBTTagList extractionList = new NBTTagList();
		for (ItemStack stack : modes.keySet()) {
			NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag("Stack", stack.serializeNBT());
			tempNBT.setInteger("Mode", modes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(TAG_EXTRACTION_MODES, extractionList);
	}

	public static SlotExtractionMode getExtractionModeForStack(ItemStack dankNull, ItemStack stack) {
		Map<ItemStack, SlotExtractionMode> modes = getExtractionModes(dankNull);
		if (!modes.isEmpty()) {
			for (ItemStack currentStack : modes.keySet()) {
				if (areStacksEqual(stack, currentStack)) {
					return modes.get(currentStack);
				}
			}
		}
		return SlotExtractionMode.KEEP_ALL;
	}

	public static void setExtractionModeForStack(ItemStack dankNull, ItemStack stack, SlotExtractionMode mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		Map<ItemStack, SlotExtractionMode> currentModes = getExtractionModes(dankNull);
		for (ItemStack currentStack : currentModes.keySet()) {
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

	public static void cycleExtractionMode(ItemStack dankNull, ItemStack stack) {
		SlotExtractionMode current = getExtractionModeForStack(dankNull, stack);
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

	public static Map<ItemStack, SlotExtractionMode> getPlacementModes(ItemStack dankNull) {
		Map<ItemStack, SlotExtractionMode> modes = Maps.<ItemStack, SlotExtractionMode>newHashMap();
		if (dankNull.hasTagCompound() && dankNull.getTagCompound().hasKey(TAG_PLACEMENT_MODES, Constants.NBT.TAG_LIST)) {
			NBTTagList extractionList = dankNull.getTagCompound().getTagList(TAG_PLACEMENT_MODES, Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < extractionList.tagCount(); i++) {
				NBTTagCompound tempNBT = extractionList.getCompoundTagAt(i);
				modes.put(new ItemStack(tempNBT.getCompoundTag("Stack")), SlotExtractionMode.values()[tempNBT.getInteger("Mode")]);
			}
		}
		return modes;
	}

	public static void setPlacementModes(ItemStack dankNull, Map<ItemStack, SlotExtractionMode> modes) {
		if (modes.isEmpty()) {
			return;
		}
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound dankNullNBT = dankNull.getTagCompound();
		NBTTagList extractionList = new NBTTagList();
		for (ItemStack stack : modes.keySet()) {
			NBTTagCompound tempNBT = new NBTTagCompound();
			if (stack.getCount() != 1) {
				stack.setCount(1);
			}
			tempNBT.setTag("Stack", stack.serializeNBT());
			tempNBT.setInteger("Mode", modes.get(stack).ordinal());
			extractionList.appendTag(tempNBT);
		}
		dankNullNBT.setTag(TAG_PLACEMENT_MODES, extractionList);
	}

	public static SlotExtractionMode getPlacementModeForStack(ItemStack dankNull, ItemStack stack) {
		Map<ItemStack, SlotExtractionMode> modes = getPlacementModes(dankNull);
		if (!modes.isEmpty()) {
			for (ItemStack currentStack : modes.keySet()) {
				if (areStacksEqual(stack, currentStack)) {
					return modes.get(currentStack);
				}
			}
		}
		return SlotExtractionMode.KEEP_1;
	}

	public static void setPlacementModeForStack(ItemStack dankNull, ItemStack stack, SlotExtractionMode mode) {
		if (!dankNull.hasTagCompound()) {
			dankNull.setTagCompound(new NBTTagCompound());
		}
		boolean alreadyAdded = false;
		Map<ItemStack, SlotExtractionMode> currentModes = getPlacementModes(dankNull);
		for (ItemStack currentStack : currentModes.keySet()) {
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

	public static void cyclePlacementMode(ItemStack dankNull, ItemStack stack) {
		SlotExtractionMode current = getPlacementModeForStack(dankNull, stack);
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
	public static void renderHUD(Minecraft mc, ScaledResolution scaledRes) {
		if (!mc.playerController.shouldDrawHUD() && !mc.player.capabilities.isCreativeMode) {
			return;
		}
		ItemStack currentItem = mc.player.inventory.getCurrentItem();
		if (currentItem.isEmpty() || currentItem.getItem() != ModItems.DANK_NULL) {
			currentItem = mc.player.getHeldItemOffhand();
		}
		if (!currentItem.isEmpty() && currentItem.getItem() == ModItems.DANK_NULL) {
			ItemStack selectedStack = DankNullUtils.getSelectedStack(DankNullUtils.getInventoryFromHeld(mc.player));
			if (!selectedStack.isEmpty()) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen0.png"));
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GuiUtils.drawTexturedModalRect(scaledRes.getScaledWidth() - 106, scaledRes.getScaledHeight() - 45, 0, 210, 106, 45, 0);
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				mc.fontRenderer.drawStringWithShadow(currentItem.getDisplayName(), ((scaledRes.getScaledWidth() * 2) - 212) + 55, (scaledRes.getScaledHeight() * 2) - 83, DankNullUtils.getColor(currentItem.getItemDamage(), true));
				String selectedStackName = selectedStack.getDisplayName();
				int itemNameWidth = mc.fontRenderer.getStringWidth(selectedStackName);
				if (itemNameWidth >= 88) {
					selectedStackName = selectedStackName.substring(0, 14).trim() + "...";
				}
				SlotExtractionMode placementMode = DankNullUtils.getPlacementModeForStack(currentItem, selectedStack);
				mc.fontRenderer.drawStringWithShadow(translate("dn.selected_item.desc") + ": " + selectedStackName, ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() * 2) - 72, 16777215);
				mc.fontRenderer.drawStringWithShadow(translate("dn.count.desc") + ": " + (isCreativeDankNull(currentItem) ? "Infinite" : selectedStack.getCount()), ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() * 2) - 61, 16777215);
				mc.fontRenderer.drawStringWithShadow(translate("dn.place.desc") + ": " + placementMode.getTooltip().replace(DankNullUtils.translate("dn.extract.desc").toLowerCase(Locale.ENGLISH), DankNullUtils.translate("dn.place.desc").toLowerCase(Locale.ENGLISH)).replace(DankNullUtils.translate("dn.extract.desc"), DankNullUtils.translate("dn.place.desc")), ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() * 2) - 50, 16777215);
				mc.fontRenderer.drawStringWithShadow(translate("dn.extract.desc") + ": " + getExtractionModeForStack(currentItem, selectedStack).getTooltip(), ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() * 2) - 40, 16777215);

				String keyBind = ModKeyBindings.getOpenDankNullKeyBind().getDisplayName();
				mc.fontRenderer.drawStringWithShadow(keyBind.equalsIgnoreCase("none") ? translate("dn.no_open_keybind.desc") : translate("dn.open_with.desc") + " " + keyBind, ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() * 2) - 29, 16777215);
				String oreDictMode = translate("dn.ore_dictionary.desc") + ": " + (getOreDictModeForStack(currentItem, selectedStack) ? translate("dn.enabled.desc") : translate("dn.disabled.desc"));
				if (!isItemOreDicted(selectedStack)) {
					oreDictMode = translate("dn.not_oredicted.desc");
				}
				mc.fontRenderer.drawStringWithShadow(oreDictMode, ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() * 2) - 18, 16777215);
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				RenderUtils.getRenderItem().renderItemAndEffectIntoGUI(currentItem, (scaledRes.getScaledWidth() - 106) + 5, scaledRes.getScaledHeight() - 20);
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

	public static void emptyDankNullDock(@Nonnull TileDankNullDock dock) {
		if (dock != null && dock.getWorld() != null) {
			dock.setInventory(null);
		}
	}

	public static String translate(String key) {
		return I18n.translateToLocal(key);
	}

	public static boolean isItemOreDicted(ItemStack stack) {
		return OreDictionary.getOreIDs(stack).length > 0;
	}

	public static ItemStack convertToOreDictedStack(ItemStack toBeConverted, ItemStack toConvertTo) {
		if (!isItemOreDicted(toBeConverted) || !isItemOreDicted(toConvertTo) || Options.disableOreDictMode) {
			return ItemStack.EMPTY;
		}
		ItemStack newStack = toConvertTo.copy();
		newStack.setCount(toBeConverted.getCount());
		return newStack;
	}

	@SideOnly(Side.SERVER)
	public static void sendConfigsToClient(EntityPlayerMP player) {
		WeakHashMapSerializable<String, Object> map = new WeakHashMapSerializable<String, Object>();
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

		SlotExtractionMode(int numberToKeep, String message) {
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