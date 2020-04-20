package p455w0rd.danknull.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rdslib.util.ItemUtils;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author BrockWS
 */
public class DankNullHandler implements IDankNullHandler {

    public int selected;
    public boolean isLocked;
    private ModGlobals.DankNullTier tier;
    private NonNullList<ItemStack> stacks;
    private Map<ItemStack, Boolean> oreStacks;
    private Map<ItemStack, ItemExtractionMode> extractionStacks;
    private Map<ItemStack, ItemPlacementMode> placementStacks;

    public DankNullHandler(final ModGlobals.DankNullTier tier) {
        this.tier = tier;
        stacks = NonNullList.withSize(this.tier.getNumRows() * 9, ItemStack.EMPTY);
        oreStacks = new HashMap<>();
        extractionStacks = new HashMap<>();
        placementStacks = new HashMap<>();
        selected = -1;
        isLocked = false;
    }

    public static List<String> getOreNames(final ItemStack stack) {
        final int[] oreIds = OreDictionary.getOreIDs(stack);
        if (oreIds.length > 0) {
            final List<String> nameList = new ArrayList<>();
            for (final int oreId : oreIds) {
                final String name = OreDictionary.getOreName(oreId);
                if (!name.equals("Unknown") && ModConfig.isValidOre(name)) {
                    nameList.add(name);
                }
            }
            return nameList;
        }
        return new ArrayList<>();
    }

    @Nonnull
    @Override
    public ItemStack getExtractableStackInSlot(final int slot) {
        validateSlot(slot);
        ItemStack slotStack = getStackList().get(slot).copy();
        if (!slotStack.isEmpty()) {
            if (getExtractionMode(slotStack) == ItemExtractionMode.KEEP_ALL) {
                slotStack = ItemStack.EMPTY;
            } else {
                final int amountToBeKept = getExtractionMode(slotStack).getNumberToKeep();
                if (slotStack.getCount() > amountToBeKept) {
                    final ItemStack availableStack = slotStack.copy();
                    availableStack.setCount(slotStack.getCount() - amountToBeKept);
                    return availableStack;
                }
                return ItemStack.EMPTY;
            }
        }
        return slotStack;
    }

    @Override
    public ItemStack getFullStackInSlot(final int slot) {
        validateSlot(slot);
        return getStackList().get(slot);
    }

    @Nonnull
    @Override
    public ItemStack getRenderableStackForSlot(final int slot) {
        final ItemStack visualStack = getStackInSlot(slot).copy();
        if (!visualStack.isEmpty()) {
            visualStack.setCount(1);
        }
        return visualStack;
    }

    @Override
    public void setStackInSlot(final int slot, @Nonnull final ItemStack stack) {
        validateSlot(slot);
        getStackList().set(slot, stack);
        //sort();
//        updateSelectedSlot();
        onDataChanged();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, final boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }

        stack = convertOreDict(stack);
        ItemStack existingStack = getStackList().get(slot);

        if(existingStack.isEmpty()) {
            if (!simulate) {
                getStackList().set(slot, stack.copy());
                //sort();
//        updateSelectedSlot();
                onDataChanged();
            }
            return ItemStack.EMPTY;
        }

        if (!ItemHandlerHelper.canItemStacksStack(stack, existingStack)) {
           return stack;
        }

        int newInternalCount = Math.min(existingStack.getCount() + stack.getCount(), tier.getMaxStackSize());
        int returnCount = existingStack.getCount() + stack.getCount() - newInternalCount;

        if (!simulate) {
            existingStack.setCount(newInternalCount);
            //sort();
//        updateSelectedSlot();
            onDataChanged();
        }

        if (returnCount == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack returnStack = stack.copy();
        returnStack.setCount(returnCount);
        return returnStack;
    }

    @Override
    public boolean isOreDictFiltered(final ItemStack stack) {
        for (final ItemStack storedStack : getStackList()) {
            if (!storedStack.isEmpty() && oreMatches(storedStack, stack)) {
                return true;
            }
        }
        return false;
    }

    private ItemStack convertOreDict(final ItemStack incomingStack) {
        for (final ItemStack storedStack : getStackList()) {
            if (isOre(storedStack) && !isOre(incomingStack) && oreMatches(storedStack, incomingStack)) {
                final ItemStack newStack = storedStack.copy();
                newStack.setCount(incomingStack.getCount());
                return newStack;
            }
        }
        return incomingStack;
    }

