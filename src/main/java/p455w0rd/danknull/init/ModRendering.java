package p455w0rd.danknull.init;

import net.minecraftforge.common.MinecraftForge;
import p455w0rd.danknull.client.render.DankNullRenderer;

/**
 * @author p455w0rd
 *
 */
public class ModRendering {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new DankNullRenderer());
	}

}
