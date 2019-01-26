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
package p455w0rd.endermanevo.client.model;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;

/**
 * @author p455w0rd
 *
 */
public class ModelEndermanBase extends ModelBiped {

	public Block carriedBlock;
	public boolean isCarrying;
	public boolean isAttacking;
	public boolean isPartying;

	public ModelEndermanBase(float scale) {
		super(0.0F, -14.0F, 64, 32);
		bipedHeadwear = new ModelRenderer(this, 0, 16);
		bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale - 0.5F);
		bipedHeadwear.setRotationPoint(0.0F, -14.0F, 0.0F);
		bipedBody = new ModelRenderer(this, 32, 16);
		bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
		bipedBody.setRotationPoint(0.0F, -14.0F, 0.0F);
		bipedRightArm = new ModelRenderer(this, 56, 0);
		bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, scale);
		bipedRightArm.setRotationPoint(-3.0F, -12.0F, 0.0F);
		bipedLeftArm = new ModelRenderer(this, 56, 0);
		bipedLeftArm.mirror = true;
		bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, scale);
		bipedLeftArm.setRotationPoint(5.0F, -12.0F, 0.0F);
		bipedRightLeg = new ModelRenderer(this, 56, 0);
		bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, scale);
		bipedRightLeg.setRotationPoint(-2.0F, -2.0F, 0.0F);
		bipedLeftLeg = new ModelRenderer(this, 56, 0);
		bipedLeftLeg.mirror = true;
		bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, scale);
		bipedLeftLeg.setRotationPoint(2.0F, -2.0F, 0.0F);
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
		bipedHead.showModel = true;
		bipedBody.rotateAngleX = 0.0F;
		bipedBody.rotationPointY = -14.0F;
		bipedBody.rotationPointZ = -0.0F;
		bipedRightLeg.rotateAngleX -= 0.0F;
		bipedLeftLeg.rotateAngleX -= 0.0F;
		bipedRightArm.rotateAngleX = (float) (bipedRightArm.rotateAngleX * 0.5D);
		bipedLeftArm.rotateAngleX = (float) (bipedLeftArm.rotateAngleX * 0.5D);
		bipedRightLeg.rotateAngleX = (float) (bipedRightLeg.rotateAngleX * 0.5D);
		bipedLeftLeg.rotateAngleX = (float) (bipedLeftLeg.rotateAngleX * 0.5D);

		if (bipedRightArm.rotateAngleX > 0.4F) {
			bipedRightArm.rotateAngleX = 0.4F;
		}

		if (bipedLeftArm.rotateAngleX > 0.4F) {
			bipedLeftArm.rotateAngleX = 0.4F;
		}

		if (bipedRightArm.rotateAngleX < -0.4F) {
			bipedRightArm.rotateAngleX = -0.4F;
		}

		if (bipedLeftArm.rotateAngleX < -0.4F) {
			bipedLeftArm.rotateAngleX = -0.4F;
		}

		if (bipedRightLeg.rotateAngleX > 0.4F) {
			bipedRightLeg.rotateAngleX = 0.4F;
		}

		if (bipedLeftLeg.rotateAngleX > 0.4F) {
			bipedLeftLeg.rotateAngleX = 0.4F;
		}

		if (bipedRightLeg.rotateAngleX < -0.4F) {
			bipedRightLeg.rotateAngleX = -0.4F;
		}

		if (bipedLeftLeg.rotateAngleX < -0.4F) {
			bipedLeftLeg.rotateAngleX = -0.4F;
		}

		if (isCarrying) {
			if (carriedBlock != null && carriedBlock == Blocks.RED_FLOWER) {
				bipedRightArm.rotateAngleX = -0.5F;
				bipedLeftArm.rotateAngleX = -0.5F;
				bipedRightArm.rotateAngleZ = -0.15F;
				bipedLeftArm.rotateAngleZ = 0.15F;
			}
			else {
				bipedRightArm.rotateAngleX = -0.5F;
				bipedLeftArm.rotateAngleX = -0.5F;
				bipedRightArm.rotateAngleZ = 0.05F;
				bipedLeftArm.rotateAngleZ = -0.05F;
			}
		}

		if (isPartying) {
			bipedHead.rotateAngleY = ageInTicks;
			if (!isCarrying) {
				//this.tail.rotateAngleX = 1.5388988F;
				//this.tail.rotationPointY = 22.97F;
				//bipedBody.rotationPointY = -18.4F;
				bipedLeftArm.rotateAngleX = ageInTicks;
				//bipedLeftArm.rotationPointY = 18.84F;
				bipedRightArm.rotateAngleX = -ageInTicks;
				//bipedRightArm.rotationPointY = 18.84F;
				//++bipedLeftLeg.rotationPointY;
				//++bipedRightLeg.rotationPointY;
				//++bipedLeftLeg.rotateAngleX;
				//++bipedRightLeg.rotateAngleX;
			}
		}

		bipedRightArm.rotationPointZ = 0.0F;
		bipedLeftArm.rotationPointZ = 0.0F;
		bipedRightLeg.rotationPointZ = 0.0F;
		bipedLeftLeg.rotationPointZ = 0.0F;
		bipedRightLeg.rotationPointY = -5.0F;
		bipedLeftLeg.rotationPointY = -5.0F;
		bipedHead.rotationPointZ = -0.0F;
		bipedHead.rotationPointY = -13.0F;
		bipedHeadwear.rotationPointX = bipedHead.rotationPointX;
		bipedHeadwear.rotationPointY = bipedHead.rotationPointY;
		bipedHeadwear.rotationPointZ = bipedHead.rotationPointZ;
		bipedHeadwear.rotateAngleX = bipedHead.rotateAngleX;
		bipedHeadwear.rotateAngleY = bipedHead.rotateAngleY;
		bipedHeadwear.rotateAngleZ = bipedHead.rotateAngleZ;

		if (isAttacking) {
			bipedHead.rotationPointY -= 5.0F;
		}
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
	}

}