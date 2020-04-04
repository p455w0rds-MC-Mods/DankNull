package p455w0rd.danknull.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModGlobals.NBT;
import p455w0rdslib.api.client.ICustomItemRenderer;
import p455w0rdslib.api.client.ItemLayerWrapper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author p455w0rd
 */
public class TESRDankNullDock extends TileEntitySpecialRenderer<TileDankNullDock> {

    public static void renderDankNull(@Nonnull final ItemStack stack) {
        ItemStack dankNullStack = ItemStack.EMPTY;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT.BLOCKENTITYTAG)) {
            final NBTTagCompound tag = stack.getTagCompound().getCompoundTag(NBT.BLOCKENTITYTAG);
            if (tag != null && tag.hasKey(NBT.DOCKEDSTACK)) {
                dankNullStack = new ItemStack(tag.getCompoundTag(NBT.DOCKEDSTACK));
            }
            if (!dankNullStack.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(-0.75, -0.15, 0.75);
                GlStateManager.rotate(180.0F, 1.0F, 0F, 0F);
                GlStateManager.scale(0.5D, 0.5D, 0.5D);
                GlStateManager.enableDepth();
                GlStateManager.enableLighting();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                DankNullRenderer.getRendererForItem(dankNullStack.getItem()).renderByItem(dankNullStack, Minecraft.getMinecraft().getRenderPartialTicks());
                GlStateManager.enableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public void render(final TileDankNullDock te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        final ItemStack stack = te.getDankNull();
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.4, z + 0.5);
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            Minecraft.getMinecraft().getItemRenderer().renderItem(Minecraft.getMinecraft().player, stack, ItemCameraTransforms.TransformType.NONE);
            GlStateManager.translate(-x, -y, -z);
            GlStateManager.popMatrix();
        }
    }

    public static class DankNullDockItemRenderer extends TileEntityItemStackRenderer implements ICustomItemRenderer {

        private static final Map<Item, ICustomItemRenderer> CACHE = new HashMap<>();
        public static TransformType transformType;
        public ItemLayerWrapper model;

        private DankNullDockItemRenderer(@Nonnull final Item item) {
            registerRenderer(item, this);
        }

        private static void registerRenderer(final Item item, final ICustomItemRenderer instance) {
            CACHE.put(item, instance);
        }

        public static ICustomItemRenderer getRendererForItem(final Item item) {
            if (!CACHE.containsKey(item)) {
                new DankNullDockItemRenderer(item);
            }
            return CACHE.get(item);
        }

        @Override
        public void renderByItem(@Nonnull final ItemStack stack, final float partialTicks) {
            final BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
            final IBlockState state = ModBlocks.DANKNULL_DOCK.getDefaultState();
            final World world = Minecraft.getMinecraft().world;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            final Tessellator tessellator = Tessellator.getInstance();
            final BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            brd.getBlockModelRenderer().renderModel(world, brd.getModelForState(state), state, BlockPos.ORIGIN, buffer, false, world.rand.nextLong());
            tessellator.draw();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            GlStateManager.rotate(180, 0, 0, 180);
            renderDankNull(stack);
        }

        @Override
        public TransformType getTransformType() {
            return transformType;
        }

        @Override
        public void setTransformType(final TransformType type) {
            transformType = type;
        }

    }

}
