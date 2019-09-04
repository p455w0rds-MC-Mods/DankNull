package p455w0rd.danknull.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModGlobals;
import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
public class GuiModConfig extends GuiConfig {

	public GuiModConfig(final GuiScreen parent) {
		super(getParent(parent), getConfigElements(), ModGlobals.MODID, false, false, getTitle(parent));
	}

	private static GuiScreen getParent(final GuiScreen parent) {
		return parent;
	}

	private static List<IConfigElement> getConfigElements() {
		final List<IConfigElement> configElements = new ArrayList<>();
		final Configuration config = ModConfig.CONFIG;
		if (config != null) {
			final ConfigCategory categoryClient = config.getCategory(ModConfig.CLIENT_CAT);
			configElements.addAll(new ConfigElement(categoryClient).getChildElements());
		}
		return configElements;
	}

	private static String getTitle(final GuiScreen parent) {
		return TextUtils.translate(ModGlobals.NAME + " Config");
	}

}