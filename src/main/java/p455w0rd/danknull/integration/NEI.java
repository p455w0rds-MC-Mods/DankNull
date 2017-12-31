package p455w0rd.danknull.integration;

import codechicken.nei.NEIController;
import codechicken.nei.handler.NEIClientEventHandler;
import p455w0rd.danknull.init.ModIntegration.Mods;
import p455w0rd.danknull.integration.nei.NEIControllerOverride;

/**
 * @author p455w0rd
 *
 */
public class NEI {

	public static void init() {
		if (Mods.NEI.isLoaded()) {
			for (int i = 0; i < NEIClientEventHandler.inputHandlers.size(); i++) {
				if (NEIClientEventHandler.inputHandlers.get(i) instanceof NEIController) {
					NEIClientEventHandler.inputHandlers.remove(i);
					NEIClientEventHandler.inputHandlers.add(new NEIControllerOverride());
				}
			}
		}
	}

}
