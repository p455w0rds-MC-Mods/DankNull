package p455w0rd.danknull.client.render;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.render.item.IItemRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

/**
 * @author p455w0rd
 *
 */
public class PModelRegistryHelper extends ModelRegistryHelper {

	public static void registerMetaRenderer(Item item, IItemRenderer renderer, int damage) {
		final ModelResourceLocation modelLoc = new ModelResourceLocation(Item.REGISTRY.getNameForObject(item) + "" + damage + damage, "inventory");
		ModelLoader.setCustomModelResourceLocation(item, damage, modelLoc);
		register(modelLoc, renderer);
	}

}