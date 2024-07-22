package dev.thorinwasher.blockanimator.paper.handle;

import dev.thorinwasher.blockanimator.animation.Animation;
import dev.thorinwasher.blockanimator.animation.AnimationFrame;
import dev.thorinwasher.blockanimator.paper.ClassChecker;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Animator1_16_4 implements AnimatorHandle {

    private final Animation<BlockState> animation;
    private final World world;
    Map<Vector3D, FallingBlock> blockEntityMap = new HashMap<>();
    private static final boolean SHOULD_AUTO_EXPIRE_IMPLEMENTED = ClassChecker.methodExists("org.bukkit.entity.FallingBlock", "shouldAutoExpire", boolean.class);

    public Animator1_16_4(Animation<BlockState> animation, World world) {
        this.animation = animation;
        this.world = world;
    }

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
            FallingBlock fallingBlock = blockEntityMap.get(entry.getKey());
            Location to = VectorConverter.toLocation(entry.getValue(), world).add(-0.5, 0, -0.5);
            if (fallingBlock == null) {
                BlockState blockState = animation.supplier().getBlock(entry.getKey());
                BlockData blockData = blockState.getBlockData();
                fallingBlock = world.spawnFallingBlock(to, blockData);
                fallingBlock.setPersistent(false);
                fallingBlock.setDropItem(false);
                fallingBlock.setGravity(false);
                blockEntityMap.put(entry.getKey(), fallingBlock);
            } else {
                moveFallingBlock(fallingBlock, to);
            }
            if (entry.getKey().equals(entry.getValue())) {
                fallingBlock.remove();
                blockEntityMap.remove(entry.getKey());
                BlockState blockState = animation.supplier().getBlock(entry.getKey());
                blockState.update(true, false);
            }
        }
        return false;
    }

    private void moveFallingBlock(FallingBlock fallingBlock, Location to) {
        Vector delta = to.clone().subtract(fallingBlock.getLocation()).toVector();
        double previousVelocityLength = fallingBlock.getVelocity().length();
        if (to.getBlock().getType().isAir() && (previousVelocityLength == 0 || previousVelocityLength * 2 > delta.length())) {
            fallingBlock.setVelocity(delta);
        } else {
            fallingBlock.teleport(to);
            fallingBlock.setVelocity(new Vector());
        }
    }

    private void setAutoExpire(boolean autoExpire, FallingBlock fallingBlock) {
        if (SHOULD_AUTO_EXPIRE_IMPLEMENTED) {
            try {
                FallingBlock.class.getMethod("shouldAutoExpire", boolean.class).invoke(fallingBlock, false);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                // Almost unreachable code except for illegal access exception. Should probably throw an error if reached
                throw new RuntimeException(e);
            }
        }
    }
}
