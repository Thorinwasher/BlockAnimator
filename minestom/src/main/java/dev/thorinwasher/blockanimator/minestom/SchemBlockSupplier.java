package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import net.hollowcube.schem.Rotation;
import net.hollowcube.schem.Schematic;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemBlockSupplier implements BlockSupplier<Block> {

    private final Map<Vector3D, Block> blockMap = new HashMap<>();
    private final Instance instance;

    public SchemBlockSupplier(Schematic schematic, Rotation rotation, Point offset, Instance instance) {
        schematic.apply(rotation, (point, block) -> {
            if (block.isAir()) {
                return;
            }
            blockMap.put(VectorConversion.toVector3D(VectorConversion.blockVec(point.add(offset))), block);
        });
        this.instance = instance;
    }

    @Override
    public Block getBlock(Vector3D targetPosition) {
        return blockMap.get(targetPosition);
    }

    @Override
    public List<Vector3D> getPositions() {
        return new ArrayList<>(blockMap.keySet());
    }

    @Override
    public void placeBlock(Vector3D identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), blockMap.get(identifier), false);
    }
}
