package dev.thorinwasher.blockanimator.selector;

import dev.thorinwasher.blockanimator.util.Directions;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;

public class GrowingDendriteSelector implements BlockSelector {

    private static final Random RANDOM = new Random();
    private final double growthDirectionAddChance;
    private final Map<Vector3D, Set<Vector3D>> growthMap = new HashMap<>();

    public GrowingDendriteSelector(double growthDirectionAddChance) {
        this.growthDirectionAddChance = growthDirectionAddChance;
    }

    @Override
    public CompiledBlockSelector compile(List<Vector3D> blocks) {

        LinkedList<List<Vector3D>> points = new LinkedList<>();
        points.add(newStartingSeed(blocks));
        Set<Vector3D> blockSet = new HashSet<>(blocks);
        while (growthMap.size() < blocks.size()) {
            points.add(nextStep(blockSet));
        }
        return new CompiledBlockSelector(points);
    }

    private List<Vector3D> nextStep(Set<Vector3D> blocks) {
        List<Vector3D> output = new ArrayList<>();
        Map<Vector3D, Set<Vector3D>> tempGrowthMap = new HashMap<>(growthMap);
        for (Map.Entry<Vector3D, Set<Vector3D>> entry : tempGrowthMap.entrySet()) {
            for (Vector3D direction : entry.getValue()) {
                Vector3D growth = entry.getKey().add(direction);
                if (!growthMap.containsKey(growth) && blocks.contains(growth)) {
                    output.add(growth);
                    growthMap.put(growth, new HashSet<>(Set.of(direction)));
                }
            }
            if (RANDOM.nextDouble() < growthDirectionAddChance && entry.getValue().size() < Directions.DIRECTIONS.size()) {
                List<Vector3D> possibleDirectionsToAdd = new ArrayList<>(Directions.DIRECTIONS);
                possibleDirectionsToAdd.removeAll(entry.getValue());
                growthMap.get(entry.getKey()).add(possibleDirectionsToAdd.get(RANDOM.nextInt(possibleDirectionsToAdd.size())));
            }
        }
        if (output.isEmpty()) {
            List<Vector3D> leftoverBlocks = blocks.stream()
                    .filter(vector3D -> !growthMap.containsKey(vector3D))
                    .toList();
            return newStartingSeed(leftoverBlocks);
        }
        return output;
    }

    private List<Vector3D> newStartingSeed(List<Vector3D> blocks) {
        Vector3D startingPoint = blocks.get(RANDOM.nextInt(blocks.size()));
        growthMap.put(startingPoint, new HashSet<>(Directions.DIRECTIONS));
        return new ArrayList<>(List.of(startingPoint));
    }
}
