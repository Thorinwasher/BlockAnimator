package dev.thorinwasher.blockanimator;

import dev.thorinwasher.blockanimator.blockanimations.BlockMoveAnimation;
import dev.thorinwasher.blockanimator.blockanimations.CompiledBlockMoveAnimation;
import dev.thorinwasher.blockanimator.selector.BlockSelector;
import dev.thorinwasher.blockanimator.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.timer.BlockTimer;
import dev.thorinwasher.blockanimator.timer.CompiledBlockTimer;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Animation<B>(BlockSelector blockSelector, BlockMoveAnimation blockMoveAnimation,
                           BlockSupplier<B> blockSupplier, BlockTimer blockTimer) {

    public CompiledAnimation<B> compile() {
        CompiledBlockSelector blockSelector = blockSelector().compile(blockSupplier().getPositions());
        CompiledBlockTimer blockTimer = blockTimer().compile(blockSupplier().getPositions().size());
        List<Map<Vector3D, Vector3D>> frames = new ArrayList<>();
        int tick = 0;
        for (int fetchAmount : blockTimer.frames()) {
            for (int count = 0; count < fetchAmount; count++) {
                Vector3D target = blockSelector.next();
                CompiledBlockMoveAnimation compiledBlockMoveAnimation = blockMoveAnimation().compile(target);
                mergeBlockAnimationToFrames(frames, compiledBlockMoveAnimation, target, tick);
            }
            tick++;
        }
        return new CompiledAnimation<>(frames.stream().map(AnimationFrame::new).toList(), blockSupplier());
    }

    private void mergeBlockAnimationToFrames(List<Map<Vector3D, Vector3D>> frames, CompiledBlockMoveAnimation compiledBlockMoveAnimation, Vector3D target, int tick) {
        for (Vector3D newPosition : compiledBlockMoveAnimation.frames()) {
            if (frames.size() <= tick) {
                frames.add(new HashMap<>());
            }
            frames.get(tick).put(target, newPosition);
            tick++;
        }
    }
}
