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

import java.util.Random;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import p455w0rd.endermanevo.api.EndermanType;
import p455w0rd.endermanevo.init.ModRendering;

/**
 * @author p455w0rd
 *
 */
public abstract class RenderEndermanBase<T extends EntityLiving> extends RenderLiving<T> {

	protected final ModelBase endermanModel;
	private final Random rnd = new Random();
	private final EndermanType endermanType;

	public RenderEndermanBase(EndermanType endermanType) {
		super(ModRendering.getRenderManager(), endermanType.getModel(), endermanType.getShadowSize());
		this.endermanType = endermanType;
		endermanModel = super.mainModel;
	}

	public EndermanType getEndermanType() {
		return endermanType;
	}

	public Random getRandom() {
		return rnd;
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(T entity) {
		return endermanType.getEntityTexture();
	}

}
