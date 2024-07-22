package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.timer.BlockTimer;
import dev.thorinwasher.blockanimator.timer.CompiledBlockTimer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface Animation<B> {

    /**
     * HAS TO BE RUN ASYNC!
     */
    void compile();

    AnimationFrame getNext();

    static void mergeBlockAnimationToFrames(CompiledBlockMoveAnimation
                                                            compiledBlockMoveAnimation, Map<Integer, AnimationFrame> frames, Vector3D target, int frame) {
        for (Vector3D newPosition : compiledBlockMoveAnimation.frames()) {
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
