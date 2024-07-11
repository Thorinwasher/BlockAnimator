package dev.thorinwasher.blockanimator;

import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.timer.BlockTimer;
import dev.thorinwasher.blockanimator.timer.CompiledBlockTimer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Animation<B> {

    private final BlockMoveAnimation blockMoveAnimation;
    private final BlockSupplier<B> blockSupplier;
    private final BlockTimer blockTimer;
    private final BlockSelector blockSelector;

    private final ConcurrentHashMap<Integer, AnimationFrame> frames = new ConcurrentHashMap<>();
    private boolean compileCompleted = false;
    private AtomicInteger currentFrame = new AtomicInteger();
    private AtomicInteger currentCompiledFrame = new AtomicInteger();

    public Animation(BlockSelector blockSelector, BlockMoveAnimation blockMoveAnimation,
                     BlockSupplier<B> blockSupplier, BlockTimer blockTimer) {
        this.blockSelector = blockSelector;
        this.blockMoveAnimation = blockMoveAnimation;
        this.blockSupplier = blockSupplier;
        this.blockTimer = blockTimer;
    }

    /**
     * HAS TO BE RUN ASYNC!
     */
    public void compile() {
        try {
            CompiledBlockSelector blockSelector = this.blockSelector.compile(blockSupplier.getPositions());
            CompiledBlockTimer blockTimer = this.blockTimer.compile(blockSupplier.getPositions().size());
            for (int fetchAmount : blockTimer.frames()) {
                for (int count = 0; count < fetchAmount; count++) {
                    Vector3D target = blockSelector.next();
                    CompiledBlockMoveAnimation compiledBlockMoveAnimation = blockMoveAnimation.compile(target);
                    mergeBlockAnimationToFrames(compiledBlockMoveAnimation, target, currentCompiledFrame.get());
                }
                if (currentCompiledFrame.incrementAndGet() > currentFrame.get() + 40) {
                    Thread.sleep(50);
                }
            }
            this.compileCompleted = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public AnimationFrame getNext() {
        return frames.remove(currentFrame.getAndIncrement());
    }

    private void mergeBlockAnimationToFrames(CompiledBlockMoveAnimation
                                                     compiledBlockMoveAnimation, Vector3D target, int frame) {
        for (Vector3D newPosition : compiledBlockMoveAnimation.frames()) {
            frames.computeIfAbsent(frame, ignored -> new AnimationFrame(new HashMap<>())).currentToDestination().put(target, newPosition);
            frame++;
        }
    }

    public AnimationStatus getStatus() {
        if (compileCompleted && frames.isEmpty()) {
            return AnimationStatus.COMPLETED;
        }
        if (currentFrame.get() + 10 < currentCompiledFrame.get() || compileCompleted) {
            return AnimationStatus.READY_FOR_ANIMATION;
        } else {
            return AnimationStatus.NOT_READY_FOR_ANIMATION;
        }
    }

    public BlockSupplier<B> supplier() {
        return blockSupplier;
    }

    public enum AnimationStatus {
        COMPLETED,
        READY_FOR_ANIMATION,
        NOT_READY_FOR_ANIMATION
    }
}
