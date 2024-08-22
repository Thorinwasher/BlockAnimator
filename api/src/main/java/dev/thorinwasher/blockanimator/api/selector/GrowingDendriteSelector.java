package dev.thorinwasher.blockanimator.api.selector;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.api.util.Directions;

import java.util.*;

public class GrowingDendriteSelector implements BlockSelector {

    private static final Random RANDOM = new Random();
    private final double growthDirectionAddChance;
    private final Map<ImmutableVector3i, Set<ImmutableVector3i>> growthMap = new HashMap<>();

    public GrowingDendriteSelector(double growthDirectionAddChance) {
        this.growthDirectionAddChance = growthDirectionAddChance;
    }

    @Override
    public CompiledBlockSelector compile(List<ImmutableVector3i> blocks) {

        LinkedList<List<ImmutableVector3i>> points = new LinkedList<>();
        points.add(newStartingSeed(blocks));
        Set<ImmutableVector3i> blockSet = new HashSet<>(blocks);
        while (growthMap.size() < blocks.size()) {
            points.add(nextStep(blockSet));
        }
        return new CompiledBlockSelector(points);
    }

    private List<ImmutableVector3i> nextStep(Set<ImmutableVector3i> blocks) {
        List<ImmutableVector3i> output = new ArrayList<>();
        Map<ImmutableVector3i, Set<ImmutableVector3i>> tempGrowthMap = new HashMap<>(growthMap);
        for (Map.Entry<ImmutableVector3i, Set<ImmutableVector3i>> entry : tempGrowthMap.entrySet()) {
            for (ImmutableVector3i direction : entry.getValue()) {
                ImmutableVector3i growth = entry.getKey().add(direction);
                if (!growthMap.containsKey(growth) && blocks.contains(growth)) {
                    output.add(growth);
                    growthMap.put(growth, new HashSet<>(Set.of(direction)));
                }
            }
            if (RANDOM.nextDouble() < growthDirectionAddChance && entry.getValue().size() < Directions.DIRECTIONS.size()) {
                List<ImmutableVector3i> possibleDirectionsToAdd = new ArrayList<>(Directions.DIRECTIONS);
                possibleDirectionsToAdd.removeAll(entry.getValue());
                growthMap.get(entry.getKey()).add(possibleDirectionsToAdd.get(RANDOM.nextInt(possibleDirectionsToAdd.size())));
            }
        }
        if (output.isEmpty()) {
            List<ImmutableVector3i> leftoverBlocks = blocks.stream()
                    .filter(Vector3d -> !growthMap.containsKey(Vector3d))
                    .toList();
            return newStartingSeed(leftoverBlocks);
        }
        return output;
    }

    private List<ImmutableVector3i> newStartingSeed(List<ImmutableVector3i> blocks) {
        ImmutableVector3i startingPoint = blocks.get(RANDOM.nextInt(blocks.size()));
        growthMap.put(startingPoint, new HashSet<>(Directions.DIRECTIONS));
        return new ArrayList<>(List.of(startingPoint));
    }
}
