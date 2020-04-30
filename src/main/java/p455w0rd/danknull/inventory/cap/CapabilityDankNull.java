package p455w0rd.danknull.inventory.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.Constants;
import p455w0rd.danknull.api.DankNullItemModes.ItemExtractionMode;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rdslib.util.ItemUtils;

import java.util.Map;

/**
 * @author BrockWS
 */
public class CapabilityDankNull {

    @CapabilityInject(IDankNullHandler.class)
    public static Capability<IDankNullHandler> DANK_NULL_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDankNullHandler.class, new Capability.IStorage<IDankNullHandler>() {
            @Override
            public NBTBase writeNBT(final Capability<IDankNullHandler> capability, final IDankNullHandler instance, final EnumFacing side) {
                final NBTTagCompound tag = new NBTTagCompound();
                final NBTTagList items = new NBTTagList();
                for (int i = 0; i < instance.getSlots(); i++) {
                    final ItemStack originalStack = instance.getFullStackInSlot(i);
                    ItemStack stack = originalStack.copy();
                    if (stack.isEmpty()) {
                        continue;
                    }
                    final NBTTagCompound item = new NBTTagCompound();
                    // Prevent issues with casting to byte
                    stack.setCount(1);
                    stack.writeToNBT(item);
                    item.setInteger("Slot", i);
                    item.setInteger("Count", originalStack.getCount());
                    items.appendTag(item);
                }
                final NBTTagList ores = new NBTTagList();
                instance.getOres().forEach((stack, ore) -> {
                    final NBTTagCompound oreTag = new NBTTagCompound();
                    oreTag.setBoolean(ModGlobals.NBT.OREDICT, ore);
                    oreTag.setTag(ModGlobals.NBT.STACK, stack.serializeNBT());
                    ores.appendTag(oreTag);
                });
                final NBTTagList extractionModes = new NBTTagList();
                instance.getExtractionModes().forEach((stack, mode) -> {
                    final NBTTagCompound extractionTag = new NBTTagCompound();
                    extractionTag.setInteger(ModGlobals.NBT.MODE, mode.ordinal());
                    extractionTag.setTag(ModGlobals.NBT.STACK, stack.serializeNBT());
                    extractionModes.appendTag(extractionTag);
                });
                final NBTTagList placementModes = new NBTTagList();
                instance.getPlacementMode().forEach((stack, mode) -> {
                    final NBTTagCompound placementTag = new NBTTagCompound();
                    placementTag.setInteger(ModGlobals.NBT.MODE, mode.ordinal());
                    placementTag.setTag(ModGlobals.NBT.STACK, stack.serializeNBT());
                    placementModes.appendTag(placementTag);
                });
                if (!items.isEmpty()) {
                    tag.setTag(ModGlobals.NBT.DANKNULL_INVENTORY, items);
                }
                if (!ores.isEmpty()) {
                    tag.setTag(ModGlobals.NBT.OREDICT_MODES, ores);
                }
                if (!extractionModes.isEmpty()) {
                    tag.setTag(ModGlobals.NBT.EXTRACTION_MODES, extractionModes);
                }
                if (!placementModes.isEmpty()) {
                    tag.setTag(ModGlobals.NBT.PLACEMENT_MODES, placementModes);
                }
                if (instance.getSelected() > -1) {
                    tag.setInteger(ModGlobals.NBT.SELECTEDINDEX, instance.getSelected());
                }
                if (instance.isLocked()) {
                    tag.setBoolean(ModGlobals.NBT.LOCKED, instance.isLocked());
                }
                return tag;
            }

            @Override
            public void readNBT(final Capability<IDankNullHandler> capability, final IDankNullHandler instance, final EnumFacing side, final NBTBase base) {
                if (instance instanceof DankNullHandler) {
                    final DankNullHandler handler = (DankNullHandler) instance;
                    final NBTTagCompound tag = (NBTTagCompound) base;
                    if (tag == null || tag.isEmpty()) {
                        return;
                    }
                    if (tag.hasKey(ModGlobals.NBT.DANKNULL_INVENTORY)) {
                        final NBTTagList items = tag.getTagList(ModGlobals.NBT.DANKNULL_INVENTORY, Constants.NBT.TAG_COMPOUND);
                        for (int i = 0; i < items.tagCount(); i++) {
                            final NBTTagCompound item = items.getCompoundTagAt(i);
                            final int slot = item.getInteger("Slot");
                            final int count = item.getInteger("Count");
                            final ItemStack stack = new ItemStack(item);
                            stack.setCount(count);
                            instance.validateSlot(slot);
                            instance.getStackList().set(slot, stack);
                        }
                    }
                    if (tag.hasKey(ModGlobals.NBT.OREDICT_MODES)) {
                        final NBTTagList items = tag.getTagList(ModGlobals.NBT.OREDICT_MODES, Constants.NBT.TAG_COMPOUND);
                        for (int i = 0; i < items.tagCount(); i++) {
                            final NBTTagCompound item = items.getCompoundTagAt(i);
                            final boolean oreDict = item.getBoolean(ModGlobals.NBT.OREDICT);
                            ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));
                            if (!stack.isEmpty()) {
                                final Map<ItemStack, Boolean> oreStacks = instance.getOres();
                                boolean foundStack = false;
                                for (final ItemStack currentStack : oreStacks.keySet()) {
                                    if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                                        oreStacks.put(currentStack, oreDict);
                                        foundStack = true;
                                        break;
                                    }
                                }
                                if (!foundStack) {
                                    stack = stack.copy();
                                    stack.setCount(1);
                                    oreStacks.put(stack, oreDict);
                                }
                            }
                        }
                    }
                    if (tag.hasKey(ModGlobals.NBT.EXTRACTION_MODES)) {
                        final NBTTagList items = tag.getTagList(ModGlobals.NBT.EXTRACTION_MODES, Constants.NBT.TAG_COMPOUND);
                        for (int i = 0; i < items.tagCount(); i++) {
                            final NBTTagCompound item = items.getCompoundTagAt(i);
                            final ItemExtractionMode mode = ItemExtractionMode.VALUES[item.getInteger(ModGlobals.NBT.MODE)];
                            ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));

