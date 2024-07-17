package dev.thorinwasher.blockanimator.testplugin.supplier;

import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.List;

public class TestSupplier implements BlockSupplier<BlockState> {

    private final Material material;
    private final int size;
    private final Location corner;

    public TestSupplier(Material material, int size, Location corner) {
        this.material = material;
        this.size = size;
        this.corner = corner.getBlock().getLocation().add(0.5, 0, 0.5); // Go to block coordinates (lazy)
    }


    @Override
    public BlockState getBlock(Vector3D targetPosition) {
        Location location = new Location(corner.getWorld(), targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
        BlockState blockState = location.getBlock().getState();
        blockState.setType(material);
        return blockState;
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
}
