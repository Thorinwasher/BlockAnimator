package dev.thorinwasher.blockanimator.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import java.util.Map;

public class CustomStructure implements Structure {

    private final BoundingBox boundingBox;
    private final Map<Location, Material> structure;
    World world = null;

    public CustomStructure(Map<Location, Material> structure) {
        this.boundingBox = new BoundingBox();
        for (Location key : structure.keySet()) {
            if (world == null) {
                world = key.getWorld();
            } else if (!world.equals(key.getWorld())) {
                throw new IllegalArgumentException("All locations has to come from the same world");
            }
            boundingBox.union(key);
        }
        this.structure = structure;
    }

    @Override
    public Map<Location, Material> getStructure() {
        return this.structure;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }


}
