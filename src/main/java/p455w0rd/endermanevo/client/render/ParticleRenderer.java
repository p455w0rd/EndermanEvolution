package p455w0rd.endermanevo.client.render;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import p455w0rdslib.util.EasyMappings;

/**
 * @author Elucent
 *
 */
public class ParticleRenderer {

	ArrayList<Particle> particles = new ArrayList<>();

	private static ParticleRenderer INSTANCE;

	public static ParticleRenderer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ParticleRenderer();
		}
		return INSTANCE;
	}

	public void updateParticles() {
		final boolean[] particleIndexes = new boolean[particles.size()];
		for (int i = 0; i < particles.size(); i++) {//must do this way to prevent CME
			if (particleIndexes[i]) {
				particles.remove(i);
			}
		}
	}

	public void renderParticles(final EntityPlayer dumbplayer, final float partialTicks) {
		if (Minecraft.getMinecraft().gameSettings.particleSetting == 2) {
			return;
		}
		final float f = ActiveRenderInfo.getRotationX();
		final float f1 = ActiveRenderInfo.getRotationZ();
		final float f2 = ActiveRenderInfo.getRotationYZ();
		final float f3 = ActiveRenderInfo.getRotationXY();
		final float f4 = ActiveRenderInfo.getRotationXZ();
		final EntityPlayer player = EasyMappings.player();
		if (player != null) {
			Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			Particle.cameraViewDir = player.getLook(partialTicks);
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			GlStateManager.disableCull();
			GlStateManager.depthMask(false);
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			final Tessellator tess = Tessellator.getInstance();
			final BufferBuilder buffer = tess.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			for (int i = 0; i < particles.size(); i++) {
				particles.get(i).renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
			}
			tess.draw();
			GlStateManager.enableCull();
			GlStateManager.depthMask(true);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableBlend();
		}
	}

	public void addParticle(final Particle particle) {
		particles.add(particle);
	}

}