package dev.thorinwasher.blockanimator.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.Map;

public interface Structure {

    Map<Location, Material> getStructure();

    World getWorld();

    BoundingBox getBoundingBox();
}
