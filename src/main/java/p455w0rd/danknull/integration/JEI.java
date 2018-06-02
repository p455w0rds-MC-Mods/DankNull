package p455w0rd.danknull.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import mezz.jei.JustEnoughItems;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.config.SessionData;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModIntegration.Mods;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.integration.jei.DankNullUpgradeWrapper;
import p455w0rd.danknull.integration.jei.PacketVanllaRecipeTransfer;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;
import p455w0rd.danknull.util.DankNullUtils;

/**
 * @author p455w0rd
 *
 */
@JEIPlugin
public class JEI implements IModPlugin {

	public static IIngredientBlacklist blacklist;

	@Override
	public void register(@Nonnull IModRegistry registry) {
		blacklist = registry.getJeiHelpers().getIngredientBlacklist();

		//blacklistItem(new ItemStack(ModItems.DANK_NULL_HOLDER, 1, OreDictionary.WILDCARD_VALUE));

		List<ItemStack> dankNulls = new ArrayList<ItemStack>();
		dankNulls.addAll(Arrays.asList(new ItemStack(ModItems.DANK_NULL, 1, 0), new ItemStack(ModItems.DANK_NULL, 1, 1), new ItemStack(ModItems.DANK_NULL, 1, 2), new ItemStack(ModItems.DANK_NULL, 1, 3), new ItemStack(ModItems.DANK_NULL, 1, 4), new ItemStack(ModItems.DANK_NULL, 1, 5)));
		registry.addIngredientInfo(dankNulls, ItemStack.class, "jei.danknull.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 0), ItemStack.class, "jei.danknull.desc0");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 1), ItemStack.class, "jei.danknull.desc1");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 2), ItemStack.class, "jei.danknull.desc2");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 3), ItemStack.class, "jei.danknull.desc3");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 4), ItemStack.class, "jei.danknull.desc4");
		registry.addIngredientInfo(new ItemStack(ModItems.DANK_NULL, 1, 5), ItemStack.class, "jei.danknull.desc5");

		registry.addIngredientInfo(new ItemStack(ModBlocks.DANKNULL_DOCK), ItemStack.class, "jei.danknull_dock.desc");

		registry.handleRecipes(RecipeDankNullUpgrade.class, recipe -> new DankNullUpgradeWrapper(registry.getJeiHelpers(), recipe), VanillaRecipeCategoryUid.CRAFTING);
		// replace vanilla crafting to support extracting from /dank/null
		/*
		if (registry.getRecipeTransferRegistry() instanceof RecipeTransferRegistry) {
			RecipeTransferRegistry tempRegistry = (RecipeTransferRegistry) registry.getRecipeTransferRegistry();
			Table<Class, String, IRecipeTransferHandler> tempTransferHandlers = ReflectionHelper.getPrivateValue(RecipeTransferRegistry.class, tempRegistry, "recipeTransferHandlers");
			StackHelper stackHelper = ReflectionHelper.getPrivateValue(RecipeTransferRegistry.class, tempRegistry, "stackHelper");
			IRecipeTransferHandlerHelper handlerHelper = ReflectionHelper.getPrivateValue(RecipeTransferRegistry.class, tempRegistry, "handlerHelper");
			IRecipeTransferHandler recipeTransferHandler = new VanillaRecipeTransferHandler(stackHelper, handlerHelper);
			tempTransferHandlers.put(ContainerWorkbench.class, VanillaRecipeCategoryUid.CRAFTING, recipeTransferHandler);
			ReflectionHelper.setPrivateValue(RecipeTransferRegistry.class, tempRegistry, tempTransferHandlers, "recipeTransferHandlers");
			FMLEventChannel channel = ReflectionHelper.getPrivateValue(mezz.jei.startup.ProxyCommon.class, JustEnoughItems.getProxy(), "channel");
			EventBus bus = ReflectionHelper.getPrivateValue(FMLEventChannel.class, channel, "eventBus");
			Map<Object, ModContainer> listeners = ReflectionHelper.getPrivateValue(EventBus.class, bus, "listenerOwners");
			for (Object packetHandler : listeners.keySet()) {
				if (packetHandler instanceof mezz.jei.network.PacketHandler) {
					mezz.jei.network.PacketHandler jeiHandler = (mezz.jei.network.PacketHandler) packetHandler;
					EnumMap<PacketIdServer, IPacketJeiHandler> handlers = ReflectionHelper.getPrivateValue(mezz.jei.network.PacketHandler.class, jeiHandler, "serverHandlers");
					handlers.put(PacketIdServer.RECIPE_TRANSFER, PacketVanllaRecipeTransfer::readPacketData);
					ReflectionHelper.setPrivateValue(mezz.jei.network.PacketHandler.class, jeiHandler, handlers, "serverHandlers");
				}
			}
		}
		 */
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime runtime) {
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry registry) {
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
	}

	public static void blacklistItem(ItemStack stack) {
		if (Mods.JEI.isLoaded() && blacklist != null && !isItemBlacklisted(stack)) {
			blacklist.addIngredientToBlacklist(stack);
		}
	}

	public static boolean isItemBlacklisted(ItemStack stack) {
		if (Mods.JEI.isLoaded()) {
			return blacklist.isIngredientBlacklisted(stack);
		}
		return false;
	}

	public static void whitelistItem(ItemStack stack) {
		if (Mods.JEI.isLoaded() && isItemBlacklisted(stack)) {
			blacklist.removeIngredientFromBlacklist(stack);
		}
	}

	public static void handleItemBlacklisting(ItemStack stack, boolean shouldBlacklist) {
		if (shouldBlacklist) {
			if (!isItemBlacklisted(stack)) {
				blacklistItem(stack);
			}
			return;
		}
		if (isItemBlacklisted(stack)) {
			whitelistItem(stack);
		}
	}

	public static class VanillaRecipeTransferHandler implements IRecipeTransferHandler<ContainerWorkbench> {

		private final StackHelper stackHelper;
		private final IRecipeTransferHandlerHelper handlerHelper;

		public VanillaRecipeTransferHandler(StackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper) {
			this.stackHelper = stackHelper;
			this.handlerHelper = handlerHelper;
		}

		@Override
		public Class<ContainerWorkbench> getContainerClass() {
			return ContainerWorkbench.class;
		}

		@Override
		public IRecipeTransferError transferRecipe(ContainerWorkbench container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
			if (!SessionData.isJeiOnServer()) {
				String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server");
				return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
			}

			List<ItemStack> dankNullStacks = Lists.newArrayList();

			Map<Integer, Slot> inventorySlots = new HashMap<>();
			for (int i = 10; i < 46; i++) {
				Slot slot = container.getSlot(i);
				inventorySlots.put(slot.slotNumber, slot);
				if (slot.getHasStack() && DankNullUtils.isDankNull(slot.getStack())) {
					dankNullStacks.add(slot.getStack());
				}
			}

			Map<Integer, Slot> craftingSlots = new HashMap<>();
			for (int i = 1; i < 10; i++) {
				Slot slot = container.getSlot(i);
				craftingSlots.put(slot.slotNumber, slot);
			}

			int inputCount = 0;
			IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
			for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
					inputCount++;
				}
			}

			if (inputCount > craftingSlots.size()) {
				Log.get().error("Error transferring recipe for container {}", container.getClass());
				return handlerHelper.createInternalError();
			}

			Map<Integer, ItemStack> availableItemStacks = new HashMap<>();
			int filledCraftSlotCount = 0;
			int emptySlotCount = 0;

			for (Slot slot : craftingSlots.values()) {
				final ItemStack stack = slot.getStack();
				if (!stack.isEmpty()) {
					if (!slot.canTakeStack(player)) {
						Log.get().error("Error transferring recipe for container {}. Player can't move item out of Crafting Slot number {}", container.getClass(), slot.slotNumber);
						return handlerHelper.createInternalError();
					}
					filledCraftSlotCount++;
					availableItemStacks.put(slot.slotNumber, stack.copy());
				}
			}

			for (Slot slot : inventorySlots.values()) {
				final ItemStack stack = slot.getStack();
				if (!stack.isEmpty()) {
					availableItemStacks.put(slot.slotNumber, stack.copy());
				}
				else {
					emptySlotCount++;
				}
			}

			// check if we have enough inventory space to shuffle items around to their final locations
			if (filledCraftSlotCount - inputCount > emptySlotCount) {
				String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full");
				return handlerHelper.createUserErrorWithTooltip(message);
			}

			StackHelper.MatchingItemsResult matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, itemStackGroup.getGuiIngredients());

