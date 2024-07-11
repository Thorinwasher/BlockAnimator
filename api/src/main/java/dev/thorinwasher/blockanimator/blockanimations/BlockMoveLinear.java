package dev.thorinwasher.blockanimator.blockanimations;

import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.PathCompletionSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;
import java.util.stream.Stream;

public class BlockMoveLinear implements BlockMoveAnimation {
    private final Vector3D from;
    private final PathCompletionSupplier pathCompletionSupplier;

    public BlockMoveLinear(Vector3D from, PathCompletionSupplier pathCompletionSupplier) {
        this.pathCompletionSupplier = pathCompletionSupplier;
        this.from = from;
    }

    @Override
    public CompiledBlockMoveAnimation compile(Vector3D to) {
        List<Double> compiledPathCompletion = pathCompletionSupplier.compile(to.distance(from));
        Vector3D travelNorm = to.subtract(from).normalize();
        Stream<Vector3D> points = compiledPathCompletion.stream().map(pathCompletion -> from.add(travelNorm.scalarMultiply(pathCompletion)));
        return new CompiledBlockMoveAnimation(Stream.concat(points, Stream.of(to)).toList());
    }
}
