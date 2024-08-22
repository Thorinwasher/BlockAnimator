package dev.thorinwasher.blockanimator.papertest.supplier;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.joml.Vector3d;
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
    public BlockData getBlock(ImmutableVector3i targetPosition) {
        return material.createBlockData();
    }

    @Override
    public List<ImmutableVector3i> getPositions() {
        List<ImmutableVector3i> output = new ArrayList<>();
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                for (int dz = 0; dz < size; dz++) {
                    output.add(new ImmutableVector3i(corner.getBlockX() + dx, corner.getBlockY() + dy, corner.getBlockZ() + dz));
                }
            }
        }
        return output;
    }

    @Override
    public void placeBlock(ImmutableVector3i identifier) {
        VectorConverter.toLocation(identifier, corner.getWorld()).getBlock().setType(material);
    }
}
