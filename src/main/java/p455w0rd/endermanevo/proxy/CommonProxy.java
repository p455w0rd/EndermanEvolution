package p455w0rd.endermanevo.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import p455w0rd.endermanevo.init.ModBlocks;
import p455w0rd.endermanevo.init.ModConfig;
import p455w0rd.endermanevo.init.ModEntities;
import p455w0rd.endermanevo.init.ModEvents;
import p455w0rd.endermanevo.init.ModIntegration;
import p455w0rd.endermanevo.init.ModNetworking;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent e) {
		ModConfig.init();
		ModBlocks.init();
		ModIntegration.preInit();
		ModNetworking.init();
		ModEvents.init();
	}

	public void init(FMLInitializationEvent e) {
		ModEntities.init();
		ModIntegration.init();
	}

	public EntityPlayer getPlayer() {
		return null;
	}

}
