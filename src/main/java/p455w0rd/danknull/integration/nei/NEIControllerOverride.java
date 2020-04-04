package p455w0rd.danknull.integration.nei;

import codechicken.nei.NEIController;
import net.minecraft.client.gui.GuiScreen;
import p455w0rd.danknull.client.gui.GuiDankNull;

/**
 * @author p455w0rd
 */
public class NEIControllerOverride extends NEIController {

    @Override
    public boolean mouseClicked(GuiScreen screen, int mouseX, int mouseY, int button) {

        if (screen instanceof GuiDankNull) {
            return false;
        }
        return super.mouseClicked(screen, mouseX, mouseY, button);
    }

}
