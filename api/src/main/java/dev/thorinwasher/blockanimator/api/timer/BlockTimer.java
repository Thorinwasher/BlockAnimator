package dev.thorinwasher.blockanimator.api.timer;

import dev.thorinwasher.blockanimator.api.selector.CompiledBlockSelector;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.List;

public interface BlockTimer {

    boolean hasNext(int totalBlockAmount);

    List<ImmutableVector3i> fetch(CompiledBlockSelector blockSelector, int totalBlockAmount);
}
