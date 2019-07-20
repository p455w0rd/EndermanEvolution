package p455w0rd.endermanevo.init;

import java.util.*;

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
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.blocks.BlockEnderFlower;
import p455w0rd.endermanevo.client.model.layers.LayerEntityCharge;
import p455w0rd.endermanevo.client.model.layers.LayerSkullEyes;
import p455w0rd.endermanevo.client.render.ParticleRenderer;
import p455w0rd.endermanevo.entity.EntityEvolvedEnderman;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.init.ModConfig.ConfigOptions;
import p455w0rd.endermanevo.init.ModIntegration.Mods;
import p455w0rd.endermanevo.integration.TiC;
import p455w0rd.endermanevo.items.ItemSkullBase;
import p455w0rd.endermanevo.util.*;
import p455w0rdslib.util.EasyMappings;
import p455w0rdslib.util.MathUtils;

@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModEvents {

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		for (final Item item : ModItems.getList()) {
			event.getRegistry().register(item);
		}
	}

	@SubscribeEvent
	public static void registerBlock(final RegistryEvent.Register<Block> event) {
		for (final Block block : ModBlocks.getList()) {
			event.getRegistry().register(block);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerModels(final ModelRegistryEvent event) {
		ModBlocks.preInitModels();
		ModItems.registerTEISRs(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onModelBake(final ModelBakeEvent event) {
		ModItems.initModels(event);
	}

	@SubscribeEvent
	public static void onItemUse(final RightClickBlock event) {
		final ItemStack stack = event.getItemStack();
		if (stack.getItem() == Items.DYE && EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata())) {
			BlockEnderFlower.tryBonemeal(stack, event.getWorld(), event.getPos(), event.getEntityPlayer(), event.getHand());
		}
	}

	@SubscribeEvent
	public static void onSpawnPackSize(final LivingPackSizeEvent event) {
		if (event.getEntityLiving() instanceof EntityEvolvedEnderman) {
			event.setMaxPackSize(ConfigOptions.endermanMaxSpawn);
		}
		else if (event.getEntityLiving() instanceof EntityFrienderman) {
			event.setMaxPackSize(ConfigOptions.friendermanMaxSpawn);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderLivingBase(final RenderLivingEvent.Pre<EntityLivingBase> event) {
		final RenderLivingBase<EntityLivingBase> renderer = event.getRenderer();
		final List<LayerRenderer<EntityLivingBase>> layers = renderer.layerRenderers;
		boolean isEyesLayerAdded = false;
		boolean isChargeLayerAdded = false;
		for (final LayerRenderer<EntityLivingBase> layer : layers) {
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

		if (EntityUtils.isWearingCustomSkull(event.getEntity())) {
			if (renderer.getMainModel() instanceof ModelBiped) {
				final ModelBiped bipedModel = (ModelBiped) renderer.getMainModel();
				if (!bipedModel.bipedHead.isHidden || !bipedModel.bipedHeadwear.isHidden) {
					bipedModel.bipedHead.isHidden = true;
					bipedModel.bipedHeadwear.isHidden = true;
				}
			}
			if (event.getEntity() instanceof EntityPlayer) {
				final ItemSkullBase skull = EntityUtils.getSkullItem(event.getEntity());
				if (!isChargeLayerAdded) {
					if (skull == ModItems.SKULL_EVOLVED_ENDERMAN) {
						renderer.addLayer(new LayerEntityCharge<>(renderer, renderer.getMainModel()));
					}
				}
				else if (skull != ModItems.SKULL_EVOLVED_ENDERMAN) {
					removeEntityChargeLayer(event.getEntity(), layers);
				}
			}
		}
		else {
			if (renderer.getMainModel() instanceof ModelBiped) {
				final ModelBiped bipedModel = (ModelBiped) renderer.getMainModel();
				if (bipedModel.bipedHead.isHidden || bipedModel.bipedHeadwear.isHidden) {
					bipedModel.bipedHead.isHidden = false;
					bipedModel.bipedHeadwear.isHidden = false;
				}
			}
			if (isChargeLayerAdded && event.getEntity() instanceof EntityPlayer) {
				removeEntityChargeLayer(event.getEntity(), layers);
				/*
				final Iterator<LayerRenderer<EntityLivingBase>> iterator = layers.iterator();
				LayerEntityCharge<EntityLivingBase> layerToRemove = null;
				while (iterator.hasNext()) {
					final LayerRenderer<EntityLivingBase> currentLayer = iterator.next();
					if (currentLayer instanceof LayerEntityCharge) {
						layerToRemove = (LayerEntityCharge<EntityLivingBase>) currentLayer;
						break;
					}
				}
				if (layerToRemove != null) {
					layers.remove(layerToRemove);
				}
				*/
			}
		}
	}

	private static void removeEntityChargeLayer(final EntityLivingBase entity, final List<LayerRenderer<EntityLivingBase>> layers) {
		final Iterator<LayerRenderer<EntityLivingBase>> iterator = layers.iterator();
		LayerEntityCharge<EntityLivingBase> layerToRemove = null;
		while (iterator.hasNext()) {
			final LayerRenderer<EntityLivingBase> currentLayer = iterator.next();
			if (currentLayer instanceof LayerEntityCharge) {
				layerToRemove = (LayerEntityCharge<EntityLivingBase>) currentLayer;
				break;
			}
		}
		if (layerToRemove != null) {
			layers.remove(layerToRemove);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderAfterWorld(final RenderWorldLastEvent event) {
		ParticleRenderer.getInstance().renderParticles(EasyMappings.player(), event.getPartialTicks());
	}

	@SubscribeEvent
	public static void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving() instanceof EntityEnderman) {
			final EntityEnderman enderman = (EntityEnderman) event.getEntityLiving();
			if (enderman.getAttackTarget() == null) {
				final DataParameter<Boolean> screaming = EntityEnderman.SCREAMING;
				final EntityDataManager dm = enderman.getDataManager();
				dm.set(screaming, Boolean.valueOf(false));
				dm.setDirty(screaming);
			}
		}
	}

	@SubscribeEvent
	public static void onEntitySpawn(final LivingSpawnEvent.CheckSpawn event) {
		if (event.getWorld().isRemote || !(event.getEntityLiving() instanceof EntityEvolvedEnderman)) {
			return;
		}
		final World world = event.getWorld();
		final int radius = 32;
		final List<EntityEvolvedEnderman> endermanList = world.getEntitiesWithinAABB(EntityEvolvedEnderman.class, new AxisAlignedBB(event.getX() - radius, 0, event.getZ() - radius, event.getX() + radius, world.getHeight(), event.getZ() + radius));
		if (endermanList.size() >= ConfigOptions.endermanMaxSpawn) {
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public static void onSetEntitySpawnGroupSize(final LivingPackSizeEvent event) {
		if (event.getEntityLiving() instanceof EntityEvolvedEnderman) {
			event.setMaxPackSize(ConfigOptions.endermanMaxSpawn);
		}
		else if (event.getEntityLiving() instanceof EntityFrienderman) {
			event.setMaxPackSize(ConfigOptions.friendermanMaxSpawn);
		}
	}

	@SubscribeEvent
	public static void onTargetSelect(final LivingSetAttackTargetEvent event) {
		if (event.getEntityLiving() instanceof EntityEnderman && event.getTarget() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) event.getTarget();
			final ItemStack stack = player.inventory.armorInventory.get(3);
			boolean stopAttack = false;
			if (!stack.isEmpty() && stack.getItem() instanceof ItemSkullBase) {
				final ItemSkullBase skull = (ItemSkullBase) stack.getItem();
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
	public static void onMobDrop(final LivingDropsEvent event) {
		if (event.getSource().getTrueSource() instanceof EntityLivingBase) { //only respect death by living entity
			final World world = event.getEntity().getEntityWorld();
			final double x = event.getEntity().posX;
			final double y = event.getEntity().posY;
			final double z = event.getEntity().posZ;
			final EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
			if (event.getEntity() instanceof EntityDragon) {
				final ItemStack frienderPearls = new ItemStack(ModItems.FRIENDER_PEARL, 16);
				final EntityItem drop = new EntityItem(world, x, y, z, frienderPearls);
				event.getDrops().add(drop);
			}
			else if (event.getEntity() instanceof EntityWither) {
				final double randNumber = Math.random();
				final double d = randNumber * 100.0D;
				final int n = (int) d;
				if (n == 50) {
					final ItemStack frienderPearls = new ItemStack(ModItems.FRIENDER_PEARL, 8);
					final EntityItem drop = new EntityItem(world, x + 5.0D, y + 2.0D, z + 5.0D, frienderPearls);
					event.getDrops().add(drop);
				}
			}
			else if (event.getEntity() instanceof EntityEnderman || event.getEntity() instanceof EntityFrienderman) {
				if (!(event.getEntity() instanceof EntityFrienderman)) {
					final ItemStack frienderPearls = new ItemStack(ModItems.FRIENDER_PEARL, MathUtils.getRandom(1, 3));
					final double r = world.rand.nextDouble();
					if (r <= 0.05D * (event.getLootingLevel() * 5)) {
						event.getDrops().add(new EntityItem(world, x, y, z, frienderPearls));
					}
				}
				final ItemStack skullDrop = EntityUtils.getSkullDrop(event.getEntityLiving());

				if (attacker != null && attacker instanceof EntityLivingBase && skullDrop != null) {
					final ItemStack attackItem = attacker.getHeldItemMainhand();
					if (attackItem != null) {
						if (Mods.TINKERS.isLoaded() && TiC.isTinkersItem(attackItem) && TiC.hasBeheading(attackItem)) {
							final int beheadingLevel = TiC.getBeheadingLevel(attackItem);
							if (beheadingLevel > event.getSource().getTrueSource().getEntityWorld().rand.nextInt(10)) {
								final EntityItem skullEntity = new EntityItem(world, x, y, z, skullDrop);
								skullEntity.setDefaultPickupDelay();
								event.getDrops().add(skullEntity);
							}
						}
						else {
							final EntityItem skullEntity = new EntityItem(world, x, y, z, skullDrop);
							if (event.getLootingLevel() > event.getSource().getTrueSource().getEntityWorld().rand.nextInt(3)) {
								event.getDrops().add(skullEntity);
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLootTablesLoaded(final LootTableLoadEvent event) {
		if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) || event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON) || event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID) || event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE) || event.getName().equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY) || event.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE)) {
			final LootPool mainPool = event.getTable().getPool("main");
			if (mainPool != null) {
				if (event.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) || event.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE) || event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
					mainPool.addEntry(new LootEntryItem(ModItems.FRIENDER_PEARL, 10, 0, new LootFunction[] {}, new LootCondition[0], ModGlobals.MODID + ":friender_pearl_loot"));
				}
			}
		}

	}

	// rainbow colors

	@SubscribeEvent
	public static void tickEvent(final TickEvent event) {
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
	public static void onPlayerTick(final PlayerTickEvent event) {
		final EntityPlayer player = event.player;
		if (player.world.isRemote) {
			if (player == Minecraft.getMinecraft().player && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !ConfigOptions.showSkullParticles) {
				return;
			}
			if (EntityUtils.isWearingCustomSkull(player)) {
				final Random rand = player.world.rand;
				final double x = player.posX + (rand.nextDouble() - 0.5D) * player.width;
				final double y = player.posY + rand.nextDouble() * player.height - 0.25D;
				final double z = player.posZ + (rand.nextDouble() - 0.5D) * player.width;
				final double sx = (rand.nextDouble() - 0.5D) * 2.0D;
				final double sy = -rand.nextDouble();
				final double sz = (rand.nextDouble() - 0.5D) * 2.0D;
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
