package dev.thorinwasher.blockanimator.api.animator;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

public interface BlockAnimator<B> {

    void blockMove(ImmutableVector3i identifier, Vector3d to, BlockSupplier<B> blockSupplier);

    void blockPlace(ImmutableVector3i identifier, BlockSupplier<B> blockSupplier);

    void blockDestroy(ImmutableVector3i identifier);

    void finishAnimation(BlockSupplier<B> blockSupplier);

}
