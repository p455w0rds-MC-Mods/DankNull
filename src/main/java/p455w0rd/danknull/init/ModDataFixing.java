package p455w0rd.danknull.init;

import javax.annotation.Nonnull;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.*;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import p455w0rd.danknull.init.ModGlobals.NBT;

/**
 * @author p455w0rd
 *
 */
public class ModDataFixing {

	private static final int DANKNULL_FIXES_VERSION = 101;

	public static void registerWalkers() {
		FMLCommonHandler.instance().getDataFixer().registerVanillaWalker(FixTypes.BLOCK_ENTITY, new DankNullWalker());
	}

	public static void registerFixes() {
		final ModFixs modFixs = FMLCommonHandler.instance().getDataFixer().init(ModGlobals.MODID, DANKNULL_FIXES_VERSION);
		modFixs.registerFix(FixTypes.ITEM_INSTANCE, DankNullFixer.getInstance());
	}

	public static class DankNullWalker implements IDataWalker {

		@Nonnull
		@Override
		public NBTTagCompound process(@Nonnull final IDataFixer fixer, @Nonnull final NBTTagCompound tileNBT, final int version) {
			if (ModBlocks.DANKNULL_DOCK.getRegistryName().equals(new ResourceLocation(tileNBT.getString(NBT.ID)))) {
				if (tileNBT.hasKey(NBT.DANKNULL_INVENTORY, Constants.NBT.TAG_COMPOUND)) {
					final NBTTagCompound itemstackHandler = tileNBT.getCompoundTag(NBT.DANKNULL_INVENTORY);
					DataFixesManager.processInventory(fixer, itemstackHandler, version, "Items");
					ModLogger.debug("Ran DankNullWalker on inventory {} of TileEntity {}", NBT.DANKNULL_INVENTORY, tileNBT.getString(NBT.ID));
				}
			}
			return tileNBT;
		}

	}

	public static class DankNullFixer implements IFixableData {

		private static final DankNullFixer INSTANCE = new DankNullFixer();
		private static final ResourceLocation oldDankNull = new ResourceLocation(ModGlobals.MODID, "dank_null");
		private static final ResourceLocation oldDankNullPanel = new ResourceLocation(ModGlobals.MODID, "dank_null_panel");

		private DankNullFixer() {
		}

		public static DankNullFixer getInstance() {
			return INSTANCE;
		}

		@Override
		public int getFixVersion() {
			return DANKNULL_FIXES_VERSION;
		}

		@Override
		public NBTTagCompound fixTagCompound(final NBTTagCompound nbt) {
			if (nbt.hasKey(NBT.ID, Constants.NBT.TAG_STRING)) {
				final ResourceLocation regName = new ResourceLocation(nbt.getString(NBT.ID));
				if (isValid(regName)) {
					if (nbt.hasKey(NBT.DAMAGE, Constants.NBT.TAG_SHORT)) {
						final int dmg = nbt.getShort(NBT.DAMAGE);
						final String newName = regName.equals(oldDankNull) ? getNewDNRegName(dmg) : getNewPanelRegName(dmg);
						nbt.setString(NBT.ID, newName);
						nbt.setShort(NBT.DAMAGE, (short) 0);
						ModLogger.debug("Item with id {}:{} found. Updated to {}:0", regName.toString(), dmg, newName);
					}
				}
			}
			return nbt;
		}

		private boolean isValid(final ResourceLocation regName) {
			return regName.equals(oldDankNull) || regName.equals(oldDankNullPanel);
		}

		private String getNewDNRegName(final int oldMeta) {
			return ModGlobals.MODID + ":dank_null_" + oldMeta;
		}

		private String getNewPanelRegName(final int oldMeta) {
			return ModGlobals.MODID + ":dank_null_panel_" + oldMeta;
		}

	}

}
