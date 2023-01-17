package org.goldpiggymc.endermanevolution.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import org.goldpiggymc.endermanevolution.BlockManager;
import org.goldpiggymc.endermanevolution.client.entity.renderer.EvolvedEndermanEntityRenderer;
import org.goldpiggymc.endermanevolution.client.entity.renderer.FriendermanEntityRenderer;
import org.goldpiggymc.endermanevolution.entity.EntityManager;
import org.goldpiggymc.endermanevolution.entity.custom.EvolvedEnderman;

public class EndermanEvo implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
            RenderLayer.getCutout(),
            BlockManager.ENDER_FLOWER
        );

        EntityRendererRegistry.register(EntityManager.FRIENDERMAN, FriendermanEntityRenderer::new);
        EntityRendererRegistry.register(EntityManager.EVOLVED_ENDERMAN, EvolvedEndermanEntityRenderer::new);
    }
}
