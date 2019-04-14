package p455w0rd.endermanevo.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;
import p455w0rd.endermanevo.blocks.BlockSkullBase;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.render.ItemLayerWrapper;
import p455w0rd.endermanevo.init.ModBlocks;
import p455w0rd.endermanevo.init.ModMaterials;
import p455w0rdslib.util.MathUtils;

/**
 * @author p455w0rd
 *
 */
public class ItemSkullBase extends ItemArmor implements IModelHolder {

	private final BlockSkullBase skullBlock;
	@SideOnly(Side.CLIENT)
	ItemLayerWrapper wrappedModel;

	public ItemSkullBase(final String name, final BlockSkullBase block) {
		super(ModMaterials.SKULL_MATERIAL, 0, EntityEquipmentSlot.HEAD);
		setRegistryName(name);
		setUnlocalizedName(name);
		setMaxStackSize(64);
		setMaxDamage(0);
		skullBlock = block;
	}

	@Override
	public EnumActionResult onItemUse(final EntityPlayer player, final World worldIn, BlockPos pos, final EnumHand hand, EnumFacing facing, final float hitX, final float hitY, final float hitZ) {
		if (facing == EnumFacing.DOWN) {
			return EnumActionResult.FAIL;
		}
		else {
			if (worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) {
				facing = EnumFacing.UP;
				pos = pos.down();
			}
			final IBlockState iblockstate = worldIn.getBlockState(pos);
			final Block block = iblockstate.getBlock();
			final boolean flag = block.isReplaceable(worldIn, pos);
			if (!flag) {
				if (!worldIn.getBlockState(pos).getMaterial().isSolid() && !worldIn.isSideSolid(pos, facing, true)) {
					return EnumActionResult.FAIL;
				}

				pos = pos.offset(facing);
			}
			final ItemStack stack = player.getHeldItem(hand);
			if (player.canPlayerEdit(pos, facing, stack) && Blocks.SKULL.canPlaceBlockAt(worldIn, pos)) {
				if (worldIn.isRemote || skullBlock == null) {
					return EnumActionResult.SUCCESS;
				}
				else {
					worldIn.setBlockState(pos, skullBlock.getDefaultState().withProperty(BlockSkull.FACING, facing), 11);
					int i = 0;

					if (facing == EnumFacing.UP) {
						i = MathUtils.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
					}
					final TileEntity tileentity = worldIn.getTileEntity(pos);
					if (tileentity instanceof TileBlockSkull) {
						final TileBlockSkull tileentityskull = (TileBlockSkull) tileentity;
						tileentityskull.setSkullRotation(i);
					}
					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
			}
			else {
				return EnumActionResult.FAIL;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(final EntityLivingBase entityLiving, final ItemStack stack, final EntityEquipmentSlot armorSlot, final ModelBiped original) {
		return TileBlockSkull.getModel(getRegistryName().getResourcePath());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(final ItemStack stack, final Entity entity, final EntityEquipmentSlot slot, final String layer) {
		return TileBlockSkull.getModel(getRegistryName().getResourcePath()).getTexture().getResourceDomain() + ":" + TileBlockSkull.getModel(getRegistryName().getResourcePath()).getTexture().getResourcePath();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel(final IModelHolder item) {
		ModelLoader.setCustomModelResourceLocation(this, 0, getModelResource(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getModelResource(final Item item) {
		return new ModelResourceLocation(getRegistryName(), "inventory");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemLayerWrapper getWrappedModel() {
		return wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setWrappedModel(final ItemLayerWrapper wrappedModel) {
		this.wrappedModel = wrappedModel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldUseInternalTEISR() {
		return true;
	}

	public boolean isEndermanSkull() {
		return false;
	}

	public static class Enderman extends ItemSkullBase {

		public Enderman() {
			super("enderman_skull", ModBlocks.ENDERMAN_SKULL);
		}

		@Override
		public boolean isEndermanSkull() {
			return true;
		}

	}

	public static class EvolvedEnderman extends ItemSkullBase {

		public EvolvedEnderman() {
			super("enderman_evolved_skull", ModBlocks.ENDERMAN2_SKULL);
		}

		@Override
		public boolean isEndermanSkull() {
			return true;
		}

	}

	public static class Frienderman extends ItemSkullBase {

		public Frienderman() {
			super("frienderman_skull", ModBlocks.FRIENDERMAN_SKULL);
		}

		@Override
		public boolean isEndermanSkull() {
			return true;
		}

	}

}