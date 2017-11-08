package p455w0rd.endermanevo.entity;

import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import p455w0rd.endermanevo.util.EnumParticles;
import p455w0rd.endermanevo.util.ParticleUtil;
import p455w0rdslib.util.EasyMappings;

/**
 * @author p455w0rd
 *
 */
public class EntityEndermite2 extends EntityEndermite {

	private int lifetime;

	public EntityEndermite2(World worldIn) {
		super(worldIn);
	}

	@Override
	public void onLivingUpdate() {
		updateArmSwingProgress();
		float f = getBrightness();

		if (f > 0.5F) {
			idleTime += 2;
		}

		if (jumpTicks > 0) {
			--jumpTicks;
		}

		if (newPosRotationIncrements > 0 && !canPassengerSteer()) {
			double d0 = posX + (interpTargetX - posX) / newPosRotationIncrements;
			double d1 = posY + (interpTargetY - posY) / newPosRotationIncrements;
			double d2 = posZ + (interpTargetZ - posZ) / newPosRotationIncrements;
			double d3 = MathHelper.wrapDegrees(interpTargetYaw - rotationYaw);
			rotationYaw = (float) (rotationYaw + d3 / newPosRotationIncrements);
			rotationPitch = (float) (rotationPitch + (interpTargetPitch - rotationPitch) / newPosRotationIncrements);
			--newPosRotationIncrements;
			setPosition(d0, d1, d2);
			setRotation(rotationYaw, rotationPitch);
		}
		else if (!isServerWorld()) {
			motionX *= 0.98D;
			motionY *= 0.98D;
			motionZ *= 0.98D;
		}

		if (Math.abs(motionX) < 0.003D) {
			motionX = 0.0D;
		}

		if (Math.abs(motionY) < 0.003D) {
			motionY = 0.0D;
		}

		if (Math.abs(motionZ) < 0.003D) {
			motionZ = 0.0D;
		}

		EasyMappings.world(this).profiler.startSection("ai");

		if (isMovementBlocked()) {
			isJumping = false;
			moveStrafing = 0.0F;
			moveForward = 0.0F;
			randomYawVelocity = 0.0F;
		}
		else if (isServerWorld()) {
			EasyMappings.world(this).profiler.startSection("newAi");
			updateEntityActionState();
			EasyMappings.world(this).profiler.endSection();
		}

		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("jump");

		if (isJumping) {
			if (isInWater()) {
				handleJumpWater();
			}
			else if (isInLava()) {
				handleJumpLava();
			}
			else if (onGround && jumpTicks == 0) {
				jump();
				jumpTicks = 10;
			}
		}
		else {
			jumpTicks = 0;
		}

		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("travel");
		moveStrafing *= 0.98F;
		moveForward *= 0.98F;
		randomYawVelocity *= 0.9F;
		move(MoverType.SELF, moveStrafing, randomYawVelocity, moveForward);
		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("push");
		collideWithNearbyEntities();
		EasyMappings.world(this).profiler.endSection();

		EasyMappings.world(this).profiler.startSection("looting");

		if (!EasyMappings.world(this).isRemote && canPickUpLoot() && !dead && EasyMappings.world(this).getGameRules().getBoolean("mobGriefing")) {
			for (EntityItem entityitem : EasyMappings.world(this).getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D))) {
				if (!entityitem.isDead && entityitem.getItem() != null && !entityitem.cannotPickup()) {
					updateEquipmentIfNeeded(entityitem);
				}
			}
		}

		EasyMappings.world(this).profiler.endSection();

		if (EasyMappings.world(this).isRemote) {
			for (int i = 0; i < 2; ++i) {
				ParticleUtil.spawn(EnumParticles.PORTAL_GREEN, EasyMappings.world(this), posX + (rand.nextDouble() - 0.5D) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5D) * width, (rand.nextDouble() - 0.5D) * 2.0D, -rand.nextDouble(), (rand.nextDouble() - 0.5D) * 2.0D);
			}
		}
		else {
			if (!isNoDespawnRequired()) {
				++lifetime;
			}

			if (lifetime >= 2400) {
				setDead();
			}
		}
	}

}
