package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.paper.EntityUtils;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class BlockDisplayEquivalent {


    private final BlockData blockData;
    private final Vector3D position;
    private final World world;
    private float size;
    private ArmorStand armorStand;

    public BlockDisplayEquivalent(BlockData blockData, Vector3D position, World world, float size) {
        this.blockData = blockData;
        this.position = position;
        this.size = size;
        this.world = world;
    }

    public void spawn() {
        this.armorStand = world.spawn(determinePositionFromSize(position), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setPersistent(false);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        updateSize();
    }

    public void move(Vector3D to) {
        Location toLocation = determinePositionFromSize(to);
        if (armorStand.getPassengers().isEmpty()) {
            armorStand.teleport(toLocation);
            return;
        }
        List<Entity> passengers = armorStand.getPassengers();
        passengers.forEach(armorStand::removePassenger);
        armorStand.teleport(toLocation);
        passengers.forEach(armorStand::addPassenger);
    }

    public void setSize(float size) {
        if (size != this.size) {
            this.size = size;
            updateSize();
        }
    }

    public void remove() {
        armorStand.getPassengers().forEach(Entity::remove);
        armorStand.remove();
    }

    public void freeze() {
        armorStand.setVelocity(new Vector());
    }

    public void updateSize() {
        if (size == 1F) {
            armorStand.setSmall(false);
            if (armorStand.getPassengers().isEmpty()) {
                FallingBlock fallingBlock = EntityUtils.spawnFallingBlock(world, blockData, VectorConverter.toVector3D(armorStand.getLocation()));
                armorStand.addPassenger(fallingBlock);
            }
            return;
        }
        armorStand.getPassengers().forEach(Entity::remove);
        if (size >= 0.5F) {
            armorStand.getEquipment().setHelmet(new ItemStack(blockData.getMaterial()));
            armorStand.setSmall(false);
            return;
        }
        if (size >= 0.25F) {
            armorStand.getEquipment().setHelmet(new ItemStack(blockData.getMaterial()));
            armorStand.setSmall(true);
            return;
        }
        armorStand.getEquipment().clear();
    }

    private Location determinePositionFromSize(Vector3D position) {
        if (size == 1F) {
            return VectorConverter.toLocation(position, world).subtract(-0.5, 1.475, -0.5);
        }
        if (size >= 0.5F) {
            return VectorConverter.toLocation(position, world).subtract(-0.5, 1.475 - 0.25, -0.5);
        }
        return VectorConverter.toLocation(position, world).subtract(-0.5, 0.9875 - 0.625, -0.5);
    }
}
