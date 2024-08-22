package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.joml.Vector3d;

public class VectorConversion {

    public static Vec toVec(Vector3d Vector3d) {
        return new Vec(Vector3d.x(), Vector3d.y(), Vector3d.z());
    }

    public static Vec toVec(ImmutableVector3i immutableVector3i) {
        return new Vec(immutableVector3i.x(), immutableVector3i.y(), immutableVector3i.z());
    }

    public static Vector3d toVector3d(Point vec) {
        return new Vector3d(vec.x(), vec.y(), vec.z());
    }

    public static Vec blockVec(Point point) {
        return new Vec(point.blockX(), point.blockY(), point.blockZ());
    }

    public static ImmutableVector3i toImmutableVector3i(Point point) {
        return new ImmutableVector3i(point.blockX(), point.blockY(), point.blockZ());
    }
}
