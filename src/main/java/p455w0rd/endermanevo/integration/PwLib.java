package p455w0rd.endermanevo.integration;

import net.minecraftforge.common.capabilities.Capability;
import p455w0rd.endermanevo.init.ModConfig.ConfigOptions;
import p455w0rdslib.capabilities.CapabilityLightEmitter;

/**
 * @author p455w0rd
 *
 */
public class PwLib {

	public static boolean checkCap(final Capability<?> capability) {
		return CapabilityLightEmitter.checkCap(capability) && ConfigOptions.enableColoredLighting;
	}

}
