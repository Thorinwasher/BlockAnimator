package dev.thorinwasher.blockanimator.paper;

import dev.thorinwasher.blockanimator.Animation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;


public abstract class Animator {

    protected final Animation<BlockState> animation;
    protected final World world;

    public Animator(Animation<BlockState> animation, World world) {
        this.animation = animation;
        this.world = world;
    }

    public abstract boolean nextTick();

    protected Location toLocation(Vector3D vector3D) {
        return new Location(world, vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }
}
