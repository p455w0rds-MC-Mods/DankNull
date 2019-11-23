package p455w0rd.danknull.init;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

/**
 * @author p455w0rd
 *
 */
public class ModGuiFactory implements IModGuiFactory {

	private static final String TITLE = ModGlobals.NAME + " Config";
	GuiConfig configGui = null;

	@Override
	public void initialize(final Minecraft mc) {
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(final GuiScreen parent) {
		if (configGui == null) {
			configGui = new GuiConfig(parent, ModConfig.getClientConfigElements(), ModGlobals.MODID, false, false, TITLE, "");
		}
		return configGui;
	}

	@Nullable
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

}
