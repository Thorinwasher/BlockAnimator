package dev.thorinwasher.blockanimator.paper;

import dev.thorinwasher.blockanimator.animation.Animation;
import dev.thorinwasher.blockanimator.paper.handle.Animator1_16_4;
import dev.thorinwasher.blockanimator.paper.handle.Animator1_19_4;
import dev.thorinwasher.blockanimator.paper.handle.AnimatorHandle;
import org.bukkit.World;
import org.bukkit.block.BlockState;


public class Animator {

    private final AnimatorHandle handle;

    public Animator(Animation<BlockState> animation, World world) {
        if (ClassChecker.classExists("org.bukkit.entity.BlockDisplay")) {
            this.handle = new Animator1_19_4(animation, world);
        } else {
            this.handle = new Animator1_16_4(animation, world);
        }
    }

    public boolean nextTick() {
        return handle.nextTick();
    }
}
