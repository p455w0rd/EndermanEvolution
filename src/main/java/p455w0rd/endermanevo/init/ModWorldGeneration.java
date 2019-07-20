package p455w0rd.endermanevo.init;

import net.minecraftforge.fml.common.registry.GameRegistry;
import p455w0rd.endermanevo.init.ModConfig.ConfigOptions;
import p455w0rd.endermanevo.worldgen.EnderFlowerGenerator;

/**
 * @author p455w0rd
 *
 */
public class ModWorldGeneration {

	private static final EnderFlowerGenerator ENDER_FLOWER = new EnderFlowerGenerator();

	public static void init() {
		if (ConfigOptions.enableEnderFlowerWorldGen) {
			GameRegistry.registerWorldGenerator(ENDER_FLOWER, 0);
		}
	}

}
