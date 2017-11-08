package p455w0rd.endermanevo.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import p455w0rd.endermanevo.client.render.RenderEnderman2;
import p455w0rd.endermanevo.client.render.RenderEndermite2;
import p455w0rd.endermanevo.client.render.RenderFrienderman;
import p455w0rd.endermanevo.entity.EntityEnderman2;
import p455w0rd.endermanevo.entity.EntityEndermite2;
import p455w0rd.endermanevo.entity.EntityFrienderPearl;
import p455w0rd.endermanevo.entity.EntityFrienderman;

/**
 * @author p455w0rd
 *
 */
public class ModRendering {

	public static void init() {
		RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
		rm.entityRenderMap.put(EntityEnderman2.class, new RenderEnderman2(rm));
		rm.entityRenderMap.put(EntityFrienderman.class, new RenderFrienderman(rm));
		rm.entityRenderMap.put(EntityFrienderPearl.class, new RenderSnowball<EntityFrienderPearl>(rm, ModItems.FRIENDER_PEARL, itemRenderer));
		rm.entityRenderMap.put(EntityEndermite2.class, new RenderEndermite2(rm));
	}

}
