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
import p455w0rd.endermanevo.entity.*;
import p455w0rd.endermanevo.init.ModConfig.ConfigOptions;
import p455w0rdslib.util.BiomeUtils;

public class ModEntities {

	public static void init() {
		int endermanEggColor1 = new Color(254, 85, 176).getRGB();
		int endermanEggColor2 = new Color(97, 230, 150).getRGB();
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "enderman_evolved"), EntityEvolvedEnderman.class, ModGlobals.MODID + ":evolved_enderman", 0, EndermanEvolution.INSTANCE, 80, 3, false, 0, endermanEggColor2);
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "frienderman"), EntityFrienderman.class, ModGlobals.MODID + ":frienderman", 1, EndermanEvolution.INSTANCE, 80, 3, false, 0, endermanEggColor1);
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "friender_pearl"), EntityFrienderPearl.class, ModGlobals.MODID + ":friender_pearl", 2, EndermanEvolution.INSTANCE, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ModGlobals.MODID, "evolved_endermite"), EntityEvolvedEndermite.class, ModGlobals.MODID + ":evolved_endermite", 4, EndermanEvolution.INSTANCE, 80, 3, false, new Color(1, 66, 16).getRGB(), endermanEggColor2);

		EntitySpawnPlacementRegistry.setPlacementType(EntityEvolvedEnderman.class, SpawnPlacementType.ON_GROUND);
		EntitySpawnPlacementRegistry.setPlacementType(EntityFrienderman.class, SpawnPlacementType.ON_GROUND);
		EntityRegistry.addSpawn(EntityEvolvedEnderman.class, ConfigOptions.endermanSpawnProbability, 1, ConfigOptions.endermanMaxSpawn, EnumCreatureType.MONSTER, getBiomeList());
		EntityRegistry.addSpawn(EntityFrienderman.class, ConfigOptions.friendermanSpawnProbability, 1, ConfigOptions.friendermanMaxSpawn, EnumCreatureType.CREATURE, Biomes.SKY, Biomes.DESERT, Biomes.HELL);
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
