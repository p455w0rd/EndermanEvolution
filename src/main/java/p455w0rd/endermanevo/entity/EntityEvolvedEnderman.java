package p455w0rd.endermanevo.entity;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.*;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.*;
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

	private static final DataParameter<Boolean> IS_AGGRO = EntityDataManager.<Boolean>createKey(EntityEvolvedEnderman.class, DataSerializers.BOOLEAN);

	public EntityEvolvedEnderman(final World worldIn) {
		super(worldIn);
	}

	private boolean shouldAttackPlayer(final EntityPlayer player) {
		final ItemStack itemstack = player.inventory.armorInventory.get(3);

		if (itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) || itemstack.getItem() instanceof ItemSkullBase) {
			return false;
		}
		else {
			final Vec3d vec3d = player.getLook(1.0F).normalize();
			Vec3d vec3d1 = new Vec3d(posX - player.posX, getEntityBoundingBox().minY + getEyeHeight() - (player.posY + player.getEyeHeight()), posZ - player.posZ);
			final double d0 = vec3d1.lengthVector();
			vec3d1 = vec3d1.normalize();
			final double d1 = vec3d.dotProduct(vec3d1);
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
		targetTasks.addTask(2, new EntityEvolvedEnderman.EntityAIEndermanHurtByTarget(this));
		targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityEvolvedEndermite.class, 10, true, false, (@Nullable final EntityEvolvedEndermite p_apply_1_) -> p_apply_1_.isSpawnedByPlayer()));
	}

	private static final String IS_AGGRO_KEY = "IsAggro";

	@Override
	public void writeEntityToNBT(final NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setBoolean(IS_AGGRO_KEY, isAggro());
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (compound.hasKey(IS_AGGRO_KEY)) {
			if (isAggro() != compound.getBoolean(IS_AGGRO_KEY)) {
				setAggro(!isAggro());
			}
		}
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(IS_AGGRO, Boolean.valueOf(false));
	}

	public void setAggro(final boolean isAggro) {
		dataManager.set(IS_AGGRO, Boolean.valueOf(isAggro));
	}

	public boolean isAggro() {
		return dataManager.get(IS_AGGRO).booleanValue();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isInvisibleToPlayer(final EntityPlayer player) {
		return false;
	}

	@Override
	public Team getTeam() {
		return null;
	}

	@Override
	@Nullable
	public IEntityLivingData onInitialSpawn(final DifficultyInstance difficulty, @Nullable final IEntityLivingData livingdata) {
		final int radius = 32;
		final List<EntityEvolvedEnderman> endermanList = world.getEntitiesWithinAABB(EntityEvolvedEnderman.class, new AxisAlignedBB(posX - radius, 0, posZ - radius, posX + radius, world.getHeight(), posZ + radius));
		if (endermanList.size() >= ConfigOptions.ENDERMAN_MAX_SPAWN) {
			return null;
		}
		if (!getCanSpawnHere()) {
			//setDead();
		}
		return super.onInitialSpawn(difficulty, livingdata);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender() {
		final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(posX), 0, MathHelper.floor(posZ));
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
		final BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor(posX), 0, MathHelper.floor(posZ));
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
	public boolean attackEntityFrom(final DamageSource source, float amount) {
		if (isEntityInvulnerable(source) || world.isRemote || !ForgeHooks.onLivingAttack(this, source, amount)) {
			return false;
		}
		if ((source instanceof EntityDamageSourceIndirect || source.isProjectile()) && !isInWater()) {
			if (source.isProjectile() && !(source.getImmediateSource() instanceof EntitySnowball)) {
				final Entity sourceEntity = source.getTrueSource();
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
				final boolean flag = false;
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
				final Entity entity = source.getTrueSource();
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
					}
					else {
						attackedAtYaw = (int) (Math.random() * 2.0D) * 180;
					}
				}
				if (getHealth() <= 0.0F) {
					final SoundEvent soundevent = getDeathSound();
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
	public boolean attemptTeleport(final double x, final double y, final double z) {
		if (isInWater()) {
			return false;
		}
		final double d0 = posX;
		final double d1 = posY;
		final double d2 = posZ;
		posX = x;
		posY = y;
		posZ = z;
		boolean flag = false;
		BlockPos blockpos = new BlockPos(this);
		final Random random = getRNG();
		if (EasyMappings.world(this).isBlockLoaded(blockpos)) {
			boolean flag1 = false;
			while (!flag1 && blockpos.getY() > 0) {
				final BlockPos blockpos1 = blockpos.down();
				final IBlockState iblockstate = EasyMappings.world(this).getBlockState(blockpos1);
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
					final double d6 = j / 127.0D;
					final float f = (random.nextFloat() - 0.5F) * 0.2F;
					final float f1 = (random.nextFloat() - 0.5F) * 0.2F;
					final float f2 = (random.nextFloat() - 0.5F) * 0.2F;
					final double d3 = d0 + (posX - d0) * d6 + (random.nextDouble() - 0.5D) * width * 2.0D;
					final double d4 = d1 + (posY - d1) * d6 + random.nextDouble() * height;
					final double d5 = d2 + (posZ - d2) * d6 + (random.nextDouble() - 0.5D) * width * 2.0D;
					ParticleUtil.spawn(getParticle(), EasyMappings.world(this), d3, d4, d5, f, f1, f2);
				}
			}
			if (this instanceof EntityCreature) {
				((EntityCreature) this).getNavigator().clearPath();
			}
			return true;
		}
	}

	private EnumParticles getParticle() {
		return isAggro() ? EnumParticles.PORTAL_RED : EnumParticles.PORTAL_GREEN;
	}

	@Override
	public float getBlockPathWeight(final BlockPos pos) {
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
				final double x = posX + (rand.nextDouble() - 0.5D) * width;
				final double y = posY + rand.nextDouble() * height - 0.25D;
				final double z = posZ + (rand.nextDouble() - 0.5D) * width;
				final double sx = (rand.nextDouble() - 0.5D) * 2.0D;
				final double sy = -rand.nextDouble();
				final double sz = (rand.nextDouble() - 0.5D) * 2.0D;
				ParticleUtil.spawn(getParticle(), EasyMappings.world(this), x, y, z, sx, sy, sz);
			}
		}
		isJumping = false;
		updateArmSwingProgress();
		final float f = getBrightness();
		if (f > 0.5F) {
			idleTime += 2;
		}
		if (jumpTicks > 0) {
			--jumpTicks;
		}
		if (newPosRotationIncrements > 0 && !canPassengerSteer()) {
			final double d0 = posX + (interpTargetX - posX) / newPosRotationIncrements;
			final double d1 = posY + (interpTargetY - posY) / newPosRotationIncrements;
			final double d2 = posZ + (interpTargetZ - posZ) / newPosRotationIncrements;
			final double d3 = MathHelper.wrapDegrees(interpTargetYaw - rotationYaw);
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
		boolean flag = getFlag(7);
		if (flag && !onGround && !isRiding()) {
			final ItemStack itemstack = getItemStackFromSlot(EntityEquipmentSlot.CHEST);

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
		travel(moveStrafing, randomYawVelocity, moveForward);
		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("push");
		collideWithNearbyEntities();
		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("looting");
		if (!EasyMappings.world(this).isRemote && canPickUpLoot() && !dead && EasyMappings.world(this).getGameRules().getBoolean("mobGriefing")) {
			for (final EntityItem entityitem : EasyMappings.world(this).getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D))) {
				if (!entityitem.isDead && entityitem.getItem() != null && !entityitem.cannotPickup()) {
					updateEquipmentIfNeeded(entityitem);
				}
			}
		}
		EasyMappings.world(this).profiler.endSection();
	}

	static class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {

		private final EntityEvolvedEnderman enderman;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;
		private int direMessageTime;
		private TextComponentString aggroMessage;
		private final TextComponentString nextTimeMessage = new TextComponentString(TextFormatting.GREEN + "" + TextFormatting.BOLD + "You're not worth my time -_-");

		public AIFindPlayer(final EntityEvolvedEnderman p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			enderman = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			final double d0 = getTargetDistance();
			player = enderman.world.getNearestAttackablePlayer(enderman.posX, enderman.posY, enderman.posZ, d0, d0, (Function<EntityPlayer, Double>) null, (@Nullable final EntityPlayer p_apply_1_) -> p_apply_1_ != null && enderman.shouldAttackPlayer(p_apply_1_));
			return player != null;
		}

		@Override
		public void startExecuting() {
			aggroTime = 5;
			teleportTime = 0;
			direMessageTime = 0;
		}

		@Override
		public void resetTask() {
			player = null;
			targetEntity = null;
			direMessageTime = 0;
			aggroTime = 0;
			aggroMessage = null;
			super.resetTask();
		}

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
				final boolean shouldContinue = targetEntity != null && targetEntity.isEntityAlive() ? true : super.shouldContinueExecuting();
				enderman.setAggro(shouldContinue);
				return shouldContinue;
			}
		}

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
					if (aggroTime <= 0) {
						aggroTime = (targetEntity.getEntityWorld().rand.nextInt(20) + 1) * 20;
						aggroMessage = new TextComponentString(TextFormatting.GREEN + "" + TextFormatting.BOLD + targetEntity.getDisplayNameString() + "!! I'm such a huge fan, gimme hugz!");
						enderman.setAggro(true);
					}
					if (aggroTime == 1) {
						targetEntity.sendStatusMessage(nextTimeMessage, true);
						enderman.setAggro(false);
						resetTask();
						return;
					}
					aggroTime--;
					if (direMessageTime == 0) {
						targetEntity.sendStatusMessage(aggroMessage, true);
						direMessageTime = 20 * 30;
					}
					else {
						direMessageTime--;
					}
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

		public AIPlaceBlock(final EntityEnderman p_i45843_1_) {
			enderman = p_i45843_1_;
		}

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

		@Override
		public void updateTask() {
			final Random random = enderman.getRNG();
			final World world = enderman.world;
			final int i = MathHelper.floor(enderman.posX - 1.0D + random.nextDouble() * 2.0D);
			final int j = MathHelper.floor(enderman.posY + random.nextDouble() * 2.0D);
			final int k = MathHelper.floor(enderman.posZ - 1.0D + random.nextDouble() * 2.0D);
			final BlockPos blockpos = new BlockPos(i, j, k);
			final IBlockState iblockstate = world.getBlockState(blockpos);
			final IBlockState iblockstate1 = world.getBlockState(blockpos.down());
			final IBlockState iblockstate2 = enderman.getHeldBlockState();
			if (iblockstate2 != null && canPlaceBlock(world, blockpos, iblockstate2.getBlock(), iblockstate, iblockstate1)) {
				world.setBlockState(blockpos, iblockstate2, 3);
				enderman.setHeldBlockState((IBlockState) null);
			}
		}

		private boolean canPlaceBlock(final World p_188518_1_, final BlockPos p_188518_2_, final Block p_188518_3_, final IBlockState p_188518_4_, final IBlockState p_188518_5_) {
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

		public AITakeBlock(final EntityEnderman p_i45841_1_) {
			enderman = p_i45841_1_;
		}

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

		@Override
		public void updateTask() {
			final Random random = enderman.getRNG();
			final World world = enderman.world;
			final int i = MathHelper.floor(enderman.posX - 2.0D + random.nextDouble() * 4.0D);
			final int j = MathHelper.floor(enderman.posY + random.nextDouble() * 3.0D);
			final int k = MathHelper.floor(enderman.posZ - 2.0D + random.nextDouble() * 4.0D);
			final BlockPos blockpos = new BlockPos(i, j, k);
			final IBlockState iblockstate = world.getBlockState(blockpos);
			final Block block = iblockstate.getBlock();
			final RayTraceResult raytraceresult = world.rayTraceBlocks(new Vec3d(MathHelper.floor(enderman.posX) + 0.5F, j + 0.5F, MathHelper.floor(enderman.posZ) + 0.5F), new Vec3d(i + 0.5F, j + 0.5F, k + 0.5F), false, true, false);
			final boolean flag = raytraceresult != null && raytraceresult.getBlockPos().equals(blockpos);
			if (EntityEvolvedEnderman.getCarriable(block) && flag) {
				enderman.setHeldBlockState(iblockstate);
				world.setBlockToAir(blockpos);
			}
		}
	}

	public class EntityAIEndermanHurtByTarget extends EntityAITarget {

		private int revengeTimerOld;

		public EntityAIEndermanHurtByTarget(final EntityCreature creatureIn) {
			super(creatureIn, true);
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			final int i = taskOwner.getRevengeTimer();
			final EntityLivingBase entitylivingbase = taskOwner.getRevengeTarget();
			return i != revengeTimerOld && entitylivingbase != null && this.isSuitableTarget(entitylivingbase, false);
		}

		@Override
		public void startExecuting() {
			taskOwner.setAttackTarget(taskOwner.getRevengeTarget());
			target = taskOwner.getAttackTarget();
			revengeTimerOld = taskOwner.getRevengeTimer();
			unseenMemoryTicks = 5 * 20;
			if (taskOwner.getEntityWorld() != null && taskOwner.getEntityWorld().provider.getDimensionType() == DimensionType.NETHER) {
				alertOthers();
			}
			super.startExecuting();
		}

		protected void alertOthers() {
			final double d0 = getTargetDistance();
			for (final EntityCreature entitycreature : taskOwner.world.getEntitiesWithinAABB(taskOwner.getClass(), new AxisAlignedBB(taskOwner.posX, taskOwner.posY, taskOwner.posZ, taskOwner.posX + 1.0D, taskOwner.posY + 1.0D, taskOwner.posZ + 1.0D).grow(d0, 10.0D, d0))) {
				if (taskOwner != entitycreature && entitycreature.getAttackTarget() == null && (!(taskOwner instanceof EntityTameable) || ((EntityTameable) taskOwner).getOwner() == ((EntityTameable) entitycreature).getOwner()) && !entitycreature.isOnSameTeam(taskOwner.getRevengeTarget())) {
					setEntityAttackTarget(entitycreature, taskOwner.getRevengeTarget());
				}
			}
		}

		protected void setEntityAttackTarget(final EntityCreature creatureIn, final EntityLivingBase entityLivingBaseIn) {
			creatureIn.setAttackTarget(entityLivingBaseIn);
		}

	}

	@Override
	public void setAttackTarget(@Nullable final EntityLivingBase entitylivingbaseIn) {
		super.setAttackTarget(entitylivingbaseIn);
		setAggro(getAttackTarget() != null);
	}

}
