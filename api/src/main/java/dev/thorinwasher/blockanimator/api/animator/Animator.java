package dev.thorinwasher.blockanimator.api.animator;

import dev.thorinwasher.blockanimator.api.animation.Animation;
import dev.thorinwasher.blockanimator.api.animation.AnimationFrame;
import dev.thorinwasher.blockanimator.api.animation.BlockAnimationFrame;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

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
        blockAnimator.tick();
        for (Map.Entry<ImmutableVector3i, BlockAnimationFrame> entry : frame.currentToDestination().entrySet()) {
            ImmutableVector3i identifier = entry.getKey();
            Vector3d position = entry.getValue().position();
            switch (entry.getValue().moveType()) {
                case PLACE -> blockAnimator.blockPlace(identifier, supplier);
                case MOVE -> blockAnimator.blockMove(identifier, position, supplier, entry.getValue().transform());
                case DESTROY -> blockAnimator.blockDestroy(identifier);
            }
        }
        return false;
    }

    public void addOnCompletion(Runnable runnable) {
        this.onCompletion = runnable;
    }
}

