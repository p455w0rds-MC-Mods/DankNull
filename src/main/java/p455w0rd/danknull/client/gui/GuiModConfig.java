package p455w0rd.danknull.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import p455w0rdslib.util.TextUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import p455w0rd.danknull.init.ModConfig;
import p455w0rd.danknull.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class GuiModConfig extends GuiConfig {

	public GuiModConfig(GuiScreen parent) {
		super(getParent(parent), getConfigElements(), ModGlobals.MODID, false, false, getTitle(parent));
	}

	private static GuiScreen getParent(GuiScreen parent) {
		return parent;
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> configElements = new ArrayList<IConfigElement>();
		Configuration config = ModConfig.CONFIG;
		if (config != null) {
			ConfigCategory categoryClient = config.getCategory(ModConfig.CLIENT_CAT);
			configElements.addAll(new ConfigElement(categoryClient).getChildElements());
		}
		return configElements;
	}

	private static String getTitle(GuiScreen parent) {
		return TextUtils.translate(ModGlobals.NAME + " Config");
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}
}