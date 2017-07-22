package p455w0rd.danknull.api;

import p455w0rd.danknull.blocks.tiles.TileDankNullDock.RedstoneMode;

/**
 * @author p455w0rd
 *
 */
public interface IRedstoneControllable {

	void setRedstoneMode(RedstoneMode mode);

	RedstoneMode getRedstoneMode();

	boolean isRedstoneRequirementMet();

	boolean hasRSSignal();

	void setRSSignal(boolean isPowered);

}
