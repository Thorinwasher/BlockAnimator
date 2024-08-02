package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class WEVectorConverter {

    public static BlockVector3 toBlockVector3(Vector3D vector3D) {
        return BlockVector3.at(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    public static Vector3D toVector3D(BlockVector3 blockVector3) {
        return new Vector3D(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ());
    }
}
