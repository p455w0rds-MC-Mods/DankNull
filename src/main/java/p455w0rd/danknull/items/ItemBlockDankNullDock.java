package p455w0rd.danknull.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.TESRDankNullDock.DankNullDockItemRenderer;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rd.danknull.integration.PwLib;
import p455w0rdslib.api.client.*;
import p455w0rdslib.integration.Albedo;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
public class ItemBlockDankNullDock extends ItemBlock implements IModelHolder {

	ItemLayerWrapper wrappedModel;

	public ItemBlockDankNullDock() {
		super(ModBlocks.DANKNULL_DOCK);
		setRegistryName(ModBlocks.DANKNULL_DOCK.getRegistryName());
	}

	@Override
	public ICapabilityProvider initCapabilities(final ItemStack stack, final NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(final Capability<?> capability, final EnumFacing facing) {
				return Albedo.albedoCapCheck(capability) || PwLib.checkCap(capability);
			}

			@Override
			public <T> T getCapability(final Capability<T> capability, final EnumFacing facing) {
				if (hasCapability(capability, facing)) {
					if (Albedo.albedoCapCheck(capability)) {
						return p455w0rd.danknull.integration.Albedo.getStackCapability(stack);
					}
					else if (PwLib.checkCap(capability)) {
						return PwLib.getStackCapability(getDockedDankNull(stack));
					}
				}
				return null;
			}
		};
	}

	public static ItemStack getDockedDankNull(final ItemStack dankDock) {
		ItemStack dockedDank = ItemStack.EMPTY;
		if (dankDock.hasTagCompound() && dankDock.getTagCompound().hasKey(NBT.BLOCKENTITYTAG, Constants.NBT.TAG_COMPOUND)) {
			final NBTTagCompound nbt = dankDock.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
			if (!nbt.hasNoTags()) {
				dockedDank = new ItemStack(nbt.getCompoundTag(NBT.DOCKEDSTACK));
			}
		}
		return dockedDank;
	}

	public static boolean isDankNullDock(final ItemStack stack) {
		return stack.getItem() instanceof ItemBlockDankNullDock;
	}

	@Override
	public String getItemStackDisplayName(final ItemStack stack) {
		String name = TextUtils.translate(getUnlocalizedName() + ".name").trim();
		if (Options.callItDevNull) {
			name = name.replace("/dank/", "/dev/");
		}
		return name;
	}

	@Override
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, getModelResource(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemLayerWrapper getWrappedModel() {
		return wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setWrappedModel(final ItemLayerWrapper wrappedModel) {
		this.wrappedModel = wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldUseInternalTEISR() {
		return true;
	}

	@Override
	public ICustomItemRenderer getRenderer() {
		return DankNullDockItemRenderer.getRendererForItem(this);
	}

}
