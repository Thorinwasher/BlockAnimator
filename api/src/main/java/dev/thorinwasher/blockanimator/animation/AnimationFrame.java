package dev.thorinwasher.blockanimator.animation;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Map;

public record AnimationFrame(Map<Vector3D, Vector3D> currentToDestination) {
}