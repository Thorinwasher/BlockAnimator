package dev.thorinwasher.blockanimator.paper;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;
import org.bukkit.Location;
import org.bukkit.World;

public class VectorConverter {


    public static Location toLocation(Vector3d Vector3d, World world) {
        return new Location(world, Vector3d.x(), Vector3d.y(), Vector3d.z());
    }

    public static Location toLocation(ImmutableVector3i Vector3d, World world) {
        return new Location(world, Vector3d.x(), Vector3d.y(), Vector3d.z());
    }

    public static Vector3d toVector3d(Location location) {
        return new Vector3d(location.getX(), location.getY(), location.getZ());
    }
}
