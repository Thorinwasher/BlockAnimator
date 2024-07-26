package dev.thorinwasher.blockanimator.api.util;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public class Directions {
    public static final List<Vector3D> DIRECTIONS = getDirections();

    private static List<Vector3D> getDirections() {
        return List.of(new Vector3D(1, 0, 0),
                new Vector3D(-1, 0, 0),
                new Vector3D(0, 1, 0),
                new Vector3D(0, -1, 0),
                new Vector3D(0, 0, 1),
                new Vector3D(0, 0, -1));
    }
}
