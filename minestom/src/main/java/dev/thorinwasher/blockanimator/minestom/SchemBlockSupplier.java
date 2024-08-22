package dev.thorinwasher.blockanimator.minestom;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import net.hollowcube.schem.Rotation;
import net.hollowcube.schem.Schematic;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemBlockSupplier implements BlockSupplier<Block> {

    private final Map<ImmutableVector3i, Block> blockMap = new HashMap<>();
    private final Instance instance;

    public SchemBlockSupplier(Schematic schematic, Rotation rotation, Point offset, Instance instance) {
        schematic.apply(rotation, (point, block) -> {
            if (block.isAir()) {
                return;
            }
            blockMap.put(VectorConversion.toImmutableVector3i(point.add(offset)), block);
        });
        this.instance = instance;
    }

    @Override
    public Block getBlock(ImmutableVector3i targetPosition) {
        return blockMap.get(targetPosition);
    }

    @Override
    public List<ImmutableVector3i> getPositions() {
        return new ArrayList<>(blockMap.keySet());
    }

    @Override
    public void placeBlock(ImmutableVector3i identifier) {
        instance.setBlock(VectorConversion.toVec(identifier), blockMap.get(identifier), false);
    }
}
