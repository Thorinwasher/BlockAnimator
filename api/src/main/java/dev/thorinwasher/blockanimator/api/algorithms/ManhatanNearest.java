package dev.thorinwasher.blockanimator.api.algorithms;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ManhatanNearest {


    public static Optional<Vector3D> findClosestPosition(Vector3D closeTo, Predicate<Vector3D> validPositionFilter, int maxDistance) {
        for (int manhatanDistance = 1; manhatanDistance <= maxDistance; manhatanDistance++) {
            Optional<Vector3D> possibleCloseTo = findExactDistance(closeTo, validPositionFilter, manhatanDistance);
            if (possibleCloseTo.isPresent()) {
                return possibleCloseTo;
            }
        }
        return Optional.empty();
    }

    public static Optional<Vector3D> findExactDistance(Vector3D centerPoint, Predicate<Vector3D> filter, int manhatanDistance) {
        for (int x = 0; x <= manhatanDistance; x++) {
            int remainingDistanceAfterX = manhatanDistance - x;
            for (int y = 0; y <= remainingDistanceAfterX; y++) {
                int z = remainingDistanceAfterX - y;
                List<Vector3D> possiblePoints = getPossiblePoints(x, y, z);
                for (Vector3D possiblePoint : possiblePoints) {
                    Vector3D point = centerPoint.add(possiblePoint);
                    if (filter.test(point)) {
                        return Optional.of(point);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static List<Vector3D> getPossiblePoints(int x, int y, int z) {
        List<Vector3D> output = new ArrayList<>();
        for (int ix : getNegativeVariations(x)) {
            for(int iy : getNegativeVariations(y)) {
                for (int iz : getNegativeVariations(z)) {
                    output.add(new Vector3D(ix, iy, iz));
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
