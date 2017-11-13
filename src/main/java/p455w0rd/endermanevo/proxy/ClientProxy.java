package p455w0rd.endermanevo.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import p455w0rd.endermanevo.init.ModCreativeTab;
import p455w0rd.endermanevo.init.ModRendering;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		ModCreativeTab.init();
		ModRendering.preInit();
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		ModRendering.init();
	}

	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}

}
