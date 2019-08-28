package p455w0rd.danknull.util.cap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;

import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.integration.PwLib;
import p455w0rdslib.integration.Albedo;
import p455w0rdslib.util.CapabilityUtils;

/**
 * @author BrockWS
 */
public class DankNullCapabilityProvider implements /*ICapabilitySerializable<NBTTagCompound>*/ ICapabilityProvider {

	private ModGlobals.DankNullTier tier;
	private ItemStack stack;
	private IDankNullHandler dankNullHandler;

	public DankNullCapabilityProvider(ModGlobals.DankNullTier tier, ItemStack stack) {
		this.tier = tier;
		this.stack = stack;
		this.dankNullHandler = new DankNullHandler(tier);
		if (stack.hasTagCompound())
			CapabilityDankNull.DANK_NULL_CAPABILITY.readNBT(this.dankNullHandler, null, stack.getTagCompound());
	}

	@Override
	public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
		return capability == CapabilityDankNull.DANK_NULL_CAPABILITY || CapabilityUtils.isItemHandler(capability) || Albedo.albedoCapCheck(capability) || PwLib.checkCap(capability);
	}

	@Override
	public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
		if (hasCapability(capability, facing)) {
			if (Albedo.albedoCapCheck(capability)) {
				return p455w0rd.danknull.integration.Albedo.getStackCapability(stack);
			} else if (PwLib.checkCap(capability)) {
				return PwLib.getStackCapability(stack);
			} else if (CapabilityUtils.isItemHandler(capability)) {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.dankNullHandler);
			} else if (capability == CapabilityDankNull.DANK_NULL_CAPABILITY) {
				return CapabilityDankNull.DANK_NULL_CAPABILITY.cast(this.dankNullHandler);
			}
		}
		return null;
	}

//	@Override
//	public NBTTagCompound serializeNBT() {
//		return (NBTTagCompound) CapabilityDankNull.DANK_NULL_CAPABILITY.writeNBT(this.dankNullHandler, null);
//	}
//
//	@Override
//	public void deserializeNBT(NBTTagCompound nbt) {
//		CapabilityDankNull.DANK_NULL_CAPABILITY.readNBT(this.dankNullHandler, null, nbt);
//	}
}
