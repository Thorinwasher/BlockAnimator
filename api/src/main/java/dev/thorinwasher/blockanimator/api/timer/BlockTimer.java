package dev.thorinwasher.blockanimator.api.timer;

import dev.thorinwasher.blockanimator.api.selector.CompiledBlockSelector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.List;

public interface BlockTimer {

    boolean hasNext(int totalBlockAmount);

    List<Vector3D> fetch(CompiledBlockSelector blockSelector, int totalBlockAmount);
}
