/*
 * This file is part of Enderman Evolution.
 * Copyright (c) 2016, p455w0rd (aka TheRealp455w0rd), All rights reserved
 * unless
 * otherwise stated.
 *
 * Enderman Evolution is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * Enderman Evolution is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * MIT License for more details.
 *
 * You should have received a copy of the MIT License
 * along with Enderman Evolution. If not, see
 * <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.endermanevo.client.model.layers;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import p455w0rd.endermanevo.client.render.RenderEvolvedEnderman;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class LayerEndermanEvolvedEyes implements LayerRenderer<EntityEvolvedEnderman> {
	private static final ResourceLocation RES_ENDERMAN_EYES = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman_evolved_eyes.png");
	private final RenderEvolvedEnderman endermanRenderer;

	public LayerEndermanEvolvedEyes(final RenderEvolvedEnderman endermanRendererIn) {
		endermanRenderer = endermanRendererIn;
	}

	@Override
	public void doRenderLayer(final EntityEvolvedEnderman entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scale) {
		/*
		endermanRenderer.bindTexture(RES_ENDERMAN_EYES);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);//!entitylivingbaseIn.isInvisible());
		
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		endermanRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		endermanRenderer.setLightmap(entitylivingbaseIn);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		*/
		//endermanRenderer.setLightmap(entitylivingbaseIn);
		//endermanRenderer.bindTexture(RES_ENDERMAN_EYES);
		GlStateManager.pushMatrix();
		final float brightnessX = OpenGlHelper.lastBrightnessX;
		final float brightnessY = OpenGlHelper.lastBrightnessY;
		RenderHelper.enableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

		//render(netHeadYaw, headPitch, entitylivingbaseIn);
		GlStateManager.color(0, 0.75f, 0, 0.5f);

		endermanRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
		//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
		//

		//EntityUtils.getSkullModel(entitylivingbaseIn).renderLightMap(netHeadYaw, headPitch, entitylivingbaseIn);
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}