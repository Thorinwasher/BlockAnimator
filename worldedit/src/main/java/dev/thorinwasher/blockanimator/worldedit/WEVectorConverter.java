package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.math.BlockVector3;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

public class WEVectorConverter {

    public static BlockVector3 toBlockVector3(ImmutableVector3i Vector3d) {
        return BlockVector3.at(Vector3d.x(), Vector3d.y(), Vector3d.z());
    }

    public static ImmutableVector3i toImmutableVector3i(BlockVector3 blockVector3) {
        return new ImmutableVector3i(blockVector3.getX(), blockVector3.getY(), blockVector3.getZ());
    }
}
