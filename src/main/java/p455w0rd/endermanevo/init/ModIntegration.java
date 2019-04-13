package p455w0rd.endermanevo.init;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import p455w0rd.endermanevo.integration.*;

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

	public static void postInit() {
		if (Mods.IE.isLoaded()) {
			IE.registerClocheRecipe();
		}
	}

	public static enum Mods {
			TOP("theoneprobe", "The One Probe"),
			ENDERSTORAGE("enderstorage", "Ender Storage"),
			WAILA("waila", "WAILA"),
			IRONCHESTS("ironchest", "Iron Chests"), JEI("jei", "Just Enough Items"),
			TINKERS("tconstruct", "Tinkers Construct"), IE("immersiveengineering", "Immersive Engineering");

		private final String modid, name;

		Mods(final String modidIn, final String nameIn) {
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
