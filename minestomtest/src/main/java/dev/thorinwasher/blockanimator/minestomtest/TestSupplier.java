package dev.thorinwasher.blockanimator.minestomtest;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.minestom.VectorConversion;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.joml.Vector3d;

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
    public Block getBlock(ImmutableVector3i targetPosition) {
        return material;
    }

    @Override
    public List<ImmutableVector3i> getPositions() {
        List<ImmutableVector3i> output = new ArrayList<>();
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                for (int dz = 0; dz < size; dz++) {
                    output.add(new ImmutableVector3i(corner.blockX() + dx, corner.blockY() + dy, corner.blockZ() + dz));
                }
            }
        }
        return output;
    }

    @Override
    public void placeBlock(ImmutableVector3i identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), material, false);
    }
}
