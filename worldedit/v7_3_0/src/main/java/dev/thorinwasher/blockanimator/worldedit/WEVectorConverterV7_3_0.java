package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class WEVectorConverterV7_3_0 {

    public static BlockVector3 toBlockVector3(Vector3D vector3D) {
        return new BlockVector3((int) vector3D.getX(), (int) vector3D.getY(), (int) vector3D.getZ());
    }

    public static Vector3D toVector3D(BlockVector3 blockVector3) {
        return new Vector3D(blockVector3.x(), blockVector3.y(), blockVector3.z());
    }
}
