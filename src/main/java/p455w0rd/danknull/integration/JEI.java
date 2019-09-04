package p455w0rd.danknull.integration;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import mezz.jei.JustEnoughItems;
import mezz.jei.api.*;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.*;
import mezz.jei.config.ServerInfo;
import mezz.jei.startup.StackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import p455w0rd.danknull.container.ContainerDankNull;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.integration.jei.DankNullUpgradeWrapper;
import p455w0rd.danknull.integration.jei.PacketVanllaRecipeTransfer;
import p455w0rd.danknull.inventory.PlayerSlot;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.recipes.RecipeDankNullUpgrade;
import p455w0rdslib.LibGlobals.Mods;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("unused")
@JEIPlugin
public class JEI implements IModPlugin {

	public static IIngredientBlacklist blacklist;

	@Override
	public void register(@Nonnull final IModRegistry registry) {
		blacklist = registry.getJeiHelpers().getIngredientBlacklist();

		//blacklistItem(new ItemStack(ModItems.DANK_NULL_HOLDER, 1, OreDictionary.WILDCARD_VALUE));

		final List<ItemStack> dankNulls = new ArrayList<>();
		dankNulls.addAll(Arrays.asList(
				//@formatter:off
				new ItemStack(ModItems.REDSTONE_DANKNULL),
				new ItemStack(ModItems.LAPIS_DANKNULL),
				new ItemStack(ModItems.IRON_DANKNULL),
				new ItemStack(ModItems.GOLD_DANKNULL),
				new ItemStack(ModItems.DIAMOND_DANKNULL),
				new ItemStack(ModItems.EMERALD_DANKNULL))
				//@formatter:on
		);
		registry.addIngredientInfo(dankNulls, VanillaTypes.ITEM, "jei.danknull.desc");
		registry.addIngredientInfo(new ItemStack(ModItems.REDSTONE_DANKNULL), VanillaTypes.ITEM, "jei.danknull.desc0");
		registry.addIngredientInfo(new ItemStack(ModItems.LAPIS_DANKNULL), VanillaTypes.ITEM, "jei.danknull.desc1");
		registry.addIngredientInfo(new ItemStack(ModItems.IRON_DANKNULL), VanillaTypes.ITEM, "jei.danknull.desc2");
		registry.addIngredientInfo(new ItemStack(ModItems.GOLD_DANKNULL), VanillaTypes.ITEM, "jei.danknull.desc3");
		registry.addIngredientInfo(new ItemStack(ModItems.DIAMOND_DANKNULL), VanillaTypes.ITEM, "jei.danknull.desc4");
		registry.addIngredientInfo(new ItemStack(ModItems.EMERALD_DANKNULL), VanillaTypes.ITEM, "jei.danknull.desc5");
		registry.addIngredientInfo(new ItemStack(ModBlocks.DANKNULL_DOCK), VanillaTypes.ITEM, "jei.danknull_dock.desc");
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
	public void onRuntimeAvailable(final IJeiRuntime runtime) {
	}

	@Override
	public void registerIngredients(final IModIngredientRegistration registry) {
	}

	@Override
	public void registerItemSubtypes(final ISubtypeRegistry registry) {
	}

	@Override
	public void registerCategories(final IRecipeCategoryRegistration registry) {
	}

	public static void blacklistItem(final ItemStack stack) {
		if (Mods.JEI.isLoaded() && blacklist != null && !isItemBlacklisted(stack)) {
			blacklist.addIngredientToBlacklist(stack);
		}
	}

	public static boolean isItemBlacklisted(final ItemStack stack) {
		if (Mods.JEI.isLoaded()) {
			return blacklist.isIngredientBlacklisted(stack);
		}
		return false;
	}

	public static void whitelistItem(final ItemStack stack) {
		if (Mods.JEI.isLoaded() && isItemBlacklisted(stack)) {
			blacklist.removeIngredientFromBlacklist(stack);
		}
	}

	public static void handleItemBlacklisting(final ItemStack stack, final boolean shouldBlacklist) {
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

		public VanillaRecipeTransferHandler(final StackHelper stackHelper, final IRecipeTransferHandlerHelper handlerHelper) {
			this.stackHelper = stackHelper;
			this.handlerHelper = handlerHelper;
		}

		@Override
		public Class<ContainerWorkbench> getContainerClass() {
			return ContainerWorkbench.class;
		}

		@Override
		public IRecipeTransferError transferRecipe(final ContainerWorkbench container, final IRecipeLayout recipeLayout, final EntityPlayer player, final boolean maxTransfer, final boolean doTransfer) {
			if (!ServerInfo.isJeiOnServer()) {
				final String tooltipMessage = TextUtils.translate("jei.tooltip.error.recipe.transfer.no.server");
				return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
			}

			final List<ItemStack> dankNullStacks = Lists.newArrayList();

			final Map<Integer, Slot> inventorySlots = new HashMap<>();
			for (int i = 10; i < 46; i++) {
				final Slot slot = container.getSlot(i);
				inventorySlots.put(slot.slotNumber, slot);
				if (slot.getHasStack() && ItemDankNull.isDankNull(slot.getStack())) {
					dankNullStacks.add(slot.getStack());
				}
			}

			final Map<Integer, Slot> craftingSlots = new HashMap<>();
			for (int i = 1; i < 10; i++) {
				final Slot slot = container.getSlot(i);
				craftingSlots.put(slot.slotNumber, slot);
			}

			int inputCount = 0;
			final IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
			for (final IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
					inputCount++;
				}
			}

			if (inputCount > craftingSlots.size()) {
				ModLogger.error(String.format("Error transferring recipe for container {}", container.getClass()));
				return handlerHelper.createInternalError();
			}

			final Map<Integer, ItemStack> availableItemStacks = new HashMap<>();
			int filledCraftSlotCount = 0;
			int emptySlotCount = 0;

			for (final Slot slot : craftingSlots.values()) {
				final ItemStack stack = slot.getStack();
				if (!stack.isEmpty()) {
					if (!slot.canTakeStack(player)) {
						ModLogger.error(String.format("Error transferring recipe for container {}. Player can't move item out of Crafting Slot number {}", container.getClass(), slot.slotNumber));
						return handlerHelper.createInternalError();
					}
					filledCraftSlotCount++;
					availableItemStacks.put(slot.slotNumber, stack.copy());
				}
			}

			for (final Slot slot : inventorySlots.values()) {
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
				final String message = TextUtils.translate("jei.tooltip.error.recipe.transfer.inventory.full");
				return handlerHelper.createUserErrorWithTooltip(message);
			}

			final StackHelper.MatchingItemsResult matchingItemsResult = stackHelper.getMatchingItems(availableItemStacks, itemStackGroup.getGuiIngredients());

			final Map<Integer, ItemStack> recipe = Maps.newHashMap();
			for (int i = 1; i < itemStackGroup.getGuiIngredients().size(); i++) {
				final int slotNum = (int) itemStackGroup.getGuiIngredients().keySet().toArray()[i];
				recipe.put(slotNum, itemStackGroup.getGuiIngredients().get(slotNum).getDisplayedIngredient());
			}
			final int matchingDankNulls = 0;
			for (final IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
				if (ingredient.getDisplayedIngredient() == null || ingredient.getDisplayedIngredient().isEmpty()) {
					continue;
				}
				for (final ItemStack dankNull : dankNullStacks) {
					//					if (DankNullUtils.isFilteredOreDict(DankNullUtils.getNewDankNullInventory(dankNull), ingredient.getDisplayedIngredient())) {
					//						matchingDankNulls++;
					//					}
				}
			}
			final boolean foundInDankNull = false;
			if (matchingItemsResult.missingItems.size() > 0 || matchingDankNulls > 0) {
				for (final IGuiIngredient<ItemStack> filteredIngredient : itemStackGroup.getGuiIngredients().values()) {
					final ItemStack filteredStack = filteredIngredient.getDisplayedIngredient();
					if (filteredStack == null || filteredStack.isEmpty()) {
						continue;
					}
					for (final ItemStack dankNull : dankNullStacks) {
						//						if (DankNullUtils.isFilteredOreDict(DankNullUtils.getNewDankNullInventory(dankNull), filteredStack)) {
						//							foundInDankNull = true;
						//						}
					}
				}
				if (!foundInDankNull) {
					final String message = TextUtils.translate("jei.tooltip.error.recipe.transfer.missing");
					return handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems);
				}
			}

			final List<Integer> craftingSlotIndexes = new ArrayList<>(craftingSlots.keySet());
			Collections.sort(craftingSlotIndexes);

			final List<Integer> inventorySlotIndexes = new ArrayList<>(inventorySlots.keySet());
			Collections.sort(inventorySlotIndexes);

			// check that the slots exist and can be altered
			for (final Map.Entry<Integer, Integer> entry : matchingItemsResult.matchingItems.entrySet()) {
				final int craftNumber = entry.getKey();
				final int slotNumber = craftingSlotIndexes.get(craftNumber);
				if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) {
					ModLogger.error(String.format("Slot {} outside of the inventory's size {}", slotNumber, container.inventorySlots.size()));
					return handlerHelper.createInternalError();
				}
			}

