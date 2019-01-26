package p455w0rd.endermanevo.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import p455w0rd.endermanevo.entity.EntityFrienderPearl;
import p455w0rd.endermanevo.init.ModItems;
import p455w0rd.endermanevo.init.ModRendering;

/**
 * @author p455w0rd
 *
 */
public class RenderFrienderPearl extends RenderSnowball<EntityFrienderPearl> {

	public RenderFrienderPearl() {
		super(ModRendering.getRenderManager(), ModItems.FRIENDER_PEARL, Minecraft.getMinecraft().getRenderItem());
	}

}
