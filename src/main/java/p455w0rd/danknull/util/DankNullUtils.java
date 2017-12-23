package p455w0rd.danknull.util;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import p455w0rd.danknull.DankNull;
import p455w0rd.danknull.init.ModEvents;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.init.ModLogger;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.DankNullItemHandler;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.PacketSetSelectedItem;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;
import p455w0rdslib.util.GuiUtils;
import p455w0rdslib.util.ItemUtils;
import p455w0rdslib.util.RenderUtils;

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
		String message = TextFormatting.YELLOW + "" + TextFormatting.ITALIC + "" + getItemByIndex(inventory, index).getDisplayName() + " Selected";
		//player.sendMessage(new TextComponentString(message));
		if (player.getEntityWorld().isRemote) {
			ModEvents.getInstance().setSelectedMessage(message);
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
				return getInventoryFromStack(dankNull);
			}
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

	public static boolean isFiltered(InventoryDankNull inventory, ItemStack filteredStack) {
		if (inventory != null) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					if (ItemUtils.areItemsEqual(inventory.getStackInSlot(i), filteredStack)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isFilteredOreDict(InventoryDankNull inventory, ItemStack filteredStack) {
		int[] ids = OreDictionary.getOreIDs(filteredStack);
		if (inventory != null && ids.length > 0) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					for (int id : ids) {
						String name = OreDictionary.getOreName(id);
						List<ItemStack> oreList = OreDictionary.getOres(name);
						for (ItemStack ore : oreList) {
							if (OreDictionary.itemMatches(ore, inventory.getStackInSlot(i), false)) {
								return true;
							}
						}
					}
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
		if (isFiltered(inventory, stack)) {
			return getItemByIndex(inventory, getIndexForStack(inventory, stack));
		}
		return ItemStack.EMPTY;
	}

	public static int getIndexForStack(InventoryDankNull inventory, ItemStack filteredStack) {
		if (isFiltered(inventory, filteredStack)) {
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

	public static InventoryDankNull getInventoryFromStack(@Nonnull ItemStack stack) {
		return getNewDankNullInventory(stack);
	}

	public static int getDankNullMaxStackSize(@Nonnull ItemStack itemStackIn) {
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
	}

	public static EnumActionResult placeBlock(@Nonnull IBlockState state, World world, BlockPos pos) {
		return world.setBlockState(pos, state, 2) ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
	}

	public static IRecipe addDankNullUpgradeRecipe(String recipeName, Object... params) {
		ShapedPrimer primer = CraftingHelper.parseShaped(params);
		IRecipe recipe = new RecipeDankNullUpgrade(primer.input).setRegistryName(new ResourceLocation(ModGlobals.MODID, recipeName));
		return recipe;
	}

	public static final String TAG_EXTRACTION_MODES = "ExtractionModes";

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
				if (stack.isItemEqual(currentStack)) {
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
		ItemStack tempStack = stack.copy();
		tempStack.setCount(1);
		Map<ItemStack, SlotExtractionMode> currentModes = getExtractionModes(dankNull);
		for (ItemStack currentStack : currentModes.keySet()) {
			if (tempStack.isItemEqual(currentStack)) {
				currentModes.put(currentStack, mode);
				alreadyAdded = true;
			}
		}
		if (!alreadyAdded) {
			currentModes.put(tempStack, mode);
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
			DankNull.PROXY.getPlayer().sendMessage(new TextComponentString(getExtractionModeForStack(dankNull, stack).getMessage()));
		}
	}

	public static void renderHUD(Minecraft mc, ScaledResolution scaledRes) {
		if (!mc.playerController.shouldDrawHUD() && !mc.player.capabilities.isCreativeMode) {
			return;
		}
		ItemStack currentItem = mc.player.inventory.getCurrentItem();
		if (!currentItem.isEmpty() && currentItem.getItem() == ModItems.DANK_NULL) {
			ItemStack selectedStack = DankNullUtils.getSelectedStack(DankNullUtils.getInventoryFromHeld(mc.player));
			if (!selectedStack.isEmpty()) {
				Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen0.png"));
				GuiUtils.drawTexturedModalRect(scaledRes.getScaledWidth() - 106, scaledRes.getScaledHeight() - 24, 0, 232, 106, 24, 0);
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				mc.fontRenderer.drawStringWithShadow(currentItem.getDisplayName(), ((scaledRes.getScaledWidth() * 2) - 212) + 55, (scaledRes.getScaledHeight() - 20) * 2, DankNullUtils.getColor(currentItem.getItemDamage(), false));
				String selectedStackName = selectedStack.getDisplayName();
				int itemNameWidth = mc.fontRenderer.getStringWidth(selectedStackName);
				if (itemNameWidth >= 88) {
					selectedStackName = selectedStackName.substring(0, 14).trim() + "...";
				}
				mc.fontRenderer.drawStringWithShadow("Selected Item: " + selectedStackName, ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() - 14) * 2, 16777215);
				mc.fontRenderer.drawStringWithShadow("Count: " + selectedStack.getCount(), ((scaledRes.getScaledWidth() * 2) - 212) + 45, (scaledRes.getScaledHeight() - 9) * 2, 16777215);
				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				RenderUtils.getRenderItem().renderItemAndEffectIntoGUI(currentItem, (scaledRes.getScaledWidth() - 106) + 5, scaledRes.getScaledHeight() - 20);
				GlStateManager.popMatrix();
			}
		}
	}

	public static enum SlotExtractionMode {

			KEEP_ALL(Integer.MAX_VALUE, "not extract"), KEEP_1(1, "extract all but one"),
			KEEP_16(16, "extract all but 16"), KEEP_64(64, "extract all but 64"), KEEP_NONE(0, "extract all items");

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
			return "Will " + msg + " from this slot";
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return "Do " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

}