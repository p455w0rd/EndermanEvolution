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
package p455w0rd.endermanevo.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import p455w0rd.endermanevo.api.EndermanType;
import p455w0rd.endermanevo.client.model.ModelEvolvedEnderman;
import p455w0rd.endermanevo.client.model.layers.*;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;

/**
 * @author p455w0rd
 *
 */
public class RenderEvolvedEnderman extends RenderEndermanBase<EntityEvolvedEnderman> {

	public RenderEvolvedEnderman() {
		super(EndermanType.EVOLED);
		addLayer(new LayerEndermanEvolvedEyes(this));
		addLayer(new LayerHeldBlock2(this));
		addLayer(new LayerEntityCharge<>(this, endermanModel));
	}

	@Override
	public void doRender(final EntityEvolvedEnderman entity, double x, final double y, double z, final float entityYaw, final float partialTicks) {
		if (endermanModel instanceof ModelEvolvedEnderman) {
			final ModelEvolvedEnderman model = (ModelEvolvedEnderman) endermanModel;
			final IBlockState iblockstate = entity.getHeldBlockState();
			model.isCarrying = iblockstate != null;
			model.isAttacking = entity.isScreaming();
			if (entity.isScreaming()) {
				x += getRandom().nextGaussian() * 0.02D;
				z += getRandom().nextGaussian() * 0.02D;
			}
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
			GlStateManager.popMatrix();
		}
	}

}
