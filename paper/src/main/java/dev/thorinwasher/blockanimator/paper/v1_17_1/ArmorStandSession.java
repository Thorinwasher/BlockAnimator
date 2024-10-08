package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApiStatus.Internal
public class ArmorStandSession {

    private final ArmorStandPool pool;
    private final World world;
    private final Map<Size, ArmorStand> armorStandMap = new HashMap<>();
    private Vector3d pos;
    private Size currentSize;

    public ArmorStandSession(ArmorStandPool pool, Vector3d startingPos, World world) {
        this.pool = pool;
        this.world = world;
        for (Size size : Size.values()) {
            if (size == Size.NOTHING) {
                continue;
            }
            armorStandMap.put(size, pool.fetch(determinePositionFromSize(startingPos, size)));
        }
    }

    public void moveTo(Vector3d pos) {
        for (Map.Entry<Size, ArmorStand> armorStandEntry : armorStandMap.entrySet()) {
            moveTo(armorStandEntry.getValue(), determinePositionFromSize(pos, armorStandEntry.getKey()));
        }
        this.pos = pos;
    }

    public Optional<ArmorStand> nextArmorStand(Size size) {
        if (currentSize == size || size == Size.NOTHING) {
            return Optional.empty();
        }
        if(currentSize != null) {
            pool.free(armorStandMap.get(currentSize));
            armorStandMap.put(currentSize, pool.fetch(determinePositionFromSize(pos, currentSize)));
        }
        currentSize = size;
        return Optional.of(armorStandMap.get(size));
    }

    private void moveTo(@Nullable ArmorStand armorStand, Vector3d pos) {
        if (armorStand == null) {
            return;
        }
        Location toLocation = VectorConverter.toLocation(pos, world);
        if (armorStand.getPassengers().isEmpty()) {
            armorStand.teleport(toLocation);
            return;
        }
        List<Entity> passengers = armorStand.getPassengers();
        passengers.forEach(armorStand::removePassenger);
        armorStand.teleport(toLocation);
        passengers.forEach(armorStand::addPassenger);
    }

    public void end() {
        armorStandMap.values().forEach(pool::free);
    }

    public void freeze() {
        for (ArmorStand armorStand : armorStandMap.values()) {
            armorStand.setVelocity(new Vector());
        }
    }

    private Vector3d determinePositionFromSize(Vector3d position, Size size) {
        if (size == Size.LARGE) {
            return new Vector3d(position).sub(-0.5, 0, -0.5);
        }
        if (size == Size.MEDIUM) {
            return new Vector3d(position).sub(-0.5, 1.475 - 0.25, -0.5);
        }
        return new Vector3d(position).sub(-0.5, 0.9875 - 0.625, -0.5);
    }

}
