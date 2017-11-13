/*
 * This file is part of p455w0rd's Things.
 * Copyright (c) 2016, p455w0rd (aka TheRealp455w0rd), All rights reserved
 * unless
 * otherwise stated.
 *
 * p455w0rd's Things is free software: you can redistribute it and/or modify
 * it under the terms of the MIT License.
 *
 * p455w0rd's Things is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * MIT License for more details.
 *
 * You should have received a copy of the MIT License
 * along with p455w0rd's Things. If not, see
 * <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.endermanevo.client.model.layers;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import p455w0rd.endermanevo.client.render.CustomChestRenderer;
import p455w0rd.endermanevo.client.render.CustomChestRenderer.ChestType;
import p455w0rd.endermanevo.client.render.RenderFrienderman;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import p455w0rd.endermanevo.integration.EnderStorage;
import p455w0rd.endermanevo.integration.IronChests;
import p455w0rdslib.util.RenderUtils;

/**
 * @author p455w0rd
 *
 */
public class LayerHeldBlock3 implements LayerRenderer<EntityFrienderman> {
	private final RenderFrienderman endermanRenderer;

	public LayerHeldBlock3(RenderFrienderman endermanRendererIn) {
		endermanRenderer = endermanRendererIn;
	}

	@Override
	public void doRenderLayer(EntityFrienderman frienderman, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		IBlockState iblockstate = frienderman.getHeldBlockState();
		ItemStack stack = frienderman.getHeldItemStack();

		if (iblockstate != null) {
			if (iblockstate.getBlock() == Blocks.RED_FLOWER) {
				GlStateManager.enableRescaleNormal();
				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.04F, 0.6875F + -0.085F, -1.0F);
				GlStateManager.rotate(220.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				Minecraft.getMinecraft().getItemRenderer().renderItem(frienderman, new ItemStack(Blocks.RED_FLOWER), ItemCameraTransforms.TransformType.NONE);
				GlStateManager.popMatrix();
				GlStateManager.disableRescaleNormal();
			}
			else {
				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
				GlStateManager.enableRescaleNormal();
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 0.6875F, -0.75F);
				GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.25F, 0.1875F, 0.25F);
				GlStateManager.scale(-0.5F, -0.5F, 0.5F);
				int i = frienderman.getBrightnessForRender();
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
		else if (!stack.isEmpty()) {
			if (frienderman.deathTime > 0) {
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.25F, 0.6875F, -0.75F);
			GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translate(0.6F, 0.1F, -0.25F);
			GlStateManager.scale(-0.7F, -0.7F, 0.7F);
			if (Mods.ENDERSTORAGE.isLoaded() && frienderman.isHoldingEnderStorageChest()) {
				EnderStorage.renderItemChest(stack, -frienderman.getLidAngle());
			}
			else if (frienderman.isHoldingVanillaChest()) {
				ChestType type = null;
				switch (frienderman.getVanillaChestType()) {
				case ENDER:
					type = ChestType.ENDER;
					break;
				case NORMAL:
					type = ChestType.NORMAL;
					break;
				case TRAPPED:
				default:
					type = ChestType.TRAPPED;
					break;
				}
				CustomChestRenderer.renderChest(type, -frienderman.getLidAngle());
			}
			else if (frienderman.isHoldingVanillaShulkerBox()) {
				CustomChestRenderer.renderShulkerBox(BlockShulkerBox.getColorFromItem(stack.getItem()).getMetadata(), frienderman.getLidAngle());
			}
			else if (frienderman.isHoldingIronChest()) {
				IronChests.renderChest(stack, -frienderman.getLidAngle());
			}
			else if (frienderman.isHoldingIronShulkerBox()) {
				IronChests.renderShulkerBox(stack, frienderman.getLidAngle());
			}
			else {
				RenderUtils.getRenderItem().renderItem(stack, RenderUtils.getMesher().getItemModel(stack));
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
