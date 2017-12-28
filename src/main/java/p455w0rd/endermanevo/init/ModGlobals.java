package p455w0rd.endermanevo.init;

import codechicken.lib.CodeChickenLib;

public class ModGlobals {
	public static final String MODID = "endermanevo";
	public static final String VERSION = "1.0.1";
	public static final String NAME = "Enderman Evolution";
	public static final String SERVER_PROXY = "p455w0rd.endermanevo.proxy.CommonProxy";
	public static final String CLIENT_PROXY = "p455w0rd.endermanevo.proxy.ClientProxy";
	public static final String GUI_FACTORY = "p455w0rd.endermanevo.init.ModGuiFactory";
	public static final String CONFIG_FILE = "config/EndermanEvolution.cfg";
	public static final String DEPENDANCIES = "after:EnderStorage;after:ironchest;after:JEI;after:theoneprobe;after:Waila;required-after:p455w0rdslib@[2.0.22,);" + CodeChickenLib.MOD_VERSION_DEP + "";

	public static float TIME = 0.0F;
	public static float TIME2 = 0.0F;
	public static long TIME_LONG = 0L;
	public static int ALPHA = 255;
	public static int RED = 255;
	public static int GREEN = 0;
	public static int BLUE = 0;
	public static int TURN = 0;
	public static int TIMER = 0;

}
