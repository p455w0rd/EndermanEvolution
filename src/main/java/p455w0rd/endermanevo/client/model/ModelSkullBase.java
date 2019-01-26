package p455w0rd.endermanevo.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import p455w0rd.endermanevo.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class ModelSkullBase extends ModelBiped {

	private static ModelEnderman ENDERMAN_MODEL_INSTANCE = new ModelEnderman(0.0F);

	public ModelRenderer head;
	protected ModelRenderer overlay;
	private boolean renderOverlay;
	private ResourceLocation TEXTURE = null;
	private ResourceLocation LIGHTMAP = null;

	public ModelSkullBase() {
		this(32);
	}

	protected ModelSkullBase(int height) {
		textureWidth = 64;
		textureHeight = height;
		renderOverlay = true;
		head = new ModelRenderer(this, 0, 0);
		overlay = new ModelRenderer(this, 32, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
		head.setRotationPoint(0.0F, 0.0F, 0.0F);
		overlay.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		overlay.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	public static ModelEnderman getEndermanModelInstace() {
		return ENDERMAN_MODEL_INSTANCE;
	}

	public ResourceLocation getTexture() {
		return TEXTURE;
	}

	public ResourceLocation getLightMap() {
		return LIGHTMAP;
	}

	protected void setLightMap(ResourceLocation texture) {
		LIGHTMAP = texture;
	}

	protected void setTexture(ResourceLocation texture) {
		TEXTURE = texture;
	}

	public float playerRenderOffset() {
		return 0;
	}

	protected ModelSkullBase hideOverlay() {
		renderOverlay = false;
		return this;
	}

	public void render(float rotationX) {
		render(rotationX, 0.0F, null);
	}

	public void render(float rotationX, float rotationY, EntityLivingBase entity) {
		/*
		bipedBody.showModel = false;
		bipedLeftLeg.showModel = false;
		bipedRightLeg.showModel = false;
		bipedLeftArm.showModel = false;
		bipedRightArm.showModel = false;
		bipedHead.showModel = true;
		bipedHeadwear.showModel = true;
		bipedHead = head;
		bipedHeadwear = overlay;
		*/
		if (entity != null && entity.isSneaking()) {
			GlStateManager.translate(0.0F, 0.25F, 0.0F);
		}
		else {
			GlStateManager.translate(0.0F, 0.02F, 0.0F);
		}
		head.rotateAngleY = rotationX / (180F / (float) Math.PI);
		head.rotateAngleX = rotationY / (180F / (float) Math.PI);
		if (entity == null || (entity != null && !entity.isInvisible())) {
			head.render(0.0625F);
		}
		if (renderOverlay) {
			if (entity == null || (entity != null && !entity.isInvisible())) {
				renderOverlay(rotationX, rotationY);
			}
		}
		if (entity != null && entity.isSneaking()) {
			GlStateManager.translate(0.0F, -0.25F, 0.0F);
		}
		else {
			GlStateManager.translate(0.0F, -0.02F, 0.0F);
		}
		if (this instanceof Enderman2) {
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
			GlStateManager.color(r, g, b, 0.5F);
			//GlStateManager.disableLighting();
			RenderHelper.enableStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 260.0F, 260.0F);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			//GlStateManager.translate(0.5F, 0.1F, 0.5F);
			GlStateManager.scale(1.1F, 1.1F, 1.1F);
			//modelbase.render(rot);

			head.render(0.0625F);

			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			//GlStateManager.disableAlpha();
			GlStateManager.depthMask(true);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, oldTexX, oldTexY);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	public void bindTexture(ResourceLocation texture) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (entityIn != null && entityIn instanceof EntityLivingBase) {
			render(netHeadYaw, headPitch, (EntityLivingBase) entityIn);
		}
		else {
			render(netHeadYaw, headPitch, null);
		}

	}

	public void renderOverlay(float rotationX) {
		renderOverlay(rotationX, 0.0F);
	}

	public void renderOverlay(float rotationX, float rotationY) {
		overlay.rotateAngleY = rotationX / (180F / (float) Math.PI);
		overlay.rotateAngleX = rotationY / (180F / (float) Math.PI);
		overlay.render(0.0625F);
	}

	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void renderLightMap(float skullRotation) {
		renderLightMap(skullRotation, 0.0F, null);
	}

	public void renderLightMap(float skullRotation, float skullPitch, EntityLivingBase entity) {

		GlStateManager.pushMatrix();
		//GlStateManager.enableBlend();
		//GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
		//GlStateManager.depthMask(true);
		//GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		float brightnessX = OpenGlHelper.lastBrightnessX;
		float brightnessY = OpenGlHelper.lastBrightnessY;
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680f, 0f);
		RenderHelper.enableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		if (entity != null && entity instanceof EntityLivingBase) {
			render(skullRotation, skullPitch, entity);
		}
		else {
			render(skullRotation, skullPitch, null);
		}

		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
		//GlStateManager.disableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();
	}

	public void renderLightMapOnPlayerHead(World world, EntityLivingBase wearer) {
		GlStateManager.pushMatrix();
		//GlStateManager.enableBlend();
		//GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
		//GlStateManager.depthMask(true);
		float brightnessX = OpenGlHelper.lastBrightnessX;
		float brightnessY = OpenGlHelper.lastBrightnessY;
		//GlStateManager.disableLighting();
		RenderHelper.enableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
		//head.render(0.01F);
		render(wearer.getRotationYawHead(), wearer.rotationPitch, wearer);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightnessX, brightnessY);
		//GlStateManager.disableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.popMatrix();
	}

	public static class Enderman extends ModelSkullBase {

		protected static final ResourceLocation TEXTURE_ENDERMAN = new ResourceLocation("textures/entity/enderman/enderman.png");
		protected static final ResourceLocation LIGHTMAP_ENDERMAN = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
		private static Enderman INSTANCE = new Enderman();

		public Enderman() {
			setTexture(TEXTURE_ENDERMAN);
			setLightMap(LIGHTMAP_ENDERMAN);
			ModelEnderman model = getEndermanModelInstace();
			head = model.bipedHead;
			overlay = model.bipedHeadwear;
			model.isAttacking = true;
			head.setRotationPoint(0F, 0F, 0F);
			overlay.setRotationPoint(0F, 0F, 0F);
		}

		public static Enderman getInstance() {
			return INSTANCE;
		}

	}

	public static class Frienderman extends ModelSkullBase {

		protected static final ResourceLocation TEXTURE_ENDERMAN = new ResourceLocation(ModGlobals.MODID, "textures/entity/frienderman.png");
		protected static final ResourceLocation LIGHTMAP_ENDERMAN = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
		private static Frienderman INSTANCE = new Frienderman();

		public Frienderman() {
			setTexture(TEXTURE_ENDERMAN);
			setLightMap(LIGHTMAP_ENDERMAN);
			ModelEnderman model = getEndermanModelInstace();
			head = model.bipedHead;
			overlay = model.bipedHeadwear;
			model.isAttacking = true;
			head.setRotationPoint(0F, 0F, 0F);
			overlay.setRotationPoint(0F, 0F, 0F);
		}

		public static Frienderman getInstance() {
			return INSTANCE;
		}

	}

	public static class Enderman2 extends ModelSkullBase {

		protected static final ResourceLocation TEXTURE_ENDERMAN = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman_evolved.png");
		protected static final ResourceLocation LIGHTMAP_ENDERMAN = new ResourceLocation(ModGlobals.MODID, "textures/entity/enderman_evolved_eyes.png");
		private static Enderman2 INSTANCE = new Enderman2();

		public Enderman2() {
			setTexture(TEXTURE_ENDERMAN);
			setLightMap(LIGHTMAP_ENDERMAN);
			ModelEnderman model = getEndermanModelInstace();
			head = model.bipedHead;
			overlay = model.bipedHeadwear;
			model.isAttacking = true;
			model.bipedHeadwear.showModel = true;
			model.bipedHeadwear.isHidden = false;
			head.setRotationPoint(0F, 0F, 0F);
			overlay.setRotationPoint(0F, 0F, 0F);
		}

		public static Enderman2 getInstance() {
			return INSTANCE;
		}

	}

}