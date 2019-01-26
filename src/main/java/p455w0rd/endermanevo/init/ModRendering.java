package p455w0rd.endermanevo.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.render.*;
import p455w0rd.endermanevo.entity.*;

/**
 * @author p455w0rd
 *
 */
public class ModRendering {

	private static RenderManager renderManager;

	public static void registerTileEntityRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileBlockSkull.class, new TESRBlockSkull());
	}

	public static RenderManager getRenderManager() {
		if (renderManager == null) {
			renderManager = Minecraft.getMinecraft().getRenderManager();
		}
		return renderManager;
	}

	private static void registerEntityRenderer(Class<? extends Entity> entityClass, Render<? extends Entity> renderer) {
		getRenderManager().entityRenderMap.put(entityClass, renderer);
	}

	public static void registerEntityRenderers() {
		registerEntityRenderer(EntityEvolvedEnderman.class, new RenderEvolvedEnderman());
		registerEntityRenderer(EntityFrienderman.class, new RenderFrienderman());
		registerEntityRenderer(EntityFrienderPearl.class, new RenderFrienderPearl());
		registerEntityRenderer(EntityEvolvedEndermite.class, new RenderEvolvedEndermite());
	}

}
