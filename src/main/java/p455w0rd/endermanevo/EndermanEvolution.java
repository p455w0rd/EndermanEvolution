package p455w0rd.endermanevo;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.endermanevo.init.ModGlobals;
import p455w0rd.endermanevo.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, guiFactory = ModGlobals.GUI_FACTORY, acceptedMinecraftVersions = "[1.12.2]", certificateFingerprint = "@FINGERPRINT@")
public class EndermanEvolution {

	@SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Instance("endermanevo")
	public static EndermanEvolution INSTANCE;

	@EventHandler
	public void preInit(final FMLPreInitializationEvent e) {
		INSTANCE = this;
		PROXY.preInit(e);
	}

	@EventHandler
	public void init(final FMLInitializationEvent e) {
		PROXY.init(e);
	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent e) {
		PROXY.postInit(e);
	}

}
