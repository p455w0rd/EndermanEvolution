package p455w0rd.endermanevo.client.model.layers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class LayerEntityCharge<T extends EntityLivingBase> implements LayerRenderer<EntityLivingBase> {

	private static final ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation(ModGlobals.MODID, "textures/entity/charge_nocolor.png");
	private final RenderLivingBase<T> entityRenderer;
	private final ModelBase entityModel;

	public LayerEntityCharge(final RenderLivingBase<T> rendererIn, final ModelBase modelIn) {
		entityRenderer = rendererIn;
		entityModel = modelIn;
	}

	@Override
	public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
		final boolean flag = !entitylivingbaseIn.isInvisible();
		EntityEvolvedEnderman enderman = null;
		if (entitylivingbaseIn instanceof EntityEvolvedEnderman) {
			enderman = (EntityEvolvedEnderman) entitylivingbaseIn;
		}
		if (!flag) {
			return;
		}
		GlStateManager.depthMask(!flag);
		entityRenderer.bindTexture(LIGHTNING_TEXTURE);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		final float f = entitylivingbaseIn.ticksExisted + partialTicks;
		GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		float r = 0;
		float g = 0.75F;
		final float b = 0;
		if (enderman != null && enderman.isAggro()) {
			g = 0;
			r = 0.75F;
		}
		GlStateManager.color(r, g, b, 0.5F);
		RenderHelper.enableStandardItemLighting();
		final float oldTexX = OpenGlHelper.lastBrightnessX;
		final float oldTexY = OpenGlHelper.lastBrightnessY;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 260.0F, 260.0F);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		if (entitylivingbaseIn instanceof EntitySlime) {
			GlStateManager.scale(1.3F, 1.5F, 1.3F);
			GlStateManager.translate(0.0F, -0.4F, 0.0F);
		}
		else if (entitylivingbaseIn instanceof EntityPlayer) {
			GlStateManager.scale(1.1F, 1.1F, 1.1F);
			GlStateManager.translate(0.0F, -0.03F, 0.0F);
		}
		else {
			GlStateManager.scale(1.1F, 1.1F, 1.1F);
			GlStateManager.translate(0.0F, 0.05F, 0.0F);
		}
		entityModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		GlStateManager.matrixMode(5890);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.depthMask(flag);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldTexX, oldTexY);
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

}