			Map<Integer, ItemStack> recipe = Maps.newHashMap();
			for (int i = 1; i < itemStackGroup.getGuiIngredients().size(); i++) {
				int slotNum = (int) itemStackGroup.getGuiIngredients().keySet().toArray()[i];
				recipe.put(slotNum, itemStackGroup.getGuiIngredients().get(slotNum).getDisplayedIngredient());
			}
			int matchingDankNulls = 0;
			for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.getDisplayedIngredient() == null || ingredient.getDisplayedIngredient().isEmpty()) {
					continue;
				}
				for (ItemStack dankNull : dankNullStacks) {
					if (DankNullUtils.isFilteredOreDict(DankNullUtils.getNewDankNullInventory(dankNull), ingredient.getDisplayedIngredient())) {
						matchingDankNulls++;
					}
				}
			}
			boolean foundInDankNull = false;
			if (matchingItemsResult.missingItems.size() > 0 || matchingDankNulls > 0) {
				for (IGuiIngredient<ItemStack> filteredIngredient : itemStackGroup.getGuiIngredients().values()) {
					ItemStack filteredStack = filteredIngredient.getDisplayedIngredient();
					if (filteredStack == null || filteredStack.isEmpty()) {
						continue;
					}
					for (ItemStack dankNull : dankNullStacks) {
						if (DankNullUtils.isFilteredOreDict(DankNullUtils.getNewDankNullInventory(dankNull), filteredStack)) {
							foundInDankNull = true;
						}
					}
				}
				if (!foundInDankNull) {
					String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
					return handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems);
				}
			}

			List<Integer> craftingSlotIndexes = new ArrayList<>(craftingSlots.keySet());
			Collections.sort(craftingSlotIndexes);

			List<Integer> inventorySlotIndexes = new ArrayList<>(inventorySlots.keySet());
			Collections.sort(inventorySlotIndexes);

			// check that the slots exist and can be altered
			for (Map.Entry<Integer, Integer> entry : matchingItemsResult.matchingItems.entrySet()) {
				int craftNumber = entry.getKey();
				int slotNumber = craftingSlotIndexes.get(craftNumber);
				if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) {
					Log.get().error("Slot {} outside of the inventory's size {}", slotNumber, container.inventorySlots.size());
					return handlerHelper.createInternalError();
				}
			}

			if (doTransfer) {
				//PacketVanllaRecipeTransfer packet = new PacketVanllaRecipeTransfer(recipe, craftingSlotIndexes, inventorySlotIndexes, maxTransfer);
				PacketVanllaRecipeTransfer packet = new PacketVanllaRecipeTransfer(recipe, maxTransfer);
				JustEnoughItems.getProxy().sendPacketToServer(packet);
			}

			return null;
		}

	}

	public static final class VanillaRecipeTransferHandlerServer {

		public static void setItems(EntityPlayer player, Map<Integer, ItemStack> slotIdMap, List<Integer> craftingSlots, List<Integer> inventorySlots, boolean maxTransfer) {
			Container container = player.openContainer;

			// grab items from slots
			Map<Integer, ItemStack> slotMap = new HashMap<>(slotIdMap.size());
			Map<ItemStack, Integer> slotMapReverse = new HashMap<>(slotIdMap.size());

			for (Map.Entry<Integer, ItemStack> entry : slotIdMap.entrySet()) {
				Slot slot = container.getSlot(entry.getKey());
				final ItemStack slotStack = slot.getStack();
				if (slotStack.isEmpty()) {
					continue;
				}
				ItemStack stack = slotStack.copy();
				stack.setCount(1);

				slotMap.put(entry.getKey(), stack);
				slotMapReverse.put(stack, entry.getKey());
			}

			int maxRemovedSets = maxTransfer ? 64 : 1;
			for (Map.Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
				ItemStack stack = entry.getValue();
				if (stack.isStackable()) {
					Integer craftNumber = entry.getKey();
					Integer slotNumber = craftingSlots.get(craftNumber);
					Slot craftSlot = container.getSlot(slotNumber);
					int maxStackSize = Math.min(craftSlot.getItemStackLimit(stack), stack.getMaxStackSize());
					maxRemovedSets = Math.min(maxRemovedSets, maxStackSize);
				}
				else {
					maxRemovedSets = 1;
				}
			}

			boolean needsDankNull = false;
			if (slotMap.isEmpty()) {
				List<ItemStack> dankNulls = DankNullUtils.getAllDankNulls(player);
				for (ItemStack dankNull : dankNulls) {
					needsDankNull = true;
					maxRemovedSets++;
				}
			}

			if (maxRemovedSets <= 0) {
				return;
			}

			// remove required recipe items
			/*
			int removedSets = removeSetsFromInventory(container, slotIdMap.values(), craftingSlots, inventorySlots, maxRemovedSets);

			if (removedSets == 0) {
				return;
			}
			*/
			if (!removeSetsFromInventory(container, slotIdMap.values(), craftingSlots, inventorySlots)) {
				return;
			}

			// clear the crafting grid
			List<ItemStack> clearedCraftingItems = new ArrayList<>();
			for (Integer craftingSlotNumber : craftingSlots) {
				Slot craftingSlot = container.getSlot(craftingSlotNumber);
				if (craftingSlot.getHasStack()) {
					ItemStack craftingItem = craftingSlot.decrStackSize(Integer.MAX_VALUE);
					clearedCraftingItems.add(craftingItem);
				}
			}

			// put items into the crafting grid
			for (Map.Entry<Integer, ItemStack> entry : slotIdMap.entrySet()) {
				Integer craftNumber = entry.getKey() - 1;
				Integer slotNumber = craftingSlots.get(craftNumber);
				Slot slot = container.getSlot(slotNumber);

				ItemStack stack = entry.getValue();
				if (stack.isEmpty()) {
					continue;
				}
				stack.setCount(stack.getCount() * 1);
				if (slot.isItemValid(stack)) {
					slot.putStack(stack);
				}
				else {
					clearedCraftingItems.add(stack);
				}
			}

			if (needsDankNull) {

			}

			// put cleared items back into the inventory
			for (ItemStack oldCraftingItem : clearedCraftingItems) {
				int added = addStack(container, inventorySlots, oldCraftingItem);
				if (added < oldCraftingItem.getCount()) {
					if (!player.inventory.addItemStackToInventory(oldCraftingItem)) {
						player.dropItem(oldCraftingItem, false);
					}
				}
			}
			if (container instanceof ContainerDankNull) {
				((ContainerDankNull) container).sync();
			}
		}

		public static void setItems(EntityPlayer player, Map<Integer, ItemStack> recipe, boolean maxTransfer) {
			Container container = player.openContainer;

			// Map to tell which recipe slots have been filled
			Map<Integer, ItemStack> recipeSlotsStatus = Maps.newHashMap();
			// Applicable slots with needed items in inventory
			Map<Integer, ItemStack> inventorySlotsToUse = Maps.newHashMap();
			// Applicable slots with /dank/nulls containing needed items
			Map<Integer, ItemStack> dankNulls = Maps.newHashMap();
			// Crafting grid slot numbers
			List<Integer> craftingSlotNumbers = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
			// Inventory slot numbers
			List<Integer> inventorySlotNumbers = Lists.newArrayList();
			for (int i = 10; i < 46; i++) {
				inventorySlotNumbers.add(i);
			}

			// clear the crafting grid
			List<ItemStack> clearedCraftingItems = new ArrayList<>();
			for (Integer craftingSlotNumber : craftingSlotNumbers) {
				Slot craftingSlot = container.getSlot(craftingSlotNumber);
				if (craftingSlot.getHasStack()) {
					ItemStack craftingItem = craftingSlot.getStack().copy();
					clearedCraftingItems.add(craftingItem);
					craftingSlot.putStack(ItemStack.EMPTY);
				}
			}

			// put cleared items back into the inventory
			for (ItemStack oldCraftingItem : clearedCraftingItems) {
				//int added = addStack(container, , oldCraftingItem);
				//if (added < oldCraftingItem.getCount()) {
				if (!player.inventory.addItemStackToInventory(oldCraftingItem)) {
					player.dropItem(oldCraftingItem, false);
				}
				//}
			}

			// grab items from inventory
			for (int i = 0; i < inventorySlotNumbers.size(); i++) {
				Slot slot = container.getSlot(inventorySlotNumbers.get(i));
				if (slot.getHasStack()) {
					for (Map.Entry<Integer, ItemStack> recipeItem : recipe.entrySet()) {
						if (!recipeSlotsStatus.containsKey(recipeItem.getKey())) {
							if (recipeItem.getValue().isEmpty()) {
								recipeSlotsStatus.put(recipeItem.getKey(), ItemStack.EMPTY);
								continue;
							}
							if (ItemStack.areItemsEqual(slot.getStack(), recipeItem.getValue()) && ItemStack.areItemStackTagsEqual(slot.getStack(), recipeItem.getValue())) {
								recipeSlotsStatus.put(recipeItem.getKey(), recipeItem.getValue());
								slot.getStack().shrink(recipeItem.getValue().getCount());
							}
						}
					}
				}
			}

			boolean recipeIsFulfilled = true;
			for (Map.Entry<Integer, ItemStack> recipeItem : recipe.entrySet()) {
				if (!recipeSlotsStatus.containsKey(recipeItem.getKey()) || !(ItemStack.areItemsEqual(recipeSlotsStatus.get(recipeItem.getKey()), recipeItem.getValue()) && ItemStack.areItemStackTagsEqual(recipeSlotsStatus.get(recipeItem.getKey()), recipeItem.getValue()))) {
					recipeIsFulfilled = false;
				}
			}

			// recipe not fulfilled yet, so check for applicable /dank/nulls
			if (!recipeIsFulfilled) {
				for (int i = 0; i < inventorySlotNumbers.size(); i++) {
					Slot slot = container.getSlot(inventorySlotNumbers.get(i));
				}
			}
			/*
			for (Map.Entry<Integer, ItemStack> entry : recipe.entrySet()) {
				Slot slot = container.getSlot(entry.getKey());
				final ItemStack slotStack = slot.getStack();
				if (slotStack.isEmpty()) {
					continue;
				}
				ItemStack stack = slotStack.copy();
				stack.setCount(1);
				//slotMap.put(entry.getKey(), stack);
			}

			int maxRemovedSets = maxTransfer ? 64 : 1;

			for (Map.Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
				ItemStack stack = entry.getValue();
				if (stack.isStackable()) {
					Integer craftNumber = entry.getKey();
					Integer slotNumber = craftingSlots.get(craftNumber);
					Slot craftSlot = container.getSlot(slotNumber);
					int maxStackSize = Math.min(craftSlot.getItemStackLimit(stack), stack.getMaxStackSize());
					maxRemovedSets = Math.min(maxRemovedSets, maxStackSize);
				}
				else {
					maxRemovedSets = 1;
				}
			}

			boolean needsDankNull = false;
			if (slotMap.isEmpty()) {
				List<ItemStack> dankNulls = DankNullUtils.getAllDankNulls(player);
				for (ItemStack dankNull : dankNulls) {
					needsDankNull = true;
					maxRemovedSets++;
				}
			}

			if (maxRemovedSets <= 0) {
				return;
			}

			if (!removeSetsFromInventory(container, slotIdMap.values(), craftingSlots, inventorySlots)) {
				return;
			}
			*/

			// put items into the crafting grid
			for (Map.Entry<Integer, ItemStack> entry : recipe.entrySet()) {
				Integer craftNumber = entry.getKey() - 1;
				Integer slotNumber = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9).get(craftNumber);
				Slot slot = container.getSlot(slotNumber);

				ItemStack stack = entry.getValue();
				if (stack.isEmpty()) {
					continue;
				}
				stack.setCount(stack.getCount() * 1);
				if (slot.isItemValid(stack)) {
					slot.putStack(stack);
				}
				else {
					clearedCraftingItems.add(stack);
				}
			}

			container.detectAndSendChanges();
		}

		private static int removeSetsFromInventory(Container container, Collection<ItemStack> required, List<Integer> craftingSlots, List<Integer> inventorySlots, final int maxRemovedSets) {
			int removedSets = 0;
			while (removedSets < maxRemovedSets) {
				if (removeSetsFromInventory(container, required, craftingSlots, inventorySlots)) {
					removedSets++;
				}
			}
			return removedSets;
		}

		private static boolean removeSetsFromInventory(Container container, Iterable<ItemStack> required, List<Integer> craftingSlots, List<Integer> inventorySlots) {
			final Map<Slot, ItemStack> originalSlotContents = new HashMap<>();

			for (ItemStack matchingStack : required) {
				if (matchingStack.isEmpty()) {
					continue;
				}
				final ItemStack requiredStack = matchingStack.copy();
				//boolean isDankNull = false;
				if (requiredStack.isEmpty()) {
					continue;
				}
				InventoryDankNull dankNullInv = null;
				Slot slot = getSlotWithStack(container, requiredStack, craftingSlots, inventorySlots);
				/*
				for (int slotNum : inventorySlots) {
					Slot slot1 = container.getSlot(slotNum);
					if (slot1 != null && slot1.getHasStack()) {
						dankNullInv = DankNullUtils.getNewDankNullInventory(slot1.getStack());
						if (DankNullUtils.isDankNull(slot1.getStack()) && DankNullUtils.isFilteredOreDict(dankNullInv, requiredStack)) {
							//DankNullUtils.decrDankNullStackSize(DankNullUtils.getNewDankNullInventory(slot1.getStack()), requiredStack, requiredStack.getCount());
							isDankNull = true;
						}
					}
				}
				*/
				boolean tryDankNull = false;
				if ((slot == null || slot.getStack().isEmpty())) {
					// abort! put removed items back where they came from
					for (Map.Entry<Slot, ItemStack> slotEntry : originalSlotContents.entrySet()) {
						ItemStack stack = slotEntry.getValue();
						slotEntry.getKey().putStack(stack);
					}
					tryDankNull = true;
					//continue;
				}

				if (!tryDankNull) {
					if (!originalSlotContents.containsKey(slot)) {
						originalSlotContents.put(slot, slot.getStack().copy());
					}
					ItemStack removed = slot.decrStackSize(requiredStack.getCount());
					requiredStack.shrink(removed.getCount());
				}
				else {
					for (int slotNum : inventorySlots) {
						Slot slot1 = container.getSlot(slotNum);
						if (slot1 != null && slot1.getHasStack()) {
							if (DankNullUtils.isDankNull(slot1.getStack()) && DankNullUtils.isFilteredOreDict(DankNullUtils.getNewDankNullInventory(slot1.getStack()), requiredStack)) {
								DankNullUtils.decrDankNullStackSize(DankNullUtils.getNewDankNullInventory(slot1.getStack()), requiredStack, requiredStack.getCount());
							}
						}
					}
				}
			}

			return true;
		}

		@Nullable
		private static Slot getSlotWithStack(Container container, ItemStack stack, List<Integer> craftingSlots, List<Integer> inventorySlots) {
			Slot slot = getSlotWithStack(container, craftingSlots, stack);
			if (slot == null) {
				slot = getSlotWithStack(container, inventorySlots, stack);
			}

			return slot;
		}

		private static int addStack(Container container, Collection<Integer> slotIndexes, ItemStack stack) {
			int added = 0;
			// Add to existing stacks first
			for (final Integer slotIndex : slotIndexes) {
				if (slotIndex >= 0 && slotIndex < container.inventorySlots.size()) {
					final Slot slot = container.getSlot(slotIndex);
					final ItemStack inventoryStack = slot.getStack();
					// Check that the slot's contents are stackable with this stack
					if (!inventoryStack.isEmpty() && (inventoryStack.isStackable() && inventoryStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inventoryStack, stack) || (DankNullUtils.isDankNull(inventoryStack) && DankNullUtils.isFiltered(DankNullUtils.getNewDankNullInventory(inventoryStack), stack)))) {
						boolean isDankNull = false;
						if (DankNullUtils.isDankNull(inventoryStack)) {
							isDankNull = true;
						}
						final int remain = stack.getCount() - added;
						final int maxStackSize = Math.min(slot.getItemStackLimit(isDankNull ? stack : inventoryStack), isDankNull ? stack.getMaxStackSize() : inventoryStack.getMaxStackSize());
						final int space = maxStackSize - (isDankNull ? stack.getCount() : inventoryStack.getCount());
						if (space > 0) {

							// Enough space
							if (space >= remain) {
								if (isDankNull) {
									DankNullUtils.decrDankNullStackSize(DankNullUtils.getNewDankNullInventory(inventoryStack), stack, remain);
									//DankNullUtils.addFilteredStackToDankNull(DankNullUtils.getNewDankNullInventory(inventoryStack), stack);
									stack.grow(remain);
								}
								else {
									inventoryStack.grow(remain);
								}
								return stack.getCount();
							}

							// Not enough space
							if (!isDankNull) {
								inventoryStack.setCount(inventoryStack.getMaxStackSize());
							}
							else {
								//stack.setCount(size);
							}
							added += space;
						}
					}
				}
			}

			if (added >= stack.getCount()) {
				return added;
			}

			for (final Integer slotIndex : slotIndexes) {
				if (slotIndex >= 0 && slotIndex < container.inventorySlots.size()) {
					final Slot slot = container.getSlot(slotIndex);
					final ItemStack inventoryStack = slot.getStack();
					if (inventoryStack.isEmpty()) {
						ItemStack stackToAdd = stack.copy();
						stackToAdd.setCount(stack.getCount() - added);
						slot.putStack(stackToAdd);
						return stack.getCount();
					}
				}
			}

			return added;
		}

		@Nullable
		private static Slot getSlotWithStack(Container container, Iterable<Integer> slotNumbers, ItemStack itemStack) {
			for (Integer slotNumber : slotNumbers) {
				if (slotNumber >= 0 && slotNumber < container.inventorySlots.size()) {
					Slot slot = container.getSlot(slotNumber);
					ItemStack slotStack = slot.getStack();
					if (ItemStack.areItemsEqual(itemStack, slotStack) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
						return slot;
					}
				}
			}
			return null;
		}
	}

}