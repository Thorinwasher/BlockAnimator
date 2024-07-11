package dev.thorinwasher.blockanimator.paper;

import dev.thorinwasher.blockanimator.CompiledAnimation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;


public abstract class Animator {

    protected final CompiledAnimation<BlockState> animation;
    protected final World world;
    protected int tick = 0;

    public Animator(CompiledAnimation<BlockState> compiledAnimation, World world) {
        this.animation = compiledAnimation;
        this.world = world;
    }

    public abstract boolean nextTick();

    protected Location toLocation(Vector3D vector3D) {
        return new Location(world, vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }
}
