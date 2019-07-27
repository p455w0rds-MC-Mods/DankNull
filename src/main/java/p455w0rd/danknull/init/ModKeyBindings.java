package p455w0rd.danknull.init;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * @author p455w0rd
 *
 */
public class ModKeyBindings {

	private static final String CATEGORY = "key.categories.danknull";
	private static KeyBinding nextItem;
	private static KeyBinding previousItem;
	private static KeyBinding openDankNull;
	private static KeyBinding toggleHUDOverlay;

	public static void register() {
		ClientRegistry.registerKeyBinding(getNextItemKeyBind());
		ClientRegistry.registerKeyBinding(getPreviousItemKeyBind());
		ClientRegistry.registerKeyBinding(getOpenDankNullKeyBind());
		ClientRegistry.registerKeyBinding(getToggleHUDKeyBind());
	}

	public static boolean isAnyModKeybindPressed() {
		return getNextItemKeyBind().isPressed() || //@formatter:off
				getPreviousItemKeyBind().isPressed() ||
				getOpenDankNullKeyBind().isPressed() ||
				getToggleHUDKeyBind().isPressed();//@formatter:on
	}

	public static KeyBinding getNextItemKeyBind() {
		if (nextItem == null) {
			nextItem = new KeyBinding("key.next_item.desc", Keyboard.CHAR_NONE, CATEGORY);
		}
		return nextItem;
	}

	public static KeyBinding getPreviousItemKeyBind() {
		if (previousItem == null) {
			previousItem = new KeyBinding("key.previous_item.desc", Keyboard.CHAR_NONE, CATEGORY);
		}
		return previousItem;
	}

	public static KeyBinding getOpenDankNullKeyBind() {
		if (openDankNull == null) {
			openDankNull = new KeyBinding("key.open_danknull.desc", Keyboard.CHAR_NONE, CATEGORY);
		}
		return openDankNull;
	}

	public static KeyBinding getToggleHUDKeyBind() {
		if (toggleHUDOverlay == null) {
			toggleHUDOverlay = new KeyBinding("key.togglehud.desc", Keyboard.CHAR_NONE, CATEGORY);
		}
		return toggleHUDOverlay;
	}

}
