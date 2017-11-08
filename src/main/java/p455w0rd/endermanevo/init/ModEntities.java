package p455w0rd.endermanevo.init;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import p455w0rd.endermanevo.EndermanEvolution;
import p455w0rd.endermanevo.entity.EntityEnderman2;
import p455w0rd.endermanevo.entity.EntityEndermite2;
import p455w0rd.endermanevo.entity.EntityFrienderPearl;
import p455w0rd.endermanevo.entity.EntityFrienderman;
import p455w0rd.endermanevo.init.ModConfig.ConfigOptions;
import p455w0rdslib.util.BiomeUtils;

public class ModEntities {

	public static void init() {
		int endermanEggColor1 = new Color(254, 85, 176).getRGB();
		int endermanEggColor2 = new Color(97, 230, 150).getRGB();
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "enderman2"), EntityEnderman2.class, "Enderman2", 0, EndermanEvolution.INSTANCE, 80, 3, false, 0, endermanEggColor2);
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "frienderman"), EntityFrienderman.class, "Frienderman", 1, EndermanEvolution.INSTANCE, 80, 3, false, 0, endermanEggColor1);
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "friender_pearl"), EntityFrienderPearl.class, "FrienderPearl", 2, EndermanEvolution.INSTANCE, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "endermite2"), EntityEndermite2.class, "Endermite2", 4, EndermanEvolution.INSTANCE, 80, 3, false, new Color(1, 66, 16).getRGB(), endermanEggColor2);

		EntitySpawnPlacementRegistry.setPlacementType(EntityEnderman2.class, SpawnPlacementType.ON_GROUND);
		EntitySpawnPlacementRegistry.setPlacementType(EntityFrienderman.class, SpawnPlacementType.ON_GROUND);
		if (ConfigOptions.ENABLE_ENDERMAN) {
			EntityRegistry.addSpawn(EntityEnderman2.class, ConfigOptions.ENDERMAN_PROBABILITY, 1, ConfigOptions.ENDERMAN_MAX_SPAWN, EnumCreatureType.MONSTER, getBiomeList());
		}
		if (ConfigOptions.ENABLE_FRIENDERMAN) {
			EntityRegistry.addSpawn(EntityFrienderman.class, ConfigOptions.ENDERMAN_PROBABILITY, 1, ConfigOptions.ENDERMAN_MAX_SPAWN, EnumCreatureType.CREATURE, Biomes.SKY, Biomes.DESERT, Biomes.HELL);
		}
	}

	private static Biome[] getBiomeList() {
		List<Biome> biomes = new ArrayList<Biome>();
		List<Biome> biomeList = BiomeUtils.getBiomeList();
		for (Biome currentBiome : biomeList) {
			List<SpawnListEntry> spawnList = currentBiome.getSpawnableList(EnumCreatureType.MONSTER);
			for (SpawnListEntry spawnEntry : spawnList) {
				if (spawnEntry.entityClass == EntityEnderman.class) {
					biomes.add(currentBiome);
				}
			}
		}
		return biomes.toArray(new Biome[biomes.size()]);
	}

}
