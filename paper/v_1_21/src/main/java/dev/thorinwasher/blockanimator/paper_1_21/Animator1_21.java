package dev.thorinwasher.blockanimator.paper_1_21;

import dev.thorinwasher.blockanimator.AnimationFrame;
import dev.thorinwasher.blockanimator.paper.Animator;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;

public class Animator1_21 extends Animator {

    Map<Vector3D, BlockDisplay> blockEntityMap = new HashMap<>();

    public Animator1_21(dev.thorinwasher.blockanimator.CompiledAnimation<BlockState> compiledAnimation, World world) {
        super(compiledAnimation, world);
    }

    @Override
    public boolean nextTick() {
        if (animation.frames().size() == tick) {
            return true;
        }
        AnimationFrame frame = animation.frames().get(tick);
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
        tick ++;
        return false;
    }
}