			if (doTransfer) {
				//PacketVanllaRecipeTransfer packet = new PacketVanllaRecipeTransfer(recipe, craftingSlotIndexes, inventorySlotIndexes, maxTransfer);
				final PacketVanllaRecipeTransfer packet = new PacketVanllaRecipeTransfer(recipe, maxTransfer);
				JustEnoughItems.getProxy().sendPacketToServer(packet);
			}

			return null;
		}

	}

	public static final class VanillaRecipeTransferHandlerServer {

		public static void setItems(final EntityPlayer player, final Map<Integer, ItemStack> slotIdMap, final List<Integer> craftingSlots, final List<Integer> inventorySlots, final boolean maxTransfer) {
			final Container container = player.openContainer;

			// grab items from slots
			final Map<Integer, ItemStack> slotMap = new HashMap<>(slotIdMap.size());
			final Map<ItemStack, Integer> slotMapReverse = new HashMap<>(slotIdMap.size());

			for (final Map.Entry<Integer, ItemStack> entry : slotIdMap.entrySet()) {
				final Slot slot = container.getSlot(entry.getKey());
				final ItemStack slotStack = slot.getStack();
				if (slotStack.isEmpty()) {
					continue;
				}
				final ItemStack stack = slotStack.copy();
				stack.setCount(1);

				slotMap.put(entry.getKey(), stack);
				slotMapReverse.put(stack, entry.getKey());
			}

			int maxRemovedSets = maxTransfer ? 64 : 1;
			for (final Map.Entry<Integer, ItemStack> entry : slotMap.entrySet()) {
				final ItemStack stack = entry.getValue();
				if (stack.isStackable()) {
					final Integer craftNumber = entry.getKey();
					final Integer slotNumber = craftingSlots.get(craftNumber);
					final Slot craftSlot = container.getSlot(slotNumber);
					final int maxStackSize = Math.min(craftSlot.getItemStackLimit(stack), stack.getMaxStackSize());
					maxRemovedSets = Math.min(maxRemovedSets, maxStackSize);
				}
				else {
					maxRemovedSets = 1;
				}
			}

			boolean needsDankNull = false;
			if (slotMap.isEmpty()) {
				final List<PlayerSlot> dankNulls = ItemDankNull.getDankNullsForPlayer(player);
				for (final PlayerSlot slot : dankNulls) {
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
			final List<ItemStack> clearedCraftingItems = new ArrayList<>();
			for (final Integer craftingSlotNumber : craftingSlots) {
				final Slot craftingSlot = container.getSlot(craftingSlotNumber);
				if (craftingSlot.getHasStack()) {
					final ItemStack craftingItem = craftingSlot.decrStackSize(Integer.MAX_VALUE);
					clearedCraftingItems.add(craftingItem);
				}
			}

			// put items into the crafting grid
			for (final Map.Entry<Integer, ItemStack> entry : slotIdMap.entrySet()) {
				final Integer craftNumber = entry.getKey() - 1;
				final Integer slotNumber = craftingSlots.get(craftNumber);
				final Slot slot = container.getSlot(slotNumber);

				final ItemStack stack = entry.getValue();
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
			for (final ItemStack oldCraftingItem : clearedCraftingItems) {
				final int added = addStack(container, inventorySlots, oldCraftingItem);
				if (added < oldCraftingItem.getCount()) {
					if (!player.inventory.addItemStackToInventory(oldCraftingItem)) {
						player.dropItem(oldCraftingItem, false);
					}
				}
			}
			if (container instanceof ContainerDankNull) {
				((ContainerDankNull) container).detectAndSendChanges();
			}
		}

		public static void setItems(final EntityPlayer player, final Map<Integer, ItemStack> recipe, final boolean maxTransfer) {
			final Container container = player.openContainer;

			// Map to tell which recipe slots have been filled
			final Map<Integer, ItemStack> recipeSlotsStatus = Maps.newHashMap();
			// Applicable slots with needed items in inventory
			//final Map<Integer, ItemStack> inventorySlotsToUse = Maps.newHashMap();
			// Applicable slots with /dank/nulls containing needed items
			//final Map<Integer, ItemStack> dankNulls = Maps.newHashMap();
			// Crafting grid slot numbers
			final List<Integer> craftingSlotNumbers = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);
			// Inventory slot numbers
			final List<Integer> inventorySlotNumbers = Lists.newArrayList();
			for (int i = 10; i < 46; i++) {
				inventorySlotNumbers.add(i);
			}

			// clear the crafting grid
			final List<ItemStack> clearedCraftingItems = new ArrayList<>();
			for (final Integer craftingSlotNumber : craftingSlotNumbers) {
				final Slot craftingSlot = container.getSlot(craftingSlotNumber);
				if (craftingSlot.getHasStack()) {
					final ItemStack craftingItem = craftingSlot.getStack().copy();
					clearedCraftingItems.add(craftingItem);
					craftingSlot.putStack(ItemStack.EMPTY);
				}
			}

			// put cleared items back into the inventory
			for (final ItemStack oldCraftingItem : clearedCraftingItems) {
				//int added = addStack(container, , oldCraftingItem);
				//if (added < oldCraftingItem.getCount()) {
				if (!player.inventory.addItemStackToInventory(oldCraftingItem)) {
					player.dropItem(oldCraftingItem, false);
				}
				//}
			}

			// grab items from inventory
			for (int i = 0; i < inventorySlotNumbers.size(); i++) {
				final Slot slot = container.getSlot(inventorySlotNumbers.get(i));
				if (slot.getHasStack()) {
					for (final Map.Entry<Integer, ItemStack> recipeItem : recipe.entrySet()) {
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
			for (final Map.Entry<Integer, ItemStack> recipeItem : recipe.entrySet()) {
				if (!recipeSlotsStatus.containsKey(recipeItem.getKey()) || !(ItemStack.areItemsEqual(recipeSlotsStatus.get(recipeItem.getKey()), recipeItem.getValue()) && ItemStack.areItemStackTagsEqual(recipeSlotsStatus.get(recipeItem.getKey()), recipeItem.getValue()))) {
					recipeIsFulfilled = false;
				}
			}

			// recipe not fulfilled yet, so check for applicable /dank/nulls
			if (!recipeIsFulfilled) {
				for (int i = 0; i < inventorySlotNumbers.size(); i++) {
					//final Slot slot = container.getSlot(inventorySlotNumbers.get(i));
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
			for (final Map.Entry<Integer, ItemStack> entry : recipe.entrySet()) {
				final Integer craftNumber = entry.getKey() - 1;
				final Integer slotNumber = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9).get(craftNumber);
				final Slot slot = container.getSlot(slotNumber);

				final ItemStack stack = entry.getValue();
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

		private static int removeSetsFromInventory(final Container container, final Collection<ItemStack> required, final List<Integer> craftingSlots, final List<Integer> inventorySlots, final int maxRemovedSets) {
			int removedSets = 0;
			while (removedSets < maxRemovedSets) {
				if (removeSetsFromInventory(container, required, craftingSlots, inventorySlots)) {
					removedSets++;
				}
			}
			return removedSets;
		}

		private static boolean removeSetsFromInventory(final Container container, final Iterable<ItemStack> required, final List<Integer> craftingSlots, final List<Integer> inventorySlots) {
			final Map<Slot, ItemStack> originalSlotContents = new HashMap<>();

			for (final ItemStack matchingStack : required) {
				if (matchingStack.isEmpty()) {
					continue;
				}
				final ItemStack requiredStack = matchingStack.copy();
				//boolean isDankNull = false;
				if (requiredStack.isEmpty()) {
					continue;
				}
				//				final InventoryDankNull dankNullInv = null;
				final Slot slot = getSlotWithStack(container, requiredStack, craftingSlots, inventorySlots);
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
				if (slot == null || slot.getStack().isEmpty()) {
					// abort! put removed items back where they came from
					for (final Map.Entry<Slot, ItemStack> slotEntry : originalSlotContents.entrySet()) {
						final ItemStack stack = slotEntry.getValue();
						slotEntry.getKey().putStack(stack);
					}
					tryDankNull = true;
					//continue;
				}

				if (!tryDankNull) {
					if (!originalSlotContents.containsKey(slot)) {
						originalSlotContents.put(slot, slot.getStack().copy());
					}
					final ItemStack removed = slot.decrStackSize(requiredStack.getCount());
					requiredStack.shrink(removed.getCount());
				}
				else {
					for (final int slotNum : inventorySlots) {
						final Slot slot1 = container.getSlot(slotNum);
						if (slot1 != null && slot1.getHasStack()) {
							//							if (DankNullUtils.isDankNull(slot1.getStack()) && DankNullUtils.isFilteredOreDict(DankNullUtils.getNewDankNullInventory(slot1.getStack()), requiredStack)) {
							//								DankNullUtils.decrDankNullStackSize(DankNullUtils.getNewDankNullInventory(slot1.getStack()), requiredStack, requiredStack.getCount());
							//							}
						}
					}
				}
			}

			return true;
		}

		@Nullable
		private static Slot getSlotWithStack(final Container container, final ItemStack stack, final List<Integer> craftingSlots, final List<Integer> inventorySlots) {
			Slot slot = getSlotWithStack(container, craftingSlots, stack);
			if (slot == null) {
				slot = getSlotWithStack(container, inventorySlots, stack);
			}

			return slot;
		}

		private static int addStack(final Container container, final Collection<Integer> slotIndexes, final ItemStack stack) {
			final int added = 0;
			// Add to existing stacks first
			for (final Integer slotIndex : slotIndexes) {
				if (slotIndex >= 0 && slotIndex < container.inventorySlots.size()) {
					final Slot slot = container.getSlot(slotIndex);
					final ItemStack inventoryStack = slot.getStack();
					// Check that the slot's contents are stackable with this stack
					//					if (!inventoryStack.isEmpty() && (inventoryStack.isStackable() && inventoryStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(inventoryStack, stack) || DankNullUtils.isDankNull(inventoryStack) && DankNullUtils.isFiltered(DankNullUtils.getNewDankNullInventory(inventoryStack), stack))) {
					//						boolean isDankNull = false;
					//						if (DankNullUtils.isDankNull(inventoryStack)) {
					//							isDankNull = true;
					//						}
					//						final int remain = stack.getCount() - added;
					//						final int maxStackSize = Math.min(slot.getItemStackLimit(isDankNull ? stack : inventoryStack), isDankNull ? stack.getMaxStackSize() : inventoryStack.getMaxStackSize());
					//						final int space = maxStackSize - (isDankNull ? stack.getCount() : inventoryStack.getCount());
					//						if (space > 0) {
					//
					//							// Enough space
					//							if (space >= remain) {
					//								if (isDankNull) {
					//									DankNullUtils.decrDankNullStackSize(DankNullUtils.getNewDankNullInventory(inventoryStack), stack, remain);
					//									//DankNullUtils.addFilteredStackToDankNull(DankNullUtils.getNewDankNullInventory(inventoryStack), stack);
					//									stack.grow(remain);
					//								}
					//								else {
					//									inventoryStack.grow(remain);
					//								}
					//								return stack.getCount();
					//							}
					//
					//							// Not enough space
					//							if (!isDankNull) {
					//								inventoryStack.setCount(inventoryStack.getMaxStackSize());
					//							}
					//							else {
					//								//stack.setCount(size);
					//							}
					//							added += space;
					//						}
					//					}
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
						final ItemStack stackToAdd = stack.copy();
						stackToAdd.setCount(stack.getCount() - added);
						slot.putStack(stackToAdd);
						return stack.getCount();
					}
				}
			}

			return added;
		}

		@Nullable
		private static Slot getSlotWithStack(final Container container, final Iterable<Integer> slotNumbers, final ItemStack itemStack) {
			for (final Integer slotNumber : slotNumbers) {
				if (slotNumber >= 0 && slotNumber < container.inventorySlots.size()) {
					final Slot slot = container.getSlot(slotNumber);
					final ItemStack slotStack = slot.getStack();
					if (ItemStack.areItemsEqual(itemStack, slotStack) && ItemStack.areItemStackTagsEqual(itemStack, slotStack)) {
						return slot;
					}
				}
			}
			return null;
		}
	}

}