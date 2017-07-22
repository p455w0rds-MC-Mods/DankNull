package p455w0rd.danknull.util;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.DankNullItemHandler;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.PacketSetSelectedItem;
import p455w0rdslib.util.ItemUtils;

/**
 * @author p455w0rd
 *
 */
public class DankNullUtils {

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
					if (isFiltered(getNewDankNullInventory(itemStack), stack) != null) {
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

	public static void setSelectedStackIndex(InventoryDankNull inventory, int index) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			setSelectedStackIndex(inventory, index, true);
		}
	}

	public static void setSelectedStackIndex(InventoryDankNull inventory, int index, boolean sync) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			ItemStack dankNull = inventory.getDankNull();
			if (!dankNull.hasTagCompound()) {
				dankNull.setTagCompound(new NBTTagCompound());
			}
			//NBTTagCompound capData = getInventory(dankNull).serializeNBT();
			dankNull.getTagCompound().setInteger("selectedIndex", index);
			//getInventory(dankNull).deserializeNBT(capData);
			if (FMLCommonHandler.instance().getSide().isClient()) {
				ModNetworking.INSTANCE.sendToServer(new PacketSetSelectedItem(index));
			}
		}
	}

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
				ModNetworking.INSTANCE.sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
			else {
				newIndex = currentIndex + 1;
				ModNetworking.INSTANCE.sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
			if (player != null) {
				displaySelectedMessage(inventory, player, newIndex);
			}
		}
	}

	public static void displaySelectedMessage(InventoryDankNull inventory, EntityPlayer player, int index) {
		if (!getItemByIndex(inventory, index).isEmpty()) {
			reArrangeStacks(inventory);
		}
		player.sendMessage(new TextComponentString(TextFormatting.BLUE + "" + TextFormatting.ITALIC + "" + getItemByIndex(inventory, index).getDisplayName() + " Selected"));
	}

	public static void setPreviousSelectedStack(InventoryDankNull inventory, EntityPlayer player) {
		int currentIndex = getSelectedStackIndex(inventory);
		int totalSize = getItemCount(inventory);
		int maxIndex = totalSize - 1;
		int newIndex = 0;
		if (totalSize > 1) {
			if (currentIndex == 0) {
				newIndex = maxIndex;
				ModNetworking.INSTANCE.sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
			else {
				newIndex = currentIndex - 1;
				ModNetworking.INSTANCE.sendToServer(new PacketSetSelectedItem(newIndex));
				setSelectedStackIndex(inventory, newIndex);
			}
			if (player != null) {
				displaySelectedMessage(inventory, player, newIndex);
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

	/*
		public static int getItemCount(ContainerDankNull container) {
			List<ItemStack> dankInventory = container.inventoryItemStacks;
			int numItems = 0;
			for (int i = 0; i < dankInventory.size(); i++) {
				if (dankInventory.get(i) != null) {
					++numItems;
				}
			}
			return numItems;
		}
	
		public static NBTTagList getInventoryTagList(ItemStack itemStackIn) {
			if (itemStackIn != null) {
				if ((itemStackIn.hasTagCompound()) && (itemStackIn.getTagCompound().hasKey("danknull-inventory"))) {
					return itemStackIn.getTagCompound().getTagList("danknull-inventory", 10);
				}
			}
			return null;
		}
	
	
			public static void decrStackSize(ItemStack dankNull, int index, int amount) {
				if (dankNull == null) {
					return;
				}
				ItemStack indexedStack = getItemByIndex(dankNull, index);
				int newStackSize = getStackSize(indexedStack) - amount;
				NBTTagCompound nbtTC = indexedStack.getTagCompound();
				NBTTagList tagList = dankNull.getTagCompound().getTagList("danknull-inventory", 10);
				if (newStackSize >= 1L) {
					nbtTC.setLong(InventoryDankNull.TAG_COUNT, newStackSize);
				}
				else {
					tagList.removeTag(index);
					reArrangeStacks(dankNull);
				}
			}
		**/
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
			if (selectedStack != null) {
				return selectedStack.getCount();
			}
		}
		return 0;
	}

	public static InventoryDankNull getInventoryFromHeld(EntityPlayer player) {
		if (player != null && !player.getHeldItemMainhand().isEmpty() && DankNullUtils.isDankNull(player.getHeldItemMainhand())) {
			return getInventoryFromStack(player.getHeldItemMainhand());
		}
		return null;
	}

	public static ItemStack getSelectedStack(InventoryDankNull inventory) {
		if (inventory != null && !inventory.getDankNull().isEmpty()) {
			ItemStack dankNull = inventory.getDankNull();
			//if ((itemStackIn.hasTagCompound()) && (itemStackIn.getTagCompound().hasKey("danknull-inventory"))) {
			if (!dankNull.hasTagCompound()) {
				setSelectedStackIndex(inventory, isEmpty(inventory) ? 1 : 0);
			}

			//if (dankNull.getTagCompound().hasKey("danknull-inventory")) {
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

	public static ItemStack isFiltered(InventoryDankNull inventory, ItemStack filteredStack) {
		if (inventory != null) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					if (ItemUtils.areItemsEqual(inventory.getStackInSlot(i), filteredStack)) {
						return filteredStack;
					}
				}
			}
		}
		return null;
	}

	public static DankNullItemHandler getHandler(ItemStack dankNull) {
		if (hasDankNullHandler(dankNull)) {
			return (DankNullItemHandler) dankNull.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		}
		return null;
	}

	public static boolean hasDankNullHandler(ItemStack dankNull) {
		return dankNull.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	/*
		public static boolean addFilteredStackToDankNull(ItemStack itemStackIn, ItemStack newStack) {
			if (itemStackIn != null || newStack == null) {
				ItemStack filteredStack = isFiltered(itemStackIn, newStack);
				if (filteredStack != null) {
					int maxStackSize = getDankNullMaxStackSize(itemStackIn);
					int currentFilteredStackSize = getStackSize(filteredStack);
					int itemToAddStackSize = newStack.stackSize;
					if (currentFilteredStackSize + itemToAddStackSize > maxStackSize) {
						setDankNullStackSize(filteredStack, maxStackSize);
					}
					else {
						setDankNullStackSize(filteredStack, currentFilteredStackSize + itemToAddStackSize);
					}
					return true;
				}
			}
			return false;
		}
	*/
	public static boolean addFilteredStackToDankNull(InventoryDankNull inventory, ItemStack filteredStack) {
		if (getIndexForStack(inventory, filteredStack) >= 0) {
			ItemStack currentStack = getFilteredStack(inventory, filteredStack);
			currentStack.grow(filteredStack.getCount());
			if (currentStack.getCount() > DankNullUtils.getDankNullMaxStackSize(inventory)) {
				currentStack.setCount(DankNullUtils.getDankNullMaxStackSize(inventory));
			}
			inventory.setInventorySlotContents(getIndexForStack(inventory, filteredStack), currentStack);
			//getInventory(dankNull).serializeNBT();
			return true;
		}
		return false;
	}

	public static ItemStack getFilteredStack(InventoryDankNull inventory, ItemStack stack) {
		if (!isFiltered(inventory, stack).isEmpty()) {
			return getItemByIndex(inventory, getIndexForStack(inventory, stack));
		}
		return ItemStack.EMPTY;
	}

	public static int getIndexForStack(InventoryDankNull inventory, ItemStack filteredStack) {
		if (!isFiltered(inventory, filteredStack).isEmpty()) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					if (ItemUtils.areItemsEqual(inventory.getStackInSlot(i), filteredStack)) {
						return i;
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
		if (inventory == null || stack == null) {
			return;
		}
		if (!isFiltered(inventory, stack).isEmpty()) {
			ItemStack currentStack = getFilteredStack(inventory, stack);
			currentStack.setCount(currentStack.getCount() - amount);
			if (currentStack.getCount() <= 0) {
				currentStack = ItemStack.EMPTY;
			}
			inventory.markDirty();
		}
		/*
		int newStackSize = getStackSize(dankNullStack) - amount;
		NBTTagCompound nbtTC = dankNullStack.getTagCompound();
		if (newStackSize >= 1L) {
			nbtTC.setInteger(InventoryDankNull.TAG_COUNT, newStackSize);
		}
		else {
			if (itemStackIn.hasTagCompound() && itemStackIn.getTagCompound().hasKey("danknull-inventory")) {
				NBTTagList tagList = itemStackIn.getTagCompound().getTagList("danknull-inventory", Constants.NBT.TAG_COMPOUND);
				if (tagList != null && tagList.tagCount() > 0) {
					int index = getSelectedStackIndex(itemStackIn);
					if (index != -1 && tagList.tagCount() > index && tagList.get(index) != null) {
						tagList.removeTag(index);
					}
				}
				reArrangeStacks(itemStackIn);
				setSelectedIndexApplicable(itemStackIn);
			}
		}
		return itemStackIn;
		*/
	}

	public static InventoryDankNull getNewDankNullInventory(ItemStack stack) {
		return (stack.getItem() instanceof ItemDankNull) ? new InventoryDankNull(stack) : null;
	}

	public static InventoryDankNull getInventoryFromStack(ItemStack stack) {
		return getNewDankNullInventory(stack);
	}

	public static int getDankNullMaxStackSize(ItemStack itemStackIn) {
		int level = itemStackIn.getItemDamage() + 1;
		if (level == 6) {
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
		//getInventory(dankNull).serializeNBT();
	}
	/*
		public static ItemStack getVanillaStack(ItemStack stack) {
			if (isDankNullStack(stack)) {
				ItemStack newStack = stack.copy();
				if (newStack.hasTagCompound() && newStack.getTagCompound().hasKey(InventoryDankNull.TAG_COUNT)) {
					newStack.getTagCompound().removeTag(InventoryDankNull.TAG_COUNT);
					return newStack;
				}
			}
			return stack == null ? null : stack.copy();
		}
	*/

	public static EnumActionResult placeBlock(@Nonnull IBlockState state, World world, BlockPos pos) {
		return world.setBlockState(pos, state, 2) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}
}