                            if (!stack.isEmpty()) {
                                final Map<ItemStack, ItemExtractionMode> extractionStacks = instance.getExtractionModes();
                                for (final ItemStack currentStack : extractionStacks.keySet()) {
                                    if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                                        extractionStacks.put(currentStack, mode);
                                        return;
                                    }
                                }
                                stack = stack.copy();
                                stack.setCount(1);
                                extractionStacks.put(stack, mode);
                            }

                        }
                    }
                    if (tag.hasKey(ModGlobals.NBT.PLACEMENT_MODES)) {
                        final NBTTagList items = tag.getTagList(ModGlobals.NBT.PLACEMENT_MODES, Constants.NBT.TAG_COMPOUND);
                        for (int i = 0; i < items.tagCount(); i++) {
                            final NBTTagCompound item = items.getCompoundTagAt(i);
                            final ItemPlacementMode mode = ItemPlacementMode.VALUES[item.getInteger(ModGlobals.NBT.MODE)];
                            ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));

                            if (!stack.isEmpty()) {
                                final Map<ItemStack, ItemPlacementMode> placementStacks = instance.getPlacementMode();
                                for (final ItemStack currentStack : placementStacks.keySet()) {
                                    if (ItemUtils.areItemStacksEqualIgnoreSize(currentStack, stack)) {
                                        placementStacks.put(currentStack, mode);
                                        return;
                                    }
                                }
                                stack = stack.copy();
                                stack.setCount(1);
                                placementStacks.put(stack, mode);
                            }
                        }
                    }
                    if (tag.hasKey(ModGlobals.NBT.SELECTEDINDEX)) {
                        handler.setSelected(tag.getInteger(ModGlobals.NBT.SELECTEDINDEX));
                    }
                    if (tag.hasKey(ModGlobals.NBT.LOCKED)) {
                        handler.isLocked = tag.getBoolean(ModGlobals.NBT.LOCKED);
                    }
                }
            }
        }, () -> null);
    }
}
