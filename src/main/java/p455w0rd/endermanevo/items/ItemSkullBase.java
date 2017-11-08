package p455w0rd.endermanevo.items;

import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;
import p455w0rd.endermanevo.blocks.BlockSkullBase;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.render.ItemSkullRenderer;
import p455w0rd.endermanevo.init.ModBlocks;
import p455w0rd.endermanevo.init.ModMaterials;
import p455w0rdslib.util.MathUtils;

/**
 * @author p455w0rd
 *
 */
public class ItemSkullBase extends ItemArmor implements IModelHolder {

	private BlockSkullBase skullBlock;
	private String NAME = "";

	public ItemSkullBase(String name, BlockSkullBase block) {
		super(ModMaterials.SKULL_MATERIAL, 0, EntityEquipmentSlot.HEAD);
		//BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, ItemArmor.DISPENSER_BEHAVIOR);
		NAME = name;
		setRegistryName(NAME);
		setUnlocalizedName(NAME);
		setMaxStackSize(64);
		setMaxDamage(0);
		skullBlock = block;
		ForgeRegistries.ITEMS.register(this);
	}

	public String getName() {
		return NAME;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing == EnumFacing.DOWN) {
			return EnumActionResult.FAIL;
		}
		else {
			if (worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) {
				facing = EnumFacing.UP;
				pos = pos.down();
			}
			IBlockState iblockstate = worldIn.getBlockState(pos);
			Block block = iblockstate.getBlock();
			boolean flag = block.isReplaceable(worldIn, pos);

			if (!flag) {
				if (!worldIn.getBlockState(pos).getMaterial().isSolid() && !worldIn.isSideSolid(pos, facing, true)) {
					return EnumActionResult.FAIL;
				}

				pos = pos.offset(facing);
			}
			ItemStack stack = player.getHeldItem(hand);
			if (player.canPlayerEdit(pos, facing, stack) && Blocks.SKULL.canPlaceBlockAt(worldIn, pos)) {
				if (worldIn.isRemote) {
					return EnumActionResult.SUCCESS;
				}
				else {
					worldIn.setBlockState(pos, skullBlock.getDefaultState().withProperty(BlockSkull.FACING, facing), 11);
					int i = 0;

					if (facing == EnumFacing.UP) {
						i = MathUtils.floor(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
					}

					TileEntity tileentity = worldIn.getTileEntity(pos);

					if (tileentity instanceof TileBlockSkull) {
						TileBlockSkull tileentityskull = (TileBlockSkull) tileentity;
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
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack stack, EntityEquipmentSlot armorSlot, ModelBiped original) {
		return TileBlockSkull.getModel(NAME);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String layer) {
		return TileBlockSkull.getModel(NAME).getTexture().getResourceDomain() + ":" + TileBlockSkull.getModel(NAME).getTexture().getResourcePath();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		//ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		ModelRegistryHelper.registerItemRenderer(this, ItemSkullRenderer.getInstance());
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

	public static class Enderman2 extends ItemSkullBase {

		public Enderman2() {
			super("enderman2_skull", ModBlocks.ENDERMAN2_SKULL);
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