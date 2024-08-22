package dev.thorinwasher.blockanimator.api.algorithms;

import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ManhatanNearest {


    public static Optional<Vector3d> findClosestPosition(Vector3d closeTo, Predicate<Vector3d> validPositionFilter, int maxDistance) {
        for (int manhatanDistance = 1; manhatanDistance <= maxDistance; manhatanDistance++) {
            Optional<Vector3d> possibleCloseTo = findExactDistance(closeTo, validPositionFilter, manhatanDistance);
            if (possibleCloseTo.isPresent()) {
                return possibleCloseTo;
            }
        }
        return Optional.empty();
    }

    public static Optional<Vector3d> findExactDistance(Vector3d centerPoint, Predicate<Vector3d> filter, int manhatanDistance) {
        for (int x = 0; x <= manhatanDistance; x++) {
            int remainingDistanceAfterX = manhatanDistance - x;
            for (int y = 0; y <= remainingDistanceAfterX; y++) {
                int z = remainingDistanceAfterX - y;
                List<Vector3d> possiblePoints = getPossiblePoints(x, y, z);
                for (Vector3d possiblePoint : possiblePoints) {
                    Vector3d point = centerPoint.add(possiblePoint);
                    if (filter.test(point)) {
                        return Optional.of(point);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static List<Vector3d> getPossiblePoints(int x, int y, int z) {
        List<Vector3d> output = new ArrayList<>();
        for (int ix : getNegativeVariations(x)) {
            for(int iy : getNegativeVariations(y)) {
                for (int iz : getNegativeVariations(z)) {
                    output.add(new Vector3d(ix, iy, iz));
                }
            }
        }
        return output;
    }

    private static int[] getNegativeVariations(int number) {
        if (number == 0) {
            return new int[]{0};
        }
        return new int[]{-number, number};
    }
}
