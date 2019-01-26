package p455w0rd.endermanevo.api;

import mcjty.theoneprobe.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author p455w0rd
 *
 */
public interface ITOPEntityDisplayOverride {

	void overrideStandardInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data);

}
