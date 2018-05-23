package p455w0rd.endermanevo.blocks;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.init.ModCreativeTab;
import p455w0rd.endermanevo.init.ModGlobals;
import p455w0rd.endermanevo.init.ModItems;
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
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.063D, 0.67D),
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.126D, 0.67D),
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.189D, 0.67D),
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.252D, 0.67D),
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.315D, 0.67D),
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.378D, 0.67D),
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.441D, 0.67D),
			new AxisAlignedBB(0.33D, 0.0D, 0.33D, 0.67D, 0.75D, 0.67D)
	};
	public static final List<Block> VALID_SOILS = Lists.newArrayList(Blocks.NETHERRACK, Blocks.DIRT, Blocks.GRASS, Blocks.FARMLAND);
	public static final List<Block> VALID_BONEMEAL_SOILS = Lists.newArrayList(Blocks.END_STONE, Blocks.END_BRICKS, Blocks.END_PORTAL_FRAME);

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

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			if (getAge(state) == getMaxAge() && hand == EnumHand.MAIN_HAND) {
				int fortune = 0;
				ItemStack heldStack = player.getHeldItemMainhand();
				if (!heldStack.isEmpty()) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(heldStack);
					if (!enchantments.isEmpty()) {
						if (enchantments.containsKey(Enchantments.FORTUNE)) {
							fortune = enchantments.get(Enchantments.FORTUNE);
						}
					}
				}
				dropBlockAsItem(world, pos, state, fortune);
				if (!player.capabilities.isCreativeMode) {
					world.setBlockState(pos, getDefaultState().withProperty(STAGE, Integer.valueOf(0)), 3);
				}
				return true;
			}
		}
		return false;
	}

	private PropertyInteger getAgeProperty() {
		return STAGE;
	}

	private int getMaxAge() {
		return 7;
	}

	private int getAge(IBlockState state) {
		return state.getValue(getAgeProperty()).intValue();
	}

	private IBlockState withAge(int age) {
		return getDefaultState().withProperty(getAgeProperty(), Integer.valueOf(age));
	}

	private boolean isMaxAge(IBlockState state) {
		return state.getValue(getAgeProperty()).intValue() >= getMaxAge();
	}

	public static boolean isValidSoil(Block block) {
		return BlockEnderFlower.VALID_SOILS.contains(block) || BlockEnderFlower.VALID_BONEMEAL_SOILS.contains(block);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FLOWER_AABB[state.getValue(getAgeProperty()).intValue()];
	}

	private boolean canSustainBush(IBlockState state) {
		return isValidSoil(state.getBlock());
	}

	private int getBonemealAgeIncrease(World world) {
		return world.rand.nextInt(8);//MathHelper.getInt(worldIn.rand, 2, 5);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		checkAndDropBlock(world, pos, state);
		float f = getGrowChance(this, world, pos);
		if (f == 1.0f) {
			boolean bonusSoil = VALID_BONEMEAL_SOILS.contains(world.getBlockState(pos.down()).getBlock());
			if (bonusSoil || world.getLightFromNeighbors(pos.up()) <= 8) {
				int i = getAge(state);
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
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (getAge(state) >= getMaxAge()) {
			for (int i = 0; i < 2; ++i) {
				double x = (pos.getX() + 0.5D) + (rand.nextDouble() - 0.5D) * 0.34D;
				double y = (pos.getY() + 0.5D) + rand.nextDouble() * 0.55D - 0.25D;
				double z = (pos.getZ() + 0.5D) + (rand.nextDouble() - 0.5D) * 0.34D;
				double sx = (rand.nextDouble() - 0.5D) * 2.0D;
				double sy = -rand.nextDouble();
				double sz = (rand.nextDouble() - 0.5D) * 2.0D;
				ParticleUtil.spawn(EnumParticles.PORTAL_GREEN, world, x, y, z, sx, sy, sz);
			}
		}
	}

	protected static float getGrowChance(Block block, World world, BlockPos pos) {
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		checkAndDropBlock(worldIn, pos, state);
	}

	private void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!canBlockStay(worldIn, pos, state)) {
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	private boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		if (state.getBlock() == this) {
			IBlockState soil = world.getBlockState(pos.down());
			boolean isNextToWater = false;
			for (EnumFacing offset : EnumFacing.HORIZONTALS) {
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
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(getItemBlock());
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return getItemBlock();
	}

	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		int age = getAge(state);
		Random rand = world instanceof World ? ((World) world).rand : ModGlobals.RNG;
		drops.clear();
		int numFlowers = 1 + (rand.nextFloat() > 0.97 ? 1 : 0);
		drops.add(new ItemStack(getItemBlock(), numFlowers));
		if (age >= getMaxAge()) {
			drops.add(new ItemStack(ModItems.ENDER_FRAGMENT, 1 + rand.nextInt(fortune + 1)));
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos) && isValidSoil(soil.getBlock());
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
		return VALID_BONEMEAL_SOILS.contains(world.getBlockState(pos.down()).getBlock()) ? !isMaxAge(state) : false;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
		Block block = world.getBlockState(pos.down()).getBlock();
		return VALID_BONEMEAL_SOILS.contains(block);
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
		if (getAge(state) < getMaxAge()) {
			int i = Math.min(getAge(state) + getBonemealAgeIncrease(world), 7);
			if (i <= getMaxAge()) {
				world.setBlockState(pos, withAge(i), 2);
			}
		}
	}

	public static boolean tryBonemeal(ItemStack stack, World world, BlockPos target, EntityPlayer player, @Nullable EnumHand hand) {
		IBlockState iblockstate = world.getBlockState(target);
		//int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, world, target, iblockstate, stack, hand);
		//if (hook != 0) {
		//	return hook > 0;
		//}
		if (iblockstate.getBlock() instanceof BlockEnderFlower) {
			BlockEnderFlower flower = (BlockEnderFlower) iblockstate.getBlock();
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

	public static void spawnBonemealParticles(World world, BlockPos pos) {
		IBlockState iblockstate = world.getBlockState(pos);
		Random rand = ModGlobals.RNG;

		if (iblockstate.getMaterial() != Material.AIR) {
			for (int i = 0; i < 15; ++i) {
				double d0 = rand.nextGaussian() * 0.02D;
				double d1 = rand.nextGaussian() * 0.02D;
				double d2 = rand.nextGaussian() * 0.02D;
				world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat() * iblockstate.getBoundingBox(world, pos).maxY, pos.getZ() + rand.nextFloat(), d0, d1, d2);
			}
		}
		else {
			for (int i1 = 0; i1 < 15; ++i1) {
				double d0 = rand.nextGaussian() * 0.02D;
				double d1 = rand.nextGaussian() * 0.02D;
				double d2 = rand.nextGaussian() * 0.02D;
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
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return withAge(meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return getAge(state);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {
				STAGE
		});
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.getPlantType("EnderCrops");
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) {
			return getDefaultState();
		}
		return state;
	}

}
