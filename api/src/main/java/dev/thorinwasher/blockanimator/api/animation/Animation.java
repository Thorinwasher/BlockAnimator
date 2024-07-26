package dev.thorinwasher.blockanimator.api.animation;

import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.api.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.api.container.TwoTuple;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public interface Animation<B> {

    /**
     * HAS TO BE RUN ASYNC!
     */
    void compile();

    @ApiStatus.Internal
    AnimationFrame getNext();

    @ApiStatus.Internal
    static void mergeBlockAnimationToFrames(CompiledBlockMoveAnimation
                                                    compiledBlockMoveAnimation, Map<Integer, AnimationFrame> frames, Vector3D target, int frame) {
        for (TwoTuple<Vector3D, BlockMoveType> newPosition : compiledBlockMoveAnimation.frames()) {
            frames.computeIfAbsent(frame, ignored -> new AnimationFrame(new HashMap<>())).currentToDestination().put(target, newPosition);
            frame++;
        }
    }

    @ApiStatus.Internal
    AnimationStatus getStatus();

    @ApiStatus.Internal
    BlockSupplier<B> supplier();

    enum AnimationStatus {
        COMPLETED,
        READY_FOR_ANIMATION,
        COMPILING_TO_BUFFER
    }
}
