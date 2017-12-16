package p455w0rd.danknull.integration;

import com.google.common.base.Function;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import p455w0rd.danknull.entity.EntityPFakePlayer;

/**
 * @author p455w0rd
 *
 */
public class STG {

	public static void registerFakePlayer() {
		FMLInterModComms.sendFunctionMessage("stg", "", "p455w0rd.danknull.integration.STG$GetDankNullFakePlayer");
	}

	public static class GetDankNullFakePlayer implements Function<EntityLivingBase, Boolean> {

		@Override
		public Boolean apply(EntityLivingBase input) {
			return !(input instanceof EntityPFakePlayer);
		}

	}

}
