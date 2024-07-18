package dev.thorinwasher.blockanimator.paper.handle;

import dev.thorinwasher.blockanimator.Animation;
import dev.thorinwasher.blockanimator.AnimationFrame;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class Animator1_19_4 implements AnimatorHandle {

    private final Animation<BlockState> animation;
    private final World world;
    Map<Vector3D, BlockDisplay> blockEntityMap = new HashMap<>();

    public Animator1_19_4(Animation<BlockState> animation, World world) {
        this.animation = animation;
        this.world = world;
    }

    @Override
    public boolean nextTick() {
        Animation.AnimationStatus status = animation.getStatus();
        if (status == Animation.AnimationStatus.COMPLETED) {
            return true;
        }
        if (status == Animation.AnimationStatus.NOT_READY_FOR_ANIMATION) {
            return false;
        }
        AnimationFrame frame = animation.getNext();
        for (Map.Entry<Vector3D, Vector3D> entry : frame.currentToDestination().entrySet()) {
            BlockDisplay blockDisplay = blockEntityMap.get(entry.getKey());
            Location to = VectorConverter.toLocation(entry.getValue(), world).add(-0.5, 0, -0.5);
            if (blockDisplay == null) {
                blockDisplay = world.spawn(to, BlockDisplay.class);
                blockDisplay.setBlock(animation.supplier().getBlock(entry.getKey()).getBlockData());
                blockDisplay.setPersistent(false);
                blockDisplay.setGravity(false);
                blockEntityMap.put(entry.getKey(), blockDisplay);
            } else {
                moveBlockDisplay(blockDisplay, to);
            }
            if (entry.getKey().equals(entry.getValue())) {
                blockDisplay.remove();
                blockEntityMap.remove(entry.getKey());
                BlockState blockState = animation.supplier().getBlock(entry.getKey());
                blockState.update(true, false);
            }
        }
        return false;
    }

    private void moveBlockDisplay(BlockDisplay blockDisplay, Location to) {
        Location current = blockDisplay.getLocation();
        Vector delta = to.clone().subtract(current).toVector();
        Transformation transformation = blockDisplay.getTransformation();
        blockDisplay.setTransformation(new Transformation(delta.toVector3f(), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
    }
}