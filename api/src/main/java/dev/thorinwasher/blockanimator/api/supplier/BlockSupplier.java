package dev.thorinwasher.blockanimator.api.supplier;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public interface BlockSupplier<B> {
    B getBlock(Vector3D targetPosition);

    List<Vector3D> getPositions();

    void placeBlock(Vector3D identifier);
}
