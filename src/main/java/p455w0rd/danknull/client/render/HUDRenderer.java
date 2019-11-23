package p455w0rd.danknull.client.render;

import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.danknull.api.DankNullItemModes.ItemPlacementMode;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.*;
import p455w0rd.danknull.init.ModConfig.Options;
import p455w0rd.danknull.init.ModGlobals.DankNullTier;
import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rdslib.util.*;

/**
 * @author p455w0rd
 *
 */
public class HUDRenderer {

	@SideOnly(Side.CLIENT)
	public static void renderHUD(final Minecraft mc, final ScaledResolution scaledRes) {
		if (!Options.showHUD || !mc.playerController.shouldDrawHUD() && !mc.player.capabilities.isCreativeMode) {
			return;
		}
		ItemStack currentItem = mc.player.inventory.getCurrentItem();
		if (currentItem.isEmpty() || !ItemDankNull.isDankNull(currentItem)) {
			currentItem = mc.player.getHeldItemOffhand();
		}
		if (!currentItem.isEmpty() && ItemDankNull.isDankNull(currentItem)) {
			final IDankNullHandler dankNullHandler = currentItem.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
			if (dankNullHandler.getSelected() < 0) {
				return;
			}
			final ItemStack selectedStack = dankNullHandler.getFullStackInSlot(dankNullHandler.getSelected());
			final TextureManager tm = mc.renderEngine;
			if (tm != null && !selectedStack.isEmpty()) {

				tm.bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/gui/danknullscreen0.png"));
				GlStateManager.enableBlend();
				GlStateManager.enableAlpha();
				GuiUtils.drawTexturedModalRect(scaledRes.getScaledWidth() - 106, scaledRes.getScaledHeight() - 45, 0, 210, 106, 45, 0);
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				mc.fontRenderer.drawStringWithShadow(currentItem.getDisplayName(), scaledRes.getScaledWidth() * 2 - 212 + 55, scaledRes.getScaledHeight() * 2 - 83, dankNullHandler.getTier().getHexColor(true));
				String selectedStackName = selectedStack.getDisplayName();
				final int itemNameWidth = mc.fontRenderer.getStringWidth(selectedStackName);
				if (itemNameWidth >= 88 && selectedStackName.length() >= 14) {
					selectedStackName = selectedStackName.substring(0, 14).trim() + "...";
				}
				final ItemPlacementMode placementMode = dankNullHandler.getPlacementMode(selectedStack);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.selected_item.desc") + ": " + selectedStackName, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 72, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.count.desc") + ": " + (ItemDankNull.getTier(currentItem) == DankNullTier.CREATIVE ? "Infinite" : selectedStack.getCount()), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 61, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.place.desc") + ": " + placementMode.getTooltip().replace(TextUtils.translate("dn.extract.desc").toLowerCase(Locale.ENGLISH), TextUtils.translate("dn.place.desc").toLowerCase(Locale.ENGLISH)).replace(TextUtils.translate("dn.extract.desc"), TextUtils.translate("dn.place.desc")), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 50, 16777215);
				mc.fontRenderer.drawStringWithShadow(TextUtils.translate("dn.extract.desc") + ": " + dankNullHandler.getExtractionMode(selectedStack).getTooltip(), scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 40, 16777215);

				final String keyBind = ModKeyBindings.getOpenDankNullKeyBind().getDisplayName();
				mc.fontRenderer.drawStringWithShadow(keyBind.equalsIgnoreCase("none") ? TextUtils.translate("dn.no_open_keybind.desc") : TextUtils.translate("dn.open_with.desc") + " " + keyBind, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 29, 16777215);
				String oreDictMode = TextUtils.translate("dn.ore_dictionary.desc") + ": " + (dankNullHandler.isOre(selectedStack) ? TextUtils.translate("dn.enabled.desc") : TextUtils.translate("dn.disabled.desc"));
				final boolean isOreDicted = DankNullHandler.getOreNames(selectedStack).size() > 0;
				if (!isOreDicted) {
					oreDictMode = TextUtils.translate("dn.not_oredicted.desc");
				}

				mc.fontRenderer.drawStringWithShadow(oreDictMode, scaledRes.getScaledWidth() * 2 - 212 + 45, scaledRes.getScaledHeight() * 2 - 18, 16777215);

				RenderHelper.enableGUIStandardItemLighting();
				GlStateManager.popMatrix();
				GlStateManager.pushMatrix();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				RenderUtils.getRenderItem().renderItemAndEffectIntoGUI(currentItem, scaledRes.getScaledWidth() - 106 + 5, scaledRes.getScaledHeight() - 20);
				GlStateManager.popMatrix();
			}
		}
	}

	public static void toggleHUD() {
		Options.showHUD = !Options.showHUD;
		ModConfig.getInstance().get(Configuration.CATEGORY_CLIENT, "showHUD", true).setValue(Options.showHUD);
		ModConfig.getInstance().save();
	}

}
