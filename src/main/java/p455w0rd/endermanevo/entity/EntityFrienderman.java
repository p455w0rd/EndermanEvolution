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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.ironchest.common.blocks.chest.IronChestType;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.ITOPEntityInfoProvider;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import p455w0rd.endermanevo.init.ModItems;
import p455w0rd.endermanevo.init.ModNetworking;
import p455w0rd.endermanevo.init.ModRegistries;
import p455w0rd.endermanevo.integration.EnderStorage;
import p455w0rd.endermanevo.integration.IronChests;
import p455w0rd.endermanevo.network.PacketFriendermanRegistrySync;
import p455w0rd.endermanevo.util.ChestUtils;
import p455w0rd.endermanevo.util.ChestUtils.VanillaChestTypes;
import p455w0rd.endermanevo.util.EnumParticles;
import p455w0rd.endermanevo.util.FriendermanUtils.ChestType;
import p455w0rd.endermanevo.util.ParticleUtil;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.InventoryUtils;
import p455w0rdslib.util.MCUtils;
import p455w0rdslib.util.MathUtils;
import p455w0rdslib.util.PlayerUUIDUtils;

/**
 * @author p455w0rd
 *
 */
public class EntityFrienderman extends EntityCreature implements IEntityOwnable, ITOPEntityInfoProvider {

