package dev.thorinwasher.blockanimator.papertest.supplier;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class TestSupplier implements BlockSupplier<BlockData> {

    private final Material material;
    private final int size;
    private final Location corner;

    public TestSupplier(Material material, int size, Location corner) {
        this.material = material;
        this.size = size;
        this.corner = corner.toBlockLocation();
    }


    @Override
    public BlockData getBlock(Vector3D targetPosition) {
        return material.createBlockData();
    }

    @Override
    public List<Vector3D> getPositions() {
        List<Vector3D> output = new ArrayList<>();
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                for (int dz = 0; dz < size; dz++) {
                    output.add(new Vector3D(corner.getX() + dx, corner.getY() + dy, corner.getZ() + dz));
                }
            }
        }
        return output;
    }

    @Override
    public void placeBlock(Vector3D identifier) {
        VectorConverter.toLocation(identifier, corner.getWorld()).getBlock().setType(material);
    }
}
