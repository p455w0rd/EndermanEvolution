package p455w0rd.endermanevo.blocks.tiles;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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

	public TileBlockSkull(final String name) {
		entity = name;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.setString(ENTITY_TAG, entity);
		return nbt;
	}

	@Override
	public void readFromNBT(final NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		entity = nbt.getString(ENTITY_TAG);
	}

	@Override
	public boolean canRenderBreaking() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return Double.MAX_VALUE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	public String getEntity() {
		return entity;
	}

	public ModelSkullBase getModel() {
		return getModels().get(entity);
	}

	public static ModelSkullBase getModel(final String name) {
		return getModels().get(name);
	}

	private static Map<String, ModelSkullBase> SKULL_MODELS = Maps.newHashMap();

	private static Map<String, ModelSkullBase> getModels() {
		if (SKULL_MODELS.isEmpty()) {
			SKULL_MODELS.put("enderman_skull", ModelSkullBase.Enderman.getInstance());
			SKULL_MODELS.put("frienderman_skull", ModelSkullBase.Frienderman.getInstance());
			SKULL_MODELS.put("enderman_evolved_skull", ModelSkullBase.Enderman2.getInstance());
		}
		return SKULL_MODELS;
	}

}