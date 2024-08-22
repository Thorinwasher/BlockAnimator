package dev.thorinwasher.blockanimator.api.animation;

import dev.thorinwasher.blockanimator.api.algorithms.ManhatanNearest;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.api.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.selector.BlockSelector;
import dev.thorinwasher.blockanimator.api.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SequentialAnimation<B> implements Animation<B> {

    private final BlockSupplier<B> blockSupplier;
    private final PathCompletionSupplier pathCompletionSupplier;
    private final ConcurrentHashMap<Integer, AnimationFrame> frames = new ConcurrentHashMap<>();
    private final Set<ImmutableVector3i> placedBlocks = new HashSet<>();
    private final Map<ImmutableVector3i, Integer> blocksCurrentlyAnimating = new HashMap<>();
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

    private Vector3d getBlockFrom(Vector3d blockTo) {
        return ManhatanNearest.findClosestPosition(blockTo, placedBlocks::contains, 4).orElse(blockTo);
    }

    @Override
    public void compile() {
        try {
            BlockMoveAnimation blockMoveAnimation = new BlockMoveLinear(this::getBlockFrom, pathCompletionSupplier);
            CompiledBlockSelector compiledBlockSelector = blockSelector.compile(blockSupplier.getPositions());
            List<ImmutableVector3i> next = compiledBlockSelector.next();
            while (next != null && !next.isEmpty()) {
                int current = currentCompiledFrame.get();
                if (blocksCurrentlyAnimating.isEmpty()) {
                    for (ImmutableVector3i positionToAnimate : next) {
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
        List<ImmutableVector3i> toRemove = new ArrayList<>();
        for (Map.Entry<ImmutableVector3i, Integer> entry : blocksCurrentlyAnimating.entrySet()) {
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
