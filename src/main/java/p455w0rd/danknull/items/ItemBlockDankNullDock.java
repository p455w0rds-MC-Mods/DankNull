package p455w0rd.danknull.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import p455w0rdslib.util.TextUtils;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.TESRDankNullDock.DankNullDockItemRenderer;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rdslib.api.client.*;

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
