package dev.thorinwasher.blockanimator.api.selector;

import dev.thorinwasher.blockanimator.api.container.TwoTuple;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.*;

public class RandomSpherical implements BlockSelector {

    private final static Random RANDOM = new Random();

    @Override
    public CompiledBlockSelector compile(List<ImmutableVector3i> blocks) {
        ImmutableVector3i vectorSum = new ImmutableVector3i(0, 0, 0);
        for (ImmutableVector3i vector3d : blocks) {
            vectorSum = vectorSum.add(vectorSum);
        }
        Vector3d centerPoint = vectorSum.asVector3d().mul(1 / ((double) blocks.size()));
        List<TwoTuple<ImmutableVector3i, Integer>> scoredValues = new ArrayList<>(blocks.stream().map(vec -> evaluateRandomlyFromCenter(vec, centerPoint)).toList());
        Collections.shuffle(scoredValues, RANDOM);
        scoredValues.sort(Comparator.comparing(TwoTuple::second));
        Queue<List<ImmutableVector3i>> queue = new LinkedList<>();
        scoredValues.forEach(tuple -> queue.add(List.of(tuple.first())));
        return new CompiledBlockSelector(queue);
    }

    private TwoTuple<ImmutableVector3i, Integer> evaluateRandomlyFromCenter(ImmutableVector3i vec, Vector3d centerPoint) {
        return new TwoTuple<>(vec, (int) vec.asVector3d().distance(centerPoint) + RANDOM.nextInt(0, 2));
    }

}
