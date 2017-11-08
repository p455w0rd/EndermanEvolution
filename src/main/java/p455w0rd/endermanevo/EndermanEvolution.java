package p455w0rd.endermanevo;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import p455w0rd.endermanevo.init.ModGlobals;
import p455w0rd.endermanevo.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, acceptedMinecraftVersions = "1.12")
public class EndermanEvolution {

	@SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Mod.Instance("endermanevo")
	public static EndermanEvolution INSTANCE;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		INSTANCE = this;
		PROXY.preInit(e);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		PROXY.init(e);
	}

}
