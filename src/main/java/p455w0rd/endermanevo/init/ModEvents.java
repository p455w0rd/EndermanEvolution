package p455w0rd.endermanevo.init;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.client.model.layers.LayerSkullEyes;
import p455w0rd.endermanevo.client.render.ParticleRenderer;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.items.ItemSkullBase;
import p455w0rd.endermanevo.util.EntityUtils;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.MCPrivateUtils;
import p455w0rdslib.util.MathUtils;
import p455w0rdslib.util.ReflectionUtils;

public class ModEvents {

	private static List<EntityLivingBase> ENTITY_LIST = Lists.<EntityLivingBase>newArrayList();

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ModEvents());
	}
	/*
		@SubscribeEvent
		public void onRespawn(PlayerEvent.Clone e) {
			List<EntityFrienderman> friendermanList = Lists.newArrayList();
			EntityPlayer newPlayer = e.getEntityPlayer();
			for (int entityID : ModRegistries.getTamedFriendermanRegistry().keySet()) {
				EntityFrienderman entity = (EntityFrienderman) e.getOriginal().getEntityWorld().getEntityByID(entityID);
				if (entity != null && ModRegistries.getTamedFriendermanRegistry().get(entity.getEntityId()) != null && ModRegistries.getTamedFriendermanRegistry().get(entity.getEntityId()).equals(e.getEntityPlayer().getUniqueID())) {
					if (entity.isTamed() && !entity.isSitting()) {
						friendermanList.add(entity);
					}
				}
			}
			for (EntityFrienderman frienderman : friendermanList) {
				frienderman.changeDimension(newPlayer.dimension);
				frienderman.setPosition(newPlayer.posX, newPlayer.posY + 2, newPlayer.posZ);
			}
		}
	
		@SubscribeEvent
		public void onDimensionChange(EntityTravelToDimensionEvent e) {
			List<EntityFrienderman> friendermanList = Lists.newArrayList();
			if (!(e.getEntity() instanceof EntityPlayer)) {
				return;
			}
			EntityPlayer newPlayer = (EntityPlayer) e.getEntity();
			World world = newPlayer.getEntityWorld();
			for (int entityID : ModRegistries.getTamedFriendermanRegistry().keySet()) {
				EntityFrienderman entity = (EntityFrienderman) world.getEntityByID(entityID);
				if (entity != null && ModRegistries.getTamedFriendermanRegistry().get(entity.getEntityId()) != null && ModRegistries.getTamedFriendermanRegistry().get(entity.getEntityId()).equals(newPlayer.getUniqueID())) {
					if (entity.isTamed() && !entity.isSitting()) {
						friendermanList.add(entity);
					}
				}
			}
			for (EntityFrienderman frienderman : friendermanList) {
	
				BlockPos playerPos = new BlockPos(newPlayer.posX, newPlayer.posY, newPlayer.posZ);
				BlockPos spawnPos = playerPos.east(3).west(3).up(2);
	
				for (int i = 0; i < Integer.MAX_VALUE; i++) {
					spawnPos = playerPos.east(i).west(i).down();
					IBlockState state = world.getBlockState(spawnPos);
					Block spawnBlock = state.getBlock();
					if (spawnBlock.canCreatureSpawn(state, world, spawnPos, SpawnPlacementType.ON_GROUND) && !(world.getBlockState(spawnPos.up()).getBlock() instanceof BlockPortal)) {
						break;
					}
				}
	
				EntityFrienderman newEntity = (EntityFrienderman) TeleportUtils.teleportEntity(frienderman, e.getDimension(), e.getDimension() == -1 ? spawnPos.getX() / 8 : spawnPos.getX(), e.getDimension() == -1 ? spawnPos.getY() / 8 : spawnPos.getY(), e.getDimension() == -1 ? spawnPos.getZ() / 8 : spawnPos.getZ(), frienderman.rotationYaw, frienderman.rotationPitch);
				if (newEntity != null) {
					ModRegistries.registerTamedFrienderman(newPlayer, newEntity);
					ModNetworking.INSTANCE.sendToAll(new PacketFriendermanRegistrySync(ModRegistries.getTamedFriendermanRegistry()));
				}
				//frienderman.changeDimension(e.getDimension());
				//p455w0rdslib.util.EntityUtils.teleportEntityToDimension(newPlayer.getEntityWorld(), frienderman, e.getDimension());
				//frienderman.setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ());
			}
		}
	
		@SubscribeEvent
		public void onDeath(LivingDeathEvent e) {
			if (!e.isCanceled() && !e.getEntityLiving().getEntityWorld().isRemote && e.getEntityLiving() instanceof EntityFrienderman) {
				EntityFrienderman frienderman = (EntityFrienderman) e.getEntityLiving();
				ModRegistries.unregisterTamedFrienderman(frienderman);
				ModNetworking.INSTANCE.sendToAll(new PacketFriendermanRegistrySync(ModRegistries.getTamedFriendermanRegistry()));
			}
		}
	*/

	@SubscribeEvent
	public void onDeath(LivingDeathEvent e) {
		if (!e.isCanceled() && e.getEntityLiving() instanceof EntitySkeleton) {
			if (ENTITY_LIST.contains(e.getEntityLiving())) {
				ENTITY_LIST.remove(e.getEntityLiving());
			}
		}
	}

	@SuppressWarnings("unchecked")
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderLivingBase(RenderLivingEvent.Pre<?> event) {
		RenderLivingBase<EntityLivingBase> renderer = (RenderLivingBase<EntityLivingBase>) event.getRenderer();
		List<LayerRenderer<EntityLivingBase>> layers = ReflectionHelper.getPrivateValue(RenderLivingBase.class, renderer, ReflectionUtils.determineSRG("layerRenderers"));
		boolean isEyesLayerAdded = false;
		for (LayerRenderer<EntityLivingBase> layer : layers) {
			if (layer instanceof LayerSkullEyes) {
				isEyesLayerAdded = true;
			}
		}
		if (!isEyesLayerAdded) {
			renderer.addLayer(new LayerSkullEyes(renderer));
		}
		if (EntityUtils.isWearingCustomSkull(event.getEntity())) {
			if (renderer.getMainModel() instanceof ModelBiped) {
				ModelBiped bipedModel = (ModelBiped) renderer.getMainModel();
				if (!bipedModel.bipedHead.isHidden || !bipedModel.bipedHeadwear.isHidden) {
					bipedModel.bipedHead.isHidden = true;
					bipedModel.bipedHeadwear.isHidden = true;
				}
			}
		}
		else {
			if (renderer.getMainModel() instanceof ModelBiped) {
				ModelBiped bipedModel = (ModelBiped) renderer.getMainModel();
				if (bipedModel.bipedHead.isHidden || bipedModel.bipedHeadwear.isHidden) {
					bipedModel.bipedHead.isHidden = false;
					bipedModel.bipedHeadwear.isHidden = false;
				}
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderAfterWorld(RenderWorldLastEvent event) {
		ParticleRenderer.getInstance().renderParticles(EasyMappings.player(), event.getPartialTicks());
	}

	// Enderman skulls

	@SubscribeEvent
	public void onLiving(LivingEvent.LivingUpdateEvent e) {
		if (e.getEntityLiving() instanceof EntityEnderman) {
			EntityEnderman enderman = (EntityEnderman) e.getEntityLiving();
			if (enderman.getAttackTarget() == null) {
				MCPrivateUtils.setEndermanScreaming(enderman, false);
			}
		}
	}

	@SubscribeEvent
	public void onTargetSelect(LivingSetAttackTargetEvent e) {
		if (e.getEntityLiving() instanceof EntityEnderman && e.getTarget() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.getTarget();
			ItemStack stack = player.inventory.armorInventory.get(3);
			boolean stopAttack = false;
			if (!stack.isEmpty() && stack.getItem() instanceof ItemSkullBase) {
				ItemSkullBase skull = (ItemSkullBase) stack.getItem();
				if (skull.isEndermanSkull()) {
					stopAttack = true;
				}
			}
			if (stopAttack) {
				((EntityLiving) e.getEntityLiving()).setAttackTarget(null);
			}
		}
	}

	// mob drops

	@SubscribeEvent
	public void onMobDrop(LivingDropsEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) { //only respect death by living entity
			World world = event.getEntity().getEntityWorld();
			double x = event.getEntity().posX;
			double y = event.getEntity().posY;
			double z = event.getEntity().posZ;
			EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			if ((event.getEntity() instanceof EntityDragon)) {
				ItemStack frienderPearls = new ItemStack(ModItems.FRIENDER_PEARL, 16);
				EntityItem drop = new EntityItem(world, x, y, z, frienderPearls);
				event.getDrops().add(drop);
			}
			else if ((event.getEntity() instanceof EntityWither)) {
				double randNumber = Math.random();
				double d = randNumber * 100.0D;
				int n = (int) d;
				if (n == 50) {
					ItemStack frienderPearls = new ItemStack(ModItems.FRIENDER_PEARL, 8);
					EntityItem drop = new EntityItem(world, x + 5.0D, y + 2.0D, z + 5.0D, frienderPearls);
					event.getDrops().add(drop);
				}
			}
			else if (event.getEntity() instanceof EntityEnderman || event.getEntity() instanceof EntityFrienderman) {
				if (!(event.getEntity() instanceof EntityFrienderman)) {
					ItemStack frienderPearls = new ItemStack(ModItems.FRIENDER_PEARL, MathUtils.getRandom(1, 3));
					double r = world.rand.nextDouble();
					if (r <= (0.05D * (event.getLootingLevel() * 5))) {
						event.getDrops().add(new EntityItem(world, x, y, z, frienderPearls));
					}
				}
				ItemStack skullDrop = EntityUtils.getSkullDrop(event.getEntityLiving());

				if (attacker != null && attacker instanceof EntityLivingBase && skullDrop != null) {
					double r = Math.random();
					ItemStack attackItem = attacker.getHeldItemMainhand();
					if (attackItem != null) {
						EntityItem skullEntity = new EntityItem(world, x, y, z, skullDrop);
						if (r <= (0.05D * (event.getLootingLevel() * 5))) {
							event.getDrops().add(skullEntity);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLootTablesLoaded(LootTableLoadEvent event) {
		if ((event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) || (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) || (event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) || (event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)) || (event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY)) || (event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE))) {
			LootPool mainPool = event.getTable().getPool("main");
			if (mainPool != null) {
				if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) || event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE) || event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
					mainPool.addEntry(new LootEntryItem(ModItems.FRIENDER_PEARL, 10, 0, new LootFunction[] {}, new LootCondition[0], ModGlobals.MODID + ":friender_pearl_loot"));
				}
			}
		}

	}

	// rainbow colors

	@SubscribeEvent
	public void tickEvent(TickEvent e) {
		/*
		if (e.type == Type.SERVER) {
			List<EntityFrienderman> friendermanList = Lists.newArrayList();
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			if (server != null) {
				for (WorldServer world : server.worlds) {
					for (Entity entity : world.getLoadedEntityList()) {
						if (entity instanceof EntityFrienderman) {
							friendermanList.add((EntityFrienderman) entity);
						}
					}
				}
				for (EntityFrienderman frienderman : friendermanList) {
					if (frienderman.isTamed() && frienderman.getOwner() != null && !frienderman.isSitting()) {
						if (frienderman.dimension != frienderman.getOwner().dimension) {
							TeleportUtils.teleportEntity(frienderman, frienderman.getOwner().dimension, frienderman.getOwner().posX + 3, frienderman.getOwner().posY + 1, frienderman.getOwner().posZ + 3);
						}
					}
				}
			}
		}
		*/

		ModGlobals.TIME_LONG++;
		if (ModGlobals.TIME % 0.5 == 0) {
			ModGlobals.TIME2++;
		}
		if (ModGlobals.TIME2 > 360) {
			ModGlobals.TIME2 = 0;
		}
		if (ModGlobals.TURN == 0) {
			ModGlobals.GREEN++;
			if (ModGlobals.GREEN == 255) {
				ModGlobals.TURN = 1;
			}
		}
		if (ModGlobals.TURN == 1) {
			ModGlobals.RED--;
			if (ModGlobals.RED == 0) {
				ModGlobals.TURN = 2;
			}
		}
		if (ModGlobals.TURN == 2) {
			ModGlobals.BLUE++;
			if (ModGlobals.BLUE == 255) {
				ModGlobals.TURN = 3;
			}
		}
		if (ModGlobals.TURN == 3) {
			ModGlobals.GREEN--;
			if (ModGlobals.GREEN == 0) {
				ModGlobals.TURN = 4;
			}
		}
		if (ModGlobals.TURN == 4) {
			ModGlobals.RED++;
			if (ModGlobals.RED == 255) {
				ModGlobals.TURN = 5;
			}
		}
		if (ModGlobals.TURN == 5) {
			ModGlobals.BLUE--;
			if (ModGlobals.BLUE == 0) {
				ModGlobals.TURN = 0;
			}
		}
	}

}
