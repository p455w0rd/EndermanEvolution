package p455w0rd.endermanevo.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.render.TESRBlockSkull;
import p455w0rd.endermanevo.init.ModCreativeTab;
import p455w0rd.endermanevo.init.ModRendering;
import p455w0rdslib.util.EasyMappings;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
		ModCreativeTab.init();
		ClientRegistry.bindTileEntitySpecialRenderer(TileBlockSkull.class, new TESRBlockSkull());
	}

	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		ModRendering.init();
	}

	@Override
	public EntityPlayer getPlayer() {
		return EasyMappings.player();
	}

}
