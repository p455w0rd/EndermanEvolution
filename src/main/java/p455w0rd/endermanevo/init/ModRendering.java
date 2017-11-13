package p455w0rd.endermanevo.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.render.RenderEvolvedEnderman;
import p455w0rd.endermanevo.client.render.RenderEvolvedEndermite;
import p455w0rd.endermanevo.client.render.RenderFrienderman;
import p455w0rd.endermanevo.client.render.TESRBlockSkull;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;
import p455w0rd.endermanevo.entity.EntityEvolvedEndermite;
import p455w0rd.endermanevo.entity.EntityFrienderPearl;
import p455w0rd.endermanevo.entity.EntityFrienderman;

/**
 * @author p455w0rd
 *
 */
public class ModRendering {

	public static void preInit() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileBlockSkull.class, new TESRBlockSkull());
	}

	public static void init() {
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
		rm.entityRenderMap.put(EntityEvolvedEnderman.class, new RenderEvolvedEnderman(rm));
		rm.entityRenderMap.put(EntityFrienderman.class, new RenderFrienderman(rm));
		rm.entityRenderMap.put(EntityFrienderPearl.class, new RenderSnowball<EntityFrienderPearl>(rm, ModItems.FRIENDER_PEARL, itemRenderer));
		rm.entityRenderMap.put(EntityEvolvedEndermite.class, new RenderEvolvedEndermite(rm));
	}

}
