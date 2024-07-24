package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.container.TwoTuple;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.HashMap;
import java.util.Map;

public interface Animation<B> {

    /**
     * HAS TO BE RUN ASYNC!
     */
    void compile();

    AnimationFrame getNext();

    static void mergeBlockAnimationToFrames(CompiledBlockMoveAnimation
                                                    compiledBlockMoveAnimation, Map<Integer, AnimationFrame> frames, Vector3D target, int frame) {
        for (TwoTuple<Vector3D, BlockMoveType> newPosition : compiledBlockMoveAnimation.frames()) {
            frames.computeIfAbsent(frame, ignored -> new AnimationFrame(new HashMap<>())).currentToDestination().put(target, newPosition);
            frame++;
        }
    }

    AnimationStatus getStatus();

    BlockSupplier<B> supplier();

    enum AnimationStatus {
        COMPLETED,
        READY_FOR_ANIMATION,
        NOT_READY_FOR_ANIMATION
    }
}
