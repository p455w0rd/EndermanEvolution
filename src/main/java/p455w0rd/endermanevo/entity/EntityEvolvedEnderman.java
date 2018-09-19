package p455w0rd.endermanevo.entity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.init.ModConfig.ConfigOptions;
import p455w0rd.endermanevo.items.ItemSkullBase;
import p455w0rd.endermanevo.util.EnumParticles;
import p455w0rd.endermanevo.util.ParticleUtil;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.MCPrivateUtils;

public class EntityEvolvedEnderman extends EntityEnderman {

	//private int targetChangeTime = 0;

	public EntityEvolvedEnderman(World worldIn) {
		super(worldIn);
	}

	private boolean shouldAttackPlayer(EntityPlayer player) {
		ItemStack itemstack = player.inventory.armorInventory.get(3);

		if (itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) || itemstack.getItem() instanceof ItemSkullBase) {
			return false;
		}
		else {
			Vec3d vec3d = player.getLook(1.0F).normalize();
			Vec3d vec3d1 = new Vec3d(posX - player.posX, getEntityBoundingBox().minY + getEyeHeight() - (player.posY + player.getEyeHeight()), posZ - player.posZ);
			double d0 = vec3d1.lengthVector();
			vec3d1 = vec3d1.normalize();
			double d1 = vec3d.dotProduct(vec3d1);
			return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(this) : false;
		}
	}

	@Override
	protected void initEntityAI() {
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		tasks.addTask(7, new EntityAIWander(this, 1.0D));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(8, new EntityAILookIdle(this));
		tasks.addTask(10, new EntityEvolvedEnderman.AIPlaceBlock(this));
		tasks.addTask(11, new EntityEvolvedEnderman.AITakeBlock(this));
		targetTasks.addTask(1, new EntityEvolvedEnderman.AIFindPlayer(this));
		targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntityEvolvedEndermite>(this, EntityEvolvedEndermite.class, 10, true, false, (@Nullable EntityEvolvedEndermite p_apply_1_) -> p_apply_1_.isSpawnedByPlayer()));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInvisibleToPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public Team getTeam() {
		return null;
	}

	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
		int radius = 32;
		List<EntityEvolvedEnderman> endermanList = world.getEntitiesWithinAABB(EntityEvolvedEnderman.class, new AxisAlignedBB(posX - radius, 0, posZ - radius, posX + radius, world.getHeight(), posZ + radius));
		if (endermanList.size() >= ConfigOptions.ENDERMAN_MAX_SPAWN) {
			return null;
			//setDead();
		}
		if (!getCanSpawnHere()) {
			//setDead();
		}
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(posX), 0, MathHelper.floor(posZ));

		if (world != null && world.isBlockLoaded(blockpos$mutableblockpos)) {
			blockpos$mutableblockpos.setY(MathHelper.floor(posY + getEyeHeight()));
			return world.getCombinedLight(blockpos$mutableblockpos, 0);
		}
		else {
			return 15;
		}
	}

	@Override
	public float getBrightness() {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(posX), 0, MathHelper.floor(posZ));

		if (world != null && world.isBlockLoaded(blockpos$mutableblockpos)) {
			blockpos$mutableblockpos.setY(MathHelper.floor(posY + getEyeHeight()));
			return world.getLightBrightness(blockpos$mutableblockpos);
		}
		else {
			return 1.0F;
		}
	}

	@Override
	protected void updateAITasks() {
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isEntityInvulnerable(source) || world.isRemote || !ForgeHooks.onLivingAttack(this, source, amount)) {
			return false;
		}
		if ((source instanceof EntityDamageSourceIndirect || source.isProjectile()) && !isInWater()) {
			if (source.isProjectile() && !(source.getImmediateSource() instanceof EntitySnowball)) {
				Entity sourceEntity = source.getTrueSource();
				if (sourceEntity != null && attemptTeleport(sourceEntity.posX + (rand.nextInt(3) == 2 ? -1 : 1), sourceEntity.posY, sourceEntity.posZ + (rand.nextInt(3) == 2 ? -1 : 1))) {
					if (sourceEntity instanceof EntityLivingBase) {
						setRevengeTarget((EntityLivingBase) sourceEntity);
					}
					return true;
				}
			}
			else {
				for (int i = 0; i < 64; ++i) {
					if (teleportRandomly()) {
						return true;
					}
				}
			}
			return false;
		}
		else {
			idleTime = 0;

			if (getHealth() <= 0.0F) {
				return false;
			}
			else if (source.isFireDamage() && isPotionActive(MobEffects.FIRE_RESISTANCE)) {
				return false;
			}
			else {
				if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null) {
					getItemStackFromSlot(EntityEquipmentSlot.HEAD).damageItem((int) (amount * 4.0F + rand.nextFloat() * amount * 2.0F), this);
					amount *= 0.75F;
				}

				boolean flag = false;

				limbSwingAmount = 1.5F;
				boolean flag1 = true;

				if (hurtResistantTime > maxHurtResistantTime / 2.0F) {
					if (amount <= lastDamage) {
						return false;
					}

					damageEntity(source, amount - lastDamage);
					lastDamage = amount;
					flag1 = false;
				}
				else {
					lastDamage = amount;
					hurtResistantTime = maxHurtResistantTime;
					damageEntity(source, amount);
					maxHurtTime = 10;
					hurtTime = maxHurtTime;
				}

				attackedAtYaw = 0.0F;
				Entity entity = source.getTrueSource();

				if (entity != null) {
					if (entity instanceof EntityLivingBase) {
						setRevengeTarget((EntityLivingBase) entity);
					}

					if (entity instanceof EntityPlayer) {
						recentlyHit = 100;
						attackingPlayer = (EntityPlayer) entity;
					}
				}

				if (flag1) {
					if (flag) {
						EasyMappings.world(this).setEntityState(this, (byte) 29);
					}
					else if (source instanceof EntityDamageSource && ((EntityDamageSource) source).getIsThornsDamage()) {
						EasyMappings.world(this).setEntityState(this, (byte) 33);
					}
					else {
						EasyMappings.world(this).setEntityState(this, (byte) 2);
					}

					if (source != DamageSource.DROWN && (!flag || amount > 0.0F)) {
						markVelocityChanged();
					}

					if (entity != null) {
						double d1 = entity.posX - posX;
						double d0;

						for (d0 = entity.posZ - posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
							d1 = (Math.random() - Math.random()) * 0.01D;
						}

						attackedAtYaw = (float) (MathHelper.atan2(d0, d1) * (180D / Math.PI) - rotationYaw);
						if (source.isProjectile()) {
							source.getImmediateSource().setDead();
						}
						else {
							knockBack(entity, 0.4F, d1, d0);
						}
						//entity.setDead();
					}
					else {
						attackedAtYaw = (int) (Math.random() * 2.0D) * 180;
					}
				}

				if (getHealth() <= 0.0F) {
					SoundEvent soundevent = getDeathSound();

					if (flag1 && soundevent != null) {
						playSound(soundevent, getSoundVolume(), getSoundPitch());
					}

					onDeath(source);
				}
				else if (flag1) {
					playHurtSound(source);
				}

				if (!flag || amount > 0.0F) {
					MCPrivateUtils.setLastDamageSource(this, source);
					MCPrivateUtils.setLastDamageStamp(this, EasyMappings.world(this).getTotalWorldTime());
				}

				return !flag || amount > 0.0F;
			}
		}
	}

	@Override
	public boolean attemptTeleport(double x, double y, double z) {
		if (isInWater()) {
			return false;
		}
		double d0 = posX;
		double d1 = posY;
		double d2 = posZ;
		posX = x;
		posY = y;
		posZ = z;
		boolean flag = false;
		BlockPos blockpos = new BlockPos(this);
		Random random = getRNG();

		if (EasyMappings.world(this).isBlockLoaded(blockpos)) {
			boolean flag1 = false;

			while (!flag1 && blockpos.getY() > 0) {
				BlockPos blockpos1 = blockpos.down();
				IBlockState iblockstate = EasyMappings.world(this).getBlockState(blockpos1);

				if (iblockstate.getMaterial().blocksMovement()) {
					flag1 = true;
				}
				else {
					--posY;
					blockpos = blockpos1;
				}
			}

			if (flag1) {
				setPositionAndUpdate(posX, posY, posZ);

				if (EasyMappings.world(this).getCollisionBoxes(this, getEntityBoundingBox()).isEmpty() && !EasyMappings.world(this).containsAnyLiquid(getEntityBoundingBox())) {
					flag = true;
				}
			}
		}

		if (!flag) {
			setPositionAndUpdate(d0, d1, d2);
			return false;
		}
		else {
			if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
				for (int j = 0; j < 128; ++j) {
					double d6 = j / 127.0D;
					float f = (random.nextFloat() - 0.5F) * 0.2F;
					float f1 = (random.nextFloat() - 0.5F) * 0.2F;
					float f2 = (random.nextFloat() - 0.5F) * 0.2F;
					double d3 = d0 + (posX - d0) * d6 + (random.nextDouble() - 0.5D) * width * 2.0D;
					double d4 = d1 + (posY - d1) * d6 + random.nextDouble() * height;
					double d5 = d2 + (posZ - d2) * d6 + (random.nextDouble() - 0.5D) * width * 2.0D;
					ParticleUtil.spawn(EnumParticles.PORTAL_GREEN, EasyMappings.world(this), d3, d4, d5, f, f1, f2);
				}
			}
			if (this instanceof EntityCreature) {
				((EntityCreature) this).getNavigator().clearPath();
			}

			return true;
		}
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		return ConfigOptions.ENDERMAN_DAY_SPAWN ? 0.0F : super.getBlockPathWeight(pos);
	}

	@Override
	protected boolean isValidLightLevel() {
		return ConfigOptions.ENDERMAN_DAY_SPAWN ? true : super.isValidLightLevel();
	}

	@Override
	public void onLivingUpdate() {
		if (getEntityWorld() != null && getEntityWorld().isRemote) {
			for (int i = 0; i < 2; ++i) {
				double x = posX + (rand.nextDouble() - 0.5D) * width;
				double y = posY + rand.nextDouble() * height - 0.25D;
				double z = posZ + (rand.nextDouble() - 0.5D) * width;
				double sx = (rand.nextDouble() - 0.5D) * 2.0D;
				double sy = -rand.nextDouble();
				double sz = (rand.nextDouble() - 0.5D) * 2.0D;
				ParticleUtil.spawn(EnumParticles.PORTAL_GREEN, EasyMappings.world(this), x, y, z, sx, sy, sz);
			}
		}

		isJumping = false;
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
		// updateElytra()
		boolean flag = getFlag(7);

		if (flag && !onGround && !isRiding()) {
			ItemStack itemstack = getItemStackFromSlot(EntityEquipmentSlot.CHEST);

			if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack)) {
				flag = true;

				if (!world.isRemote && (ticksElytraFlying + 1) % 20 == 0) {
					itemstack.damageItem(1, this);
				}
			}
			else {
				flag = false;
			}
		}
		else {
			flag = false;
		}

		if (!world.isRemote) {
			setFlag(7, flag);
		}
		//updateElytra() end
		travel(moveStrafing, randomYawVelocity, moveForward);
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
		//super.onLivingUpdate();
	}

	static class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
		private final EntityEvolvedEnderman enderman;
		/** The player */
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(EntityEvolvedEnderman p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			enderman = p_i45842_1_;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = enderman.world.getNearestAttackablePlayer(enderman.posX, enderman.posY, enderman.posZ, d0, d0, (Function<EntityPlayer, Double>) null, (@Nullable EntityPlayer p_apply_1_) -> p_apply_1_ != null && enderman.shouldAttackPlayer(p_apply_1_));
			return player != null;
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			aggroTime = 5;
			teleportTime = 0;
		}

		/**
		 * Reset the task's internal state. Called when this task is interrupted by another one
		 */
		@Override
		public void resetTask() {
			player = null;
			super.resetTask();
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean shouldContinueExecuting() {
			if (player != null) {
				if (!enderman.shouldAttackPlayer(player)) {
					return false;
				}
				else {
					enderman.faceEntity(player, 10.0F, 10.0F);
					return true;
				}
			}
			else {
				return targetEntity != null && targetEntity.isEntityAlive() ? true : super.shouldContinueExecuting();
			}
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@Override
		public void updateTask() {
			if (player != null) {
				if (--aggroTime <= 0) {
					targetEntity = player;
					player = null;
					super.startExecuting();
				}
			}
			else {
				if (targetEntity != null) {
					if (enderman.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSq(enderman) < 16.0D) {
							enderman.teleportRandomly();
						}

						teleportTime = 0;
					}
					else if (targetEntity.getDistanceSq(enderman) > 256.0D && teleportTime++ >= 30 && enderman.teleportToEntity(targetEntity)) {
						teleportTime = 0;
					}
				}

				super.updateTask();
			}
		}
	}

	static class AIPlaceBlock extends EntityAIBase {
		private final EntityEnderman enderman;

		public AIPlaceBlock(EntityEnderman p_i45843_1_) {
			enderman = p_i45843_1_;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (enderman.getHeldBlockState() == null) {
				return false;
			}
			else if (!enderman.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			}
			else {
				return enderman.getRNG().nextInt(2000) == 0;
			}
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@Override
		public void updateTask() {
			Random random = enderman.getRNG();
			World world = enderman.world;
			int i = MathHelper.floor(enderman.posX - 1.0D + random.nextDouble() * 2.0D);
			int j = MathHelper.floor(enderman.posY + random.nextDouble() * 2.0D);
			int k = MathHelper.floor(enderman.posZ - 1.0D + random.nextDouble() * 2.0D);
			BlockPos blockpos = new BlockPos(i, j, k);
			IBlockState iblockstate = world.getBlockState(blockpos);
			IBlockState iblockstate1 = world.getBlockState(blockpos.down());
			IBlockState iblockstate2 = enderman.getHeldBlockState();

			if (iblockstate2 != null && canPlaceBlock(world, blockpos, iblockstate2.getBlock(), iblockstate, iblockstate1)) {
				world.setBlockState(blockpos, iblockstate2, 3);
				enderman.setHeldBlockState((IBlockState) null);
			}
		}

		private boolean canPlaceBlock(World p_188518_1_, BlockPos p_188518_2_, Block p_188518_3_, IBlockState p_188518_4_, IBlockState p_188518_5_) {
			if (!p_188518_3_.canPlaceBlockAt(p_188518_1_, p_188518_2_)) {
				return false;
			}
			else if (p_188518_4_.getMaterial() != Material.AIR) {
				return false;
			}
			else if (p_188518_5_.getMaterial() == Material.AIR) {
				return false;
			}
			else {
				return p_188518_5_.isFullCube();
			}
		}
	}

	static class AITakeBlock extends EntityAIBase {
		private final EntityEnderman enderman;

		public AITakeBlock(EntityEnderman p_i45841_1_) {
			enderman = p_i45841_1_;
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (enderman.getHeldBlockState() != null) {
				return false;
			}
			else if (!enderman.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			}
			else {
				return enderman.getRNG().nextInt(20) == 0;
			}
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@Override
		public void updateTask() {
			Random random = enderman.getRNG();
			World world = enderman.world;
			int i = MathHelper.floor(enderman.posX - 2.0D + random.nextDouble() * 4.0D);
			int j = MathHelper.floor(enderman.posY + random.nextDouble() * 3.0D);
			int k = MathHelper.floor(enderman.posZ - 2.0D + random.nextDouble() * 4.0D);
			BlockPos blockpos = new BlockPos(i, j, k);
			IBlockState iblockstate = world.getBlockState(blockpos);
			Block block = iblockstate.getBlock();
			RayTraceResult raytraceresult = world.rayTraceBlocks(new Vec3d(MathHelper.floor(enderman.posX) + 0.5F, j + 0.5F, MathHelper.floor(enderman.posZ) + 0.5F), new Vec3d(i + 0.5F, j + 0.5F, k + 0.5F), false, true, false);
			boolean flag = raytraceresult != null && raytraceresult.getBlockPos().equals(blockpos);

			if (EntityEvolvedEnderman.getCarriable(block) && flag) {
				enderman.setHeldBlockState(iblockstate);
				world.setBlockToAir(blockpos);
			}
		}
	}

}
