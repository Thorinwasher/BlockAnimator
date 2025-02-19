package dev.thorinwasher.blockanimator.api.animation;

import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.api.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.api.selector.BlockSelector;
import dev.thorinwasher.blockanimator.api.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.api.timer.BlockTimer;

import java.util.List;
import java.util.Map;
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
            List<ImmutableVector3i> totalBlocks = blockSupplier.getPositions();
            int totalBlockAmount = totalBlocks.size();
            CompiledBlockSelector blockSelector = this.blockSelector.compile(totalBlocks);
            while (blockTimer.hasNext(totalBlockAmount)) {
                for (ImmutableVector3i target : blockTimer.fetch(blockSelector, totalBlockAmount)) {
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
        AnimationFrame frame = frames.remove(currentFrame.getAndIncrement());
        return frame == null ? new AnimationFrame(Map.of()) : frame;
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
            return AnimationStatus.COMPILING;
        }
    }

    @Override
    public BlockSupplier<B> supplier() {
        return blockSupplier;
    }
}
