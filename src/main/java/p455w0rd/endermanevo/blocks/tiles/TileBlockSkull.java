package p455w0rd.endermanevo.blocks.tiles;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.client.model.ModelSkullBase;

/**
 * @author p455w0rd
 *
 */
public class TileBlockSkull extends TileEntitySkull {

	private String entity;
	private final String ENTITY_TAG = "Entity";

	public TileBlockSkull() {
	}

	public TileBlockSkull(String name) {
		entity = name;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setString(ENTITY_TAG, entity);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		entity = nbt.getString(ENTITY_TAG);
	}

	@Override
	public boolean canRenderBreaking() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos().add(-1, -1, -1), getPos().add(2, 2, 2));
	}

	public String getEntity() {
		return entity;
	}

	public ModelSkullBase getModel() {
		return getModels().get(entity);
	}

	public static ModelSkullBase getModel(String name) {
		return getModels().get(name);
	}

	private static Map<String, ModelSkullBase> getModels() {
		Map<String, ModelSkullBase> modelMap = Maps.newHashMap();
		modelMap.put("enderman", ModelSkullBase.Enderman.getInstance());
		modelMap.put("frienderman", ModelSkullBase.Frienderman.getInstance());
		modelMap.put("enderman2", ModelSkullBase.Enderman2.getInstance());
		return modelMap;
	}

}