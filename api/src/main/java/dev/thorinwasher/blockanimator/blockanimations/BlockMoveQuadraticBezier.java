package dev.thorinwasher.blockanimator.blockanimations;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class BlockMoveQuadraticBezier implements BlockMoveAnimation {

    private final Vector3D from;
    private final double speed;
    private static final Random RANDOM = new Random();
    private final Supplier<Double> controlPointLengthSupplier;

    public BlockMoveQuadraticBezier(Vector3D from, double speed, Supplier<Double> controlPointLengthSupplier) {
        this.from = from;
        this.speed = speed;
        this.controlPointLengthSupplier = controlPointLengthSupplier;
    }

    @Override
    public CompiledBlockMoveAnimation compile(Vector3D to) {
        Vector3D delta = from.subtract(to);
        Vector3D deltaNorm = delta.normalize();
        Vector3D orthogonal1 = deltaNorm.orthogonal();
        Vector3D orthogonal2 = deltaNorm.crossProduct(orthogonal1);
        double randomRadians = RANDOM.nextDouble(0, 2 * Math.PI);
        Vector3D randomOrthogonal = orthogonal1.scalarMultiply(Math.cos(randomRadians)).add(orthogonal2.scalarMultiply(Math.sin(randomRadians)));
        Vector3D controlPoint = from.add(randomOrthogonal.scalarMultiply(controlPointLengthSupplier.get()));
        double pathLength = bezierCurveLength(from, to, controlPoint);
        int steps = (int) Math.ceil(pathLength / speed);
        return new CompiledBlockMoveAnimation(calculateBezierCurve(from, to, controlPoint, steps));
    }

    private static double bezierCurveLength(Vector3D from, Vector3D to, Vector3D controlPoint) {
        int steps = 100;
        List<Vector3D> bezierCurve = calculateBezierCurve(from, to, controlPoint, steps);
        Vector3D previous = null;
        double length = 0;
        for (Vector3D point : bezierCurve) {
            if(previous != null){
                length += previous.distance(point);
            }
            previous = point;
        }
        return length;
    }

    private static List<Vector3D> calculateBezierCurve(Vector3D from, Vector3D to, Vector3D controlPoint, int steps) {
        List<Vector3D> bezierCurve = new ArrayList<>(steps + 1);
        for (int step = 0; step < steps + 1; step++) {
            double time = (double) step / steps;
            double A = Math.pow(1 - time, 2);
            double B = 2 * (1 - time)*time;
            double C = Math.pow(time, 2);
            Vector3D vectorPoint = from.scalarMultiply(A).add(controlPoint.scalarMultiply(B)).add(to.scalarMultiply(C));
            bezierCurve.add(vectorPoint);
        }
        return bezierCurve;
    }
}
