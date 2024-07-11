package dev.thorinwasher.blockanimator.paper_1_21;

import dev.thorinwasher.blockanimator.Animation;
import dev.thorinwasher.blockanimator.AnimationFrame;
import dev.thorinwasher.blockanimator.paper.Animator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;

public class Animator1_21 extends Animator {

    Map<Vector3D, BlockDisplay> blockEntityMap = new HashMap<>();

    public Animator1_21(Animation<BlockState> animation, World world) {
        super(animation, world);
    }

    @Override
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
            BlockDisplay blockDisplay = blockEntityMap.get(entry.getKey());
            if (blockDisplay == null) {
                blockDisplay = world.spawn(toLocation(entry.getValue()), BlockDisplay.class);
                blockDisplay.setBlock(animation.supplier().getBlock(entry.getKey()).getBlockData());
                blockDisplay.setPersistent(false);
                blockDisplay.setGravity(false);
                blockEntityMap.put(entry.getKey(), blockDisplay);
            } else {
                blockDisplay.teleport(toLocation(entry.getValue()), PlayerTeleportEvent.TeleportCause.PLUGIN);
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
}
