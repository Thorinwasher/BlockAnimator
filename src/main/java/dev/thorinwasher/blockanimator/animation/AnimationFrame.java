package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.util.ThreadHelper;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class AnimationFrame {

    private final Map<Entity, Location> relocations;
    private final List<BlockState> states;

    AnimationFrame(Map<Entity, Location> relocations, List<BlockState> states) {
        this.relocations = relocations;
        this.states = states;
    }

    /**
     * This will animate one animation frame during the specified ticks. The animation will start at the
     * next tick.
     * @param ticks <p>The length of the animation.</p>
     * @param plugin <p>The plugin the animation belongs to.</p>
     */
    public void applyFrame(int ticks, Plugin plugin) {
        if (ticks < 1) {
            throw new IllegalStateException("Frame has to be at least 1 tick long.");
        }
        for (BlockState blockState : states) {
            ThreadHelper.scheduleRegionTask(blockState.getLocation(), blockState::update, plugin, ticks);
        }
        for (Map.Entry<Entity, Location> entry : relocations.entrySet()) {
            Location from = entry.getKey().getLocation();
            Location to = entry.getValue();
            Entity entity = entry.getKey();
            Vector velocity = to.clone().subtract(from).toVector().multiply(((double) 20) / ticks);
            ThreadHelper.scheduleEntityTask(entity, () -> entity.setVelocity(velocity), plugin, 0);
            ThreadHelper.scheduleEntityTask(entity, () -> entity.teleport(to), plugin, ticks);
        }
    }

}
