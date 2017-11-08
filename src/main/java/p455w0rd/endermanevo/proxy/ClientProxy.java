package p455w0rd.endermanevo.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import p455w0rd.endermanevo.init.ModBlocks;
import p455w0rd.endermanevo.init.ModCreativeTab;
import p455w0rd.endermanevo.init.ModItems;
import p455w0rd.endermanevo.init.ModRendering;
import p455w0rdslib.util.EasyMappings;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		ModBlocks.preInitModels();
		ModItems.preInitModels();
		ModCreativeTab.init();
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		ModRendering.init();
	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

	@Override
	public void serverStarting(FMLServerStartingEvent e) {

	}

	@Override
	public EntityPlayer getPlayer() {
		return EasyMappings.player();
	}

}
