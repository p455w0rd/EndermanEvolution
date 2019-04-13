package p455w0rd.endermanevo.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.endermanevo.init.*;

public class CommonProxy {

	public void preInit(final FMLPreInitializationEvent e) {
		ModConfig.init();
		ModBlocks.init();
		ModIntegration.preInit();
		ModNetworking.init();
		ModEvents.init();
	}

	public void init(final FMLInitializationEvent e) {
		ModEntities.init();
		ModIntegration.init();
		ModWorldGeneration.init();
	}

	public void postInit(final FMLPostInitializationEvent e) {
		ModIntegration.postInit();
	}

	public EntityPlayer getPlayer() {
		return null;
	}

}
