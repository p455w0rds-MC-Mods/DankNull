package p455w0rd.danknull.items;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.client.render.TESRDankNullDock.DankNullDockItemRenderer;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rdslib.api.client.ICustomItemRenderer;
import p455w0rdslib.api.client.IModelHolder;
import p455w0rdslib.api.client.ItemLayerWrapper;

/**
 * @author p455w0rd
 */
public class ItemBlockDankNullDock extends ItemBlock implements IModelHolder {

    ItemLayerWrapper wrappedModel;

    public ItemBlockDankNullDock() {
        super(ModBlocks.DANKNULL_DOCK);
        setRegistryName(ModBlocks.DANKNULL_DOCK.getRegistryName());
    }

    public static ItemStack getDockedDankNull(final ItemStack dankDock) {
        ItemStack dockedDank = ItemStack.EMPTY;
        if (dankDock.hasTagCompound() && dankDock.getTagCompound().hasKey(NBT.BLOCKENTITYTAG, Constants.NBT.TAG_COMPOUND)) {
            final NBTTagCompound nbt = dankDock.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
            if (!nbt.isEmpty()) {
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
        String name = I18n.translateToLocal(getTranslationKey() + ".name").trim();
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
