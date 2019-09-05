package p455w0rd.danknull.api;

import p455w0rdslib.util.TextUtils;

/**
 * @author p455w0rd
 *
 */
public class DankNullItemModes {

	public static enum ItemExtractionMode {

			KEEP_ALL(Integer.MAX_VALUE, TextUtils.translate("dn.not_extract.desc")),
			KEEP_1(1, TextUtils.translate("dn.extract_all_but.desc") + " 1"),
			KEEP_16(16, TextUtils.translate("dn.extract_all_but.desc") + " 16"),
			KEEP_64(64, TextUtils.translate("dn.extract_all_but.desc") + " 64"),
			KEEP_NONE(0, TextUtils.translate("dn.extract_all.desc"));

		public static ItemExtractionMode[] VALUES = values();
		int number = 0;
		String msg;

		ItemExtractionMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return TextUtils.translate("dn.will.desc") + " " + msg + " " + TextUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return TextUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

	public static enum ItemPlacementMode {

			KEEP_ALL(Integer.MAX_VALUE, TextUtils.translate("dn.not_place.desc")),
			KEEP_1(1, TextUtils.translate("dn.place_all_but.desc") + " 1"),
			KEEP_16(16, TextUtils.translate("dn.place_all_but.desc") + " 16"),
			KEEP_64(64, TextUtils.translate("dn.place_all_but.desc") + " 64"),
			KEEP_NONE(0, TextUtils.translate("dn.place_all.desc"));

		public static ItemPlacementMode[] VALUES = values();
		int number = 0;
		String msg;

		ItemPlacementMode(final int numberToKeep, final String message) {
			number = numberToKeep;
			msg = message;
		}

		public int getNumberToKeep() {
			return number;
		}

		public String getMessage() {
			return TextUtils.translate("dn.will.desc") + " " + msg + " " + TextUtils.translate("dn.from_slot.desc");
		}

		public String getTooltip() {
			if (toString().equals("KEEP_ALL")) {
				return TextUtils.translate("dn.do.desc") + " " + msg;
			}
			return msg.substring(0, 1).toUpperCase() + msg.substring(1);
		}

	}

}
