package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

import java.util.List;

@ApiStatus.Internal
public class BlockDisplayEquivalent {


    private final BlockData blockData;
    private Vector3d position;
    private final World world;
    private Size size;
    private ArmorStand armorStand;

    public BlockDisplayEquivalent(BlockData blockData, Vector3d position, World world, float size) {
        this.blockData = blockData;
        this.position = position;
        this.size = Size.fromFloat(size);
        this.world = world;
        armorStand = spawn();
    }

    public void move(Vector3d to, float scale) {
        position = to;
        boolean sizeUpdated = setSize(scale);
        if (sizeUpdated) {
            updateSize();
            return;
        }
        Location toLocation = VectorConverter.toLocation(determinePositionFromSize(position), world);
        List<Entity> passengers = armorStand.getPassengers();
        if (passengers.isEmpty()) {
            armorStand.teleport(toLocation);
            return;
        }
        passengers.forEach(armorStand::removePassenger);
        armorStand.teleport(toLocation);
        passengers.forEach(armorStand::addPassenger);
    }

    private boolean setSize(float size) {
        Size newSize = Size.fromFloat(size);
        Size oldSize = this.size;
        this.size = newSize;
        return newSize != oldSize;
    }

    public void remove() {
        armorStand.getPassengers().forEach(Entity::remove);
        armorStand.getPassengers().forEach(armorStand::removePassenger);
        armorStand.getEquipment().clear();
        armorStand.remove();
    }

    public void freeze() {
        armorStand.setVelocity(new Vector());
    }

    private void updateSize() {
        remove();
        this.armorStand = spawn();
        switch (size) {
            case LARGE -> {
                FallingBlock fallingBlock = EntityUtils.spawnFallingBlock(world, blockData, determinePositionFromSize(position));
                armorStand.addPassenger(fallingBlock);
            }
            case MEDIUM -> armorStand.getEquipment().setHelmet(new ItemStack(blockData.getMaterial()));
            case SMALL -> {
                armorStand.getEquipment().setHelmet(new ItemStack(blockData.getMaterial()));
                armorStand.setSmall(true);
            }
        }
    }

    private ArmorStand spawn() {
        return world.spawn(VectorConverter.toLocation(determinePositionFromSize(position), world), ArmorStand.class, armorStand1 -> {
            armorStand1.setVisible(false);
            armorStand1.setPersistent(false);
            armorStand1.setGravity(false);
            armorStand1.setCollidable(false);
            armorStand1.setMarker(true);
        });
    }

    private Vector3d determinePositionFromSize(Vector3d position) {
        if (size == Size.LARGE) {
            return new Vector3d(position).sub(-0.5, 0, -0.5);
        }
        if (size == Size.MEDIUM) {
            return new Vector3d(position).sub(-0.5, 1.475 - 0.25, -0.5);
        }
        return new Vector3d(position).sub(-0.5, 0.9875 - 0.625, -0.5);
    }
}