    private boolean oreMatches(final ItemStack storedStack, final ItemStack incomingStack) {
        final List<String> oreNamesForStoredStack = getOreNames(storedStack);
        final List<String> oreNamesForIncomingStack = getOreNames(incomingStack);
        for (final String currentStoredName : oreNamesForStoredStack) {
            if (ModConfig.isValidOre(currentStoredName)) {
                for (final String currentIncomingName : oreNamesForIncomingStack) {
                    if (ModConfig.isValidOre(currentIncomingName)) {
                        if (currentIncomingName.equals(currentStoredName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        if (amount < 1) {
            return ItemStack.EMPTY;
        }
        validateSlot(slot);

        final ItemStack existing = getStackList().get(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final int existingCount = existing.getCount();
        final int extract = Math.min(amount, existing.getMaxStackSize());
        if (existingCount <= extract) {
            if (!simulate) {
                getStackList().set(slot, ItemStack.EMPTY);
                //sort();
//        updateSelectedSlot();
                onDataChanged();
            }
            return existing;
        } else {
            if (!simulate) {
                getStackList().set(slot, ItemHandlerHelper.copyStackWithSize(existing, existingCount - extract));
                //sort();
//        updateSelectedSlot();
                onDataChanged();
            }
            return ItemHandlerHelper.copyStackWithSize(existing, extract);
        }
    }

    @Override
    public boolean containsItemStack(@Nonnull final ItemStack stack) {
        return findItemStack(stack) > -1;
    }

    @Override
    public int findItemStack(@Nonnull final ItemStack stack) {
        for (int i = 0; i < getStackList().size(); i++) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(getStackList().get(i), stack)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ImmutableList<Integer> findItemStacks(@Nonnull ItemStack stack) {
        ImmutableList.Builder<Integer> results = ImmutableList.builder();
        for (int i = 0; i < getStackList().size(); i++) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(getStackList().get(i), stack)) {
                results.add(i);
            }
        }
        return results.build();
    }

    @Override
    public int stackCount() {
        int count = 0;
        for (final ItemStack stack : getStackList()) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Nonnull
    @Override
    public ModGlobals.DankNullTier getTier() {
        return tier;
    }

    @Override
    public int getSelected() {
        return selected;
    }

    @Override
    public void setSelected(final int slot) {
        selected = slot;
        onDataChanged();
    }

    @Override
    public void cycleSelected(final boolean forward) {
        final List<Integer> blockSlots = getBlockStacksSlots();
        if (Options.skipNonBlocksOnCycle) {
            final int numBlockSlots = blockSlots.size();
            if (numBlockSlots > 0) {
                if (blockSlots.size() == 1) {
                    if (getSelected() != blockSlots.get(0)) {
                        setSelected(blockSlots.get(0));
                    }
                    return;
                } else {
                    final int current = getSelected();
                    if (!blockSlots.contains(current)) {
                        setSelected(blockSlots.get(0));
                        return;
                    }
                    final int min = 0;
                    final int max = blockSlots.size() - 1;
                    final int currentIndex = getBlockSlotIndex(current);
                    if (forward) {
                        if (currentIndex != max) {
                            setSelected(blockSlots.get(currentIndex + 1));
                            return;
                        }
                        setSelected(blockSlots.get(0));
                    } else {
                        if (currentIndex != min) {
                            setSelected(blockSlots.get(currentIndex - 1));
                            return;
                        }
                        setSelected(blockSlots.get(max));
                    }
                    return;
                }
            }
        }
        final int current = getSelected();
        final int stackCount = stackCount();
        int newIndex = 0;
        if (stackCount > 1) {
            if (forward) {
                if (current != stackCount - 1) {
                    newIndex = current + 1;
                }
            } else {
                if (current == 0) {
                    newIndex = stackCount - 1;
                } else {
                    newIndex = current - 1;
                }
            }
        }
        if (current != newIndex) {
            setSelected(newIndex);
        }
    }

    private int getBlockSlotIndex(final int slot) {
        for (int i = 0; i < getBlockStacksSlots().size(); i++) {
            if (getBlockStacksSlots().get(i) == slot) {
                return i;
            }
        }
        return -1;
    }

    private List<Integer> getBlockStacksSlots() {
        final List<Integer> blockStackSlots = Lists.newArrayList();
        for (int i = 0; i < getSlots(); i++) {
            final ItemStack fullStack = getFullStackInSlot(i);
            if (!fullStack.isEmpty() && fullStack.getItem() instanceof ItemBlock) {
                blockStackSlots.add(i);
            }
        }
        Collections.sort(blockStackSlots);
        return blockStackSlots;
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void setLocked(final boolean lock) {
        isLocked = lock;
        onDataChanged();
    }

    @Override
    public boolean isLockingSupported() {
        return tier.isCreative();
    }

    @Override
    public void setOre(@Nonnull ItemStack stack, final boolean ore) {
        if (stack.isEmpty()) {
            return;
        }
        for (final ItemStack currentStack : oreStacks.keySet()) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                oreStacks.put(currentStack, ore);
                onDataChanged();
                return;
            }
        }
        stack = stack.copy();
        stack.setCount(1);
        oreStacks.put(stack, ore);
        onDataChanged();
    }

    @Override
    public boolean isOre(@Nonnull final ItemStack stack) {
        for (final ItemStack currentStack : oreStacks.keySet()) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                return oreStacks.get(currentStack);
            }
        }
        return false;
    }

    @Override
    public boolean isOreSupported(@Nonnull final ItemStack stack) {
        return OreDictionary.getOreIDs(stack).length > 0;
    }

    @Nonnull
    @Override
    public Map<ItemStack, Boolean> getOres() {
        return oreStacks;
    }

    @Override
    public void setExtractionMode(@Nonnull ItemStack stack, @Nonnull final ItemExtractionMode mode) {
        if (stack.isEmpty()) {
            return;
        }
        for (final ItemStack currentStack : extractionStacks.keySet()) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                extractionStacks.put(currentStack, mode);
                onDataChanged();
                return;
            }
        }
        stack = stack.copy();
        stack.setCount(1);
        extractionStacks.put(stack, mode);
        onDataChanged();
    }

    @Override
    public void cycleExtractionMode(@Nonnull final ItemStack stack, final boolean forward) {
        ItemExtractionMode current = getExtractionMode(stack);
        final ItemExtractionMode[] values = ItemExtractionMode.values();
        if (forward) {
            if (current.ordinal() == values.length - 1) {
                current = values[0];
            } else {
                current = values[current.ordinal() + 1];
            }
        } else {
            if (current.ordinal() == 0) {
                current = values[values.length - 1];
            } else {
                current = values[current.ordinal() - 1];
            }
        }
        setExtractionMode(stack, current);
    }

    @Nonnull
    @Override
    public ItemExtractionMode getExtractionMode(@Nonnull final ItemStack stack) {
        for (final ItemStack currentStack : extractionStacks.keySet()) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                return extractionStacks.get(currentStack);
            }
        }
        return ItemExtractionMode.KEEP_ALL;
    }

