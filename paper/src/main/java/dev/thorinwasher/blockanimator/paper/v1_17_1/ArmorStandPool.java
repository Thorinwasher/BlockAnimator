package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

import java.util.*;

@ApiStatus.Internal
public class ArmorStandPool {

    private final World world;
    private final Queue<ArmorStand> pool = new LinkedList<>();
    private final Set<ArmorStand> taken = new HashSet<>();
    private final Deque<List<ArmorStand>> waiting = new LinkedList<>();

    public ArmorStandPool(World world) {
        this.world = world;
        waiting.add(new ArrayList<>());
        waiting.add(new ArrayList<>());
    }

    public ArmorStand fetch(Vector3d position) {
        ArmorStand armorStand;
        if (pool.isEmpty()) {
            armorStand = spawn(position);
        } else {
            armorStand = pool.poll();
            if(armorStand.isDead()){
                armorStand = spawn(position);
            } else {
                armorStand.teleport(VectorConverter.toLocation(position, world));
                armorStand.setSmall(false);
            }
        }
        taken.add(armorStand);
        return armorStand;
    }

    public void free(ArmorStand armorStand) {
        armorStand.getPassengers().forEach(Entity::remove);
        armorStand.getPassengers().forEach(armorStand::removePassenger);
        armorStand.getEquipment().clear();
        waiting.getLast().add(armorStand);
        taken.remove(armorStand);
    }

    public void clean() {
        pool.forEach(this::remove);
        taken.forEach(this::remove);
        taken.clear();
    }

    private void remove(ArmorStand armorStand) {
        armorStand.getPassengers().forEach(Entity::remove);
        armorStand.getPassengers().forEach(armorStand::removePassenger);
        armorStand.remove();
    }

    public void tick(){
        List<ArmorStand> poll = waiting.poll();
        if(poll != null) {
            pool.addAll(poll);
        }
        waiting.add(new ArrayList<>());
    }

    private ArmorStand spawn(Vector3d position) {
        ArmorStand armorStand = world.spawn(VectorConverter.toLocation(position, world), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setPersistent(false);
        armorStand.setGravity(false);
        armorStand.setCollidable(false);
        armorStand.setMarker(true);
        return armorStand;
    }
}
