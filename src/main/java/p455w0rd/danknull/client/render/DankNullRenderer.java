package p455w0rd.danknull.client.render;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rd.danknull.init.ModItems;
import p455w0rd.danknull.inventory.InventoryDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.items.ItemDankNullHolder;
import p455w0rd.danknull.util.DankNullUtils;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
@SideOnly(Side.CLIENT)
public class DankNullRenderer implements IItemRenderer {

	private static final DankNullRenderer INSTANCE = new DankNullRenderer();
	boolean isGUI = false;
	InventoryDankNull inventory;

	public static DankNullRenderer getInstance() {
		return INSTANCE;
	}

	private InventoryDankNull getDankNullInventory() {
		return inventory;
	}

	public void setDankNullInventory(InventoryDankNull inv) {
		inventory = inv;
	}

	@Override
	public void renderItem(ItemStack item, TransformType transformType) {
		if ((item.getItem() instanceof ItemDankNullHolder)) {
			return;
		}
		if ((item.getItem() instanceof ItemDankNull)) {
			RenderManager rm = Minecraft.getMinecraft().getRenderManager();
			if (rm == null) {
				return;
			}
			GameSettings options = rm.options;
			if (options == null) {
				return;
			}
			int view = options.thirdPersonView;
			//if (getDankNullInventory() == null) {// || Minecraft.getMinecraft().player.ticksExisted % 20 == 0) {
			inventory = DankNullUtils.getNewDankNullInventory(item);
			//}
			int index = DankNullUtils.getSelectedStackIndex(inventory);
			ItemStack containedStack = DankNullUtils.getItemByIndex(inventory, index);
			//ItemStack containedStack = DankNullUtils.getSelectedStack(inventory);
			int modelDamage = item.getItemDamage();
			if (modelDamage > 6) {
				modelDamage -= 7;
			}
			IBakedModel holderModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(new ItemStack(ModItems.DANK_NULL_HOLDER, 1, modelDamage));
			if (!containedStack.isEmpty()) {
				IBakedModel containedItemModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(containedStack);

				GlStateManager.pushMatrix();
				if (((containedStack.getItem() instanceof ItemBlock)) && (!(Block.getBlockFromItem(containedStack.getItem()) instanceof BlockTorch))) {
					GlStateManager.scale(0.4D, 0.4D, 0.4D);
					if (containedItemModel.isBuiltInRenderer()) {
						if ((view > 0) || (!isStackInHand(item))) {
							GlStateManager.scale(1.1D, 1.1D, 1.1D);
							GlStateManager.translate(1.25D, 1.4D, 1.25D);
						}
						else {
							GlStateManager.translate(1.25D, 2.0D, 1.25D);
						}
					}
					else if ((view > 0) || (!isStackInHand(item))) {
						GlStateManager.translate(0.75D, 0.9D, 0.75D);
					}
					else {
						GlStateManager.translate(0.75D, 1.5D, 0.75D);
					}
				}
				else {
					GlStateManager.scale(0.5D, 0.5D, 0.5D);
					if (containedItemModel.isBuiltInRenderer()) {
						if ((view > 0) || (!isStackInHand(item))) {
							if ((containedStack.getItem() instanceof ItemSkull)) {
								if (containedStack.getItemDamage() == 5) {
									GlStateManager.scale(0.65D, 0.65D, 0.65D);
									GlStateManager.translate(1.5D, 3.0D, 1.5D);
								}
								else {
									GlStateManager.translate(0.75D, 2.25D, 1.1D);
								}
							}
							else {
								GlStateManager.scale(1.1D, 1.1D, 1.1D);
								GlStateManager.translate(1.25D, 1.4D, 1.25D);
							}
						}
						else if ((containedStack.getItem() instanceof ItemSkull)) {
							if (containedStack.getItemDamage() == 5) {
								GlStateManager.scale(0.65D, 0.65D, 0.65D);
								GlStateManager.translate(1.5D, 3.0D, 1.5D);
							}
							else {
								GlStateManager.translate(0.75D, 2.25D, 1.1D);
							}
						}
						else {
							GlStateManager.translate(0.75D, 2.0D, 1.0D);
						}
					}
					else if ((view > 0) || (!isStackInHand(item))) {
						GlStateManager.translate(0.5D, 0.9D, 0.5D);
					}
					else {
						GlStateManager.translate(0.5D, 1.5D, 0.5D);
					}
				}
				if (item.isOnItemFrame()) {
					GlStateManager.scale(1.25D, 1.25D, 1.25D);
					GlStateManager.translate(-0.2D, -0.2D, -0.5D);
				}
				if (containedItemModel.isBuiltInRenderer()) {
					if (containedStack.getItem() == Item.getItemFromBlock(ModBlocks.DANKNULL_DOCK)) {
						GlStateManager.translate(0.0D, 1.0D, 0.0D);

						GlStateManager.rotate(ModGlobals.TIME, 1.0F, ModGlobals.TIME, 1.0F);
						//GlStateManager.translate(0.0D, -5.0D, 0.0D);
					}
					else if (containedStack.getItem() == ModItems.DANK_NULL_PANEL) {
						GlStateManager.translate(0.0D, 0.1D, 0.0D);

						GlStateManager.rotate(ModGlobals.TIME, 1.0F, ModGlobals.TIME, 1.0F);
					}
					else {
						GlStateManager.translate(0.0D, 0.0D, 0.0D);

						GlStateManager.rotate(ModGlobals.TIME, 1.0F, ModGlobals.TIME, 1.0F);
						GlStateManager.translate(-0.5D, 0.0D, -0.5D);
					}
				}
				else {
					GlStateManager.rotate(ModGlobals.TIME, 1.0F, 1.0F, 1.0F);
				}

				containedItemModel = ForgeHooksClient.handleCameraTransforms(containedItemModel, ItemCameraTransforms.TransformType.NONE, false);
				String[] registryName = containedStack.getItem().getRegistryName().toString().split(":");
				String modID = registryName[0];
				if (modID.equalsIgnoreCase("danknull") || modID.equalsIgnoreCase("minecraft")) {
					if (containedStack.getItem() instanceof ItemBucket || containedStack.getItem() instanceof ItemBucketMilk) {
						//renderItem(containedStack, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(containedStack, EasyMappings.player().getEntityWorld(), EasyMappings.player()));
					}
					else {
						renderItem(containedStack, containedItemModel);
						GlStateManager.enableBlend();
					}
				}
				else {
					renderItem(containedStack, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(containedStack, EasyMappings.player().getEntityWorld(), EasyMappings.player()));
				}
				GlStateManager.popMatrix();

			}
			GlStateManager.enableLighting();

			renderItem(item, holderModel);

			GlStateManager.disableLighting();

		}

	}

