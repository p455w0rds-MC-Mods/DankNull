package p455w0rd.danknull.init;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModKeyBindings {

	private static KeyBinding nextItem;
	private static KeyBinding previousItem;

	public static void register() {
		ClientRegistry.registerKeyBinding(getNextItemKeyBind());
		ClientRegistry.registerKeyBinding(getPreviousItemKeyBind());
	}

	public static KeyBinding getNextItemKeyBind() {
		if (nextItem == null) {
			nextItem = new KeyBinding("key.next_item.desc", Keyboard.CHAR_NONE, "key.categories.danknull");
		}
		return nextItem;
	}

	public static KeyBinding getPreviousItemKeyBind() {
		if (previousItem == null) {
			previousItem = new KeyBinding("key.previous_item.desc", Keyboard.CHAR_NONE, "key.categories.danknull");
		}
		return previousItem;
	}

}
