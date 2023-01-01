package org.goldpiggymc.endermanevolution;

import io.wispforest.owo.registration.reflect.BlockRegistryContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import org.goldpiggymc.endermanevolution.blocks.EnderFlowerCrop;

public class BlockManager implements BlockRegistryContainer {
    public static final Block ENDER_FLOWER = new EnderFlowerCrop(AbstractBlock.Settings.of(Material.PLANT).nonOpaque().noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP));
}
