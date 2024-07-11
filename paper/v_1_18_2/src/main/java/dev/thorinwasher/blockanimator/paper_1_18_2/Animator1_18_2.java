package dev.thorinwasher.blockanimator.paper_1_18_2;

import dev.thorinwasher.blockanimator.AnimationFrame;
import dev.thorinwasher.blockanimator.Animation;
import dev.thorinwasher.blockanimator.paper.Animator;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;

import java.util.HashMap;
import java.util.Map;

public class Animator1_18_2 extends Animator {

    Map<Vector3D, FallingBlock> blockEntityMap = new HashMap<>();

    public Animator1_18_2(Animation<BlockState> animation, World world) {
        super(animation, world);
    }

    public boolean nextTick() {
        Animation.AnimationStatus status = animation.getStatus();
        if (status == Animation.AnimationStatus.COMPLETED) {
            return true;
        }
        if(status == Animation.AnimationStatus.NOT_READY_FOR_ANIMATION){
            return false;
        }
        AnimationFrame frame = animation.getNext();
        for (Map.Entry<Vector3D, Vector3D> entry : frame.currentToDestination().entrySet()) {
            FallingBlock fallingBlock = blockEntityMap.get(entry.getKey());
            if (fallingBlock == null) {
                BlockState blockState = animation.supplier().getBlock(entry.getKey());
                BlockData blockData = blockState.getBlockData();
                fallingBlock = world.spawnFallingBlock(toLocation(entry.getValue()), blockData);
                fallingBlock.setPersistent(false);
                fallingBlock.shouldAutoExpire(false);
                fallingBlock.setDropItem(false);
                fallingBlock.setGravity(false);
                blockEntityMap.put(entry.getKey(), fallingBlock);
            } else {
                fallingBlock.teleport(toLocation(entry.getValue()));
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

    private Vec3 toVec3(Vector3D vector3D) {
        return new Vec3(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    private Vector3D toVector3D(Vec3 vector) {
        return new Vector3D(vector.x, vector.y, vector.z);
    }
}
