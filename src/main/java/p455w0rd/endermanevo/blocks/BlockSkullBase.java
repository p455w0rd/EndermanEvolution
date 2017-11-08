package p455w0rd.endermanevo.blocks;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.minecraft.block.BlockSkull;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.init.ModItems;

/**
 * @author p455w0rd
 *
 */
public class BlockSkullBase extends BlockSkull {

	protected static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.5D, 0.75D);
	protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.5D, 0.75D, 0.75D, 1.0D);
	protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 0.5D);
	protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.5D, 0.25D, 0.25D, 1.0D, 0.75D, 0.75D);
	protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.25D, 0.5D, 0.75D, 0.75D);

	private String NAME;

	public BlockSkullBase(String name) {
		NAME = name;
		setUnlocalizedName(NAME);
		setRegistryName(NAME);
		setHardness(1.0F);
		setSoundType(SoundType.STONE);
		ForgeRegistries.BLOCKS.register(this);
		ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
	}

	@Override
	public String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	@Override
	public String getUnlocalizedName() {
		return "tile." + NAME + ".name";
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
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
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {

	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(NODROP, Boolean.valueOf(false));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileBlockSkull(NAME);
	}

	@Override
	public boolean canDispenserPlace(World world, BlockPos pos, ItemStack stack) {
		return false;
	}

	public static class Enderman extends BlockSkullBase {

		public Enderman() {
			super("enderman_skull");
		}

		@Override
		public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
			return new ItemStack(ModItems.SKULL_ENDERMAN);
		}

		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return null;
		}

		@Override
		public List<ItemStack> getDrops(IBlockAccess worldIn, BlockPos pos, IBlockState state, int fortune) {
			return Lists.newArrayList(new ItemStack(ModItems.SKULL_ENDERMAN));
		}

		@Override
		public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
			return new ItemStack(ModItems.SKULL_ENDERMAN);
		}

	}

	public static class Frienderman extends BlockSkullBase {

		public Frienderman() {
			super("frienderman_skull");
		}

		@Override
		public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
			return new ItemStack(ModItems.SKULL_FRIENDERMAN);
		}

		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return ModItems.SKULL_FRIENDERMAN;
		}

		@Override
		public List<ItemStack> getDrops(IBlockAccess worldIn, BlockPos pos, IBlockState state, int fortune) {
			return Lists.newArrayList(new ItemStack(ModItems.SKULL_FRIENDERMAN));
		}

		@Override
		public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
			return new ItemStack(ModItems.SKULL_FRIENDERMAN);
		}

	}

	public static class Enderman2 extends BlockSkullBase {

		public Enderman2() {
			super("enderman2_skull");
		}

		@Override
		public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
			return new ItemStack(ModItems.SKULL_ENDERMAN2);
		}

		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune) {
			return ModItems.SKULL_ENDERMAN2;
		}

		@Override
		public List<ItemStack> getDrops(IBlockAccess worldIn, BlockPos pos, IBlockState state, int fortune) {
			return Lists.newArrayList(new ItemStack(ModItems.SKULL_ENDERMAN2));
		}

		@Override
		public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
			return new ItemStack(ModItems.SKULL_ENDERMAN2);
		}

	}

}
