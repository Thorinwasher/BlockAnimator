package dev.thorinwasher.blockanimator.api.blockanimations;

import dev.thorinwasher.blockanimator.api.animation.BlockAnimationFrame;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.blockanimations.transformation.BlockTransformation;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BlockMoveLinear implements BlockMoveAnimation {
    private final Function<Vector3d, Vector3d> from;
    private final PathCompletionSupplier pathCompletionSupplier;
    private BlockTransformation blockTransform;

    public BlockMoveLinear(Function<Vector3d, Vector3d> from, PathCompletionSupplier pathCompletionSupplier) {
        this.pathCompletionSupplier = pathCompletionSupplier;
        this.from = from;
    }

    public BlockMoveLinear(Vector3d from, PathCompletionSupplier pathCompletionSupplier) {
        this(ignored -> from, pathCompletionSupplier);
    }

    @Override
    public CompiledBlockMoveAnimation compile(ImmutableVector3i identifier) {
        Vector3d to = identifier.asVector3d();
        Vector3d fromValue = from.apply(to);
        List<Double> compiledPathCompletion = pathCompletionSupplier.compile(to.distance(fromValue));
        List<BlockAnimationFrame> frames = new ArrayList<>();
        for (double pathCompletion : compiledPathCompletion) {
            Vector3d point = new Vector3d(fromValue).mul(1 - pathCompletion).add(new Vector3d(to).mul(pathCompletion));
            Matrix4f transform = this.blockTransform == null ? null : blockTransform.getTransform(pathCompletion);
            frames.add(new BlockAnimationFrame(point, BlockMoveType.MOVE, transform));
        }
        frames.add(new BlockAnimationFrame(to, BlockMoveType.PLACE, new Matrix4f()));
        return new CompiledBlockMoveAnimation(frames);
    }
}
