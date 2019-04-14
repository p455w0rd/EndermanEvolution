package p455w0rd.endermanevo.init;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;
import p455w0rd.endermanevo.blocks.BlockEnderFlower;
import p455w0rd.endermanevo.blocks.BlockSkullBase;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;

public class ModBlocks {

	private static final List<Block> BLOCK_LIST = new ArrayList<>();

	public static final BlockSkullBase.Enderman ENDERMAN_SKULL = new BlockSkullBase.Enderman();
	public static final BlockSkullBase.Frienderman FRIENDERMAN_SKULL = new BlockSkullBase.Frienderman();
	public static final BlockSkullBase.EvolvedEnderman ENDERMAN2_SKULL = new BlockSkullBase.EvolvedEnderman();
	public static final BlockEnderFlower ENDER_FLOWER = new BlockEnderFlower();

	public static void init() {
		GameRegistry.registerTileEntity(TileBlockSkull.class, new ResourceLocation(ModGlobals.MODID, ":tile_pskull"));
	}

	@SideOnly(Side.CLIENT)
	public static void preInitModels() {
		for (final Block block : BLOCK_LIST) {
			if (block instanceof IModelHolder) {
				((IModelHolder) block).initModel((IModelHolder) block);
			}
		}
	}

	public static List<Block> getList() {
		if (BLOCK_LIST.isEmpty()) {
			BLOCK_LIST.addAll(Arrays.asList(ENDERMAN_SKULL, FRIENDERMAN_SKULL, ENDERMAN2_SKULL, ENDER_FLOWER));
		}
		return BLOCK_LIST;
	}
}
