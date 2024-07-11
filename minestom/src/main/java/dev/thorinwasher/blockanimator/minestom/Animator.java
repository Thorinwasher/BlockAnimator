package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.AnimationFrame;
import dev.thorinwasher.blockanimator.CompiledAnimation;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.HashMap;
import java.util.Map;

public class Animator {

    private final CompiledAnimation<Block> compiledAnimation;
    private final Instance instance;
    private final Map<Vector3D, Entity> blockEntityMap = new HashMap<>();
    private int tick = 0;

    public Animator(CompiledAnimation<Block> compiledAnimation, Instance instance) {
        this.compiledAnimation = compiledAnimation;
        this.instance = instance;
    }

    public void nextTick() {
        if (compiledAnimation.frames().size() == tick) {
            return;
        }
        AnimationFrame frame = compiledAnimation.frames().get(tick);
        for (Map.Entry<Vector3D, Vector3D> entry : frame.currentToDestination().entrySet()) {
            Entity blockDisplay = blockEntityMap.get(entry.getKey());
            if (blockDisplay == null) {
                Block block = compiledAnimation.supplier().getBlock(entry.getKey());
                blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
                BlockDisplayMeta blockDisplayMeta = (BlockDisplayMeta) blockDisplay.getEntityMeta();
                blockDisplayMeta.setBlockState(block);
                blockDisplayMeta.setHasNoGravity(true);
                blockDisplay.setInstance(instance, toPos(entry.getValue()));
                blockEntityMap.put(entry.getKey(), blockDisplay);
            } else {
                blockDisplay.refreshPosition(toPos(entry.getValue()));
            }
            if (entry.getKey().equals(entry.getValue())) {
                blockDisplay.remove();
                blockEntityMap.remove(entry.getKey());
                Block block = compiledAnimation.supplier().getBlock(entry.getKey());
                instance.setBlock(toPos(entry.getKey()), block);
            }
        }
        tick++;
    }

    public boolean finished(){
        return compiledAnimation.frames().size() <= tick;
    }

    public Pos toPos(Vector3D vector3D) {
        return new Pos(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }
}
