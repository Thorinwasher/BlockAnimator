package dev.thorinwasher.blockanimator.blockanimations;

import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.FixedStepsPathCompletionSupplier;
import dev.thorinwasher.blockanimator.blockanimations.pathcompletion.PathCompletionSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockMoveQuadraticBezier implements BlockMoveAnimation {

    private final Function<Vector3D, Vector3D> from;
    private final PathCompletionSupplier pathCompletionSupplier;
    private static final Random RANDOM = new Random();
    private final Supplier<Double> controlPointLengthSupplier;

    public BlockMoveQuadraticBezier(Function<Vector3D, Vector3D> from, PathCompletionSupplier pathCompletionSupplier, Supplier<Double> controlPointLengthSupplier) {
        this.from = from;
        this.pathCompletionSupplier = pathCompletionSupplier;
        this.controlPointLengthSupplier = controlPointLengthSupplier;
    }

    public BlockMoveQuadraticBezier(Vector3D from, PathCompletionSupplier pathCompletionSupplier, Supplier<Double> controlPointLengthSupplier) {
        this(ignored -> from, pathCompletionSupplier, controlPointLengthSupplier);
    }

    @Override
    public CompiledBlockMoveAnimation compile(Vector3D to) {
        Vector3D fromValue = from.apply(to);
        Vector3D delta = fromValue.subtract(to);
        Vector3D deltaNorm = delta.normalize();
        Vector3D orthogonal1 = deltaNorm.orthogonal();
        Vector3D orthogonal2 = deltaNorm.crossProduct(orthogonal1);
        double randomRadians = RANDOM.nextDouble(0, 2 * Math.PI);
        Vector3D randomOrthogonal = orthogonal1.scalarMultiply(Math.cos(randomRadians)).add(orthogonal2.scalarMultiply(Math.sin(randomRadians)));
        Vector3D controlPoint = fromValue.add(randomOrthogonal.scalarMultiply(controlPointLengthSupplier.get()));
        double pathLength = bezierCurveLength(fromValue, to, controlPoint);
        List<Double> steps = pathCompletionSupplier.compile(pathLength);
        return new CompiledBlockMoveAnimation(calculateBezierCurve(fromValue, to, controlPoint, steps));
    }

    private static double bezierCurveLength(Vector3D from, Vector3D to, Vector3D controlPoint) {
        List<Double> steps = new FixedStepsPathCompletionSupplier(20).compile(1D);
        List<Vector3D> bezierCurve = calculateBezierCurve(from, to, controlPoint, steps);
        Vector3D previous = null;
        double length = 0;
        for (Vector3D point : bezierCurve) {
            if (previous != null) {
                length += previous.distance(point);
            }
            previous = point;
        }
        return length;
    }

    private static List<Vector3D> calculateBezierCurve(Vector3D from, Vector3D to, Vector3D controlPoint, List<Double> steps) {
        List<Vector3D> bezierCurve = new ArrayList<>(steps.size());
        for (double time : steps) {
            double A = Math.pow(1 - time, 2);
            double B = 2 * (1 - time) * time;
            double C = Math.pow(time, 2);
            Vector3D vectorPoint = from.scalarMultiply(A).add(controlPoint.scalarMultiply(B)).add(to.scalarMultiply(C));
            bezierCurve.add(vectorPoint);
        }
        bezierCurve.add(to);
        return bezierCurve;
    }
}
