package dev.thorinwasher.blockanimator.api.blockanimations;

import dev.thorinwasher.blockanimator.api.animation.BlockAnimationFrame;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.FixedStepsPathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.blockanimations.pathcompletion.PathCompletionSupplier;
import dev.thorinwasher.blockanimator.api.blockanimations.transformation.BlockTransformation;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.GeometryUtils;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockMoveQuadraticBezier implements BlockMoveAnimation {

    private final Function<Vector3d, Vector3d> from;
    private final PathCompletionSupplier pathCompletionSupplier;
    private static final Random RANDOM = new Random();
    private final Supplier<Double> controlPointLengthSupplier;
    private BlockTransformation blockTransform;

    public BlockMoveQuadraticBezier(Function<Vector3d, Vector3d> from, PathCompletionSupplier pathCompletionSupplier, Supplier<Double> controlPointLengthSupplier) {
        this.from = from;
        this.pathCompletionSupplier = pathCompletionSupplier;
        this.controlPointLengthSupplier = controlPointLengthSupplier;
    }

    public BlockMoveQuadraticBezier(Vector3d from, PathCompletionSupplier pathCompletionSupplier, Supplier<Double> controlPointLengthSupplier) {
        this(ignored -> from, pathCompletionSupplier, controlPointLengthSupplier);
    }

    @Override
    public CompiledBlockMoveAnimation compile(ImmutableVector3i identifier) {
        Vector3d to = identifier.asVector3d();
        Vector3d fromValue = from.apply(to);
        Vector3f deltaNorm = new Vector3f(new Vector3d(fromValue).sub(to).normalize());
        Vector3f orthogonal1 = new Vector3f();
        Vector3f orthogonal2 = new Vector3f();
        GeometryUtils.perpendicular(deltaNorm, orthogonal1, orthogonal2);
        double randomRadians = RANDOM.nextDouble(0, 2 * Math.PI);
        Vector3d randomOrthogonal = new Vector3d(orthogonal1).mul(Math.cos(randomRadians)).add(new Vector3d(orthogonal2).mul(Math.sin(randomRadians)));
        Vector3d controlPoint = new Vector3d(fromValue).add(randomOrthogonal.mul(controlPointLengthSupplier.get()));
        double pathLength = bezierCurveLength(fromValue, to, controlPoint);
        List<Double> steps = pathCompletionSupplier.compile(pathLength);
        return new CompiledBlockMoveAnimation(calculateBezierCurve(fromValue, to, controlPoint, steps));
    }

    private double bezierCurveLength(Vector3d from, Vector3d to, Vector3d controlPoint) {
        List<Double> steps = new FixedStepsPathCompletionSupplier(20).compile(1D);
        List<BlockAnimationFrame> bezierCurve = calculateBezierCurve(from, to, controlPoint, steps);
        Vector3d previous = null;
        double length = 0;
        for (BlockAnimationFrame point : bezierCurve) {
            if (previous != null) {
                length += previous.distance(point.position());
            }
            previous = point.position();
        }
        return length;
    }

    private List<BlockAnimationFrame> calculateBezierCurve(Vector3d from, Vector3d to, Vector3d controlPoint, List<Double> steps) {
        List<BlockAnimationFrame> bezierCurve = new ArrayList<>(steps.size());
        for (double time : steps) {
            double A = Math.pow(1 - time, 2);
            double B = 2 * (1 - time) * time;
            double C = Math.pow(time, 2);
            Vector3d vectorPoint = new Vector3d(from).mul(A).add(new Vector3d(controlPoint).mul(B)).add(new Vector3d(to).mul(C));
            Matrix4f matrix4f = blockTransform == null ? null : blockTransform.getTransform(time);
            bezierCurve.add(new BlockAnimationFrame(vectorPoint, BlockMoveType.MOVE, matrix4f));
        }
        bezierCurve.add(new BlockAnimationFrame(to, BlockMoveType.PLACE, new Matrix4f()));
        return bezierCurve;
    }
}