	private boolean isStackInHand(ItemStack itemStackIn) {
		EntityPlayer player = EasyMappings.player();
		if ((player.getHeldItemMainhand() == itemStackIn) || (player.getHeldItemOffhand() == itemStackIn)) {
			return true;
		}
		return false;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return new ArrayList<BakedQuad>();
	}

	public void renderItem(ItemStack stack, IBakedModel model) {
		if (!stack.isEmpty()) {
			if (model.isBuiltInRenderer() && !(stack.getItem() instanceof ItemDankNull) && !(stack.getItem() instanceof ItemDankNullHolder)) {
				Minecraft.getMinecraft().getItemRenderer().renderItem(EasyMappings.player(), stack, ItemCameraTransforms.TransformType.NONE);
			}
			else {
				RenderModel.render(model, stack);

				if (stack.hasEffect()) {
					if ((stack.getItem() instanceof ItemDankNull)) {
						if (!Options.superShine) {
							GlintEffectRenderer.apply(model, stack.getItemDamage());
						}
						else {
							GlintEffectRenderer.apply2(model, DankNullUtils.getColor(stack.getItemDamage(), false));
						}
					}
					else {
						GlintEffectRenderer.apply(model, -1);
					}
				}
			}
		}
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	@SubscribeEvent
	public void tickEvent(TickEvent.PlayerTickEvent e) {
		if (e.side == Side.CLIENT) {
			if (ModGlobals.TIME >= 360.1F) {
				ModGlobals.TIME = 0.0F;
			}
			ModGlobals.TIME += 0.75F;
		}
	}

	@Override
	public IModelState getTransforms() {
		return TransformUtils.DEFAULT_BLOCK;
	}
}
