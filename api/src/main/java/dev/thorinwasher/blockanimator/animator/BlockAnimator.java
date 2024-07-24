package dev.thorinwasher.blockanimator.animator;

import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface BlockAnimator<B> {

    void blockMove(Vector3D identifier, Vector3D to, BlockSupplier<B> blockSupplier);

    void blockPlace(Vector3D identifier, BlockSupplier<B> blockSupplier);

    void blockDestroy(Vector3D identifier);

    void finishAnimation(BlockSupplier<B> blockSupplier);

}
