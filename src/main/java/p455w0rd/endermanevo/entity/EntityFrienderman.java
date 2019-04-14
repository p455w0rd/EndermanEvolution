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
package p455w0rd.endermanevo.entity;

import java.util.*;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.ironchest.common.blocks.chest.IronChestType;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.*;
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
import net.minecraft.network.datasync.*;
import net.minecraft.pathfinding.*;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.ITOPEntityInfoProvider;
import p455w0rd.endermanevo.init.*;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import p455w0rd.endermanevo.integration.EnderStorage;
import p455w0rd.endermanevo.integration.IronChests;
import p455w0rd.endermanevo.network.PacketFriendermanRegistrySync;
import p455w0rd.endermanevo.util.*;
import p455w0rd.endermanevo.util.ChestUtils.VanillaChestTypes;
import p455w0rd.endermanevo.util.EntityUtils;
import p455w0rd.endermanevo.util.FriendermanUtils.ChestType;
import p455w0rdslib.util.*;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class EntityFrienderman extends EntityCreature implements IMob, IEntityOwnable, ITOPEntityInfoProvider {

	private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
	private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15000000596046448D, 0).setSaved(false);
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
	private boolean isPartying;
	private BlockPos jukeboxPosition;

	public EntityFrienderman(final World worldIn) {
		super(worldIn);
		setTamed(false);
		setSize(0.6F, 2.9F);
		stepHeight = 1.0F;
		isImmuneToFire = true;
	}

	@Override
	public void addProbeInfo(final ProbeMode mode, final IProbeInfo probeInfo, final EntityPlayer player, final World world, final Entity entity, final IProbeHitEntityData data) {
		if (!isTamed()) {
			probeInfo.horizontal().item(new ItemStack(ModItems.FRIENDER_PEARL), new ItemStyle().width(8).height(8)).text(" " + I18n.translateToLocal("waila.rightclickpearltotame"));
		}
		else {
			String ownerName = "";
			ownerName = PlayerUUIDUtils.getPlayerName(getOwnerId());
			if (ownerName == "") {
				ownerName = I18n.translateToLocal("top.unavailable");
			}
			probeInfo.horizontal().text(I18n.translateToLocal("waila.owner") + ": " + ownerName);
			probeInfo.horizontal().text(I18n.translateToLocal("waila.mode") + ": " + (isSitting() ? I18n.translateToLocal("waila.idle") : I18n.translateToLocal("waila.followingdefending")));
			if (isHoldingChest() && player == getOwner()) {
				probeInfo.horizontal().text(I18n.translateToLocal("waila.sneakrclicktotake"));
			}
		}
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
	protected void dropLoot(final boolean wasRecentlyHit, final int lootingModifier, final DamageSource source) {
		super.dropLoot(wasRecentlyHit, lootingModifier, source);
		if (isHoldingChest()) {
			entityDropItem(getHeldItemStack(), 2.0F);
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

	public float getLidAngle() {
		return dataManager.get(LID_ANGLE).floatValue();
	}

	public void setLidAngle(final float angle) {
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

	private boolean shouldAttackPlayer(final EntityPlayer player) {
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
	protected SoundEvent getHurtSound(final DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_ENDERMEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ENDERMEN_DEATH;
	}

	@Override
	public void notifyDataManagerChange(final DataParameter<?> key) {
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

	public void setHeldBlockState(@Nullable final IBlockState state) {
		if (!getHeldItemStack().isEmpty()) {
			entityDropItem(getHeldItemStack().copy(), 1.0F);
		}
		dataManager.set(CARRIED_ITEM, ItemStack.EMPTY);
		dataManager.set(CARRIED_BLOCK, Optional.fromNullable(state));
	}

	public void setHeldItemStack(@Nullable final ItemStack stack) {
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
	public void writeEntityToNBT(final NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		if (getOwner() != null) {
			compound.setString("OwnerUUID", getUUID(getOwner().getName()).toString());
			compound.setString("Owner", getOwner().getName());
		}
		else {
			if (dataManager.get(OWNER_NAME) != null && dataManager.get(OWNER_NAME) != "") {
				final UUID uuid = getUUID(dataManager.get(OWNER_NAME));
				if (uuid != null) {
					compound.setString("Owner", dataManager.get(OWNER_NAME));
					compound.setString("OwnerUUID", getUUID(dataManager.get(OWNER_NAME)).toString());
				}
			}
		}
		if (!getHeldItemStack().isEmpty()) {
			final NBTTagCompound itemStack = new NBTTagCompound();
			getHeldItemStack().writeToNBT(itemStack);
			compound.setTag("Item", itemStack);
		}
		if (chestInventory != null) {
			final NBTTagList nbtList = new NBTTagList();
			for (int i = 0; i < chestInventory.getSizeInventory(); i++) {
				if (!chestInventory.getStackInSlot(i).isEmpty()) {
					final NBTTagCompound slotNBT = new NBTTagCompound();
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

	public ItemStack writeInventoryToStack(final IInventory inventory, final ItemStack stack) {
		return getItemBlockWithInventory(inventory, stack);
	}

	public ItemStack getItemBlockWithInventory(final IInventory inventory, final ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ItemBlock && inventory != null && inventory.getSizeInventory() > 0) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			final NBTTagCompound stackNBT = stack.getOrCreateSubCompound("BlockEntityTag");
			final NBTTagList stackList = new NBTTagList();
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				if (inventory.getStackInSlot(i).isEmpty()) {
					continue;
				}
				final NBTTagCompound slotNBT = new NBTTagCompound();
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

	private UUID getUUID(final String name) {
		return PlayerUUIDUtils.getPlayerUUID(name);
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		UUID uuid = null;
		if (compound.hasKey("Owner")) {
			final String s1 = compound.getString("Owner");
			uuid = getUUID(s1);
		}

		if (uuid != null) {
			try {
				setOwnerId(uuid);
				setTamed(true);
			}
			catch (final Throwable var4) {
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
			final NBTTagList tagList = compound.getTagList("Chest", 10);
			for (int i = 0; i < tagList.tagCount(); i++) {
				final NBTTagCompound slotNBT = tagList.getCompoundTagAt(i);
				if (slotNBT != null) {
					chestInventory.setInventorySlotContents(slotNBT.getInteger("Slot"), new ItemStack(slotNBT));
				}
			}
		}
		if (isSitting() != compound.getBoolean("Sitting")) {
			setSitting(compound.getBoolean("Sitting"));
		}
	}

	@Override
	public void setAttackTarget(@Nullable final EntityLivingBase entitylivingbaseIn) {
		if (isTamed() && entitylivingbaseIn == getOwner()) {
			return;
		}
		super.setAttackTarget(entitylivingbaseIn);
		final IAttributeInstance iattributeinstance = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

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
	public boolean processInteract(final EntityPlayer player, final EnumHand hand) {
		if (EasyMappings.world(this).isRemote || hand == EnumHand.OFF_HAND) {
			return false;
		}
		ItemStack stack = player.getHeldItem(hand);
		if (isTamed()) {
			if (!stack.isEmpty()) {
				if (isOwner(player)) {
					if (stack.getItem() == ModItems.FRIENDER_PEARL) {
						if (dataManager.get(DATA_HEALTH_ID).floatValue() < 30.0F) {
							if (!player.capabilities.isCreativeMode) {
								stack.shrink(1);
							}
							heal(30.0F);
							EasyMappings.world(this).setEntityState(this, (byte) 7);
							return true;
						}
					}
					else if (ChestUtils.isVanillaChest(stack)) {
						final ItemStack chestStack = stack.copy();
						ItemStack leftOverStack = ItemStack.EMPTY;
						if (chestStack.getCount() > 1) {
							chestStack.setCount(1);
							leftOverStack = stack.copy();
							leftOverStack.shrink(1);
						}
						setHeldItemStack(chestStack);
						chestInventory = new TempChest();
						ChestUtils.loadInventoryFromStack(chestInventory, getHeldItemStack());
						stack = ItemStack.EMPTY;
						player.setHeldItem(hand, leftOverStack);
						return true;
					}
					else if (ChestUtils.isVanillaShulkerBox(stack)) {
						final ItemStack shulkerStack = stack.copy();
						ItemStack leftOverStack = ItemStack.EMPTY;
						if (shulkerStack.getCount() > 1) {
							shulkerStack.setCount(1);
							leftOverStack = stack.copy();
							leftOverStack.shrink(1);
						}
						setHeldItemStack(shulkerStack);
						chestInventory = new TempChest();
						ChestUtils.loadInventoryFromStack(chestInventory, getHeldItemStack());
						stack = ItemStack.EMPTY;
						player.setHeldItem(hand, leftOverStack);
						return true;
					}
					else if (Mods.ENDERSTORAGE.isLoaded() && stack.getItem() == EnderStorage.getEnderStorageItem() && stack.getItemDamage() == 0) {
						final ItemStack chestStack = stack.copy();
						ItemStack leftOverStack = ItemStack.EMPTY;
						if (chestStack.getCount() > 1) {
							chestStack.setCount(1);
							leftOverStack = stack.copy();
							leftOverStack.shrink(1);
						}
						setHeldItemStack(chestStack);
						player.setHeldItem(hand, leftOverStack);
						stack = ItemStack.EMPTY;
						return true;
					}
					else if (Mods.IRONCHESTS.isLoaded() && IronChests.isIronChest(stack) && IronChests.getChestType(stack) != IronChestType.DIRTCHEST9000) {
						final ItemStack chestStack = stack.copy();
						ItemStack leftOverStack = ItemStack.EMPTY;
						if (chestStack.getCount() > 1) {
							chestStack.setCount(1);
							leftOverStack = stack.copy();
							leftOverStack.shrink(1);
						}
						movingTowardItem = null;
						setHeldItemStack(chestStack);
						chestInventory = new TempChest(IronChests.getInventorySize(stack));
						ChestUtils.loadInventoryFromStack(chestInventory, getHeldItemStack());
						player.setHeldItem(hand, leftOverStack);
						stack = ItemStack.EMPTY;
						return true;
					}
					else if (Mods.IRONCHESTS.isLoaded() && IronChests.isIronShulkerBox(stack)) {
						final ItemStack chestStack = stack.copy();
						ItemStack leftOverStack = ItemStack.EMPTY;
						if (chestStack.getCount() > 1) {
							chestStack.setCount(1);
							leftOverStack = stack.copy();
							leftOverStack.shrink(1);
						}
						movingTowardItem = null;
						setHeldItemStack(chestStack);
						final int numSlots = IronChests.getShulkerBoxInventorySize(stack);
						chestInventory = new TempChest(numSlots);
						ChestUtils.loadInventoryFromStack(chestInventory, getHeldItemStack());
						player.setHeldItem(hand, leftOverStack);
						stack = ItemStack.EMPTY;
						return true;
					}
				}
			}
			else {
				if (isOwner(player)) {
					if (!player.isSneaking()) {
						if (player.getHeldItemMainhand().isEmpty()) {
							setSitting(!isSitting());
							isJumping = false;
							navigator.clearPath();
							setAttackTarget((EntityLivingBase) null);
						}
					}
					else {
						if (Mods.ENDERSTORAGE.isLoaded() && isOwner(player)) {
							if (isHoldingEnderStorageChest()) {
								player.inventory.addItemStackToInventory(getHeldItemStack().copy());
								setHeldItemStack(ItemStack.EMPTY);
								return true;
							}
						}
						if (isHoldingIronChest() && isOwner(player)) {
							movingTowardItem = null;
							ItemStack newStack = getHeldItemStack().copy();
							for (int i = 0; i < getHeldChestInventory().getSizeInventory(); i++) {
								if (!getHeldChestInventory().getStackInSlot(i).isEmpty()) {
									newStack = writeInventoryToStack(getHeldChestInventory(), getHeldItemStack().copy());
									break;
								}
							}
							player.inventory.addItemStackToInventory(newStack);
							setHeldItemStack(ItemStack.EMPTY);
							return true;
						}
						if (isHoldingIronShulkerBox() && isOwner(player)) {
							movingTowardItem = null;
							ItemStack newStack = getHeldItemStack().copy();
							for (int i = 0; i < getHeldChestInventory().getSizeInventory(); i++) {
								if (!getHeldChestInventory().getStackInSlot(i).isEmpty()) {
									newStack = writeInventoryToStack(getHeldChestInventory(), getHeldItemStack().copy());
									break;
								}
							}
							player.inventory.addItemStackToInventory(newStack);
							setHeldItemStack(ItemStack.EMPTY);
							return true;
						}
						if (isHoldingVanillaChest() && isOwner(player)) {
							ItemStack newStack = getHeldItemStack().copy();
							for (int i = 0; i < getHeldChestInventory().getSizeInventory(); i++) {
								if (!getHeldChestInventory().getStackInSlot(i).isEmpty()) {
									newStack = writeInventoryToStack(getHeldChestInventory(), getHeldItemStack().copy());
									break;
								}
							}
							player.inventory.addItemStackToInventory(newStack);
							setHeldItemStack(ItemStack.EMPTY);
							chestInventory = null;
						}
						if (isHoldingVanillaShulkerBox() && isOwner(player)) {
							ItemStack newStack = getHeldItemStack().copy();
							for (int i = 0; i < getHeldChestInventory().getSizeInventory(); i++) {
								if (!getHeldChestInventory().getStackInSlot(i).isEmpty()) {
									newStack = writeInventoryToStack(getHeldChestInventory(), getHeldItemStack().copy());
									break;
								}
							}
							player.inventory.addItemStackToInventory(newStack);
							setHeldItemStack(ItemStack.EMPTY);
							chestInventory = null;
						}
					}
				}
			}
		}
		else if (!stack.isEmpty() && stack.getItem() == ModItems.FRIENDER_PEARL) {
			if (!player.capabilities.isCreativeMode) {
				stack.shrink(1);
			}
			if (!EasyMappings.world(this).isRemote) {
				int random = 0;
				if (!player.capabilities.isCreativeMode) {
					if (EntityUtils.isWearingCustomSkull(player) && EntityUtils.getSkullItem(player) == ModItems.SKULL_FRIENDERMAN) {
						random = rand.nextInt(2);
					}
					else {
						random = rand.nextInt(10);
					}
				}
				if (random == 0) {
					ModRegistries.registerTamedFrienderman(player, this);
					ModNetworking.INSTANCE.sendToAll(new PacketFriendermanRegistrySync(ModRegistries.getTamedFriendermanRegistry()));
					setTamed(true);
					navigator.clearPath();
					setAttackTarget((EntityLivingBase) null);
					setHealth(30.0F);
					if (MCUtils.isSSP(FMLCommonHandler.instance().getMinecraftServerInstance())) {
						setOwnerId(player.getUniqueID());
					}
					else {
						setOwnerId(getUUID(player.getName()));
					}
					EasyMappings.world(this).setEntityState(this, (byte) 7);
				}
			}
		}
		return super.processInteract(player, hand);
	}

	@Override
	public boolean canBeLeashedTo(final EntityPlayer player) {
		return isTamed() && isOwner(player);
	}

	public boolean isTamed() {
		return (dataManager.get(TAMED).byteValue() & 4) != 0;
	}

	public void setTamed(final boolean tamed) {
		final byte b0 = dataManager.get(TAMED).byteValue();
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

	public void setSitting(final boolean sitting) {
		final byte b0 = dataManager.get(TAMED).byteValue();
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

	public void setOwnerId(@Nullable final UUID p_184754_1_) {
		dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(p_184754_1_));
		dataManager.set(OWNER_NAME, getPlayerName(p_184754_1_));
	}

	private String getPlayerName(final UUID uuid) {
		return PlayerUUIDUtils.getPlayerName(uuid);
	}

	private boolean canFriendermanPickupItem(final ItemStack stack) {
		return isHoldingChest() && chestHasRoom(stack) && deathTime <= 0;
	}

	public boolean isHoldingChest() {
		return isHoldingEnderStorageChest() || isHoldingVanillaChest() || isHoldingIronChest() || isHoldingVanillaShulkerBox() || isHoldingIronShulkerBox();
	}

	public boolean isHoldingIronChest() {
		if (Mods.IRONCHESTS.isLoaded()) {
			return !getHeldItemStack().isEmpty() && IronChests.isIronChest(getHeldItemStack());
		}
		return false;
	}

	public boolean isHoldingIronShulkerBox() {
		if (Mods.IRONCHESTS.isLoaded()) {
			return !getHeldItemStack().isEmpty() && IronChests.isIronShulkerBox(getHeldItemStack());
		}
		return false;
	}

	public boolean chestHasRoom(final ItemStack stack) {
		return isHoldingChest() && testInventoryInsertion(getHeldChestInventory(), stack) > 0;
	}

	public int testInventoryInsertion(final IInventory inventory, final ItemStack item) {
		if (item.isEmpty() || item.getCount() == 0) {
			return 0;
		}
		if (inventory == null) {
			return 0;
		}
		final int slotCount = inventory.getSizeInventory();
		int itemSizeCounter = item.getCount();
		for (int i = 0; i < slotCount && itemSizeCounter > 0; i++) {

			if (!inventory.isItemValidForSlot(i, item)) {
				continue;
			}
			final ItemStack inventorySlot = inventory.getStackInSlot(i);
			if (inventorySlot.isEmpty()) {
				itemSizeCounter -= Math.min(Math.min(itemSizeCounter, inventory.getInventoryStackLimit()), item.getMaxStackSize());
			}
			else if (areMergeCandidates(item, inventorySlot)) {

				final int space = inventorySlot.getMaxStackSize() - inventorySlot.getCount();
				itemSizeCounter -= Math.min(itemSizeCounter, space);
			}
		}
		if (itemSizeCounter != item.getCount()) {
			itemSizeCounter = Math.max(itemSizeCounter, 0);
			return item.getCount() - itemSizeCounter;
		}
		return 0;
	}

	boolean areMergeCandidates(final ItemStack source, final ItemStack target) {
		return source.isItemEqual(target) && ItemStack.areItemStackTagsEqual(source, target) && target.getCount() < target.getMaxStackSize();
	}

	public boolean isHoldingVanillaChest() {
		return !getHeldItemStack().isEmpty() && ChestUtils.isVanillaChest(getHeldItemStack());
	}

	public boolean isHoldingVanillaShulkerBox() {
		return !getHeldItemStack().isEmpty() && ChestUtils.isVanillaShulkerBox(getHeldItemStack());
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
			else if (isHoldingVanillaShulkerBox()) {
				return ChestType.VANILLA_SHULKER;
			}
		}
		return null;
	}

	public boolean isHoldingEnderStorageChest() {
		if (Mods.ENDERSTORAGE.isLoaded()) {
			return !getHeldItemStack().isEmpty() && getHeldItemStack().getItem() == EnderStorage.getEnderStorageItem() && getHeldItemStack().getItemDamage() == 0;
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
		else if (isHoldingIronChest() || isHoldingVanillaShulkerBox() || isHoldingIronShulkerBox()) {
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
			final UUID uuid = getOwnerId();
			return uuid == null ? null : getEntityWorld().getPlayerEntityByUUID(uuid);
		}
		catch (final IllegalArgumentException var2) {
			return null;
		}
	}

	public boolean isOwner(final EntityLivingBase entityIn) {
		return entityIn.getUniqueID().equals(getOwnerId());
	}

	public EntityAISit getAISit() {
		return aiSit;
	}

	protected void playTameEffect(final boolean play) {
		EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

		if (!play) {
			enumparticletypes = EnumParticleTypes.SMOKE_NORMAL;
		}

		for (int i = 0; i < 7; ++i) {
			final double d0 = rand.nextGaussian() * 0.02D;
			final double d1 = rand.nextGaussian() * 0.02D;
			final double d2 = rand.nextGaussian() * 0.02D;
			EasyMappings.world(this).spawnParticle(enumparticletypes, posX + rand.nextFloat() * width * 2.0F - width, posY + 0.5D + rand.nextFloat() * height, posZ + rand.nextFloat() * width * 2.0F - width, d0, d1, d2, new int[0]);
		}
	}

	@Override
	public boolean attackEntityAsMob(final Entity entityIn) {
		final boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (int) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());

		if (flag) {
			applyEnchantments(this, entityIn);
		}

		return flag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(final byte id) {
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
	public boolean attackEntityFrom(final DamageSource source, final float amount) {
		if (isEntityInvulnerable(source)) {
			return false;
		}
		if (source instanceof EntityDamageSourceIndirect || source.isProjectile()) {
			for (int i = 0; i < 64; i++) {
				teleportRandomly();
			}
			return false;
		}
		final boolean flag = super.attackEntityFrom(source, amount);
		if (source.isUnblockable() && rand.nextInt(10) != 0) {
			teleportRandomly();
		}
		return flag;
	}

	protected boolean teleportRandomly() {
		final double d0 = posX + (rand.nextDouble() - 0.5D) * 64.0D;
		final double d1 = posY + (rand.nextInt(64) - 32);
		final double d2 = posZ + (rand.nextDouble() - 0.5D) * 64.0D;
		return teleportTo(d0, d1, d2);
	}

	protected boolean teleportToEntity(final Entity entity) {
		Vec3d vec3d = new Vec3d(posX - entity.posX, getEntityBoundingBox().minY + height / 2.0F - entity.posY + entity.getEyeHeight(), posZ - entity.posZ);
		vec3d = vec3d.normalize();
		final double d1 = posX + (rand.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
		final double d2 = posY + (rand.nextInt(16) - 8) - vec3d.y * 16.0D;
		final double d3 = posZ + (rand.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
		return teleportTo(d1, d2, d3);
	}

	private boolean teleportTo(final double x, final double y, final double z) {
		final EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
		if (MinecraftForge.EVENT_BUS.post(event)) {
			return false;
		}
		final boolean flag = attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ());

		if (flag) {
			EasyMappings.world(this).playSound((EntityPlayer) null, prevPosX, prevPosY, prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, getSoundCategory(), 1.0F, 1.0F);
			playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
		}

		return flag;
	}

	@Override
	public boolean getCanSpawnHere() {
		return EasyMappings.world(this).getBlockState(new BlockPos(this).down()).canEntitySpawn(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPartying(final BlockPos pos, final boolean shouldParty) {
		jukeboxPosition = pos;
		isPartying = shouldParty;
	}

	@SideOnly(Side.CLIENT)
	public boolean isPartying() {
		return isPartying;
	}

	@Override
	public void onLivingUpdate() {
		if (getEntityWorld() != null && getEntityWorld().isRemote) {
			final double x = posX + (rand.nextDouble() - 0.5D) * width;
			final double y = posY + rand.nextDouble() * height - 0.25D;
			final double z = posZ + (rand.nextDouble() - 0.5D) * width;
			final double sx = (rand.nextDouble() - 0.5D) * 2.0D;
			final double sy = -rand.nextDouble();
			final double sz = (rand.nextDouble() - 0.5D) * 2.0D;
			ParticleUtil.spawn(EnumParticles.LOVE, getEntityWorld(), x, y, z, sx, sy, sz);
		}
		if (jukeboxPosition == null || jukeboxPosition.distanceSq(posX, posY, posZ) > 12.0D || world.getBlockState(jukeboxPosition).getBlock() != Blocks.JUKEBOX) {
			isPartying = false;
			jukeboxPosition = null;
		}
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
		travel(moveStrafing, randomYawVelocity, moveForward);
		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("push");
		collideWithNearbyEntities();
		EasyMappings.world(this).profiler.endSection();
		EasyMappings.world(this).profiler.startSection("looting");
		if (!dead || isHoldingChest() && isSitting()) {
			final List<EntityItem> nearList = getEntityWorld().getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().grow(6.0));
			for (final EntityItem entityitem : nearList) {
				if (!entityitem.isDead && !entityitem.getItem().isEmpty() && canFriendermanPickupItem(entityitem.getItem())) {
					if (movingTowardItem == null || movingTowardItem.isDead || !nearList.contains(movingTowardItem)) {
						movingTowardItem = entityitem;
					}
					final List<EntityItem> itemListNear = getEntityWorld().getEntitiesWithinAABB(EntityItem.class, getEntityBoundingBox().grow(6.0));
					if (movingTowardItem != null && itemListNear.contains(movingTowardItem)) {
						updateEquipmentIfNeeded(movingTowardItem);
					}
				}
			}
		}
		EasyMappings.world(this).profiler.endSection();
	}

	@Override
	protected void updateEquipmentIfNeeded(final EntityItem itemEntity) {
		final ItemStack stack = itemEntity.getItem();
		if (canFriendermanPickupItem(stack)) {
			if (lidClosed) {
				lidClosed = false;
				lidOpening = true;
			}
			ItemStack itemstack1 = ItemStack.EMPTY;
			if (!lidClosed && !lidOpening) {
				itemstack1 = InventoryUtils.addItem(getHeldChestInventory(), stack);
				if (itemstack1.isEmpty()) {
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

	public boolean shouldAttackEntity(final EntityLivingBase p_142018_1_, final EntityLivingBase p_142018_2_) {
		if (!(p_142018_1_ instanceof EntityCreeper) && !(p_142018_1_ instanceof EntityGhast)) {
			if (p_142018_1_ instanceof EntityFrienderman) {
				final EntityFrienderman frienderman = (EntityFrienderman) p_142018_1_;

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

		public AIFindPlayer(final EntityFrienderman p_i45842_1_) {
			super(p_i45842_1_, EntityPlayer.class, false);
			enderman = p_i45842_1_;
		}

		@Override
		public boolean shouldExecute() {
			final double d0 = getTargetDistance();
			player = EasyMappings.world(enderman).getNearestAttackablePlayer(enderman.posX, enderman.posY, enderman.posZ, d0, d0, (Function<EntityPlayer, Double>) null, (@Nullable final EntityPlayer player) -> player != null && enderman.shouldAttackPlayer(player));
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
			return targetEntity != null && targetEntity.isEntityAlive() ? true : super.shouldContinueExecuting();
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
					else if (targetEntity.getDistanceSq(enderman) > 256.0D && teleportTime++ >= 30 && enderman.teleportToEntity(targetEntity)) {
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

	static class EntityAISit extends EntityAIBase {
		private final EntityFrienderman theEntity;
		private boolean isSitting;

		public EntityAISit(final EntityFrienderman entityIn) {
			theEntity = entityIn;
			setMutexBits(5);
		}

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
				final EntityLivingBase entitylivingbase = theEntity.getOwner();
				return entitylivingbase == null ? true : theEntity.getDistanceSq(entitylivingbase) < 144.0D && entitylivingbase.getRevengeTarget() != null ? false : isSitting;
			}
		}

		@Override
		public void startExecuting() {
			theEntity.getNavigator().clearPath();
			theEntity.setSitting(true);
		}

		@Override
		public void resetTask() {
			theEntity.setSitting(false);
		}

		public void setSitting(final boolean sitting) {
			isSitting = sitting;
		}
	}

	static class EntityAICollectItem extends EntityAIBase {

		private EntityFrienderman thePet = null;

		private final PathNavigate pathFinder;

		private EntityItem targetItem = null;

		public EntityAICollectItem(final EntityFrienderman thePet) {
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
				final List<EntityItem> items = thePet.world.getEntitiesWithinAABB(EntityItem.class, thePet.getEntityBoundingBox().expand(0.0D, -1.0D, 0.0D).expand(10D, 2.0D, 10D));
				EntityItem closest = null;
				double closestDistance = Double.MAX_VALUE;
				for (final EntityItem item : items) {
					if (!item.isDead && item.onGround) {
						final double dist = item.getDistanceSq(thePet);
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
					final ItemStack stack = targetItem.getItem();
					final int preEatSize = stack.getCount();
					InventoryUtils.addItem(thePet.getHeldChestInventory(), stack);
					if (preEatSize != stack.getCount()) {
						if (stack.getCount() == 0) {
							targetItem.setDead();
						}
					}
				}
			}
		}
	}

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

		public EntityAIMoveToEntityItem(final EntityFrienderman thePetIn, final float minDistIn, final float maxDistIn) {
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

		@Override
		public boolean shouldExecute() {
			itemsNear = thePet.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, thePet.getEntityBoundingBox().expand(0.0D, -1.0D, 0.0D).expand(6.0D, 2.0D, 6.0D));
			if (!thePet.isTamed() || itemsNear.size() <= 0 || !thePet.isHoldingChest()) {
				return false;
			}
			return true;
		}

		protected void pickupItem(final EntityItem itemEntity) {
			final ItemStack stack = itemEntity.getItem();
			if (thePet.canFriendermanPickupItem(stack)) {
				if (thePet.lidClosed) {
					thePet.lidClosed = false;
					thePet.lidOpening = true;
				}
				ItemStack itemstack1 = ItemStack.EMPTY;
				if (!thePet.lidClosed && !thePet.lidOpening) {
					itemstack1 = InventoryUtils.addItem(thePet.getHeldChestInventory(), stack);
					if (itemstack1.isEmpty()) {
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

		@Override
		public boolean shouldContinueExecuting() {
			return !petPathfinder.noPath() && thePet.isTamed() && itemsNear.size() > 0 && thePet.getDistanceSq(itemsNear.get(0)) <= 6 * 6;
		}

		@Override
		public void startExecuting() {
			timeToRecalcPath = 0;
			oldWaterCost = thePet.getPathPriority(PathNodeType.WATER);
			thePet.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		@Override
		public void resetTask() {
			petPathfinder.clearPath();
			thePet.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		@Override
		public void updateTask() {
			if (itemsNear.size() <= 0) {
				return;
			}
			currentTargetItem = itemsNear.get(0);
			thePet.getLookHelper().setLookPositionWithEntity(currentTargetItem, 10.0F, thePet.getVerticalFaceSpeed());
			if (currentTargetItem != null) {
				if (--timeToRecalcPath <= 0) {
					timeToRecalcPath = 10;
					final double x = itemsNear.get(0).posX;
					final double y = itemsNear.get(0).posY;
					final double z = itemsNear.get(0).posZ;
					if (!thePet.isSitting()) {
						if (thePet.getDistanceSq(currentTargetItem) <= 6) {
							petPathfinder.tryMoveToXYZ(x + 1, y, z + 1, 0.5);
						}
						else if (thePet.getDistanceSq(currentTargetItem) > 6 && thePet.getDistanceSq(currentTargetItem) <= 8) {
							thePet.teleportTo(currentTargetItem.posX, currentTargetItem.posY, currentTargetItem.posZ);
						}

					}
					if (thePet.getDistanceSq(currentTargetItem) <= 6) {
						pickupItem(currentTargetItem);
						petPathfinder.clearPath();
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

		public EntityAIFollowOwner(final EntityFrienderman thePetIn, final double followSpeedIn, final float minDistIn, final float maxDistIn) {
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

		@Override
		public boolean shouldExecute() {
			final EntityLivingBase entitylivingbase = thePet.getOwner();

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

		@Override
		public boolean shouldContinueExecuting() {
			return !petPathfinder.noPath() && thePet.getDistanceSq(theOwner) > maxDist * maxDist && !thePet.isSitting();
		}

		@Override
		public void startExecuting() {
			timeToRecalcPath = 0;
			oldWaterCost = thePet.getPathPriority(PathNodeType.WATER);
			thePet.setPathPriority(PathNodeType.WATER, 0.0F);
		}

		@Override
		public void resetTask() {
			theOwner = null;
			petPathfinder.clearPath();
			thePet.setPathPriority(PathNodeType.WATER, oldWaterCost);
		}

		private boolean isEmptyBlock(final BlockPos pos) {
			final IBlockState iblockstate = theWorld.getBlockState(pos);
			return iblockstate.getMaterial() == Material.AIR ? true : !iblockstate.isFullCube();
		}

		@Override
		public void updateTask() {
			thePet.getLookHelper().setLookPositionWithEntity(theOwner, 10.0F, thePet.getVerticalFaceSpeed());
			if (!thePet.isSitting()) {
				if (--timeToRecalcPath <= 0) {
					timeToRecalcPath = 10;
					if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeed)) {
						if (!thePet.getLeashed()) {
							if (thePet.getDistanceSq(theOwner) >= 144.0D) {
								final int i = MathUtils.floor(theOwner.posX) - 2;
								final int j = MathUtils.floor(theOwner.posZ) - 2;
								final int k = MathUtils.floor(theOwner.getEntityBoundingBox().minY);
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

		public EntityAIOwnerHurtByTarget(final EntityFrienderman theDefendingTameableIn) {
			super(theDefendingTameableIn, false);
			theDefendingTameable = theDefendingTameableIn;
			setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			if (!theDefendingTameable.isTamed()) {
				return false;
			}
			else {
				final EntityLivingBase entitylivingbase = theDefendingTameable.getOwner();

				if (entitylivingbase == null) {
					return false;
				}
				else {
					theOwnerAttacker = entitylivingbase.getRevengeTarget();
					final int i = entitylivingbase.getRevengeTimer();
					return i != timestamp && this.isSuitableTarget(theOwnerAttacker, false) && theDefendingTameable.shouldAttackEntity(theOwnerAttacker, entitylivingbase);
				}
			}
		}

		@Override
		public void startExecuting() {
			taskOwner.setAttackTarget(theOwnerAttacker);
			final EntityLivingBase entitylivingbase = theDefendingTameable.getOwner();
			if (entitylivingbase != null) {
				timestamp = entitylivingbase.getRevengeTimer();
			}
			super.startExecuting();
		}
	}

	public static void setCarriable(final Block block, final boolean canCarry) {
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

		public TempChest(final int numSlots) {
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
			return invList.size();
		}

		@Override
		public ItemStack getStackInSlot(final int index) {
			return invList.get(index);
		}

		@Override
		public ItemStack decrStackSize(final int index, final int count) {
			final int newSize = invList.get(index).getCount() - count;
			if (newSize < 0) {
				return null;
			}
			invList.get(index).setCount(newSize);
			return invList.get(index);
		}

		@Override
		public ItemStack removeStackFromSlot(final int index) {
			return ItemStackHelper.getAndRemove(invList, index);
		}

		@Override
		public void setInventorySlotContents(final int index, final ItemStack stack) {
			if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
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
		public boolean isUsableByPlayer(final EntityPlayer player) {
			return true;
		}

		@Override
		public void openInventory(final EntityPlayer player) {
		}

		@Override
		public void closeInventory(final EntityPlayer player) {
		}

		@Override
		public boolean isItemValidForSlot(final int index, final ItemStack stack) {
			return true;
		}

		@Override
		public int getField(final int id) {
			return 0;
		}

		@Override
		public void setField(final int id, final int value) {
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

	public static boolean getCarriable(final Block block) {
		return CARRIABLE_BLOCKS.contains(block);
	}

	static {
		CARRIABLE_BLOCKS.add(Blocks.RED_FLOWER);
	}

}