package p455w0rd.danknull.inventory.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.Constants;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.util.DankNullUtils;

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
					final ItemStack stack = instance.getFullStackInSlot(i);
					if (stack.isEmpty()) {
						continue;
					}
					final NBTTagCompound item = new NBTTagCompound();
					stack.writeToNBT(item);
					item.setInteger("Slot", i);
					item.setInteger("Count", stack.getCount());
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
				if (!items.hasNoTags()) {
					tag.setTag(ModGlobals.NBT.DANKNULL_INVENTORY, items);
				}
				if (!ores.hasNoTags()) {
					tag.setTag(ModGlobals.NBT.OREDICT_MODES, ores);
				}
				if (!extractionModes.hasNoTags()) {
					tag.setTag(ModGlobals.NBT.EXTRACTION_MODES, extractionModes);
				}
				if (!placementModes.hasNoTags()) {
					tag.setTag(ModGlobals.NBT.PLACEMENT_MODES, placementModes);
				}
				if (instance.getSelected() > -1) {
					tag.setInteger(ModGlobals.NBT.SELECTEDINDEX, instance.getSelected());
				}
				if (instance.isLocked()) {
					tag.setBoolean(ModGlobals.NBT.LOCKED, instance.isLocked());
				}
				if (!instance.getUUID().isEmpty()) {
					tag.setString(ModGlobals.NBT.UUID, instance.getUUID());
				}
				return tag;
			}

			@Override
			public void readNBT(final Capability<IDankNullHandler> capability, final IDankNullHandler instance, final EnumFacing side, final NBTBase base) {
				final NBTTagCompound tag = (NBTTagCompound) base;
				if (tag.hasNoTags()) {
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
						instance.setStackInSlot(slot, stack);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.OREDICT_MODES)) {
					final NBTTagList items = tag.getTagList(ModGlobals.NBT.OREDICT_MODES, Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < items.tagCount(); i++) {
						final NBTTagCompound item = items.getCompoundTagAt(i);
						final boolean oreDict = item.getBoolean(ModGlobals.NBT.OREDICT);
						final ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));
						instance.setOre(stack, oreDict);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.EXTRACTION_MODES)) {
					final NBTTagList items = tag.getTagList(ModGlobals.NBT.EXTRACTION_MODES, Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < items.tagCount(); i++) {
						final NBTTagCompound item = items.getCompoundTagAt(i);
						final int mode = item.getInteger(ModGlobals.NBT.MODE);
						final ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));
						instance.setExtractionMode(stack, DankNullUtils.ItemExtractionMode.values()[mode]);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.PLACEMENT_MODES)) {
					final NBTTagList items = tag.getTagList(ModGlobals.NBT.PLACEMENT_MODES, Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < items.tagCount(); i++) {
						final NBTTagCompound item = items.getCompoundTagAt(i);
						final int mode = item.getInteger(ModGlobals.NBT.MODE);
						final ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));
						instance.setPlacementMode(stack, DankNullUtils.ItemPlacementMode.values()[mode]);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.SELECTEDINDEX)) {
					instance.setSelected(tag.getInteger(ModGlobals.NBT.SELECTEDINDEX));
				}
				if (tag.hasKey(ModGlobals.NBT.LOCKED)) {
					instance.setLocked(tag.getBoolean(ModGlobals.NBT.LOCKED));
				}
				if (tag.hasKey(ModGlobals.NBT.UUID)) {
					instance.setUUID(tag.getString(ModGlobals.NBT.UUID));
				}
			}
		}, () -> null);
	}
}
