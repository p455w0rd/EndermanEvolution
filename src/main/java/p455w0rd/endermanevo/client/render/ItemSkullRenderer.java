package p455w0rd.endermanevo.client.render;

import java.util.ArrayList;
import java.util.List;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
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
		renderSkull(tranforms == TransformType.FIXED ? 360.0F : 0.0F, model, stack);
	}

	private void bindTexture(ResourceLocation texture) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}

	public void renderSkull(float rot, ModelSkullBase modelIn, ItemStack stack) {
		ModelSkullBase modelbase = modelIn;
		bindTexture(modelbase.getTexture());
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();
		GlStateManager.enableLighting();
		GlStateManager.translate(0.5, 0.0, 0.5);
		if (stack.isOnItemFrame()) {
			GlStateManager.scale(-2.0F, -2.0F, 2.0F);
		}
		else {
			GlStateManager.scale(-1.5F, -1.5F, 1.5F);
		}
		if (stack.isOnItemFrame()) {
			//rot = 180.0F;
		}
		modelbase.render(rot);

		modelbase.renderOverlay(rot);
		if (modelbase.getLightMap() != null) {
			bindTexture(modelbase.getLightMap());
			modelbase.renderLightMap(rot);
		}
		GlStateManager.translate(-0.5, -0.0, -0.5);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
		//===============================
		/*
		if (modelbase instanceof Enderman2) {
			GlStateManager.pushMatrix();
			float oldTexX = OpenGlHelper.lastBrightnessX;
			float oldTexY = OpenGlHelper.lastBrightnessY;
			GlStateManager.depthMask(true);
			bindTexture(new ResourceLocation(ModGlobals.MODID, "textures/entity/charge_nocolor.png"));
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float f = Minecraft.getMinecraft().player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
			GlStateManager.translate(f * 0.01F, f * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float r = 0;
			float g = 0.75F;
			float b = 0;
			GlStateManager.color(r, g, b, 1.0F);
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			GlStateManager.translate(0.5F, 0.75F, 0.5F);
			GlStateManager.scale(1.75F, 1.75F, 1.75F);
			modelbase.render(rot);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.depthMask(true);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldTexX, oldTexY);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
			GlStateManager.popMatrix();
		}
		*/
		//=========================

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
