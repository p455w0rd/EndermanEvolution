package p455w0rd.endermanevo.client.model.layers;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import p455w0rd.endermanevo.util.EntityUtils;

/**
 * @author p455w0rd
 *
 */
public class LayerSkullEyes implements LayerRenderer<EntityLivingBase> {

	private final RenderLivingBase<EntityLivingBase> livingRenderer;

	public LayerSkullEyes(final RenderLivingBase<EntityLivingBase> renderer) {
		livingRenderer = renderer;
	}

	@Override
	public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
		if (!EntityUtils.isWearingCustomSkull(entitylivingbaseIn) || entitylivingbaseIn instanceof EntityAnimal) {
			return;
		}
		livingRenderer.bindTexture(EntityUtils.getSkullModel(entitylivingbaseIn).getLightMap());
		EntityUtils.getSkullModel(entitylivingbaseIn).renderLightMap(netHeadYaw, headPitch, entitylivingbaseIn);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}
