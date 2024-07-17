package dev.thorinwasher.blockanimator.paper;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class VectorConverter {


    public static Location toLocation(Vector3D vector3D, World world) {
        return new Location(world, vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }
}
