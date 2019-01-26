package p455w0rd.endermanevo.integration;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import p455w0rd.endermanevo.api.*;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class TOP {

	public static String doSneak = TextFormatting.BOLD + "" + TextFormatting.ITALIC + "<Sneak for Info> ";
	private static boolean registered;

	public static void init() {
		if (registered) {
			return;
		}
		registered = true;
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "p455w0rd.endermanevo.integration.TOP$GetTheOneProbe");
	}

	public static class GetTheOneProbe implements com.google.common.base.Function<ITheOneProbe, Void> {

		public static ITheOneProbe probe;

		@Nullable
		@Override
		public Void apply(ITheOneProbe theOneProbe) {
			probe = theOneProbe;
			probe.registerEntityDisplayOverride((mode, probeInfo, player, world, entity, data) -> {
				if (entity instanceof ITOPEntityDisplayOverride) {
					ITOPEntityDisplayOverride provider = (ITOPEntityDisplayOverride) entity;
					provider.overrideStandardInfo(mode, probeInfo, player, world, entity, data);
					return true;
				}
				return false;
			});
			probe.registerEntityProvider(new IProbeInfoEntityProvider() {
				@Override
				public String getID() {
					return ModGlobals.MODID + ":default";
				}

				@Override
				public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
					if (entity instanceof ITOPEntityInfoProvider) {
						ITOPEntityInfoProvider provider = (ITOPEntityInfoProvider) entity;
						provider.addProbeInfo(mode, probeInfo, player, world, entity, data);
					}
				}
			});
			probe.registerProvider(new IProbeInfoProvider() {

				@Override
				public String getID() {
					return ModGlobals.MODID + ":default";
				}

				@Override
				public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
					TileEntity te = world.getTileEntity(data.getPos());
					if (te != null && te instanceof ITOPInfoProvider) {
						ITOPInfoProvider provider = (ITOPInfoProvider) te;
						provider.addProbeInfo(mode, probeInfo, player, world, blockState, data);
					}
				}

			});
			probe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
				TileEntity te = world.getTileEntity(data.getPos());
				if (te != null && te instanceof ITOPBlockDisplayOverride) {
					ITOPBlockDisplayOverride provider = (ITOPBlockDisplayOverride) te;
					return provider.overrideStandardInfo(mode, probeInfo, player, world, blockState, data);
				}
				return false;
			});
			return null;
		}
	}

}