	private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final AttributeModifier ATTACKING_SPEED_BOOST = (new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15000000596046448D, 0)).setSaved(false);
	private static final Set<Block> CARRIABLE_BLOCKS = Sets.<Block>newIdentityHashSet();
	private static final DataParameter<Optional<IBlockState>> CARRIED_BLOCK = EntityDataManager.<Optional<IBlockState>>createKey(EntityFrienderman.class, DataSerializers.OPTIONAL_BLOCK_STATE);
	private static final DataParameter<ItemStack> CARRIED_ITEM = EntityDataManager.<ItemStack>createKey(EntityFrienderman.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<Boolean> SCREAMING = EntityDataManager.<Boolean>createKey(EntityFrienderman.class, DataSerializers.BOOLEAN);
	private int lastCreepySound;

	protected static final DataParameter<Byte> TAMED = EntityDataManager.<Byte>createKey(EntityFrienderman.class, DataSerializers.BYTE);
	protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.<Optional<UUID>>createKey(EntityFrienderman.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<String> OWNER_NAME = EntityDataManager.<String>createKey(EntityFrienderman.class, DataSerializers.STRING);
	private static final DataParameter<Float> DATA_HEALTH_ID = EntityDataManager.<Float>createKey(EntityFrienderman.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> LID_ANGLE = EntityDataManager.<Float>createKey(EntityFrienderman.class, DataSerializers.FLOAT);
	protected EntityAISit aiSit;
	public EntityItem movingTowardItem = null;
	private boolean lidClosed = true;
	private boolean lidOpening = false;
	private IInventory chestInventory = null;

	public EntityFrienderman(World worldIn) {
		super(worldIn);
		setTamed(false);
		setSize(0.6F, 2.9F);
		stepHeight = 1.0F;
		isImmuneToFire = true;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
		if (!isTamed()) {
			probeInfo.horizontal().item(new ItemStack(ModItems.FRIENDER_PEARL), new ItemStyle().width(8).height(8)).text(" Right-click with FrienderPearl to tame.");
		}
		else {
			String ownerName = "";
			ownerName = PlayerUUIDUtils.getPlayerName(getOwnerId());
			if (ownerName == "") {
				ownerName = "<Unavailable>";
			}
			probeInfo.horizontal().text("Owner: " + ownerName);
			probeInfo.horizontal().text("Mode: " + (isSitting() ? "Idle" : "Following/Defending"));
			if (isHoldingChest() && player == getOwner()) {
				probeInfo.horizontal().text("Sneak+Right-Click to take chest");
			}
			//probeInfo.horizontal().text("test");
		}
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
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {

		super.dropLoot(wasRecentlyHit, lootingModifier, source);
		if (isHoldingChest()) {
			entityDropItem(getHeldItemStack(), 2.0F);
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

	public float getLidAngle() {
		return dataManager.get(LID_ANGLE).floatValue();
	}

	public void setLidAngle(float angle) {
		dataManager.set(LID_ANGLE, Float.valueOf(angle));
	}

	private void incLidAngle() {
		setLidAngle(getLidAngle() + 0.3F);
	}

	private void decLidAngle() {
		setLidAngle(getLidAngle() - 0.3F);
	}

	public void doLidAnim() {
		if (lidClosed) {
			lidClosed = false;
			lidOpening = true;
		}
	}

	private boolean shouldAttackPlayer(EntityPlayer player) {
		if (isTamed() && player.getUniqueID() == getOwnerId()) {
			return false;
		}
		if (getAttackingEntity() != null && getAttackingEntity() == player) {
			if (isTamed() && isSitting()) {
				setSitting(false);
			}
		}
		return getAttackingEntity() != null && getAttackingEntity() == player;
	}

	public void playEndermanSound() {
		if (ticksExisted >= lastCreepySound + 400) {
			lastCreepySound = ticksExisted;
			if (!isSilent()) {
				EasyMappings.world(this).playSound(posX, posY + getEyeHeight(), posZ, SoundEvents.ENTITY_ENDERMEN_STARE, getSoundCategory(), 2.5F, 1.0F, false);
			}
		}
	}

	public boolean isScreaming() {
		return dataManager.get(SCREAMING).booleanValue();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return isScreaming() ? SoundEvents.ENTITY_ENDERMEN_SCREAM : SoundEvents.ENTITY_ENDERMEN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ENDERMEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMEN_DEATH;
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		if (SCREAMING.equals(key) && isScreaming() && EasyMappings.world(this).isRemote) {
			playEndermanSound();
		}

		super.notifyDataManagerChange(key);
	}

	@Override
	protected float getJumpUpwardsMotion() {
		return 0.5F;
	}

	@Override
	public float getEyeHeight() {
		return 2.55F;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(TAMED, Byte.valueOf((byte) 0));
		dataManager.register(OWNER_UNIQUE_ID, Optional.<UUID>absent());
		dataManager.register(OWNER_NAME, "");
		dataManager.register(DATA_HEALTH_ID, Float.valueOf(getHealth()));
		dataManager.register(CARRIED_BLOCK, Optional.<IBlockState>absent());
		dataManager.register(CARRIED_ITEM, ItemStack.EMPTY);
		dataManager.register(SCREAMING, Boolean.valueOf(false));
		dataManager.register(LID_ANGLE, Float.valueOf(0F));
	}

	@Override
	protected void initEntityAI() {
		aiSit = new EntityFrienderman.EntityAISit(this);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, aiSit);
		tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
		tasks.addTask(3, new EntityAIMoveToEntityItem(this, 1.0F, 10.0F));
		tasks.addTask(4, new EntityAIFollowOwner(this, 1.0D, 5.0F, 10.0F));
		tasks.addTask(5, new EntityAIWander(this, 0.5D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
		//tasks.addTask(8, new EntityFrienderman.AIPlaceBlock(this));
		//tasks.addTask(9, new EntityFrienderman.AITakeBlock(this));
		targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
		targetTasks.addTask(2, new EntityFrienderman.AIFindPlayer(this));
		targetTasks.addTask(3, new EntityAIHurtByTarget(this, true, new Class[0]));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		if (isTamed()) {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		}
		else {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		}
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
	}

	@Nullable
	@Override
	protected ResourceLocation getLootTable() {
		return null;
	}

	@Override
	protected void despawnEntity() {
		if (!isTamed()) {
			super.despawnEntity();
		}
	}

	public void setHeldBlockState(@Nullable IBlockState state) {
		if (getHeldItemStack() != null) {
			entityDropItem(getHeldItemStack().copy(), 1.0F);
		}
		dataManager.set(CARRIED_ITEM, ItemStack.EMPTY);
		dataManager.set(CARRIED_BLOCK, Optional.fromNullable(state));
	}

	public void setHeldItemStack(@Nullable ItemStack stack) {
		if (getHeldBlockState() != null) {
			entityDropItem(new ItemStack(getHeldBlockState().getBlock()).copy(), 1.0F);
		}
		dataManager.set(CARRIED_BLOCK, Optional.absent());
		dataManager.set(CARRIED_ITEM, stack);
	}

	@Override
	protected void updateAITasks() {
		dataManager.set(DATA_HEALTH_ID, Float.valueOf(getHealth()));
		super.updateAITasks();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if (getOwner() != null) {
			compound.setString("OwnerUUID", getUUID(getOwner().getName()).toString());
			compound.setString("Owner", getOwner().getName());
		}
		else {
			if (dataManager.get(OWNER_NAME) != null && dataManager.get(OWNER_NAME) != "") {
				UUID uuid = getUUID(dataManager.get(OWNER_NAME));
				if (uuid != null) {
					compound.setString("Owner", dataManager.get(OWNER_NAME));
					compound.setString("OwnerUUID", getUUID(dataManager.get(OWNER_NAME)).toString());
				}
			}
		}
		if (getHeldItemStack() != null) {
			NBTTagCompound itemStack = new NBTTagCompound();
			getHeldItemStack().writeToNBT(itemStack);
			compound.setTag("Item", itemStack);
		}
		if (chestInventory != null) {
			NBTTagList nbtList = new NBTTagList();
			for (int i = 0; i < chestInventory.getSizeInventory(); i++) {
				if (chestInventory.getStackInSlot(i) != null) {
					NBTTagCompound slotNBT = new NBTTagCompound();
					slotNBT.setInteger("Slot", i);
					chestInventory.getStackInSlot(i).writeToNBT(slotNBT);
					nbtList.appendTag(slotNBT);
				}
			}
			compound.setTag("Chest", nbtList);
			compound.setInteger("ChestSize", chestInventory.getSizeInventory());
		}
		compound.setBoolean("Sitting", isSitting());
	}

	public ItemStack writeInventoryToStack(IInventory inventory, ItemStack stack) {
		return getItemBlockWithInventory(inventory, stack);
	}

	public ItemStack getItemBlockWithInventory(IInventory inventory, ItemStack stack) {
		if (stack != null && stack.getItem() instanceof ItemBlock && inventory != null && inventory.getSizeInventory() > 0) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			NBTTagCompound stackNBT = stack.getOrCreateSubCompound("BlockEntityTag");
			NBTTagList stackList = new NBTTagList();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (inventory.getStackInSlot(i) == null) {
					continue;
				}
				NBTTagCompound slotNBT = new NBTTagCompound();
				slotNBT.setByte("Slot", (byte) i);
				inventory.getStackInSlot(i).writeToNBT(slotNBT);
				stackList.appendTag(slotNBT);
			}
			stackNBT.setTag("Items", stackList);
			stack.setTagInfo("BlockEntityTag", stackNBT);
			return stack;
		}
		return null;
	}

	private UUID getUUID(String name) {
		return PlayerUUIDUtils.getPlayerUUID(name);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		UUID uuid = null;
		/*
		if (compound.hasKey("LidAngle")) {
			setLidAngle(compound.getFloat("LidAngle"));
		}
		*/
		if (compound.hasKey("Owner")) {
			String s1 = compound.getString("Owner");
			uuid = getUUID(s1);
		}

		if (uuid != null) {
			try {
				setOwnerId(uuid);
				setTamed(true);
			}
			catch (Throwable var4) {
				setTamed(false);
			}
		}
		if (compound.hasKey("Item") && compound.getCompoundTag("Item") != null) {
			setHeldItemStack(new ItemStack(compound.getCompoundTag("Item")));
		}
		if (compound.hasKey("Chest")) {
			if (chestInventory == null) {
				chestInventory = new TempChest(compound.getInteger("ChestSize"));
			}
			NBTTagList tagList = compound.getTagList("Chest", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				NBTTagCompound slotNBT = tagList.getCompoundTagAt(i);
				if (slotNBT != null) {
					chestInventory.setInventorySlotContents(slotNBT.getInteger("Slot"), new ItemStack(slotNBT));
				}
			}
		}
		setSitting(compound.getBoolean("Sitting"));
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase entitylivingbaseIn) {
		if (isTamed() && entitylivingbaseIn == getOwner()) {
			return;
		}
		super.setAttackTarget(entitylivingbaseIn);
		IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

		if (entitylivingbaseIn == null) {
			dataManager.set(SCREAMING, Boolean.valueOf(false));
			iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST);
		}
		else {
			dataManager.set(SCREAMING, Boolean.valueOf(true));
			if (isTamed() && isSitting()) {
				setSitting(false);
			}
			if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST)) {
				iattributeinstance.applyModifier(ATTACKING_SPEED_BOOST);
			}
		}
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (isTamed() && !EasyMappings.world(this).isRemote) {
			if (!stack.isEmpty()) {
				if (stack.getItem() == ModItems.FRIENDER_PEARL) {
					if (dataManager.get(DATA_HEALTH_ID).floatValue() < 30.0F) {
						if (!player.capabilities.isCreativeMode) {
							stack.shrink(1);
						}
						heal(30.0F);
						//playTameEffect(true);
						EasyMappings.world(this).setEntityState(this, (byte) 7);
						return true;
					}
				}
				else if (ChestUtils.isVanillaChest(stack) && isOwner(player)) {
					ItemStack chestStack = stack.copy();
					ItemStack leftOverStack = ItemStack.EMPTY;
					if (chestStack.getCount() > 1) {
						chestStack.setCount(1);
						leftOverStack = stack.copy();
						leftOverStack.setCount(stack.getCount() - 1);
					}
					setHeldItemStack(chestStack);
					chestInventory = new TempChest();
					ChestUtils.loadInventoryFromStack(chestInventory, getHeldItemStack());
					stack = ItemStack.EMPTY; // just cleanup
					player.setHeldItem(hand, leftOverStack);
					return true;
				}
				else if (Mods.ENDERSTORAGE.isLoaded() && isOwner(player) && stack.getItem() == EnderStorage.getEnderStorageItem() && stack.getItemDamage() == 0) {
					ItemStack chestStack = stack.copy();
					ItemStack leftOverStack = ItemStack.EMPTY;
					if (chestStack.getCount() > 1) {
						chestStack.setCount(1);
						leftOverStack = stack.copy();
						leftOverStack.setCount(stack.getCount() - 1);
					}
					setHeldItemStack(chestStack); // TODO
					player.setHeldItem(hand, leftOverStack);
					stack = ItemStack.EMPTY; // just cleanup
					return true;
				}
				else if (Mods.IRONCHESTS.isLoaded() && isOwner(player) && IronChests.isIronChest(stack) && IronChests.getChestType(stack) != IronChestType.DIRTCHEST9000) {
					ItemStack chestStack = stack.copy();
					ItemStack leftOverStack = ItemStack.EMPTY;
					if (chestStack.getCount() > 1) {
						chestStack.setCount(1);
						leftOverStack = stack.copy();
						leftOverStack.setCount(stack.getCount() - 1);
					}
					movingTowardItem = null;
					setHeldItemStack(chestStack);
					chestInventory = new TempChest(IronChests.getInventorySize(stack));
					ChestUtils.loadInventoryFromStack(chestInventory, getHeldItemStack());
					player.setHeldItem(hand, leftOverStack);
					stack = null; // just cleanup
					return true;
				}
			}
			else {
				if (isOwner(player) && !EasyMappings.world(this).isRemote) {
					if (!player.isSneaking()) {
						if (player.getHeldItemMainhand() == null) {
							aiSit.setSitting(!isSitting());
							isJumping = false;
							navigator.clearPath();
							setAttackTarget((EntityLivingBase) null);
						}
					}
					else {
						if (Mods.ENDERSTORAGE.isLoaded() && isOwner(player)) {
							if (isHoldingEnderStorageChest()) {
								player.inventory.addItemStackToInventory(getHeldItemStack().copy());
								setHeldItemStack(null);
								return true;
							}
						}
						if (isHoldingIronChest() && isOwner(player)) {
							movingTowardItem = null;
							ItemStack newStack = getHeldItemStack().copy();
							for (int i = 0; i < getHeldChestInventory().getSizeInventory(); i++) {
								if (getHeldChestInventory().getStackInSlot(i) != null) {
									newStack = writeInventoryToStack(getHeldChestInventory(), getHeldItemStack().copy());
									break;
								}
							}
							player.inventory.addItemStackToInventory(newStack);
							setHeldItemStack(null);
							return true;
						}
						if (isHoldingVanillaChest() && isOwner(player)) {
							ItemStack newStack = getHeldItemStack().copy();
							for (int i = 0; i < getHeldChestInventory().getSizeInventory(); i++) {
								if (getHeldChestInventory().getStackInSlot(i) != null) {
									newStack = writeInventoryToStack(getHeldChestInventory(), getHeldItemStack().copy());
									break;
								}
							}
							player.inventory.addItemStackToInventory(newStack);
							setHeldItemStack(null);
							chestInventory = null;
						}
					}
				}
			}
		}
		else if (stack != null && stack.getItem() == ModItems.FRIENDER_PEARL) {
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}

			if (!EasyMappings.world(this).isRemote) {
				if (rand.nextInt(10) == 0 || player.capabilities.isCreativeMode) {
					ModRegistries.registerTamedFrienderman(player, this);
					ModNetworking.INSTANCE.sendToAll(new PacketFriendermanRegistrySync(ModRegistries.getTamedFriendermanRegistry()));
					setTamed(true);
					navigator.clearPath();
					setAttackTarget((EntityLivingBase) null);
					//aiSit.setSitting(true);
					setHealth(30.0F);
					if (MCUtils.isSSP(FMLCommonHandler.instance().getMinecraftServerInstance())) {
						setOwnerId(player.getUniqueID());
					}
					else {
						setOwnerId(getUUID(player.getName()));
					}
					//playTameEffect(true);
					EasyMappings.world(this).setEntityState(this, (byte) 7);
				}
				else {
					//playTameEffect(false);
					EasyMappings.world(this).setEntityState(this, (byte) 6);
				}
			}
			return true;
		}
		return super.processInteract(player, hand);
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return isTamed() && isOwner(player);
	}

	public boolean isTamed() {
		return (dataManager.get(TAMED).byteValue() & 4) != 0;
	}

	public void setTamed(boolean tamed) {
		byte b0 = dataManager.get(TAMED).byteValue();
		if (tamed) {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 | 4)));
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
		}
		else {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 & -5)));
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
			getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
		}

	}

	public boolean isSitting() {
		return (dataManager.get(TAMED).byteValue() & 1) != 0;
	}

	public void setSitting(boolean sitting) {
		byte b0 = dataManager.get(TAMED).byteValue();
		if (sitting) {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 | 1)));
		}
		else {
			dataManager.set(TAMED, Byte.valueOf((byte) (b0 & -2)));
		}
		if (aiSit != null) {
			aiSit.setSitting(sitting);
		}
	}

	@Override
	@Nullable
	public UUID getOwnerId() {
		return (UUID) ((Optional<?>) dataManager.get(OWNER_UNIQUE_ID)).orNull();
	}

	public void setOwnerId(@Nullable UUID p_184754_1_) {
		dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
		dataManager.set(OWNER_NAME, getPlayerName(p_184754_1_));
	}

	private String getPlayerName(UUID uuid) {
		return PlayerUUIDUtils.getPlayerName(uuid);
	}

	private boolean canFriendermanPickupItem(ItemStack stack) {
		return isHoldingChest() && chestHasRoom(stack) && deathTime <= 0;
	}

	public boolean isHoldingChest() {
		return isHoldingEnderStorageChest() || isHoldingVanillaChest() || isHoldingIronChest();
	}

	public boolean isHoldingIronChest() {
		if (Mods.IRONCHESTS.isLoaded()) {
			return getHeldItemStack() != null && IronChests.isIronChest(getHeldItemStack());
		}
		return false;
	}

	public boolean chestHasRoom(ItemStack stack) {
		return isHoldingChest() && testInventoryInsertion(getHeldChestInventory(), stack) > 0;//InventoryUtils.canInsertStack(getHeldChestInventory(), stack);
	}

	public int testInventoryInsertion(IInventory inventory, ItemStack item) {
		if (item == null || item.getCount() == 0) {
			return 0;
		}
		if (inventory == null) {
			return 0;
		}
		int slotCount = inventory.getSizeInventory();
		int itemSizeCounter = item.getCount();
		for (int i = 0; i < slotCount && itemSizeCounter > 0; i++) {

			if (!inventory.isItemValidForSlot(i, item)) {
				continue;
			}
			ItemStack inventorySlot = inventory.getStackInSlot(i);
			if (inventorySlot == null) {
				itemSizeCounter -= Math.min(Math.min(itemSizeCounter, inventory.getInventoryStackLimit()), item.getMaxStackSize());
			}
			else if (areMergeCandidates(item, inventorySlot)) {

				int space = inventorySlot.getMaxStackSize() - inventorySlot.getCount();
				itemSizeCounter -= Math.min(itemSizeCounter, space);
			}
		}
		if (itemSizeCounter != item.getCount()) {
			itemSizeCounter = Math.max(itemSizeCounter, 0);
			return item.getCount() - itemSizeCounter;
		}
		return 0;
	}

	boolean areMergeCandidates(ItemStack source, ItemStack target) {
		return source.isItemEqual(target) && ItemStack.areItemStackTagsEqual(source, target) && target.getCount() < target.getMaxStackSize();
	}

	public boolean isHoldingVanillaChest() {
		return getHeldItemStack() != null && ChestUtils.isVanillaChest(getHeldItemStack());
	}

	public VanillaChestTypes getVanillaChestType() {
		if (isHoldingVanillaChest()) {
			return ChestUtils.getVanillaChestType(getHeldItemStack());
		}
		return null;
	}

	public ChestType getHeldChestType() {
		if (isHoldingChest()) {
			if (isHoldingVanillaChest()) {
				return ChestType.VANILLA;
			}
			else if (isHoldingIronChest()) {
				return ChestType.IRONCHEST;
			}
			else if (isHoldingEnderStorageChest()) {
				return ChestType.ENDERSTORAGE;
			}
		}
		return null;
	}

	public boolean isHoldingEnderStorageChest() {
		if (Mods.ENDERSTORAGE.isLoaded()) {
			return getHeldItemStack() != null && getHeldItemStack().getItem() == EnderStorage.getEnderStorageItem() && getHeldItemStack().getItemDamage() == 0;
		}
		return false;
	}

	public IInventory getHeldChestInventory() {
		if (isHoldingEnderStorageChest()) {
			return EnderStorage.getInventoryFromStorage(EnderStorage.getStorageFromItem(getEntityWorld(), getHeldItemStack()));
		}
		else if (isHoldingVanillaChest()) {
			switch (getVanillaChestType()) {
			case ENDER:
				return ((EntityPlayer) getOwner()).getInventoryEnderChest();
			case NORMAL:
			case TRAPPED:
			default:
				return chestInventory;
			}

		}
		else if (isHoldingIronChest()) {
			return chestInventory;
		}
		return null;
	}

	public String getOwnerName() {
		return dataManager.get(OWNER_NAME);
	}

	@Override
	@Nullable
	public EntityLivingBase getOwner() {
		try {
			UUID uuid = getOwnerId();
			return uuid == null ? null : EasyMappings.world(this).getPlayerEntityByUUID(uuid);
		}
		catch (IllegalArgumentException var2) {
			return null;
		}
	}

	public boolean isOwner(EntityLivingBase entityIn) {
		return entityIn == getOwner();
	}

	/**
	 * Returns the AITask responsible of the sit logic
	 */
	public EntityAISit getAISit() {
		return aiSit;
	}

	protected void playTameEffect(boolean play) {
		EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

		if (!play) {
			enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
		}

		for (int i = 0; i < 7; ++i) {
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			EasyMappings.world(this).spawnParticle(enumparticletypes, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, d0, d1, d2, new int[0]);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), ((int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

		if (flag) {
			applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if (id == 7) {
			playTameEffect(true);
		}
		else if (id == 6) {
			playTameEffect(false);
		}
		else if (id == 100) {
			setSitting(true);
		}
		else if (id == 101) {
			setSitting(false);
		}
		else {
			super.handleStatusUpdate(id);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (isEntityInvulnerable(source)) {
			return false;
		}
		if ((source instanceof EntityDamageSourceIndirect)) {
			for (int i = 0; i < 64; i++) {
			}
			return false;
		}
		boolean flag = super.attackEntityFrom(source, amount);
		if ((source.isUnblockable()) && (rand.nextInt(10) != 0)) {
			teleportRandomly();
		}
		return flag;
	}

	protected boolean teleportRandomly() {
		double d0 = posX + (rand.nextDouble() - 0.5D) * 64.0D;
		double d1 = posY + (rand.nextInt(64) - 32);
		double d2 = posZ + (rand.nextDouble() - 0.5D) * 64.0D;
		return teleportTo(d0, d1, d2);
	}

	protected boolean teleportToEntity(Entity entity) {
		Vec3d vec3d = new Vec3d(posX - entity.posX, getEntityBoundingBox().minY + height / 2.0F - entity.posY + entity.getEyeHeight(), posZ - entity.posZ);
		vec3d = vec3d.normalize();
		double d1 = posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
		double d2 = posY + (rand.nextInt(16) - 8) - vec3d.y * 16.0D;
		double d3 = posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
		return teleportTo(d1, d2, d3);
	}

	private boolean teleportTo(double x, double y, double z) {
		EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		boolean flag = attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

		if (flag) {
			EasyMappings.world(this).playSound((EntityPlayer) null, prevPosX, prevPosY, prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, getSoundCategory(), 1.0F, 1.0F);
			playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}

	@Override
	public boolean getCanSpawnHere() {
		return EasyMappings.world(this).getBlockState((new BlockPos(this)).down()).canEntitySpawn(this);
	}

	@Override
	public void onLivingUpdate() {
		/*
		World w = EasyMappings.world(this);
		if (isTamed() && w != null && w.getMinecraftServer() != null) {
			boolean ownerIsOnline = false;
			GameProfile[] onlineProfiles = w.getMinecraftServer().getOnlinePlayerProfiles();
			for (GameProfile onlineProfile : onlineProfiles) {
				if (onlineProfile.getId().equals(getOwnerId())) {
					ownerIsOnline = true;
					break;
				}
			}
			if (ownerIsOnline) {
				if (getOwner() != null) {
					System.out.println(getOwner().dimension + " : " + dimension);
					if (getOwner().dimension != dimension) {
						TeleportUtils.teleportEntity(this, getOwner().dimension, getOwner().posX + 3, getOwner().posY + 1, getOwner().posZ + 3);
						return;
					}
				}
			}
		}
		*/
		//for (int i = 0; i < 2; ++i) {
		if (getEntityWorld() != null && getEntityWorld().isRemote) {
			double x = posX + (rand.nextDouble() - 0.5D) * width;
			double y = posY + rand.nextDouble() * height - 0.25D;
			double z = posZ + (rand.nextDouble() - 0.5D) * width;
			double sx = (rand.nextDouble() - 0.5D) * 2.0D;
			double sy = -rand.nextDouble();
			double sz = (rand.nextDouble() - 0.5D) * 2.0D;
			ParticleUtil.spawn(EnumParticles.LOVE, getEntityWorld(), x, y, z, sx, sy, sz);
		}
		//}

		if (!lidClosed) {
			if (getLidAngle() >= 1.5F) {
				lidOpening = false;
			}
			if (getLidAngle() < 1.5F && lidOpening) {
				incLidAngle();
			}
			else {
				decLidAngle();
			}
			if (getLidAngle() <= 0.0F) {
				setLidAngle(0.0F);
				if (!lidOpening) {
					lidClosed = true;
				}
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
		//this.updateElytra();
		move(MoverType.SELF, moveStrafing, randomYawVelocity, moveForward);
		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("push");
		collideWithNearbyEntities();
		EasyMappings.world(this).profiler.endSection();

		EasyMappings.world(this).profiler.startSection("looting");

		if ((canPickUpLoot() && !dead || (isHoldingChest() && isSitting()))) {
			//for (EntityItem entityitem : worldObj.getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().expand(0.0D, -1.0D, 0.0D).expand(6.0D, 2.0D, 6.0D))) {
			List<EntityItem> nearList = getEntityWorld().getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().expand(0.0D, -1.0D, 0.0D).expand(6.0D, 2.0D, 6.0D));
			for (EntityItem entityitem : nearList) {
				if (!entityitem.isDead && entityitem.getItem() != null && canFriendermanPickupItem(entityitem.getItem())) {
					//entityitem.setInfinitePickupDelay();
					if (movingTowardItem == null || movingTowardItem.isDead || !nearList.contains(movingTowardItem)) {
						movingTowardItem = entityitem;
					}
					List<EntityItem> itemListNear = getEntityWorld().getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().expand(0.0D, -1.0D, 0.0D).expand(6.0, 2.0D, 6.0D));
					if (movingTowardItem != null && itemListNear.contains(movingTowardItem)) {
						updateEquipmentIfNeeded(movingTowardItem);
					}
				}
			}
			//movingTowardItem = null;
		}

		EasyMappings.world(this).profiler.endSection();
	}

	@Override
	protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canFriendermanPickupItem(stack)) {
			if (lidClosed) {
				lidClosed = false;
				lidOpening = true;
			}
			ItemStack itemstack1 = null;
			if (!lidClosed && !lidOpening) {
				itemstack1 = InventoryUtils.addItem(getHeldChestInventory(), stack);
				if (itemstack1 == null) {
					if (movingTowardItem != null) {
						movingTowardItem = null;
					}
					itemEntity.setDead();
				}
				else {
					stack.setCount(itemstack1.getCount());
				}
			}
		}
	}

	public boolean shouldAttackEntity(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof EntityCreeper) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof EntityFrienderman) {
				EntityFrienderman frienderman = (EntityFrienderman) p_142018_1_;

				if (frienderman.isTamed() && frienderman.getOwner() == p_142018_2_) {
					return false;
				}
			}

			return p_142018_1_ instanceof EntityPlayer && p_142018_2_ instanceof EntityPlayer && !((EntityPlayer) p_142018_2_).canAttackPlayer((EntityPlayer) p_142018_1_) ? false : !(p_142018_1_ instanceof EntityHorse) || !((EntityHorse) p_142018_1_).isTame();
		}
		else {
			return false;
		}
	}

	static class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
		private final EntityFrienderman enderman;
		private EntityPlayer player;
		private int aggroTime;
		private int teleportTime;

		public AIFindPlayer(EntityFrienderman p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			enderman = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			double d0 = getTargetDistance();
			player = EasyMappings.world(enderman).getNearestAttackablePlayer(enderman.posX, enderman.posY, enderman.posZ, d0, d0, (Function<EntityPlayer, Double>) null, (@Nullable EntityPlayer player) -> (player != null) && (enderman.shouldAttackPlayer(player)));
			return player != null;
		}

		@Override
		public void startExecuting() {
			aggroTime = 5;
			teleportTime = 0;
		}

		@Override
		public void resetTask() {
			player = null;
			super.resetTask();
		}

		@Override
		public boolean shouldContinueExecuting() {
			if (player != null) {
				if (!enderman.shouldAttackPlayer(player)) {
					return false;
				}
				enderman.faceEntity(player, 10.0F, 10.0F);
				return true;
			}
			return (targetEntity != null) && (targetEntity.isEntityAlive()) ? true : super.shouldContinueExecuting();
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
					if (enderman.shouldAttackPlayer(targetEntity)) {
						if (targetEntity.getDistanceSq(enderman) < 16.0D) {
							enderman.teleportToEntity(targetEntity);
						}
						teleportTime = 0;
					}
					else if ((targetEntity.getDistanceSq(enderman) > 256.0D) && (teleportTime++ >= 30) && (enderman.teleportToEntity(targetEntity))) {
						teleportTime = 0;
					}
				}
				super.updateTask();
			}
		}
	}

	@Nullable
	public IBlockState getHeldBlockState() {
		return (IBlockState) ((Optional<?>) dataManager.get(CARRIED_BLOCK)).orNull();
	}

	@Nullable
	public ItemStack getHeldItemStack() {
		return dataManager.get(CARRIED_ITEM);
	}

	/*
		static class AIPlaceBlock extends EntityAIBase {
			private final EntityFrienderman enderman;
	
			public AIPlaceBlock(EntityFrienderman p_i45843_1_) {
				enderman = p_i45843_1_;
			}
	
			@Override
			public boolean shouldExecute() {
				return enderman.getHeldBlockState() == null;
			}
	
			@Override
			public void updateTask() {
				Random random = enderman.getRNG();
				World world = enderman.worldObj;
				int i = MathHelper.floor_double(enderman.posX - 1.0D + random.nextDouble() * 2.0D);
				int j = MathHelper.floor_double(enderman.posY + random.nextDouble() * 2.0D);
				int k = MathHelper.floor_double(enderman.posZ - 1.0D + random.nextDouble() * 2.0D);
				BlockPos blockpos = new BlockPos(i, j, k);
				IBlockState iblockstate = world.getBlockState(blockpos);
				IBlockState iblockstate1 = world.getBlockState(blockpos.down());
				IBlockState iblockstate2 = enderman.getHeldBlockState();
				if ((iblockstate2 != null) && (canPlaceBlock(world, blockpos, iblockstate2.getBlock(), iblockstate, iblockstate1))) {
					world.setBlockState(blockpos, iblockstate2, 3);
					enderman.setHeldBlockState((IBlockState) null);
				}
			}
	
			private boolean canPlaceBlock(World p_188518_1_, BlockPos p_188518_2_, Block p_188518_3_, IBlockState p_188518_4_, IBlockState p_188518_5_) {
				return p_188518_5_.getMaterial() == Material.AIR ? false : p_188518_4_.getMaterial() != Material.AIR ? false : !p_188518_3_.canPlaceBlockAt(p_188518_1_, p_188518_2_) ? false : p_188518_5_.isFullCube();
			}
		}
	
		static class AITakeBlock extends EntityAIBase {
			private final EntityFrienderman enderman;
	
			public AITakeBlock(EntityFrienderman p_i45841_1_) {
				enderman = p_i45841_1_;
			}
	
			@Override
			public boolean shouldExecute() {
				//return enderman.getHeldBlockState() != null ? false : (!enderman.worldObj.getGameRules().getBoolean("mobGriefing") ? false : enderman.worldObj.rand.nextInt(1) == 0);
				return enderman.getHeldBlockState() != null ? false : (!enderman.worldObj.getGameRules().getBoolean("mobGriefing") ? false : true);
			}
	
			@Override
			public void updateTask() {
				Random random = enderman.getRNG();
				World world = enderman.worldObj;
				int i = MathHelper.floor_double(enderman.posX - 2.0D + random.nextDouble() * 4.0D);
				int j = MathHelper.floor_double(enderman.posY + random.nextDouble() * 3.0D);
				int k = MathHelper.floor_double(enderman.posZ - 2.0D + random.nextDouble() * 4.0D);
				BlockPos blockpos = new BlockPos(i, j, k);
				BlockPos blockpos2 = new BlockPos(i, j + 1, k);
				IBlockState iblockstate = world.getBlockState(blockpos);
				IBlockState iblockstate2 = world.getBlockState(blockpos2);
				Block block = iblockstate.getBlock();
				Block block2 = iblockstate2.getBlock();
				RayTraceResult raytraceresult = world.rayTraceBlocks(new Vec3d(MathHelper.floor_double(enderman.posX) + 0.5F, j + 0.5F, MathHelper.floor_double(enderman.posZ) + 0.5F), new Vec3d(i + 0.5F, j + 0.5F, k + 0.5F), false, true, false);
				boolean flag = raytraceresult != null && raytraceresult.getBlockPos().equals(blockpos);
	
				if (EntityFrienderman.CARRIABLE_BLOCKS.contains(block) && flag) {
					enderman.setHeldBlockState(iblockstate);
					world.setBlockToAir(blockpos);
				}
				else if (EntityFrienderman.CARRIABLE_BLOCKS.contains(block2) && flag) {
					enderman.setHeldBlockState(iblockstate2);
					world.setBlockToAir(blockpos2);
				}
			}
		}
	*/
	static class EntityAISit extends EntityAIBase {
		private final EntityFrienderman theEntity;
		/** If the EntityTameable is sitting. */
		private boolean isSitting;

		public EntityAISit(EntityFrienderman entityIn) {
			theEntity = entityIn;
			setMutexBits(5);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (!theEntity.isTamed()) {
				return false;
			}
			else if (theEntity.isInWater()) {
				return false;
			}
			else if (!theEntity.onGround) {
				return false;
			}
			else {
				EntityLivingBase entitylivingbase = theEntity.getOwner();
				return entitylivingbase == null ? true : (theEntity.getDistanceSq(entitylivingbase) < 144.0D && entitylivingbase.getRevengeTarget() != null ? false : isSitting);
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			theEntity.getNavigator().clearPath();
			theEntity.setSitting(true);
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {
			theEntity.setSitting(false);
		}

		/**
		 * Sets the sitting flag.
		 */
		public void setSitting(boolean sitting) {
			isSitting = sitting;
		}
	}

	static class EntityAICollectItem extends EntityAIBase {

		private EntityFrienderman thePet = null;

		private PathNavigate pathFinder;

		private EntityItem targetItem = null;

		public EntityAICollectItem(EntityFrienderman thePet) {
			this.thePet = thePet;
			pathFinder = thePet.getNavigator();
			setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if (!pathFinder.noPath() || !thePet.isTamed() || thePet.deathTime > 0) {// ensure frienderman doesn't collect dropped chest in midst of dying
				return false;
			}
			if (thePet.isSitting() || !thePet.isHoldingChest()) {
				return false;
			}
			if (thePet.world != null) {

				List<EntityItem> items = thePet.world.getEntitiesWithinAABB(EntityItem.class, thePet.getEntityBoundingBox().expand(0.0D, -1.0D, 0.0D).expand(10D, 2.0D, 10D));
				EntityItem closest = null;
				double closestDistance = Double.MAX_VALUE;
				for (EntityItem item : items) {
					if (!item.isDead && item.onGround) {
						double dist = item.getDistanceSq(thePet);
						if (dist < closestDistance && thePet.testInventoryInsertion(thePet.getHeldChestInventory(), item.getItem()) > 0 && !item.isInWater()) {
							closest = item;
							closestDistance = dist;
						}
					}
				}
				if (closest != null) {
					targetItem = closest;
					return true;
				}
			}
			return false;
		}

		@Override
		public void resetTask() {
			pathFinder.clearPath();
			targetItem = null;
		}

		@Override
		public boolean shouldContinueExecuting() {
			return thePet.isEntityAlive() && !pathFinder.noPath() && !targetItem.isDead;
		}

		@Override
		public void startExecuting() {
			if (targetItem != null) {
				pathFinder.tryMoveToXYZ(targetItem.posX, targetItem.posY, targetItem.posZ, 0.4f);
			}
		}

		@Override
		public void updateTask() {
			super.updateTask();
			if (!thePet.world.isRemote) {
				if (targetItem != null && thePet.getDistanceSq(targetItem) < 1.0) {
					ItemStack stack = targetItem.getItem();
					int preEatSize = stack.getCount();
					//ItemDistribution.insertItemIntoInventory(luggage.getInventory(), stack);
					InventoryUtils.addItem(thePet.getHeldChestInventory(), stack);
					// Check that the size changed
					if (preEatSize != stack.getCount()) {
						/*
						if (luggage.lastSound > 15) {
							boolean isFood = stack.getItem() instanceof ItemFood;
							luggage.playSound(isFood? "openblocks:luggage.eat.food" : "openblocks:luggage.eat.item",
									0.5f, 1.0f + (luggage.worldObj.rand.nextFloat() * 0.2f));
							luggage.lastSound = 0;
						}
						*/
						if (stack.getCount() == 0) {
							targetItem.setDead();
						}
					}
				}
			}
		}
	}

	/**
		public EntityItem currentTargetItem = null;
	
		public void pickupItem(EntityItem itemEntity) {
			ItemStack stack = itemEntity.getEntityItem();
			//if (canFriendermanPickupItem(stack)) {
			if (InventoryUtils.canInsertStack(getHeldChestInventory(), stack)) {
				if (lidClosed) {
					lidClosed = false;
					lidOpening = true;
				}
				if (!lidClosed && !lidOpening) {
					ItemStack itemstack1 = InventoryUtils.insertItem(getHeldChestInventory(), stack);
					//ItemStack itemstack1 = stack;
					if (itemstack1 == null) {
						if (currentTargetItem != null) {
							currentTargetItem.setDead();
							currentTargetItem = null;
						}
						if (itemEntity != null) {
							itemEntity.setDead();
							itemEntity = null;
						}
					}
					else {
						stack.stackSize -= itemstack1.stackSize;
						if (stack.stackSize <= 0) {
							stack = null;
						}
					}
				}
			}
		}
	*/
	static class EntityAIMoveToEntityItem extends EntityAIBase {
		private final EntityFrienderman thePet;
		World theWorld;
		List<EntityItem> itemsNear = Lists.newArrayList();
		EntityItem currentTargetItem = null;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIMoveToEntityItem(EntityFrienderman thePetIn, float minDistIn, float maxDistIn) {
			thePet = thePetIn;
			theWorld = EasyMappings.world(thePetIn);
			petPathfinder = thePetIn.getNavigator();
			minDist = minDistIn;
			maxDist = maxDistIn;
			setMutexBits(3);

			if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			//EntityLivingBase entitylivingbase = thePet.getOwner();
			itemsNear = thePet.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, thePet.getEntityBoundingBox().expand(0.0D, -1.0D, 0.0D).expand(6.0D, 2.0D, 6.0D));
			if (!thePet.isTamed() || itemsNear.size() <= 0 || !thePet.isHoldingChest()) {
				return false;
			}
			return true;
		}

		protected void pickupItem(EntityItem itemEntity) {
			ItemStack stack = itemEntity.getItem();
			if (thePet.canFriendermanPickupItem(stack)) {
				if (thePet.lidClosed) {
					thePet.lidClosed = false;
					thePet.lidOpening = true;
				}
				ItemStack itemstack1 = null;
				if (!thePet.lidClosed && !thePet.lidOpening) {
					itemstack1 = InventoryUtils.addItem(thePet.getHeldChestInventory(), stack);
					if (itemstack1 == null) {
						if (currentTargetItem != null) {
							currentTargetItem = null;
						}
						itemEntity.setDead();
					}
					else {
						stack.setCount(itemstack1.getCount());
					}
				}
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean shouldContinueExecuting() {
			return !petPathfinder.noPath() && thePet.isTamed() && (itemsNear.size() > 0 && thePet.getDistanceSq(itemsNear.get(0)) <= 6 * 6);
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			timeToRecalcPath = 0;
			oldWaterCost = thePet.getPathPriority(PathNodeType.WATER);
			thePet.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {
			petPathfinder.clearPath();
			thePet.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		/*
				private boolean isEmptyBlock(BlockPos pos) {
					IBlockState iblockstate = theWorld.getBlockState(pos);
					return iblockstate.getMaterial() == Material.AIR ? true : !iblockstate.isFullCube();
				}
		*/
		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			if (itemsNear.size() <= 0) {
				return;
			}
			currentTargetItem = itemsNear.get(0);
			thePet.getLookHelper().setLookPositionWithEntity(currentTargetItem, 10.0F, thePet.getVerticalFaceSpeed());

			//if (!thePet.isSitting()) {
			if (currentTargetItem != null) {
				if (--timeToRecalcPath <= 0) {
					timeToRecalcPath = 10;
					/*
					double x = itemsNear.get(0).posX;
					double y = itemsNear.get(0).posY;
					double z = itemsNear.get(0).posZ;
					if (!petPathfinder.tryMoveToXYZ(x, y, z, 2.0)) {
						if (!thePet.getLeashed()) {
							if (thePet.getDistanceSq(currentTargetItem) > 144.0D) {
								int i = MathUtils.floor(currentTargetItem.posX) - 2;
								int j = MathUtils.floor(currentTargetItem.posZ) - 2;
								int k = MathUtils.floor(currentTargetItem.getEntityBoundingBox().minY);
					
								for (int l = 0; l <= 4; ++l) {
									for (int i1 = 0; i1 <= 4; ++i1) {
										if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i1)).isSideSolid(theWorld, new BlockPos(i + l, k - 1, j + i1), EnumFacing.UP) && isEmptyBlock(new BlockPos(i + l, k, j + i1)) && isEmptyBlock(new BlockPos(i + l, k + 1, j + i1))) {
											thePet.setLocationAndAngles(i + l + 0.5F, k, j + i1 + 0.5F, thePet.rotationYaw, thePet.rotationPitch);
											petPathfinder.clearPath();
											return;
										}
									}
								}
							}
						}
					}
					*/
					double x = itemsNear.get(0).posX;
					double y = itemsNear.get(0).posY;
					double z = itemsNear.get(0).posZ;
					if (!thePet.isSitting()) {
						if (thePet.getDistanceSq(currentTargetItem) <= 6) {
							petPathfinder.tryMoveToXYZ(x + 1, y, z + 1, 0.5);
						}
						else if (thePet.getDistanceSq(currentTargetItem) > 6 && thePet.getDistanceSq(currentTargetItem) <= 8) {
							thePet.teleportTo(currentTargetItem.posX, currentTargetItem.posY, currentTargetItem.posZ);
						}

					}
					if (thePet.getDistanceSq(currentTargetItem) <= 6) {
						//thePet.getLookHelper().setLookPositionWithEntity(currentTargetItem, 10.0F, thePet.getVerticalFaceSpeed());
						double d0 = thePet.posX - currentTargetItem.posX;
						double d2 = thePet.posZ - currentTargetItem.posZ;
						float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI) + 90.0D);
						//thePet.setRotation(f, 10);
						pickupItem(currentTargetItem);
						petPathfinder.clearPath();
						/*
						pickupItem(currentTargetItem);
						//thePet.currentTargetItem.setDead();
						//thePet.currentTargetItem = null;
						petPathfinder.clearPath();
						*/
					}
				}
			}
		}
	}

	static class EntityAIFollowOwner extends EntityAIBase {
		private final EntityFrienderman thePet;
		private EntityLivingBase theOwner;
		World theWorld;
		private final double followSpeed;
		private final PathNavigate petPathfinder;
		private int timeToRecalcPath;
		float maxDist;
		float minDist;
		private float oldWaterCost;

		public EntityAIFollowOwner(EntityFrienderman thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
			thePet = thePetIn;
			theWorld = EasyMappings.world(thePetIn);
			followSpeed = followSpeedIn;
			petPathfinder = thePetIn.getNavigator();
			minDist = minDistIn;
			maxDist = maxDistIn;
			setMutexBits(3);

			if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
				throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
			}
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			EntityLivingBase entitylivingbase = thePet.getOwner();

			if (entitylivingbase == null) {
				return false;
			}
			else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).isSpectator()) {
				return false;
			}
			else if (thePet.isSitting()) {
				return false;
			}
			else if (thePet.getDistanceSq(entitylivingbase) < minDist * minDist) {
				return false;
			}
			else {
				theOwner = entitylivingbase;
				return true;
			}
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		@Override
		public boolean shouldContinueExecuting() {
			return !petPathfinder.noPath() && thePet.getDistanceSq(theOwner) > maxDist * maxDist && !thePet.isSitting();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			timeToRecalcPath = 0;
			oldWaterCost = thePet.getPathPriority(PathNodeType.WATER);
			thePet.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		/**
		 * Resets the task
		 */
		@Override
		public void resetTask() {
			theOwner = null;
			petPathfinder.clearPath();
			thePet.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		private boolean isEmptyBlock(BlockPos pos) {
			IBlockState iblockstate = theWorld.getBlockState(pos);
			return iblockstate.getMaterial() == Material.AIR ? true : !iblockstate.isFullCube();
		}

		/**
		 * Updates the task
		 */
		@Override
		public void updateTask() {
			thePet.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, thePet.getVerticalFaceSpeed());

			if (!thePet.isSitting()) {
				if (--timeToRecalcPath <= 0) {
					timeToRecalcPath = 10;

					if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeed)) {
						if (!thePet.getLeashed()) {
							if (thePet.getDistanceSq(theOwner) >= 144.0D) {
								int i = MathUtils.floor(theOwner.posX) - 2;
								int j = MathUtils.floor(theOwner.posZ) - 2;
								int k = MathUtils.floor(theOwner.getEntityBoundingBox().minY);

								for (int l = 0; l <= 4; ++l) {
									for (int i1 = 0; i1 <= 4; ++i1) {
										if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i1)).isSideSolid(theWorld, new BlockPos(i + l, k - 1, j + i1), EnumFacing.UP) && isEmptyBlock(new BlockPos(i + l, k, j + i1)) && isEmptyBlock(new BlockPos(i + l, k + 1, j + i1))) {
											thePet.setLocationAndAngles(i + l + 0.5F, k, j + i1 + 0.5F, thePet.rotationYaw, thePet.rotationPitch);
											petPathfinder.clearPath();
											return;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	static class EntityAIOwnerHurtByTarget extends EntityAITarget {
		EntityFrienderman theDefendingTameable;
		EntityLivingBase theOwnerAttacker;
		private int timestamp;

		public EntityAIOwnerHurtByTarget(EntityFrienderman theDefendingTameableIn) {
			super(theDefendingTameableIn, false);
			theDefendingTameable = theDefendingTameableIn;
			setMutexBits(1);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		@Override
		public boolean shouldExecute() {
			if (!theDefendingTameable.isTamed()) {
				return false;
			}
			else {
				EntityLivingBase entitylivingbase = theDefendingTameable.getOwner();

				if (entitylivingbase == null) {
					return false;
				}
				else {
					theOwnerAttacker = entitylivingbase.getRevengeTarget();
					int i = entitylivingbase.getRevengeTimer();
					return i != timestamp && this.isSuitableTarget(theOwnerAttacker, false) && theDefendingTameable.shouldAttackEntity(theOwnerAttacker, entitylivingbase);
				}
			}
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		@Override
		public void startExecuting() {
			taskOwner.setAttackTarget(theOwnerAttacker);
			EntityLivingBase entitylivingbase = theDefendingTameable.getOwner();

			if (entitylivingbase != null) {
				timestamp = entitylivingbase.getRevengeTimer();
			}

			super.startExecuting();
		}
	}

	public static void setCarriable(Block block, boolean canCarry) {
		if (canCarry) {
			CARRIABLE_BLOCKS.add(block);
		}
		else {
			CARRIABLE_BLOCKS.remove(block);
		}
	}

	static class TempChest implements IInventory {

		NonNullList<ItemStack> invList = NonNullList.create();

		public TempChest() {
			this(27);
		}

		public TempChest(int numSlots) {
			invList = NonNullList.withSize(numSlots, ItemStack.EMPTY);
		}

		@Override
		public String getName() {
			return "inventory.chest";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TextComponentString(getName());
		}

		@Override
		public int getSizeInventory() {
			return 27;
		}

		@Override
		public ItemStack getStackInSlot(int index) {
			return invList.get(index);
		}

		@Override
		public ItemStack decrStackSize(int index, int count) {
			int newSize = invList.get(index).getCount() - count;
			if (newSize < 0) {
				return null;
			}
			invList.get(index).setCount(newSize);
			return invList.get(index);
		}

		@Override
		public ItemStack removeStackFromSlot(int index) {
			return ItemStackHelper.getAndRemove(invList, index);
		}

		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			if (stack != null && stack.getCount() > getInventoryStackLimit()) {
				stack.setCount(getInventoryStackLimit());
			}
			invList.set(index, stack);
		}

		@Override
		public int getInventoryStackLimit() {
			return 64;
		}

		@Override
		public void markDirty() {
		}

		@Override
		public boolean isUsableByPlayer(EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(EntityPlayer player) {
		}

		@Override
		public void closeInventory(EntityPlayer player) {
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return true;
		}

		@Override
		public int getField(int id) {
			return 0;
		}

		@Override
		public void setField(int id, int value) {
		}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public void clear() {
			for (int i = 0; i < invList.size(); i++) {
				invList.set(i, ItemStack.EMPTY);
			}
		}

		@Override
		public boolean isEmpty() {
			for (int i = 0; i < invList.size(); i++) {
				if (!invList.get(i).isEmpty()) {
					return false;
				}
			}
			return true;
		}

	}

	public static boolean getCarriable(Block block) {
		return CARRIABLE_BLOCKS.contains(block);
	}

	static {
		CARRIABLE_BLOCKS.add(Blocks.RED_FLOWER);
	}

}