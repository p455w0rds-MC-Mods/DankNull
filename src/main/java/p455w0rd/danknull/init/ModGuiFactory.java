package p455w0rd.danknull.init;

import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import p455w0rd.danknull.client.gui.GuiModConfig;

/**
 * @author p455w0rd
 *
 */
public class ModGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {

	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new GuiModConfig(parentScreen);
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiModConfig.class;
	}

	@Nullable
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Nullable
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

}
