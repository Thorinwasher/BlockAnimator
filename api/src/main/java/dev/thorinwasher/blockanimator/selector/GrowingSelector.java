package dev.thorinwasher.blockanimator.selector;

import dev.thorinwasher.blockanimator.util.Directions;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;

public class GrowingSelector implements BlockSelector {

    private static final Random RANDOM = new Random();
    private final Set<Vector3D> taken = new HashSet<>();
    private final Set<Vector3D> openSet = new HashSet<>();

    @Override
    public CompiledBlockSelector compile(List<Vector3D> blocks) {
        LinkedList<List<Vector3D>> output = new LinkedList<>();
        Set<Vector3D> blockset = new HashSet<>(blocks);
        while (taken.size() < blocks.size()) {
            output.add(nextStep(blockset, blocks));
        }
        return new CompiledBlockSelector(output);
    }

    private List<Vector3D> nextStep(Set<Vector3D> blocks, List<Vector3D> blockList) {
        if (openSet.isEmpty()) {
            openSet.add(getNewSeed(blockList).orElseThrow());
        }
        List<Vector3D> output = new ArrayList<>();
        List<Vector3D> temp = new ArrayList<>(openSet);
        openSet.clear();
        for (Vector3D block : temp) {
            for (Vector3D direction : Directions.DIRECTIONS) {
                Vector3D test = block.add(direction);
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

    public Optional<Vector3D> getNewSeed(List<Vector3D> blocks) {
        List<Vector3D> temp = new ArrayList<>(blocks);
        temp.removeIf(taken::contains);
        return Optional.ofNullable(temp.get(RANDOM.nextInt(temp.size())));
    }
}
