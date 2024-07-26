package dev.thorinwasher.blockanimator.api.blockanimations;

import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.container.TwoTuple;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class BlockMoveLinear implements BlockMoveAnimation {
    private final Function<Vector3D, Vector3D> from;
    private final PathCompletionSupplier pathCompletionSupplier;

    public BlockMoveLinear(Function<Vector3D, Vector3D> from, PathCompletionSupplier pathCompletionSupplier) {
        this.pathCompletionSupplier = pathCompletionSupplier;
        this.from = from;
    }

    public BlockMoveLinear(Vector3D from, PathCompletionSupplier pathCompletionSupplier) {
        this(ignored -> from, pathCompletionSupplier);
    }

    @Override
    public CompiledBlockMoveAnimation compile(Vector3D to) {
        Vector3D fromValue = from.apply(to);
        List<Double> compiledPathCompletion = pathCompletionSupplier.compile(to.distance(fromValue));
        Stream<TwoTuple<Vector3D, BlockMoveType>> points = compiledPathCompletion.stream()
                .map(pathCompletion -> fromValue.scalarMultiply(1 - pathCompletion).add(to.scalarMultiply(pathCompletion)))
                .map(vector3D -> new TwoTuple<>(vector3D, BlockMoveType.MOVE));
        return new CompiledBlockMoveAnimation(Stream.concat(points, Stream.of(new TwoTuple<>(to, BlockMoveType.PLACE))).
                toList());
    }
}
