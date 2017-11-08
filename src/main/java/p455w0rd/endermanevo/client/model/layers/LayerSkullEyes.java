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
		//System.out.println(EntityUtils.getSkullModel(entitylivingbaseIn).getLightMap().getResourcePath());

		livingRenderer.bindTexture(EntityUtils.getSkullModel(entitylivingbaseIn).getLightMap());
		EntityUtils.getSkullModel(entitylivingbaseIn).renderLightMap(netHeadYaw, headPitch, entitylivingbaseIn);
		/*
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());
		int i = 61680;
		int j = 61680;
		int k = 0;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		livingRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		//livingRenderer.setLightmap(entitylivingbaseIn, partialTicks);
		int l = entitylivingbaseIn.getBrightnessForRender(partialTicks);
		int m = l % 65536;
		int n = l / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, m, n);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		*/
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

}
