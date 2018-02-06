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

	private RenderLivingBase<EntityLivingBase> livingRenderer;

	public LayerSkullEyes(RenderLivingBase<EntityLivingBase> renderer) {
		livingRenderer = renderer;
	}

	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
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
