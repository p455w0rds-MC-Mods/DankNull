package p455w0rd.danknull.integration;

import com.google.common.collect.Lists;

import p455w0rd.danknull.init.ModIntegration.Mods;

/**
 * @author p455w0rd
 *
 */
public class ItemScroller {

	public static void blackListSlots() {
		if (Mods.ITEMSCROLLER.isLoaded()) {
			fi.dy.masa.itemscroller.config.Configs.SLOT_BLACKLIST.addAll(Lists.<String>newArrayList("p455w0rd.danknull.inventory.slot.SlotDankNull"));
		}
	}

}
