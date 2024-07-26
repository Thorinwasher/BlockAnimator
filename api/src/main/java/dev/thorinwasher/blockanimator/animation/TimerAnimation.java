package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.timer.BlockTimer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TimerAnimation<B> implements Animation<B> {

    private final BlockMoveAnimation blockMoveAnimation;
    private final BlockSupplier<B> blockSupplier;
    private final BlockTimer blockTimer;
    private final BlockSelector blockSelector;

    private final ConcurrentHashMap<Integer, AnimationFrame> frames = new ConcurrentHashMap<>();
    private boolean compileCompleted = false;
    private AtomicInteger currentFrame = new AtomicInteger();
    private AtomicInteger currentCompiledFrame = new AtomicInteger();
    private final int framesBuffer;

    public TimerAnimation(BlockSelector blockSelector, BlockMoveAnimation blockMoveAnimation,
                          BlockSupplier<B> blockSupplier, BlockTimer blockTimer, int framesBuffer) {
        this.blockSelector = blockSelector;
        this.blockMoveAnimation = blockMoveAnimation;
        this.blockSupplier = blockSupplier;
        this.blockTimer = blockTimer;
        this.framesBuffer = framesBuffer;
    }

    @Override
    public void compile() {
        try {
            List<Vector3D> totalBlocks = blockSupplier.getPositions();
            int totalBlockAmount = totalBlocks.size();
            CompiledBlockSelector blockSelector = this.blockSelector.compile(totalBlocks);
            while (blockTimer.hasNext(totalBlockAmount)) {
                for (Vector3D target : blockTimer.fetch(blockSelector, totalBlockAmount)) {
                    CompiledBlockMoveAnimation compiledBlockMoveAnimation = blockMoveAnimation.compile(target);
                    Animation.mergeBlockAnimationToFrames(compiledBlockMoveAnimation, frames, target, currentCompiledFrame.get());
                }
                if (currentCompiledFrame.incrementAndGet() > currentFrame.get() + framesBuffer * 2) {
                    Thread.sleep(50);
                }
            }
            this.compileCompleted = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
        if ((framesBuffer < currentCompiled && currentCompiled > current + 1) || compileCompleted) {
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
