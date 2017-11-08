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
package p455w0rd.endermanevo.client.render;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import p455w0rd.endermanevo.client.model.layers.LayerEnderman2Eyes;
import p455w0rd.endermanevo.client.model.layers.LayerHeldBlock2;
import p455w0rd.endermanevo.entity.EntityEnderman2;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class RenderEnderman2 extends RenderLiving<EntityEnderman2> {

	private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman2/enderman2.png");
	/** The model of the enderman */
	private final ModelEnderman endermanModel;
	private final Random rnd = new Random();

	public RenderEnderman2(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelEnderman(0.0F), 0.5F);
		endermanModel = (ModelEnderman) super.mainModel;
		addLayer(new LayerEnderman2Eyes(this));
		addLayer(new LayerHeldBlock2(this));
	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void doRender(EntityEnderman2 entity, double x, double y, double z, float entityYaw, float partialTicks) {
		IBlockState iblockstate = entity.getHeldBlockState();
		endermanModel.isCarrying = iblockstate != null;
		endermanModel.isAttacking = entity.isScreaming();

		if (entity.isScreaming()) {
			double d0 = 0.02D;
			x += rnd.nextGaussian() * 0.02D;
			z += rnd.nextGaussian() * 0.02D;
		}

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
	 */
	@Override
	protected ResourceLocation getEntityTexture(EntityEnderman2 entity) {
		return ENDERMAN_TEXTURES;
	}
}
