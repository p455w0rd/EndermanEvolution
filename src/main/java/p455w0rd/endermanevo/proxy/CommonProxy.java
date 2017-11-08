package p455w0rd.endermanevo.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
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
	}

	public void init(FMLInitializationEvent e) {
		ModEvents.init();
		ModEntities.init();
		ModIntegration.init();
	}

	public void postInit(FMLPostInitializationEvent e) {
		ModIntegration.postInit();
	}

	public void serverStarting(FMLServerStartingEvent e) {
	}

	public void serverStopped(FMLServerStoppedEvent event) {
	}

	public EntityPlayer getPlayer() {
		return null;
	}

}
