package dev.thorinwasher.blockanimator.minestomtest;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.minestom.VectorConversion;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class TestSupplier implements BlockSupplier<Block> {

    private final Block material;
    private final int size;
    private final Point corner;
    private final Instance instance;

    public TestSupplier(Block material, int size, Point corner, Instance instance) {
        this.material = material;
        this.size = size;
        this.corner = new Vec(corner.blockX(), corner.blockY(), corner.blockZ());
        this.instance = instance;
    }


    @Override
    public Block getBlock(Vector3D targetPosition) {
        return material;
    }

    @Override
    public List<Vector3D> getPositions() {
        List<Vector3D> output = new ArrayList<>();
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                for (int dz = 0; dz < size; dz++) {
                    output.add(new Vector3D(corner.x() + dx, corner.y() + dy, corner.z() + dz));
                }
            }
        }
        return output;
    }

    @Override
    public void placeBlock(Vector3D identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), material, false);
    }
}
