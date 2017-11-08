package p455w0rd.endermanevo.init;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.endermanevo.network.PacketFriendermanRegistrySync;

public class ModNetworking {

	private static int packetId = 0;
	public static SimpleNetworkWrapper INSTANCE = null;

	private static int nextID() {
		return packetId++;
	}

	public static void init() {
		if (INSTANCE == null) {
			INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModGlobals.MODID);
		}
		ModLogger.info("Registering Packet Messages");
		INSTANCE.registerMessage(PacketFriendermanRegistrySync.Handler.class, PacketFriendermanRegistrySync.class, nextID(), Side.CLIENT);
	}

}
