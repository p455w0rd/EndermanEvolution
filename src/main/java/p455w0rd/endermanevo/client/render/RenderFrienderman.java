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
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import p455w0rd.endermanevo.client.model.ModelFrienderman;
import p455w0rd.endermanevo.client.model.layers.LayerEnderman3Eyes;
import p455w0rd.endermanevo.client.model.layers.LayerHeldBlock3;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class RenderFrienderman extends RenderLiving<EntityFrienderman> {

	private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman2/enderman3.png");
	/** The model of the enderman */
	private final ModelFrienderman endermanModel;
	private final Random rnd = new Random();

	public RenderFrienderman(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelFrienderman(0.05F), 0.25F);
		endermanModel = (ModelFrienderman) super.mainModel;
		addLayer(new LayerEnderman3Eyes(this));
		addLayer(new LayerHeldBlock3(this));
	}

	/**
	 * Renders the desired {@code T} type Entity.
	 */
	@Override
	public void doRender(EntityFrienderman entity, double x, double y, double z, float entityYaw, float partialTicks) {
		IBlockState iblockstate = entity.getHeldBlockState();
		ItemStack stack = entity.getHeldItemStack();
		endermanModel.isCarrying = iblockstate != null || stack != null;
		if (endermanModel.isCarrying && iblockstate != null) {
			endermanModel.carriedBlock = iblockstate.getBlock();
		}
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
	protected ResourceLocation getEntityTexture(EntityFrienderman entity) {
		return ENDERMAN_TEXTURES;
	}
}
