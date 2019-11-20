package p455w0rd.danknull.integration;

import javax.annotation.Nonnull;

import com.rwtema.extrautils2.compatibility.CompatHelper;
import com.rwtema.extrautils2.items.ItemAngelBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 *
 */
public class ExtraUtilities {

	public static ActionResult<ItemStack> tryPlaceAngelBlock(final ItemStack dankNull, final EntityPlayer player, final EnumHand hand) {
		final IDankNullHandler dankNullHandler = dankNull.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
		final ItemStack selectedStack = dankNullHandler.getSelected() > -1 ? dankNullHandler.getFullStackInSlot(dankNullHandler.getSelected()) : ItemStack.EMPTY;
		if (isAngelBlock(selectedStack)) {
			final ActionResult<ItemStack> result = angelPlace(selectedStack, player.getEntityWorld(), player, hand);
			if (result.getType() != EnumActionResult.FAIL) {
				if (!player.isCreative()) {
					dankNullHandler.extractItem(dankNullHandler.getSelected(), 1, false);
				}
			}
			return ActionResult.newResult(result.getType(), dankNull);
		}
		return ActionResult.newResult(EnumActionResult.FAIL, dankNull);
	}

	private static boolean isAngelBlock(final ItemStack stack) {
		return stack.getItem() instanceof ItemAngelBlock;
	}

	public static boolean isAngelBlockSelected(final ItemStack dankNull) {
		if (ItemDankNull.isDankNull(dankNull)) {
			final IDankNullHandler dankNullHandler = dankNull.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
			return isAngelBlock(dankNullHandler.getFullStackInSlot(dankNullHandler.getSelected()));
		}
		return false;
	}

	private static ActionResult<ItemStack> angelPlace(@Nonnull final ItemStack itemStack, final World world, final EntityPlayer player, final EnumHand hand) {
		if (world.isRemote) {
			return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
		}

		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY + player.getEyeHeight());
		int z = (int) Math.floor(player.posZ);

		final Vec3d look = player.getLookVec();

		final EnumFacing side = EnumFacing.getFacingFromVector((float) look.x, (float) look.y, (float) look.z);

		switch (side) {
		case DOWN:
			y = (int) (Math.floor(player.getEntityBoundingBox().minY) - 1);
			break;
		case UP:
			y = (int) (Math.ceil(player.getEntityBoundingBox().maxY) + 1);
			break;
		case NORTH:
			z = (int) (Math.floor(player.getEntityBoundingBox().minZ) - 1);
			break;
		case SOUTH:
			z = (int) (Math.floor(player.getEntityBoundingBox().maxZ) + 1);
			break;
		case WEST:
			x = (int) (Math.floor(player.getEntityBoundingBox().minX) - 1);
			break;
		case EAST:
			x = (int) (Math.floor(player.getEntityBoundingBox().maxX) + 1);
			break;
		}

		final BlockPos pos = new BlockPos(x, y, z);
		if (CompatHelper.canPlaceBlockHere(world, Block.getBlockFromItem(itemStack.getItem()), pos, false, side, player, itemStack)) {
			return ActionResult.newResult(itemStack.onItemUse(player, world, pos, hand, side, 0, 0, 0), itemStack);
		}

		return ActionResult.newResult(EnumActionResult.FAIL, itemStack);
	}

}
