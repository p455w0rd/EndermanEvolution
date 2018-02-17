package p455w0rd.endermanevo.worldgen;

import static net.minecraftforge.common.BiomeDictionary.Type.DEAD;
import static net.minecraftforge.common.BiomeDictionary.Type.END;
import static net.minecraftforge.common.BiomeDictionary.Type.FOREST;
import static net.minecraftforge.common.BiomeDictionary.Type.HILLS;
import static net.minecraftforge.common.BiomeDictionary.Type.MAGICAL;
import static net.minecraftforge.common.BiomeDictionary.Type.MOUNTAIN;
import static net.minecraftforge.common.BiomeDictionary.Type.NETHER;
import static net.minecraftforge.common.BiomeDictionary.Type.PLAINS;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.IWorldGenerator;
import p455w0rd.endermanevo.blocks.BlockEnderFlower;
import p455w0rd.endermanevo.init.ModBlocks;

/**
 * @author p455w0rd
 *
 */
public class EnderFlowerGenerator implements IWorldGenerator {

	private static final Type[] VALID_BIOMES = new Type[] {
			PLAINS,
			END,
			NETHER,
			MAGICAL,
			FOREST,
			MOUNTAIN,
			HILLS
	};

	@Override
	public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		final int x = chunkX * 16 + 8;
		final int z = chunkZ * 16 + 8;
		final Biome biome = world.getBiomeForCoordsBody(new BlockPos(x, 0, z));
		if (BiomeDictionary.hasType(biome, DEAD)) {
			return;
		}

		for (Type type : VALID_BIOMES) {
			if (BiomeDictionary.hasType(biome, type)) {
				genFlower(world, rand, x, z);
			}
		}
	}

	private void genFlower(World world, Random rand, int x, int z) {
		if (rand.nextFloat() < 0.5f) {
			final int posX = x + world.rand.nextInt(16);
			final int posZ = z + world.rand.nextInt(16);
			final BlockPos newPos = getGroundPos(world, posX, posZ);
			final BlockEnderFlower flower = ModBlocks.ENDER_FLOWER;
			if (newPos != null && flower.canPlaceBlockAt(world, newPos)) {
				world.setBlockState(newPos, flower.getDefaultState().withProperty(BlockCrops.AGE, Integer.valueOf(7)), 2);
				if (world.rand.nextFloat() < 0.2f) {
					for (EnumFacing facing : EnumFacing.HORIZONTALS) {
						if (world.getBlockState(newPos.offset(facing)).getBlock() == Blocks.AIR) {
							world.setBlockState(newPos.offset(facing), flower.getDefaultState().withProperty(BlockCrops.AGE, Integer.valueOf(7)), 2);
							break;
						}
					}
				}
			}
		}
	}

	@Nullable
	public static BlockPos getGroundPos(World world, int x, int z) {
		final BlockPos topPos = world.getHeight(new BlockPos(x, 0, z));
		if (topPos.getY() == 0 && world.provider.getDimension() != 1) {
			return null;
		}
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(topPos);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (world.provider.getDimension() == -1) {
			for (int i = 0; i < topPos.getY(); i++) {
				pos.move(EnumFacing.UP);
				IBlockState tmpState = world.getBlockState(pos);
				if (BlockEnderFlower.VALID_SOILS.contains(tmpState.getBlock()) && world.getBlockState(pos.up()).getBlock() == Blocks.AIR) {
					break;
				}
			}
		}
		else {
			while (block.isLeaves(state, world, pos) || block.isWood(world, pos) || (state.getBlock().isReplaceable(world, pos) && !state.getMaterial().isLiquid())) {
				pos.move(EnumFacing.DOWN);
				if (world.provider.getDimension() == -1 && pos.getY() > 115) {
					continue;
				}
				if (pos.getY() <= 0) {
					return null;
				}

				state = world.getBlockState(pos);
			}
		}

		return pos.up();
	}

}
