package p455w0rd.danknull.integration;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import p455w0rdslib.LibGlobals.Mods;
import team.chisel.api.block.ICarvable;
import team.chisel.api.block.VariationData;

import javax.annotation.Nonnull;

/**
 * @author p455w0rd
 */
public class Chisel {

    public static boolean isBlockChiseled(@Nonnull final ItemStack stack) {
        if (Mods.CHISEL.isLoaded() && !stack.isEmpty() && stack.getItem() instanceof ItemBlock) {
            return isBlockChiseled(Block.getBlockFromItem(stack.getItem()));
        }
        return false;
    }

    public static boolean isBlockChiseled(final Block block) {
        if (Mods.CHISEL.isLoaded()) {
            return block instanceof ICarvable;
        }
        return false;
    }

    public static ICarvable getCarvable(final Block block) {
        if (block instanceof ICarvable) {
            return (ICarvable) block;
        }
        return null;
    }

    public static String getVariantName(final ItemStack stack) {
        if (isBlockChiseled(stack)) {
            final VariationData variationData = getCarvable(Block.getBlockFromItem(stack.getItem())).getVariationData(stack.getItemDamage());
            return I18n.translateToLocal(stack.getTranslationKey() + "." + variationData.name + ".desc.1");
        }
        return "";
    }

}
