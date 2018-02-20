package p455w0rd.endermanevo.init;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.client.model.layers.LayerEntityCharge;
import p455w0rd.endermanevo.client.model.layers.LayerSkullEyes;
import p455w0rd.endermanevo.client.render.ParticleRenderer;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.init.ModConfig.ConfigOptions;
import p455w0rd.endermanevo.items.ItemSkullBase;
import p455w0rd.endermanevo.util.EntityUtils;
import p455w0rd.endermanevo.util.EnumParticles;
import p455w0rd.endermanevo.util.ParticleUtil;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.MCPrivateUtils;
import p455w0rdslib.util.MathUtils;
import p455w0rdslib.util.ReflectionUtils;

public class ModEvents {

	public static void init() {
		MinecraftForge.EVENT_BUS.register(new ModEvents());
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		for (Item item : ModItems.getList()) {
			event.getRegistry().register(item);
		}
	}

	@SubscribeEvent
	public void registerBlock(RegistryEvent.Register<Block> event) {
		for (Block block : ModBlocks.getList()) {
			event.getRegistry().register(block);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		ModBlocks.preInitModels();
		ModItems.preInitModels();
	}

	@SubscribeEvent
	public void onSpawnPackSize(LivingPackSizeEvent event) {
		if (event.getEntityLiving() instanceof EntityEvolvedEnderman) {
			event.setMaxPackSize(ConfigOptions.ENDERMAN_MAX_SPAWN);
		}
		else if (event.getEntityLiving() instanceof EntityFrienderman) {
			event.setMaxPackSize(ConfigOptions.FRIENDERMAN_MAX_SPAWN);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderLivingBase(RenderLivingEvent.Pre<EntityLivingBase> event) {
		RenderLivingBase<EntityLivingBase> renderer = event.getRenderer();
		List<LayerRenderer<EntityLivingBase>> layers = ReflectionHelper.getPrivateValue(RenderLivingBase.class, renderer, ReflectionUtils.determineSRG("layerRenderers"));
		boolean isEyesLayerAdded = false;
		boolean isChargeLayerAdded = false;
		for (LayerRenderer<EntityLivingBase> layer : layers) {
			if (layer instanceof LayerEntityCharge) {
				isChargeLayerAdded = true;
				continue;
			}
			if (layer instanceof LayerSkullEyes) {
				isEyesLayerAdded = true;
			}
		}
		if (!isEyesLayerAdded) {
			renderer.addLayer(new LayerSkullEyes(renderer));
		}
		if (!isChargeLayerAdded && event.getEntity() instanceof EntityEvolvedEnderman) {
			renderer.addLayer(new LayerEntityCharge(renderer, renderer.getMainModel()));
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
		/* later
				if (event.getEntity() instanceof EntityPlayer) {
					if (!PLAYERS_WITH_FRIENDERMAN.contains(((EntityPlayer) event.getEntity()).getUniqueID())) {
						for (RenderLivingBase<? extends EntityLivingBase> renderPlayer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
							if (renderPlayer instanceof RenderPlayer) {
								renderPlayer.addLayer(new LayerMiniFrienderman());
								PLAYERS_WITH_FRIENDERMAN.add(((EntityPlayer) event.getEntity()).getUniqueID());
								break;
							}
						}
					}
				}
				*/
	}

	//private static final ArrayList<UUID> PLAYERS_WITH_FRIENDERMAN = Lists.<UUID>newArrayList();

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderAfterWorld(RenderWorldLastEvent event) {
		ParticleRenderer.getInstance().renderParticles(EasyMappings.player(), event.getPartialTicks());
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof EntityEnderman) {
			EntityEnderman enderman = (EntityEnderman) event.getEntityLiving();
			if (enderman.getAttackTarget() == null) {
				MCPrivateUtils.setEndermanScreaming(enderman, false);
			}
		}
	}

	@SubscribeEvent
	public void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
		if (event.getWorld().isRemote || !(event.getEntityLiving() instanceof EntityEvolvedEnderman)) {
			return;
		}
		World world = event.getWorld();
		int radius = 32;
		List<EntityEvolvedEnderman> endermanList = world.getEntitiesWithinAABB(EntityEvolvedEnderman.class, new AxisAlignedBB(event.getX() - radius, 0, event.getZ() - radius, event.getX() + radius, world.getHeight(), event.getZ() + radius));
		if (endermanList.size() >= ConfigOptions.ENDERMAN_MAX_SPAWN) {
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onSetEntitySpawnGroupSize(LivingPackSizeEvent event) {
		if (event.getEntityLiving() instanceof EntityEvolvedEnderman) {
			event.setMaxPackSize(ConfigOptions.ENDERMAN_MAX_SPAWN);
		}
		else if (event.getEntityLiving() instanceof EntityFrienderman) {
			event.setMaxPackSize(ConfigOptions.FRIENDERMAN_MAX_SPAWN);
		}
	}

	@SubscribeEvent
	public void onTargetSelect(LivingSetAttackTargetEvent event) {
		if (event.getEntityLiving() instanceof EntityEnderman && event.getTarget() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getTarget();
			ItemStack stack = player.inventory.armorInventory.get(3);
			boolean stopAttack = false;
			if (!stack.isEmpty() && stack.getItem() instanceof ItemSkullBase) {
				ItemSkullBase skull = (ItemSkullBase) stack.getItem();
				if (skull.isEndermanSkull()) {
					stopAttack = true;
				}
			}
			if (stopAttack) {
				((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
			}
		}
	}

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
	public void tickEvent(TickEvent event) {
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

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onPlayerTick(PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if (event.player.world.isRemote) {
			if (player == Minecraft.getMinecraft().player && ConfigOptions.SHOW_SKULL_PARTICLES) {
				if (EntityUtils.isWearingCustomSkull(player)) {
					Random rand = player.world.rand;
					double x = player.posX + (rand.nextDouble() - 0.5D) * player.width;
					double y = player.posY + rand.nextDouble() * player.height - 0.25D;
					double z = player.posZ + (rand.nextDouble() - 0.5D) * player.width;
					double sx = (rand.nextDouble() - 0.5D) * 2.0D;
					double sy = -rand.nextDouble();
					double sz = (rand.nextDouble() - 0.5D) * 2.0D;
					if (EntityUtils.getSkullItem(player) == ModItems.SKULL_FRIENDERMAN) {
						ParticleUtil.spawn(EnumParticles.LOVE, player.getEntityWorld(), x, y, z, sx, sy, sz);
					}
					else if (EntityUtils.getSkullItem(player) == ModItems.SKULL_EVOLVED_ENDERMAN) {
						ParticleUtil.spawn(EnumParticles.PORTAL_GREEN, player.getEntityWorld(), x, y, z, sx, sy, sz);
					}
					else if (EntityUtils.getSkullItem(player) == ModItems.SKULL_ENDERMAN) {
						ParticleUtil.spawn(EnumParticles.PORTAL, player.getEntityWorld(), x, y, z, sx, sy, sz);
					}
				}
			}
		}
	}

}
