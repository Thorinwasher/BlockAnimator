package dev.thorinwasher.blockanimator.api.selector;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.api.util.Directions;
import org.joml.Vector3d;

import java.util.*;

public class GrowingSelector implements BlockSelector {

    private static final Random RANDOM = new Random();
    private final Set<ImmutableVector3i> taken = new HashSet<>();
    private final Set<ImmutableVector3i> openSet = new HashSet<>();

    @Override
    public CompiledBlockSelector compile(List<ImmutableVector3i> blocks) {
        LinkedList<List<ImmutableVector3i>> output = new LinkedList<>();
        Set<ImmutableVector3i> blockset = new HashSet<>(blocks);
        while (taken.size() < blocks.size()) {
            output.add(nextStep(blockset, blocks));
        }
        return new CompiledBlockSelector(output);
    }

    private List<ImmutableVector3i> nextStep(Set<ImmutableVector3i> blocks, List<ImmutableVector3i> blockList) {
        if (openSet.isEmpty()) {
            openSet.add(getNewSeed(blockList).orElseThrow());
        }
        List<ImmutableVector3i> output = new ArrayList<>();
        List<ImmutableVector3i> temp = new ArrayList<>(openSet);
        openSet.clear();
        for (ImmutableVector3i block : temp) {
            for (ImmutableVector3i direction : Directions.DIRECTIONS) {
                ImmutableVector3i test = block.add(direction);
                if (taken.contains(test) || !blocks.contains(test)) {
                    continue;
                }
                openSet.add(test);
                taken.add(test);
                output.add(test);
            }
        }
        return output;
    }

    public Optional<ImmutableVector3i> getNewSeed(List<ImmutableVector3i> blocks) {
        List<ImmutableVector3i> temp = new ArrayList<>(blocks);
        temp.removeIf(taken::contains);
        return Optional.ofNullable(temp.get(RANDOM.nextInt(temp.size())));
    }
}
