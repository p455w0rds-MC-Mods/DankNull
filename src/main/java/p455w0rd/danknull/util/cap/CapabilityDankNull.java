package p455w0rd.danknull.util.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.Constants;

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
			public NBTBase writeNBT(Capability<IDankNullHandler> capability, IDankNullHandler instance, EnumFacing side) {
				NBTTagCompound tag = new NBTTagCompound();
				NBTTagList items = new NBTTagList();
				for (int i = 0; i < instance.getSlots(); i++) {
					ItemStack stack = instance.getStackInSlot(i);
					if (stack.isEmpty())
						continue;
					NBTTagCompound item = new NBTTagCompound();
					stack.writeToNBT(item);
					item.setInteger("Slot", i);
					item.setInteger("Count", stack.getCount());
					items.appendTag(item);
				}
				NBTTagList ores = new NBTTagList();
				instance.getOres().forEach((stack, ore) -> {
					NBTTagCompound oreTag = new NBTTagCompound();
					oreTag.setBoolean(ModGlobals.NBT.OREDICT, ore);
					oreTag.setTag(ModGlobals.NBT.STACK, stack.serializeNBT());
					ores.appendTag(oreTag);
				});
				NBTTagList extractionModes = new NBTTagList();
				instance.getExtractionModes().forEach((stack, mode) -> {
					NBTTagCompound extractionTag = new NBTTagCompound();
					extractionTag.setInteger(ModGlobals.NBT.MODE, mode.ordinal());
					extractionTag.setTag(ModGlobals.NBT.STACK, stack.serializeNBT());
					extractionModes.appendTag(extractionTag);
				});
				NBTTagList placementModes = new NBTTagList();
				instance.getPlacementMode().forEach((stack, mode) -> {
					NBTTagCompound placementTag = new NBTTagCompound();
					placementTag.setInteger(ModGlobals.NBT.MODE, mode.ordinal());
					placementTag.setTag(ModGlobals.NBT.STACK, stack.serializeNBT());
					placementModes.appendTag(placementTag);
				});
				if (!items.hasNoTags())
					tag.setTag(ModGlobals.NBT.DANKNULL_INVENTORY, items);
				if (!ores.hasNoTags())
					tag.setTag(ModGlobals.NBT.OREDICT_MODES, ores);
				if (!extractionModes.hasNoTags())
					tag.setTag(ModGlobals.NBT.EXTRACTION_MODES, extractionModes);
				if (!placementModes.hasNoTags())
					tag.setTag(ModGlobals.NBT.PLACEMENT_MODES, placementModes);
				if (instance.getSelected() > -1)
					tag.setInteger(ModGlobals.NBT.SELECTEDINDEX, instance.getSelected());
				if (instance.isLocked())
					tag.setBoolean(ModGlobals.NBT.LOCKED, instance.isLocked());
				if (!instance.getUUID().isEmpty())
					tag.setString(ModGlobals.NBT.UUID, instance.getUUID());
				return tag;
			}

			@Override
			public void readNBT(Capability<IDankNullHandler> capability, IDankNullHandler instance, EnumFacing side, NBTBase base) {
				NBTTagCompound tag = (NBTTagCompound) base;
				if (tag.hasNoTags())
					return;
				if (tag.hasKey(ModGlobals.NBT.DANKNULL_INVENTORY)) {
					NBTTagList items = tag.getTagList(ModGlobals.NBT.DANKNULL_INVENTORY, Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < items.tagCount(); i++) {
						NBTTagCompound item = items.getCompoundTagAt(i);
						int slot = item.getInteger("Slot");
						int count = item.getInteger("Count");
						ItemStack stack = new ItemStack(item);
						stack.setCount(count);
						instance.setStackInSlot(slot, stack);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.OREDICT_MODES)) {
					NBTTagList items = tag.getTagList(ModGlobals.NBT.OREDICT_MODES, Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < items.tagCount(); i++) {
						NBTTagCompound item = items.getCompoundTagAt(i);
						boolean oreDict = item.getBoolean(ModGlobals.NBT.OREDICT);
						ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));
						instance.setOre(stack, oreDict);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.EXTRACTION_MODES)) {
					NBTTagList items = tag.getTagList(ModGlobals.NBT.EXTRACTION_MODES, Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < items.tagCount(); i++) {
						NBTTagCompound item = items.getCompoundTagAt(i);
						int mode = item.getInteger(ModGlobals.NBT.MODE);
						ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));
						instance.setExtractionMode(stack, DankNullUtils.ItemExtractionMode.values()[mode]);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.PLACEMENT_MODES)) {
					NBTTagList items = tag.getTagList(ModGlobals.NBT.PLACEMENT_MODES, Constants.NBT.TAG_COMPOUND);
					for (int i = 0; i < items.tagCount(); i++) {
						NBTTagCompound item = items.getCompoundTagAt(i);
						int mode = item.getInteger(ModGlobals.NBT.MODE);
						ItemStack stack = new ItemStack(item.getCompoundTag(ModGlobals.NBT.STACK));
						instance.setPlacementMode(stack, DankNullUtils.ItemPlacementMode.values()[mode]);
					}
				}
				if (tag.hasKey(ModGlobals.NBT.SELECTEDINDEX))
					instance.setSelected(tag.getInteger(ModGlobals.NBT.SELECTEDINDEX));
				if (tag.hasKey(ModGlobals.NBT.LOCKED))
					instance.setLocked(tag.getBoolean(ModGlobals.NBT.LOCKED));
				if (tag.hasKey(ModGlobals.NBT.UUID))
					instance.setUUID(tag.getString(ModGlobals.NBT.UUID));
			}
		}, () -> null);
	}
}
