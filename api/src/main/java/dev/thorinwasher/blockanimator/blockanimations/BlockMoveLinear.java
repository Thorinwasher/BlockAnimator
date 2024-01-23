package dev.thorinwasher.blockanimator.blockanimations;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;

public class BlockMoveLinear implements BlockMoveAnimation {
    private final double velocity;
    private final Vector3D from;

    public BlockMoveLinear(Vector3D from, double velocity) {
        this.velocity = velocity;
        this.from = from;
    }

    @Override
    public CompiledBlockMoveAnimation compile(Vector3D to) {
        double travelDistance = to.distance(from);
        int stepsExceptLast = (int) Math.floor(travelDistance / velocity);
        Vector3D travel = to.subtract(from);
        Vector3D add = travel.normalize().scalarMultiply(velocity);
        ArrayList<Vector3D> frames = new ArrayList<>(stepsExceptLast + 2);
        frames.add(from);
        for (int i = 0; i < stepsExceptLast; i++) {
            frames.add(frames.get(frames.size() - 1).add(add));
        }
        frames.add(to);
        return new CompiledBlockMoveAnimation(frames);
    }
}
