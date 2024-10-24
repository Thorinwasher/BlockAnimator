package dev.thorinwasher.blockanimator.minestom;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.joml.*;

public class BlockAnimationUtils {


    public static Entity spawnBlockDisplay(Vector3d position, Block block, Instance instance, Matrix4f transform) {
        Entity blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) blockDisplay.getEntityMeta();
        blockDisplayMeta.setBlockState(block);
        blockDisplayMeta.setHasNoGravity(true);
        applyTransform(blockDisplay, transform);
        blockDisplay.setInstance(instance, VectorConversion.toVec(position).sub(getOffset(blockDisplay)));
        return blockDisplay;
    }

    public static void applyTransform(Entity entity, Matrix4f transform) {
        BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) entity.getEntityMeta();
        Quaternionf rotation = transform.getUnnormalizedRotation(new Quaternionf());
        blockDisplayMeta.setLeftRotation(new float[]{rotation.x, rotation.y, rotation.z, rotation.w});
        Vector3f scale = transform.getScale(new Vector3f());
        blockDisplayMeta.setScale(VectorConversion.toVec(new Vector3d(scale)));
    }

    public static Vec getOffset(Entity entity) {
        if (!(entity.getEntityMeta() instanceof BlockDisplayMeta blockDisplayMeta)) {
            throw new IllegalArgumentException("Expected entity to have meta of type block display");
        }
        Vec scale = blockDisplayMeta.getScale();
        return new Vec(-0.5 - scale.x()/2, -0.5 -scale.y()/2, -0.5 + scale.z()/2);
    }
}
