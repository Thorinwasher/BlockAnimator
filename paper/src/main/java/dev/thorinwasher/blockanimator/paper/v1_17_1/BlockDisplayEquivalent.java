package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.paper.EntityUtils;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

@ApiStatus.Internal
public class BlockDisplayEquivalent {


    private final BlockData blockData;
    private MoveAction cachedMove;
    private Vector3d position;
    private final World world;
    private Size size;
    private final ArmorStandSession armorStandSession;
    private boolean sizeUpdated;
    private final Queue<Integer> queuedUpdates = new LinkedList<>();
    private int internalTime = 0;

    public BlockDisplayEquivalent(BlockData blockData, Vector3d position, World world, float size, ArmorStandPool armorStandPool) {
        this.blockData = blockData;
        this.position = position;
        this.size = Size.fromFloat(size);
        this.world = world;
        this.cachedMove = new MoveAction(position, size);
        this.armorStandSession = new ArmorStandSession(armorStandPool, position, world);
    }

    public void move(Vector3d to, float scale) {
        this.cachedMove = new MoveAction(to, scale);
    }

    private boolean setSize(float size) {
        Size newSize = Size.fromFloat(size);
        Size oldSize = this.size;
        this.size = newSize;
        return newSize != oldSize;
    }

    public void remove() {
        armorStandSession.end();
    }

    public void freeze() {
        armorStandSession.freeze();
    }

    private void updateSize() {
        Optional<ArmorStand> armorStandOptional = armorStandSession.nextArmorStand(size);
        if (armorStandOptional.isEmpty()) {
            return;
        }
        ArmorStand armorStand = armorStandOptional.get();
        switch (size) {
            case LARGE -> {
                FallingBlock fallingBlock = EntityUtils.spawnFallingBlock(world, blockData, position);
                armorStand.addPassenger(fallingBlock);
            }
            case MEDIUM -> armorStand.getEquipment().setHelmet(new ItemStack(blockData.getMaterial()));
            case SMALL -> {
                armorStand.getEquipment().setHelmet(new ItemStack(blockData.getMaterial()));
                armorStand.setSmall(true);
            }
        }
        this.sizeUpdated = false;
    }

    public void tick() {
        if (!queuedUpdates.isEmpty() && queuedUpdates.peek() <= internalTime) {
            queuedUpdates.remove();
            updateSize();
        }
        if (cachedMove != null) {
            position = cachedMove.to;
            boolean sizeUpdated = setSize(cachedMove.scale);
            armorStandSession.moveTo(cachedMove.to);
            if(sizeUpdated){
                queuedUpdates.add(internalTime + 5);
            }
        }
        internalTime++;
    }

    private record MoveAction(Vector3d to, float scale) {
    }
}
