package p455w0rd.endermanevo.init;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.api.IModelHolder;
import p455w0rd.endermanevo.blocks.BlockSkullBase;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;

public class ModBlocks {

	private static final List<Block> BLOCK_LIST = new ArrayList<Block>();

	public static final BlockSkullBase.Enderman ENDERMAN_SKULL = new BlockSkullBase.Enderman();
	public static final BlockSkullBase.Frienderman FRIENDERMAN_SKULL = new BlockSkullBase.Frienderman();
	public static final BlockSkullBase.Enderman2 ENDERMAN2_SKULL = new BlockSkullBase.Enderman2();

	public static void init() {
		GameRegistry.registerTileEntity(TileBlockSkull.class, ModGlobals.MODID + ":tile_pskull");
	}

	@SideOnly(Side.CLIENT)
	public static void preInitModels() {
		for (Block block : BLOCK_LIST) {
			if (block instanceof IModelHolder) {
				((IModelHolder) block).initModel();
			}
		}
	}

	public static List<Block> getList() {
		if (BLOCK_LIST.isEmpty()) {
			BLOCK_LIST.addAll(Arrays.asList(ENDERMAN_SKULL, FRIENDERMAN_SKULL, ENDERMAN2_SKULL));
		}
		return BLOCK_LIST;
	}
}