    @Nonnull
    @Override
    public Map<ItemStack, ItemExtractionMode> getExtractionModes() {
        return extractionStacks;
    }

    @Override
    public void setPlacementMode(@Nonnull ItemStack stack, @Nonnull final ItemPlacementMode mode) {
        if (stack.isEmpty()) {
            return;
        }
        for (final ItemStack currentStack : placementStacks.keySet()) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                placementStacks.put(currentStack, mode);
                onDataChanged();
                return;
            }
        }
        stack = stack.copy();
        stack.setCount(1);
        placementStacks.put(stack, mode);
        onDataChanged();
    }

    @Override
    public void cyclePlacementMode(@Nonnull final ItemStack stack, final boolean forward) {
        ItemPlacementMode current = this.getPlacementMode(stack);
        final ItemPlacementMode[] values = ItemPlacementMode.values();
        if (forward) {
            if (current.ordinal() == values.length - 1) {
                current = values[0];
            } else {
                current = values[current.ordinal() + 1];
            }
        } else {
            if (current.ordinal() == 0) {
                current = values[values.length - 1];
            } else {
                current = values[current.ordinal() - 1];
            }
        }
        setPlacementMode(stack, current);
    }

    @Nonnull
    @Override
    public ItemPlacementMode getPlacementMode(@Nonnull final ItemStack stack) {
        for (final ItemStack currentStack : placementStacks.keySet()) {
            if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                return placementStacks.get(currentStack);
            }
        }
        return ItemPlacementMode.KEEP_1;
    }

    @Override
    public Map<ItemStack, ItemPlacementMode> getPlacementMode() {
        return placementStacks;
    }

    @Override
    public int getSlots() {
        return getStackList().size();
    }

    @Override
    public NonNullList<ItemStack> getStackList() {
        return stacks;
    }

    @Override
    public int getSlotLimit(final int slot) {
        return tier.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(final int slot, @Nonnull final ItemStack stack) {
        final boolean isSame = ItemStack.areItemsEqual(getFullStackInSlot(slot), stack) && ItemStack.areItemStackTagsEqual(getFullStackInSlot(slot), stack);
        return !(stack.getItem() instanceof ItemDankNull) && (getFullStackInSlot(slot).isEmpty() || isSame);
    }

    public void sort() {
        getStackList().sort((o1, o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            if (o1.isEmpty()) {
                return 1;
            }
            if (o2.isEmpty()) {
                return -1;
            }
            return 0;
        });
    }

    public void updateSelectedSlot() {
        final int selected = getSelected();
        if (selected >= 0 && !getFullStackInSlot(selected).isEmpty()) {
            return;
        }
        int newSelected = -1;
        if (selected > 0) {
            for (int i = selected; i > -1; i--) {
                if (getFullStackInSlot(i).isEmpty()) {
                    continue;
                }
                newSelected = i;
                break;
            }
        } else if (!getFullStackInSlot(0).isEmpty()) {
            newSelected = 0;
        }

        setSelected(newSelected);
    }

    protected void onDataChanged() {
    }

}