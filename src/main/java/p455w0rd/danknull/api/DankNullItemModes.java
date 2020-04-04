package p455w0rd.danknull.api;

import net.minecraft.util.text.translation.I18n;

/**
 * @author p455w0rd
 */
public class DankNullItemModes {

    public enum ItemExtractionMode {

        KEEP_ALL(Integer.MAX_VALUE, I18n.translateToLocal("dn.not_extract.desc")),
        KEEP_1(1, I18n.translateToLocal("dn.extract_all_but.desc") + " 1"),
        KEEP_16(16, I18n.translateToLocal("dn.extract_all_but.desc") + " 16"),
        KEEP_64(64, I18n.translateToLocal("dn.extract_all_but.desc") + " 64"),
        KEEP_NONE(0, I18n.translateToLocal("dn.extract_all.desc"));

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
            return I18n.translateToLocal("dn.will.desc") + " " + msg + " " + I18n.translateToLocal("dn.from_slot.desc");
        }

        public String getTooltip() {
            if (toString().equals("KEEP_ALL")) {
                return I18n.translateToLocal("dn.do.desc") + " " + msg;
            }
            return msg.substring(0, 1).toUpperCase() + msg.substring(1);
        }

    }

    public enum ItemPlacementMode {

        KEEP_ALL(Integer.MAX_VALUE, I18n.translateToLocal("dn.not_place.desc")),
        KEEP_1(1, I18n.translateToLocal("dn.place_all_but.desc") + " 1"),
        KEEP_16(16, I18n.translateToLocal("dn.place_all_but.desc") + " 16"),
        KEEP_64(64, I18n.translateToLocal("dn.place_all_but.desc") + " 64"),
        KEEP_NONE(0, I18n.translateToLocal("dn.place_all.desc"));

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
            return I18n.translateToLocal("dn.will.desc") + " " + msg + " " + I18n.translateToLocal("dn.from_slot.desc");
        }

        public String getTooltip() {
            if (toString().equals("KEEP_ALL")) {
                return I18n.translateToLocal("dn.do.desc") + " " + msg;
            }
            return msg.substring(0, 1).toUpperCase() + msg.substring(1);
        }

    }

}
