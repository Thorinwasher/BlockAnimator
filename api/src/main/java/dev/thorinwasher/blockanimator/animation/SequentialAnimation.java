package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.algorithms.ManhatanNearest;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SequentialAnimation<B> implements Animation<B> {

    private final BlockSupplier<B> blockSupplier;
    private final PathCompletionSupplier pathCompletionSupplier;
    private final ConcurrentHashMap<Integer, AnimationFrame> frames = new ConcurrentHashMap<>();
    private final Set<Vector3D> placedBlocks = new HashSet<>();
    private final Map<Vector3D, Integer> blocksCurrentlyAnimating = new HashMap<>();
    private final int buffer;
    private final BlockSelector blockSelector;
    private AtomicInteger currentFrame = new AtomicInteger();
    private AtomicInteger currentCompiledFrame = new AtomicInteger();
    private boolean compileCompleted;

    public SequentialAnimation(BlockSupplier<B> blockSupplier, PathCompletionSupplier pathCompletionSupplier, BlockSelector blockSelector, int buffer) {
        this.blockSupplier = blockSupplier;
        this.pathCompletionSupplier = pathCompletionSupplier;
        this.blockSelector = blockSelector;
        this.buffer = buffer;
    }

    private Vector3D getBlockFrom(Vector3D blockTo) {
        return ManhatanNearest.findClosestPosition(blockTo, placedBlocks::contains, 4).orElse(blockTo);
    }

    @Override
    public void compile() {
        try {
            BlockMoveAnimation blockMoveAnimation = new BlockMoveLinear(this::getBlockFrom, pathCompletionSupplier);
            CompiledBlockSelector compiledBlockSelector = blockSelector.compile(blockSupplier.getPositions());
            List<Vector3D> next = compiledBlockSelector.next();
            while (next != null && !next.isEmpty()) {
                int current = currentCompiledFrame.get();
                if (blocksCurrentlyAnimating.isEmpty()) {
                    for (Vector3D positionToAnimate : next) {
                        CompiledBlockMoveAnimation compiledBlockMoveAnimation = blockMoveAnimation.compile(positionToAnimate);
                        int endFrame = compiledBlockMoveAnimation.frames().size() + current - 1;
                        blocksCurrentlyAnimating.put(positionToAnimate, endFrame);
                        Animation.mergeBlockAnimationToFrames(compiledBlockMoveAnimation, frames, positionToAnimate, current);
                    }
                    next = compiledBlockSelector.next();
                }
                if (currentCompiledFrame.incrementAndGet() > currentFrame.get() + buffer * 2) {
                    Thread.sleep(50);
                }
                updatePlacedBlocks(current);
            }
            this.compileCompleted = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void updatePlacedBlocks(int current) {
        List<Vector3D> toRemove = new ArrayList<>();
        for (Map.Entry<Vector3D, Integer> entry : blocksCurrentlyAnimating.entrySet()) {
            if (entry.getValue() <= current + 1) {
                toRemove.add(entry.getKey());
            }
        }
        toRemove.forEach(blocksCurrentlyAnimating::remove);
        placedBlocks.addAll(toRemove);
    }

    @Override
    public AnimationFrame getNext() {
        return frames.remove(currentFrame.getAndIncrement());
    }

    @Override
    public AnimationStatus getStatus() {
        if (compileCompleted && frames.isEmpty()) {
            return AnimationStatus.COMPLETED;
        }
        int currentCompiled = currentCompiledFrame.get();
        int current = currentFrame.get();
        if ((buffer < currentCompiled && currentCompiled > current + 1) || compileCompleted) {
            return AnimationStatus.READY_FOR_ANIMATION;
        } else {
            return AnimationStatus.COMPILING_TO_BUFFER;
        }
    }

    @Override
    public BlockSupplier<B> supplier() {
        return blockSupplier;
    }
}
