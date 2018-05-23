package p455w0rd.endermanevo.client.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import p455w0rdslib.util.EasyMappings;

/**
 * @author Elucent
 *
 */
public class ParticleRenderer {

	ArrayList<Particle> particles = new ArrayList<Particle>();

	private static ParticleRenderer INSTANCE;

	public static ParticleRenderer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ParticleRenderer();
		}
		return INSTANCE;
	}

	public void updateParticles() {
		boolean[] particleIndexes = new boolean[particles.size()];
		for (int i = 0; i < particles.size(); i++) {//must do this way to prevent CME
			if (particleIndexes[i]) {
				particles.remove(i);
			}
		}
	}

	public void renderParticles(EntityPlayer dumbplayer, float partialTicks) {
		if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
			return;
		}
		float f = ActiveRenderInfo.getRotationX();
		float f1 = ActiveRenderInfo.getRotationZ();
		float f2 = ActiveRenderInfo.getRotationYZ();
		float f3 = ActiveRenderInfo.getRotationXY();
		float f4 = ActiveRenderInfo.getRotationXZ();
		EntityPlayer player = EasyMappings.player();
		if (player != null) {
			Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

			Particle.cameraViewDir = player.getLook(partialTicks);
			//GlStateManager.glTexParameterf(3553, 10242, 10497.0F);
			//GlStateManager.glTexParameterf(3553, 10243, 10497.0F);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			//GlStateManager.alphaFunc(GL11.GL_GREATER, 0F);
			GlStateManager.disableCull();

			GlStateManager.depthMask(false);

			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buffer = tess.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			for (int i = 0; i < particles.size(); i++) {
				particles.get(i).renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
			}
			tess.draw();

			GlStateManager.enableCull();
			GlStateManager.depthMask(true);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableBlend();
			//GlStateManager.alphaFunc(516, 0.1F);
		}
	}

	public void addParticle(Particle particle) {
		particles.add(particle);
	}
}