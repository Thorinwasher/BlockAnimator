package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.algorithms.ManhatanNearest;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveLinear;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.container.TwoTuple;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GrowingStructureAnimation<B> implements Animation<B> {

    private final BlockSupplier<B> blockSupplier;
    private final PathCompletionSupplier pathCompletionSupplier;
    private final ConcurrentHashMap<Integer, AnimationFrame> frames = new ConcurrentHashMap<>();
    private final Set<Vector3D> placedBlocks = new HashSet<>();
    private final Map<Vector3D, Integer> blocksCurrentlyAnimating = new HashMap<>();
    private final int buffer;
    private final Vector3D startingPoint;
    private AtomicInteger currentFrame = new AtomicInteger();
    private AtomicInteger currentCompiledFrame = new AtomicInteger();
    private boolean compileCompleted;

    public GrowingStructureAnimation(BlockSupplier<B> blockSupplier, Vector3D startingPoint, PathCompletionSupplier pathCompletionSupplier, int buffer) {
        if (!blockSupplier.getPositions().contains(startingPoint)) {
            throw new IllegalArgumentException("Invalid starting point");
        }
        this.blockSupplier = blockSupplier;
        this.pathCompletionSupplier = pathCompletionSupplier;
        this.startingPoint = startingPoint;
        this.buffer = buffer;
    }

    private Vector3D getBlockFrom(Vector3D blockTo) {
        return ManhatanNearest.findClosestPosition(blockTo, placedBlocks::contains, 4).orElse(blockTo);
    }

    @Override
    public void compile() {
        try {
            Set<Vector3D> blocksLeft = new HashSet<>(blockSupplier.getPositions());
            BlockMoveAnimation blockMoveAnimation = new BlockMoveLinear(this::getBlockFrom, pathCompletionSupplier);
            frames.computeIfAbsent(0, ignored -> new AnimationFrame(new HashMap<>())).currentToDestination().put(startingPoint, new TwoTuple<>(startingPoint, BlockMoveType.PLACE));
            placedBlocks.add(startingPoint);

            while (!blocksLeft.isEmpty()) {
                int current = currentCompiledFrame.get();
                Set<Vector3D> placedBlocksTemp = new HashSet<>(placedBlocks);
                placedBlocksTemp.removeAll(blocksCurrentlyAnimating.keySet());
                Set<Vector3D> positionsToAnimate = findCloseBlocksNotInitiated(placedBlocksTemp, blocksLeft);
                if (positionsToAnimate.isEmpty() && blocksCurrentlyAnimating.isEmpty()) {
                    positionsToAnimate.addAll(blocksLeft);
                }
                blocksLeft.removeAll(positionsToAnimate);
                for (Vector3D positionToAnimate : positionsToAnimate) {
                    CompiledBlockMoveAnimation compiledBlockMoveAnimation = blockMoveAnimation.compile(positionToAnimate);
                    int endFrame = compiledBlockMoveAnimation.frames().size() + current - 1;
                    blocksCurrentlyAnimating.put(positionToAnimate, endFrame);
                    Animation.mergeBlockAnimationToFrames(compiledBlockMoveAnimation, frames, positionToAnimate, current);
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

    private Set<Vector3D> findCloseBlocksNotInitiated(Set<Vector3D> placedBlocks, Set<Vector3D> blocksToBeAnimated) {
        Set<Vector3D> output = new HashSet<>(blocksToBeAnimated);
        output.removeIf(vector3D -> ManhatanNearest.findExactDistance(vector3D, placedBlocks::contains, 1).isEmpty());
        if (!output.isEmpty() || !blocksCurrentlyAnimating.isEmpty()) {
            return output;
        }
        output.addAll(blocksToBeAnimated);
        output.removeIf(vector3D -> ManhatanNearest.findClosestPosition(vector3D, placedBlocks::contains, 4).isEmpty());
        return output;
    }
}
