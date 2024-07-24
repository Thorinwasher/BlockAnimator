package dev.thorinwasher.blockanimator.animator;

import dev.thorinwasher.blockanimator.animation.Animation;
import dev.thorinwasher.blockanimator.animation.AnimationFrame;
import dev.thorinwasher.blockanimator.blockanimations.BlockMoveType;
import dev.thorinwasher.blockanimator.container.TwoTuple;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public class Animator<B> {

    private final Animation<B> animation;
    private final BlockAnimator<B> blockAnimator;
    private @Nullable Runnable onCompletion;

    public Animator(Animation<B> animation, BlockAnimator<B> blockAnimator) {
        this.animation = animation;
        this.blockAnimator = blockAnimator;
    }

    public boolean nextTick() {
        Animation.AnimationStatus status = animation.getStatus();
        if (status == Animation.AnimationStatus.COMPLETED) {
            Optional.ofNullable(onCompletion).ifPresent(Runnable::run);
            blockAnimator.finishAnimation(animation.supplier());
            return true;
        }
        if (status == Animation.AnimationStatus.COMPILING_TO_BUFFER) {
            return false;
        }
        AnimationFrame frame = animation.getNext();
        BlockSupplier<B> supplier = animation.supplier();
        for (Map.Entry<Vector3D, TwoTuple<Vector3D, BlockMoveType>> entry : frame.currentToDestination().entrySet()) {
            Vector3D identifier = entry.getKey();
            Vector3D position = entry.getValue().first();
            switch (entry.getValue().second()) {
                case PLACE -> blockAnimator.blockPlace(identifier, supplier);
                case MOVE -> blockAnimator.blockMove(identifier, position, supplier);
                case DESTROY -> blockAnimator.blockDestroy(identifier);
            }
        }
        return false;
    }

    public void addOnCompletion(Runnable runnable) {
        this.onCompletion = runnable;
    }
}

