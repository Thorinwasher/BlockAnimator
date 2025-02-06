package dev.thorinwasher.blockanimator.api.animator;

import dev.thorinwasher.blockanimator.api.animation.Animation;
import dev.thorinwasher.blockanimator.api.animation.AnimationFrame;
import dev.thorinwasher.blockanimator.api.animation.BlockAnimationFrame;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Animator<B> {

    private final Animation<B> animation;
    private final BlockAnimator<B> blockAnimator;
    private @Nullable Runnable onCompletion;
    private boolean emergencyFinished;
    private final List<ImmutableVector3i> placed = new ArrayList<>();

    public Animator(Animation<B> animation, BlockAnimator<B> blockAnimator) {
        this.animation = animation;
        this.blockAnimator = blockAnimator;
    }

    public boolean nextTick() {
        if (emergencyFinished) {
            return true;
        }
        Animation.AnimationStatus status = animation.getStatus();
        if (status == Animation.AnimationStatus.COMPLETED) {
            Optional.ofNullable(onCompletion).ifPresent(Runnable::run);
            blockAnimator.finishAnimation(animation.supplier());
            return true;
        }
        if (status == Animation.AnimationStatus.COMPILING) {
            return false;
        }
        AnimationFrame frame = animation.getNext();
        BlockSupplier<B> supplier = animation.supplier();
        for (Map.Entry<ImmutableVector3i, BlockAnimationFrame> entry : frame.currentToDestination().entrySet()) {
            ImmutableVector3i identifier = entry.getKey();
            Vector3d position = entry.getValue().position();
            switch (entry.getValue().moveType()) {
                case PLACE -> {
                    blockAnimator.blockPlace(identifier, supplier);
                    placed.add(identifier);
                }
                case MOVE -> blockAnimator.blockMove(identifier, position, supplier, entry.getValue().transform());
                case DESTROY -> blockAnimator.blockDestroy(identifier);
            }
        }
        return false;
    }

    public void addOnCompletion(Runnable runnable) {
        this.onCompletion = runnable;
    }

    public void emergencyFinish() {
        this.emergencyFinished = true;
        List<ImmutableVector3i> toPlace = new ArrayList<>(animation.supplier().getPositions());
        toPlace.removeAll(placed);
        toPlace.forEach(animation.supplier()::placeBlock);
        if (onCompletion != null) {
            onCompletion.run();
        }

    }
}

