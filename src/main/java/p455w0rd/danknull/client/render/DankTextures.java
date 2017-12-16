package p455w0rd.danknull.client.render;

import codechicken.lib.texture.TextureUtils.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import p455w0rd.danknull.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class DankTextures implements IIconRegister {

	private static TextureMap map;
	private static final String ITEMS = ModGlobals.MODID + ":items/";
	private static final DankTextures INSTANCE = new DankTextures();

	public static final ResourceLocation DOCK_TEXTURE = new ResourceLocation(ModGlobals.MODID, "textures/models/danknull_dock.png");

	public static TextureAtlasSprite DANKNULL_PANEL_0;
	public static TextureAtlasSprite DANKNULL_PANEL_1;
	public static TextureAtlasSprite DANKNULL_PANEL_2;
	public static TextureAtlasSprite DANKNULL_PANEL_3;
	public static TextureAtlasSprite DANKNULL_PANEL_4;
	public static TextureAtlasSprite DANKNULL_PANEL_5;
	public static TextureAtlasSprite[] DANKNULL_PANELS;

	public static TextureAtlasSprite DANKNULL_DOCK_SPRITE;

	public static DankTextures getInstance() {
		return INSTANCE;
	}

	@Override
	public void registerIcons(TextureMap textureMap) {
		map = textureMap;

		DANKNULL_PANELS = new TextureAtlasSprite[] {
				DANKNULL_PANEL_0 = register(ITEMS + "dank_null_panel_0"),
				DANKNULL_PANEL_1 = register(ITEMS + "dank_null_panel_1"),
				DANKNULL_PANEL_2 = register(ITEMS + "dank_null_panel_2"),
				DANKNULL_PANEL_3 = register(ITEMS + "dank_null_panel_3"),
				DANKNULL_PANEL_4 = register(ITEMS + "dank_null_panel_4"),
				DANKNULL_PANEL_5 = register(ITEMS + "dank_null_panel_5")
		};
		DANKNULL_DOCK_SPRITE = register(ModGlobals.MODID + ":models/danknull_dock_sprite");
	}

	private static TextureAtlasSprite register(String sprite) {
		return map.registerSprite(new ResourceLocation(sprite));
	}

}
