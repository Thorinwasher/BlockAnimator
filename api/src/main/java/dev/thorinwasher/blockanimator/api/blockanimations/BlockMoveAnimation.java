package dev.thorinwasher.blockanimator.api.blockanimations;

import dev.thorinwasher.blockanimator.api.blockanimations.transformation.BlockTransformation;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;

public interface BlockMoveAnimation {

    CompiledBlockMoveAnimation compile(ImmutableVector3i to);

    void addBlockTransform(BlockTransformation blockTransformation);

}
