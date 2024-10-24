package dev.thorinwasher.blockanimator.api.animation.hologram;

import dev.thorinwasher.blockanimator.api.animation.Animation;
import dev.thorinwasher.blockanimator.api.animation.AnimationFrame;
import dev.thorinwasher.blockanimator.api.animation.BlockAnimationFrame;
import dev.thorinwasher.blockanimator.api.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class HologramAnimation<B> implements Animation<B> {

    private final BlockSupplier<B> blockSupplier;
    private final HologramTransform hologramTransform;
    private final long maxTime;
    private final ImmutableVector3i hologramCenter;
    private final AtomicLong currentFrame = new AtomicLong();
    private final AtomicLong currentCompiledFrame = new AtomicLong();
    private final ConcurrentHashMap<Long, AnimationFrame> frames = new ConcurrentHashMap<>();
    private final int buffer;
    private boolean compileCompleted = false;

    /**
     * @param blockSupplier     Supplies the location of each block
     * @param hologramTransform What transformation to apply to the hologram
     * @param maxTime           How long should the hologram live
     * @param buffer            The minimum amount of compiled frames to have until animation start
     */
    public HologramAnimation(BlockSupplier<B> blockSupplier, HologramTransform hologramTransform, ImmutableVector3i hologramCenter, long maxTime, int buffer) {
        this.blockSupplier = blockSupplier;
        this.hologramCenter = hologramCenter;
        this.hologramTransform = hologramTransform;
        this.maxTime = maxTime;
        this.buffer = buffer;
    }

    @Override
    public void compile() {
        try {
            while (currentCompiledFrame.get() < maxTime) {
                Matrix4d transform = hologramTransform.getTransform(currentCompiledFrame.get());
                Quaterniond rotation = transform.getUnnormalizedRotation(new Quaterniond());
                Vector3d scale = transform.getScale(new Vector3d());
                Matrix4f blockTransformation = new Matrix4f().rotate(new Quaternionf(rotation)).scale(new Vector3f(scale));
                Map<ImmutableVector3i, BlockAnimationFrame> animationFrame = new HashMap<>();
                for (ImmutableVector3i blockPos : blockSupplier.getPositions()) {
                    Vector3d position = transform.transformPosition(blockPos.sub(hologramCenter).asVector3d()).add(hologramCenter.asVector3d());
                    animationFrame.put(blockPos, new BlockAnimationFrame(position, BlockMoveType.MOVE, blockTransformation));
                }
                frames.put(currentCompiledFrame.get(), new AnimationFrame(animationFrame));
                if (currentCompiledFrame.incrementAndGet() > currentFrame.get() + buffer * 2L) {
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
        long currentCompiled = currentCompiledFrame.get();
        long current = currentFrame.get();
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
