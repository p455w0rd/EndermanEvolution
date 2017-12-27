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
package p455w0rd.endermanevo.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.util.EnumParticles;
import p455w0rd.endermanevo.util.ParticleUtil;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
public class EntityFrienderPearl extends EntityThrowable {

	private EntityLivingBase thrower;

	public EntityFrienderPearl(World worldIn) {
		super(worldIn);
	}

	public EntityFrienderPearl(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
		thrower = throwerIn;
	}

	@SideOnly(Side.CLIENT)
	public EntityFrienderPearl(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public static void registerFixesThrowable(DataFixer p_189663_0_) {
		EntityThrowable.registerFixesThrowable(p_189663_0_, "ThrownFrienderpearl");
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		EntityLivingBase entitylivingbase = getThrower();

		if (result.entityHit != null) {
			if (result.entityHit == thrower) {
				return;
			}
			else {
				if (entitylivingbase != null && result.entityHit instanceof EntityCreature && !(result.entityHit instanceof EntityMob)) {
					EntityCreature passiveEntity = (EntityCreature) result.entityHit;
					passiveEntity.setPositionAndUpdate(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ);
					passiveEntity.fallDistance = 0.0F;
					setDead();
					return;
				}
			}
		}

		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos blockpos = result.getBlockPos();
			TileEntity tileentity = EasyMappings.world(this).getTileEntity(blockpos);

			if (tileentity instanceof TileEntityEndGateway) {
				TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

				if (entitylivingbase != null) {
					tileentityendgateway.teleportEntity(entitylivingbase);
					setDead();
					return;
				}

				tileentityendgateway.teleportEntity(this);
				return;
			}
		}
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			for (int i = 0; i < 32; ++i) {
				ParticleUtil.spawn(EnumParticles.LOVE, EasyMappings.world(this), posX, posY + rand.nextDouble() * 2.0D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
			}
		}

		if (!EasyMappings.world(this).isRemote) {
			if (entitylivingbase instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayermp = (EntityPlayerMP) entitylivingbase;

				if (entityplayermp.connection.getNetworkManager().isChannelOpen() && EasyMappings.world(entityplayermp) == EasyMappings.world(this) && !entityplayermp.isPlayerSleeping()) {
					EnderTeleportEvent event = new EnderTeleportEvent(entityplayermp, posX, posY, posZ, 5.0F);
					if (!MinecraftForge.EVENT_BUS.post(event)) {

						if (entitylivingbase.isRiding()) {
							entitylivingbase.dismountRidingEntity();
						}

						entitylivingbase.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
						entitylivingbase.fallDistance = 0.0F;
					}
				}
			}
			else if (entitylivingbase != null) {
				entitylivingbase.setPositionAndUpdate(posX, posY, posZ);
				entitylivingbase.fallDistance = 0.0F;
			}

			setDead();
		}
	}

	@Override
	public void onUpdate() {
		EntityLivingBase entitylivingbase = getThrower();

		if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive()) {
			setDead();
		}
		else {
			super.onUpdate();
		}
	}
}