package dev.thorinwasher.blockanimator.minestom;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class VectorConversion {

    public static Vec toVec(Vector3D vector3D){
        return new Vec(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    public static Vector3D toVector3D(Point vec){
        return new Vector3D(vec.x(), vec.y(), vec.z());
    }
}
