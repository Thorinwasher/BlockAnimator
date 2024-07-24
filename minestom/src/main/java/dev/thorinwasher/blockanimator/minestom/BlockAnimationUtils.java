package dev.thorinwasher.blockanimator.minestom;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class BlockAnimationUtils {


    public static Entity spawnBlockDisplay(Vector3D position, Block block, Instance instance) {
        Entity blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) blockDisplay.getEntityMeta();
        blockDisplayMeta.setBlockState(block);
        blockDisplayMeta.setHasNoGravity(true);
        blockDisplay.setInstance(instance, VectorConversion.toVec(position));
        return blockDisplay;
    }
}
