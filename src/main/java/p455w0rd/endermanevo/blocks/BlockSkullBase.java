package p455w0rd.endermanevo.blocks;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.init.ModItems;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class BlockSkullBase extends BlockSkull {

	protected static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);
	protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.5D, 0.75D, 0.75D, 1.0D);
	protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.5D);
	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.5D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.25D, 0.5D, 0.75D, 0.75D);
	ItemBlock itemBlock = null;

	private final String NAME;

	public BlockSkullBase(final String name) {
		NAME = name;
		setUnlocalizedName(NAME);
		setRegistryName(NAME);
		setHardness(1.0F);
		setSoundType(SoundType.STONE);
	}

	@Override
	public String getLocalizedName() {
		return I18n.translateToLocal(getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName() {
		return "tile." + NAME + ".name";
	}

	@Override
	public AxisAlignedBB getBoundingBox(final IBlockState state, final IBlockAccess source, final BlockPos pos) {
		switch (state.getValue(FACING)) {
		case UP:
		default:
			return DEFAULT_AABB;
		case NORTH:
			return NORTH_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case WEST:
			return WEST_AABB;
		case EAST:
			return EAST_AABB;
		}
	}

	@Override
	public void onBlockHarvested(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer player) {

	}

	@Override
	public IBlockState getStateForPlacement(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(NODROP, Boolean.valueOf(false));
	}

	@Override
	public TileEntity createNewTileEntity(final World world, final int meta) {
		return new TileBlockSkull(NAME);
	}

	@Override
	public boolean canDispenserPlace(final World world, final BlockPos pos, final ItemStack stack) {
		return false;
	}

	public static class Enderman extends BlockSkullBase {

		public Enderman() {
			super("enderman_skull");
		}

		@Override
		public ItemStack getItem(final World worldIn, final BlockPos pos, final IBlockState state) {
			return new ItemStack(ModItems.SKULL_ENDERMAN);
		}

		@Override
		public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
			return null;
		}

		@Override
		public List<ItemStack> getDrops(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final int fortune) {
			return Lists.newArrayList(new ItemStack(ModItems.SKULL_ENDERMAN));
		}

		@Override
		public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
			return new ItemStack(ModItems.SKULL_ENDERMAN);
		}

	}

	public static class Frienderman extends BlockSkullBase {

		public Frienderman() {
			super("frienderman_skull");
		}

		@Override
		public ItemStack getItem(final World worldIn, final BlockPos pos, final IBlockState state) {
			return new ItemStack(ModItems.SKULL_FRIENDERMAN);
		}

		@Override
		public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
			return ModItems.SKULL_FRIENDERMAN;
		}

		@Override
		public List<ItemStack> getDrops(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final int fortune) {
			return Lists.newArrayList(new ItemStack(ModItems.SKULL_FRIENDERMAN));
		}

		@Override
		public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
			return new ItemStack(ModItems.SKULL_FRIENDERMAN);
		}

	}

	public static class EvolvedEnderman extends BlockSkullBase {

		public EvolvedEnderman() {
			super("enderman_evolved_skull");
		}

		@Override
		public ItemStack getItem(final World worldIn, final BlockPos pos, final IBlockState state) {
			return new ItemStack(ModItems.SKULL_EVOLVED_ENDERMAN);
		}

		@Override
		public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
			return ModItems.SKULL_EVOLVED_ENDERMAN;
		}

		@Override
		public List<ItemStack> getDrops(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final int fortune) {
			return Lists.newArrayList(new ItemStack(ModItems.SKULL_EVOLVED_ENDERMAN));
		}

		@Override
		public ItemStack getPickBlock(final IBlockState state, final RayTraceResult target, final World world, final BlockPos pos, final EntityPlayer player) {
			return new ItemStack(ModItems.SKULL_EVOLVED_ENDERMAN);
		}

	}

}
