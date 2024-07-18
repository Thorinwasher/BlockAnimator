package dev.thorinwasher.blockanimator.selector;

import dev.thorinwasher.blockanimator.container.TwoTuple;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;

public class RandomSpherical implements BlockSelector {

    private final static Random RANDOM = new Random();

    @Override
    public CompiledBlockSelector compile(List<Vector3D> blocks) {
        Vector3D vectorSum = new Vector3D(0, 0, 0);
        for (Vector3D vector3D : blocks) {
            vectorSum = vector3D.add(vectorSum);
        }
        Vector3D centerPoint = vectorSum.scalarMultiply(1 / ((double) blocks.size()));
        List<TwoTuple<Vector3D, Integer>> scoredValues = new ArrayList<>(blocks.stream().map(vec -> evaluateRandomlyFromCenter(vec, centerPoint)).toList());
        Collections.shuffle(scoredValues, RANDOM);
        scoredValues.sort(Comparator.comparing(TwoTuple::second));
        Deque<Vector3D> queue = new LinkedList<>();
        scoredValues.forEach(tuple -> queue.add(tuple.first()));
        return new CompiledBlockSelector(queue);
    }

    private TwoTuple<Vector3D, Integer> evaluateRandomlyFromCenter(Vector3D vec, Vector3D centerPoint) {
        return new TwoTuple<>(vec, (int) vec.distance(centerPoint) + RANDOM.nextInt(0, 2));
    }

}
