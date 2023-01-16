package org.goldpiggymc.endermanevolution.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.goldpiggymc.endermanevolution.BlockManager;

public class EndermanEvo implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), BlockManager.ENDER_FLOWER);
    }
}
