package p455w0rd.endermanevo.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.*;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.init.*;
import p455w0rd.endermanevo.util.EnumParticles;
import p455w0rd.endermanevo.util.ParticleUtil;

/**
 * @author p455w0rd
 *
 */
public class BlockEnderFlower extends Block implements IGrowable, IPlantable {

	private static final PropertyInteger STAGE = PropertyInteger.create("age", 0, 7);
	private static final String NAME = "ender_flower";
	public static final AxisAlignedBB[] FLOWER_AABB = new AxisAlignedBB[] {
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.063D, 0.67D), new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.126D, 0.67D), new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.189D, 0.67D), new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.252D, 0.67D), new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.315D, 0.67D), new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.378D, 0.67D), new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.441D, 0.67D), new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.75D, 0.67D)
	};
	public static final List<Block> VALID_SOILS = Lists.newArrayList(Blocks.NETHERRACK, Blocks.DIRT, Blocks.GRASS, Blocks.FARMLAND);
	public static final List<Block> VALID_BONEMEAL_SOILS = Lists.newArrayList(Blocks.END_STONE, Blocks.END_BRICKS, Blocks.END_PORTAL_FRAME);
	public static final List<ItemStack> VALID_SOILS_STACKS = Lists.newArrayList(new ItemStack(Blocks.NETHERRACK), new ItemStack(Blocks.DIRT), new ItemStack(Blocks.GRASS), new ItemStack(Blocks.FARMLAND), new ItemStack(Blocks.END_STONE), new ItemStack(Blocks.END_BRICKS), new ItemStack(Blocks.END_PORTAL_FRAME));
	private static IBlockState[] stateList = null;

	public BlockEnderFlower() {
		super(Material.LEAVES, Material.LEAVES.getMaterialMapColor());
		setUnlocalizedName(NAME);
		setRegistryName(NAME);
		setDefaultState(blockState.getBaseState().withProperty(getAgeProperty(), Integer.valueOf(0)));
		setTickRandomly(true);
		setCreativeTab(ModCreativeTab.TAB);
		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		disableStats();
	}

	public static IBlockState[] getGrowthStates() {
		if (stateList == null) {
			stateList = new IBlockState[8];
			for (int i = 0; i < 8; i++) {
				stateList[i] = ModBlocks.ENDER_FLOWER.getDefaultState().withProperty(STAGE, Integer.valueOf(i));
			}
		}
		return stateList;
	}

	@Override
	public boolean onBlockActivated(final World world, final BlockPos pos, final IBlockState state, final EntityPlayer player, final EnumHand hand, final EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
		if (!world.isRemote) {
			if (getAge(state) == getMaxAge() && hand == EnumHand.MAIN_HAND) {
				dropBlockAsItem(world, pos, state, -1);
				if (!player.capabilities.isCreativeMode) {
					world.setBlockState(pos, getDefaultState().withProperty(STAGE, Integer.valueOf(0)), 3);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, float chance, final int fortune) {
		if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) {
			final NonNullList<ItemStack> drops = NonNullList.create();
			getDrops(drops, worldIn, pos, state, fortune);
			chance = ForgeEventFactory.fireBlockHarvesting(Lists.newArrayList(), worldIn, pos, state, fortune, chance, false, harvesters.get());
			for (final ItemStack drop : drops) {
				if (worldIn.rand.nextFloat() <= chance) {
					spawnAsEntity(worldIn, pos, drop);
				}
			}
		}
	}

	@Override
	public void getDrops(final NonNullList<ItemStack> drops, final IBlockAccess world, final BlockPos pos, final IBlockState state, int fortune) {
		final int age = getAge(state);
		final Random rand = world instanceof World ? ((World) world).rand : ModGlobals.RNG;
		drops.clear();
		if (fortune > -1) {
			drops.add(new ItemStack(getItemBlock(), 1));
		}
		if (fortune == -1) {
			fortune = 0;
		}
		if (age >= getMaxAge()) {
			drops.add(new ItemStack(ModItems.ENDER_FRAGMENT, 1 + rand.nextInt(fortune + 1)));
		}
	}

	@Override
	public boolean canSustainPlant(final IBlockState state, final IBlockAccess world, final BlockPos pos, final EnumFacing direction, final net.minecraftforge.common.IPlantable plantable) {
		return false;
	}

	private PropertyInteger getAgeProperty() {
		return STAGE;
	}

	private int getMaxAge() {
		return 7;
	}

	private int getAge(final IBlockState state) {
		return state.getValue(getAgeProperty()).intValue();
	}

	private IBlockState withAge(final int age) {
		return getDefaultState().withProperty(getAgeProperty(), Integer.valueOf(age));
	}

	private boolean isMaxAge(final IBlockState state) {
		return state.getValue(getAgeProperty()).intValue() >= getMaxAge();
	}

	public static boolean isValidSoil(final Block block) {
		return BlockEnderFlower.VALID_SOILS.contains(block) || BlockEnderFlower.VALID_BONEMEAL_SOILS.contains(block);
	}

	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
		return FLOWER_AABB[state.getValue(getAgeProperty()).intValue()];
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(final IBlockState blockState, final IBlockAccess worldIn, final BlockPos pos) {
		return NULL_AABB;
	}

	private boolean canSustainBush(final IBlockState state) {
		return isValidSoil(state.getBlock());
	}

	private int getBonemealAgeIncrease(final World world) {
		return world.rand.nextInt(8);//MathHelper.getInt(worldIn.rand, 2, 5);
	}

	@Override
	public void updateTick(final World world, final BlockPos pos, final IBlockState state, final Random rand) {
		checkAndDropBlock(world, pos, state);
		final float f = getGrowChance(this, world, pos);
		if (f == 1.0f) {
			final boolean bonusSoil = VALID_BONEMEAL_SOILS.contains(world.getBlockState(pos.down()).getBlock());
			if (bonusSoil || world.getLightFromNeighbors(pos.up()) <= 8) {
				final int i = getAge(state);
				if (i < getMaxAge()) {
					int newAge = bonusSoil ? i + 2 : i + 1;
					if (newAge > getMaxAge()) {
						newAge = getMaxAge();
					}
					world.setBlockState(pos, withAge(newAge), 2);
					ForgeHooks.onCropsGrowPost(world, pos, state, world.getBlockState(pos));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(final IBlockState state, final World world, final BlockPos pos, final Random rand) {
		if (getAge(state) >= getMaxAge()) {
			for (int i = 0; i < 2; ++i) {
				final double x = pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.34D;
				final double y = pos.getY() + 0.5D + rand.nextDouble() * 0.55D - 0.25D;
				final double z = pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.34D;
				final double sx = (rand.nextDouble() - 0.5D) * 2.0D;
				final double sy = -rand.nextDouble();
				final double sz = (rand.nextDouble() - 0.5D) * 2.0D;
				ParticleUtil.spawn(EnumParticles.PORTAL_GREEN, world, x, y, z, sx, sy, sz);
			}
		}
	}

	protected static float getGrowChance(final Block block, final World world, final BlockPos pos) {
		float chance = world.rand.nextFloat() + 0.1f;
		if (VALID_BONEMEAL_SOILS.contains(world.getBlockState(pos.down()).getBlock())) {
			chance += 0.5f;
		}

		if (chance > 1.0f) {
			chance = 1.0f;
		}
		return chance;
	}

	@Deprecated
	@Override
	public void neighborChanged(final IBlockState state, final World worldIn, final BlockPos pos, final Block blockIn, final BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		checkAndDropBlock(worldIn, pos, state);
	}

	private void checkAndDropBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
		if (!canBlockStay(worldIn, pos, state)) {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	private boolean canBlockStay(final World world, final BlockPos pos, final IBlockState state) {
		if (state.getBlock() == this) {
			final IBlockState soil = world.getBlockState(pos.down());
			boolean isNextToWater = false;
			for (final EnumFacing offset : EnumFacing.HORIZONTALS) {
				if (world.getBlockState(pos.offset(offset)).getMaterial() == Material.WATER) {
					isNextToWater = true;
					break;
				}
			}
			return !isNextToWater && isValidSoil(soil.getBlock());
		}
		return canSustainBush(world.getBlockState(pos.down()));
	}

	private Item getItemBlock() {
		return ModItems.ENDER_FLOWER;
	}

	@Override
	public ItemStack getItem(final World worldIn, final BlockPos pos, final IBlockState state) {
		return new ItemStack(getItemBlock());
	}

	@Override
	public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
		return getItemBlock();
	}

	@Override
	public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
		final IBlockState soil = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos) && isValidSoil(soil.getBlock());
	}

	@Override
	public boolean canGrow(final World world, final BlockPos pos, final IBlockState state, final boolean isClient) {
		return VALID_BONEMEAL_SOILS.contains(world.getBlockState(pos.down()).getBlock()) ? !isMaxAge(state) : false;
	}

	@Override
	public boolean canUseBonemeal(final World world, final Random rand, final BlockPos pos, final IBlockState state) {
		final Block block = world.getBlockState(pos.down()).getBlock();
		return VALID_BONEMEAL_SOILS.contains(block);
	}

	@Override
	public void grow(final World world, final Random rand, final BlockPos pos, final IBlockState state) {
		if (getAge(state) < getMaxAge()) {
			final int i = Math.min(getAge(state) + getBonemealAgeIncrease(world), 7);
			if (i <= getMaxAge()) {
				world.setBlockState(pos, withAge(i), 2);
			}
		}
	}

	public static boolean tryBonemeal(final ItemStack stack, final World world, final BlockPos target, final EntityPlayer player, @Nullable final EnumHand hand) {
		final IBlockState iblockstate = world.getBlockState(target);
		if (iblockstate.getBlock() instanceof BlockEnderFlower) {
			final BlockEnderFlower flower = (BlockEnderFlower) iblockstate.getBlock();
			if (flower.canGrow(world, target, iblockstate, world.isRemote)) {
				if (!world.isRemote) {
					if (flower.canUseBonemeal(world, world.rand, target, iblockstate)) {
						flower.grow(world, world.rand, target, iblockstate);
					}
					stack.shrink(1);
				}
				else {
					spawnBonemealParticles(world, target);
				}
				return true;
			}
		}
		return false;
	}

	public static void spawnBonemealParticles(final World world, final BlockPos pos) {
		final IBlockState iblockstate = world.getBlockState(pos);
		final Random rand = ModGlobals.RNG;

		if (iblockstate.getMaterial() != Material.AIR) {
			for (int i = 0; i < 5; ++i) {
				final double d0 = rand.nextGaussian() * 0.02D;
				final double d1 = rand.nextGaussian() * 0.02D;
				final double d2 = rand.nextGaussian() * 0.02D;
				world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat() * iblockstate.getBoundingBox(world, pos).maxY, pos.getZ() + rand.nextFloat(), d0, d1, d2);
			}
		}
		else {
			for (int i1 = 0; i1 < 5; ++i1) {
				final double d0 = rand.nextGaussian() * 0.02D;
				final double d1 = rand.nextGaussian() * 0.02D;
				final double d2 = rand.nextGaussian() * 0.02D;
				world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + rand.nextFloat(), pos.getY() + (double) rand.nextFloat() * 1.0f, pos.getZ() + rand.nextFloat(), d0, d1, d2, new int[0]);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(final IBlockAccess worldIn, final IBlockState state, final BlockPos pos, final EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube(final IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(final IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(final int meta) {
		return withAge(meta);
	}

	@Override
	public int getMetaFromState(final IBlockState state) {
		return getAge(state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				STAGE
		});
	}

	@Override
	public EnumPlantType getPlantType(final IBlockAccess world, final BlockPos pos) {
		return EnumPlantType.getPlantType("EnderCrops");
	}

	@Override
	public IBlockState getPlant(final IBlockAccess world, final BlockPos pos) {
		final IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) {
			return getDefaultState();
		}
		return state;
	}

}
