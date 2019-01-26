package p455w0rd.endermanevo.client.model.layers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import p455w0rd.endermanevo.client.model.ModelEndermanBase;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class LayerMiniFrienderman implements LayerRenderer<EntityPlayer> {

	private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman3.png");
	private final ModelEndermanBase endermanModel;

	public LayerMiniFrienderman() {
		endermanModel = new ModelEndermanBase(0.00001F);
	}

	@Override
	public void doRenderLayer(EntityPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		//GlStateManager.enableAlpha();
		//GlStateManager.depthMask(true);
		//GlStateManager.disableRescaleNormal();
		//GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		//GlStateManager.enableTexture2D();
		Minecraft.getMinecraft().getTextureManager().bindTexture(ENDERMAN_TEXTURES);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		endermanModel.isCarrying = false;
		endermanModel.isAttacking = false;
		endermanModel.bipedHead.offsetY = 0.25F;
		endermanModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		GlStateManager.translate(0.5F, 0.0F, 0.5F);
		//GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

}
