package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.Animation;
import dev.thorinwasher.blockanimator.AnimationFrame;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.HashMap;
import java.util.Map;

public class Animator {

    private final Animation<Block> animation;
    private final Instance instance;
    private final Map<Vector3D, Entity> blockEntityMap = new HashMap<>();

    public Animator(Animation<Block> animation, Instance instance) {
        this.animation = animation;
        this.instance = instance;
    }

    public void nextTick() {
        if (animation.getStatus() != Animation.AnimationStatus.READY_FOR_ANIMATION) {
            return;
        }
        AnimationFrame frame = animation.getNext();
        for (Map.Entry<Vector3D, Vector3D> entry : frame.currentToDestination().entrySet()) {
            Entity blockDisplay = blockEntityMap.get(entry.getKey());
            Pos to = toPos(entry.getValue());
            if (blockDisplay == null) {
                Block block = animation.supplier().getBlock(entry.getKey());
                blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
                BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) blockDisplay.getEntityMeta();
                blockDisplayMeta.setBlockState(block);
                blockDisplayMeta.setHasNoGravity(true);
                blockDisplay.setInstance(instance, to);
                blockEntityMap.put(entry.getKey(), blockDisplay);
            } else {
                moveBlockDisplay(blockDisplay, to);
            }
            if (entry.getKey().equals(entry.getValue())) {
                blockDisplay.remove();
                blockEntityMap.remove(entry.getKey());
                Block block = animation.supplier().getBlock(entry.getKey());
                instance.setBlock(toPos(entry.getKey()), block);
            }
        }
    }

    public Pos toPos(Vector3D vector3D) {
        return new Pos(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    private void moveBlockDisplay(Entity blockDisplay, Pos to) {
        Pos current = blockDisplay.getPosition();
        Vec delta = to.sub(current).asVec();
        BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) blockDisplay.getEntityMeta();
        if (instance.getBlock(to).isAir()) {
            if (blockDisplayMeta.getTranslation().isZero()) {
                blockDisplay.setVelocity(delta);
            } else {
                blockDisplayMeta.setTranslation(new Vec(0, 0, 0));
            }
        } else {
            blockDisplayMeta.setTranslation(delta);
        }
    }
}
