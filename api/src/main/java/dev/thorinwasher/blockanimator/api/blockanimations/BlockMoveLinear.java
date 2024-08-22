package dev.thorinwasher.blockanimator.api.blockanimations;

import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.container.TwoTuple;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockMoveLinear implements BlockMoveAnimation {
    private final Function<Vector3d, Vector3d> from;
    private final PathCompletionSupplier pathCompletionSupplier;

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
        Stream<TwoTuple<Vector3d, BlockMoveType>> points = compiledPathCompletion.stream()
                .map(pathCompletion -> fromValue.mul(1 - pathCompletion).add(new Vector3d(to).mul(pathCompletion)))
                .map(Vector3d -> new TwoTuple<>(Vector3d, BlockMoveType.MOVE));
        return new CompiledBlockMoveAnimation(Stream.concat(points, Stream.of(new TwoTuple<>(to, BlockMoveType.PLACE))).
                toList());
    }
}
