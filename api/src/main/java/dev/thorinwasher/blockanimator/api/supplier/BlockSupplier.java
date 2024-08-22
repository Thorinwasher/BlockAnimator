package dev.thorinwasher.blockanimator.api.supplier;

import org.joml.Vector3d;

import java.util.List;

public interface BlockSupplier<B> {
    B getBlock(ImmutableVector3i targetPosition);

    List<ImmutableVector3i> getPositions();

    void placeBlock(ImmutableVector3i identifier);

    default void placeBlocks(List<ImmutableVector3i> identifiers) {
        identifiers.forEach(this::placeBlock);
    }
}
