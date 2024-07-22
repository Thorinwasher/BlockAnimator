package dev.thorinwasher.blockanimator.blockanimations;

import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.PathCompletionSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
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
        Stream<Vector3D> points = compiledPathCompletion.stream().map(pathCompletion -> fromValue.scalarMultiply(1 - pathCompletion).add(to.scalarMultiply(pathCompletion)));
        return new CompiledBlockMoveAnimation(Stream.concat(points, Stream.of(to)).toList());
    }
}
