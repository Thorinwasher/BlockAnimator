package dev.thorinwasher.blockanimator.paper;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;

public class VectorConverter {


    public static Location toLocation(Vector3D vector3D, World world) {
        return new Location(world, vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    public static Vector3D toVector3D(Location location) {
        return new Vector3D(location.getX(), location.getY(), location.getZ());
    }
}
