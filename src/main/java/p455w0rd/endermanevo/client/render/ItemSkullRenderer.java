package p455w0rd.endermanevo.client.render;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.endermanevo.blocks.tiles.TileBlockSkull;
import p455w0rd.endermanevo.client.model.ModelSkullBase;
import p455w0rd.endermanevo.items.ItemSkullBase;

/**
 * @author p455w0rd
 *
 */
@SideOnly(Side.CLIENT)
public class ItemSkullRenderer implements IItemRenderer {

	private static final ItemSkullRenderer INSTANCE = new ItemSkullRenderer();

	public static ItemSkullRenderer getInstance() {
		return INSTANCE;
	}

	@Override
	public void renderItem(ItemStack stack, TransformType tranforms) {
		if (!(stack.getItem() instanceof ItemSkullBase)) {
			return;
		}
		ItemSkullBase item = (ItemSkullBase) stack.getItem();
		ModelSkullBase model = TileBlockSkull.getModel(item.getRegistryName().getResourcePath());
		renderSkull(0.0F, model, stack);
	}

	private void bindTexture(ResourceLocation texture) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}

	public void renderSkull(float rot, ModelSkullBase modelIn, ItemStack stack) {
		ModelSkullBase modelbase = modelIn;
		ItemSkullBase item = (ItemSkullBase) stack.getItem();

		bindTexture(modelbase.getTexture());
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		//GlStateManager.disableCull();
		//RenderHelper.enableGUIStandardItemLighting();
		float f = 0.0625F;

		//GlStateManager.enableRescaleNormal();
		//RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		GlStateManager.translate(0.5, 0.0, 0.5);
		if (stack.isOnItemFrame()) {
			GlStateManager.scale(-2.0F, -2.0F, 2.0F);
		}
		else {
			GlStateManager.scale(-1.5F, -1.5F, 1.5F);
		}
		//GlStateManager.enableAlpha();
		if (stack.isOnItemFrame()) {
			rot = 180.0F;
		}
		modelbase.render(rot);

		modelbase.renderOverlay(rot);
		if (modelbase.getLightMap() != null) {
			bindTexture(modelbase.getLightMap());
			modelbase.renderLightMap(rot);
		}

		GlStateManager.translate(-0.5, -0.0, -0.5);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		//GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		//RenderHelper.disableStandardItemLighting();
		//GlStateManager.enableCull();
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();

		//RenderHelper.enableGUIStandardItemLighting();
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return new ArrayList<BakedQuad>();
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return ItemOverrideList.NONE;
	}

	@Override
	public IModelState getTransforms() {
		return TransformUtils.DEFAULT_BLOCK;
	}

}
