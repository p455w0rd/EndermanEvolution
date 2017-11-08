package p455w0rd.endermanevo.init;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import p455w0rd.endermanevo.integration.EnderStorage;
import p455w0rd.endermanevo.integration.TOP;
import p455w0rd.endermanevo.integration.WAILA;

/**
 * @author p455w0rd
 *
 */
public class ModIntegration {

	public static void preInit() {
		if (Mods.TOP.isLoaded()) {
			TOP.init();
		}
	}

	public static void init() {
		if (Mods.ENDERSTORAGE.isLoaded()) {
			EnderStorage.init();
		}
		if (FMLCommonHandler.instance().getSide().isClient()) {
			if (Mods.WAILA.isLoaded()) {
				WAILA.init();
			}
		}
	}

	public static enum Mods {
			TOP("theoneprobe", "The One Probe"),
			ENDERSTORAGE("EnderStorage", "Ender Storage"),
			WAILA("Waila", "WAILA"),
			IRONCHESTS("ironchest", "Iron Chests"), JEI("JEI", "JEI");

		private String modid, name;

		Mods(String modidIn, String nameIn) {
			modid = modidIn;
			name = nameIn;
		}

		public String getId() {
			return modid;
		}

		public String getName() {
			return name;
		}

		public boolean isLoaded() {
			return Loader.isModLoaded(getId());
		}
	}

}
