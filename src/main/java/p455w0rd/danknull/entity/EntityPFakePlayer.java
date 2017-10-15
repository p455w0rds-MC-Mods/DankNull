package p455w0rd.danknull.entity;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * FakePlayer for placing blocks with ease since {@link Item#onItemUse} changed
 *
 * @author <a href="https://github.com/KingLemming">@KingLemming</a>
 * @see <a href="https://github.com/CoFH/CoFHCore/blob/cebcf4af36f3edde04e13a06879b84cf539aa110/src/main/java/cofh/core/entity/CoFHFakePlayer.java">CoFHFakePlayer.java</a>
 *
 */
public class EntityPFakePlayer extends FakePlayer {

	public static Map<UUID, EntityPFakePlayer> REGISTRY = Maps.newHashMap();

	public static EntityPFakePlayer getFakePlayerForParent(EntityPlayer player) {
		if (!player.world.isRemote) {
			if (!REGISTRY.containsKey(player.getUniqueID())) {
				REGISTRY.put(player.getUniqueID(), new EntityPFakePlayer(player));
			}
			return REGISTRY.get(player.getUniqueID());
		}
		return null; //client
	}

	public boolean isSneaking = false;
	public ItemStack previousItem = ItemStack.EMPTY;
	public EntityPlayer parent;

	public EntityPFakePlayer(EntityPlayer parent) {
		super((WorldServer) parent.world, new GameProfile(UUID.randomUUID(), "[/dank/null clone|" + parent.getDisplayNameString() + "]"));
		this.parent = parent;
		connection = new NetServerHandlerFake(FMLCommonHandler.instance().getMinecraftServerInstance(), this);
		addedToChunk = false;
		startRiding(parent, true);
	}

	public void setItemInHand(ItemStack m_item) {

		inventory.currentItem = 0;
		inventory.setInventorySlotContents(0, m_item);
	}

	public void setItemInHand(int slot) {

		inventory.currentItem = slot;
	}

	@Override
	public void onDeath(DamageSource cause) {
		if (cause == DamageSource.OUT_OF_WORLD) {
			connection.sendPacket(new SPacketCombatEvent(getCombatTracker(), SPacketCombatEvent.Event.ENTITY_DIED, false));
			EntityLivingBase entitylivingbase = getAttackingEntity();
			if (entitylivingbase != null) {
				entitylivingbase.onItemPickup(this, scoreValue);
			}
			extinguish();
			setFlag(0, false);
			getCombatTracker().reset();
		}
	}

	@Override
	public Entity changeDimension(int dimensionIn) {
		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, dimensionIn)) {
			return this;
		}
		ObfuscationReflectionHelper.setPrivateValue(EntityPlayerMP.class, this, true, "invulnerableDimensionChange", "field_184851_cj");

		if (dimension == 1 && dimensionIn == 1) {
			world.removeEntity(this);

			if (!queuedEndExit) {
				queuedEndExit = true;
				connection.sendPacket(new SPacketChangeGameState(4, 0.0F));
			}

			return this;
		}
		else {
			if (dimension == 0 && dimensionIn == 1) {
				dimensionIn = 1;
			}

			mcServer.getPlayerList().changePlayerDimension(this, dimensionIn);
			return this;
		}
	}

	@Override
	public double getDistanceSq(double x, double y, double z) {

		return 0F;
	}

	@Override
	public double getDistance(double x, double y, double z) {

		return 0F;
	}

	@Override
	public boolean isSneaking() {

		return parent.isSneaking();
	}

	@Override
	public ITextComponent getDisplayName() {

		return new TextComponentString(getName());
	}

	@Override
	public float getEyeHeight() {

		return getDefaultEyeHeight() + eyeHeight;
	}

	@Override
	public float getDefaultEyeHeight() {

		return 1.1F;
	}

}