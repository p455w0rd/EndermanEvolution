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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import p455w0rd.endermanevo.client.render.RenderEvolvedEnderman;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;

/**
 * @author p455w0rd
 *
 */
public class LayerHeldBlock2 implements LayerRenderer<EntityEvolvedEnderman> {
	private final RenderEvolvedEnderman endermanRenderer;

	public LayerHeldBlock2(RenderEvolvedEnderman endermanRendererIn) {
		endermanRenderer = endermanRendererIn;
	}

	@Override
	public void doRenderLayer(EntityEvolvedEnderman entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		IBlockState iblockstate = entitylivingbaseIn.getHeldBlockState();

		if (iblockstate != null) {
			BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.6875F, -0.75F);
			GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.25F, 0.1875F, 0.25F);
			GlStateManager.scale(-0.5F, -0.5F, 0.5F);
			int i = entitylivingbaseIn.getBrightnessForRender();
			int j = i % 65536;
			int k = i / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			endermanRenderer.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			blockrendererdispatcher.renderBlockBrightness(iblockstate, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}