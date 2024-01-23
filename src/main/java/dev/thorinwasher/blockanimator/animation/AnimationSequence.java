package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.util.ThreadHelper;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class AnimationSequence implements Animation {

    private final Plugin plugin;
    List<Animation> animations = new ArrayList<>();

    AnimationSequence(Plugin plugin) {
        this.plugin = plugin;
    }

    void addAnimation(Animation animation) {
        animations.add(animation);
    }

    @Override
    public void run() {
        int delay = 0;
        for (Animation animation : animations) {
            ThreadHelper.scheduleGlobalTask(animation, plugin, delay);
            delay += animation.getLength();
        }
    }


    @Override
    public int getLength() {
        int output = 0;
        for (Animation animation : animations) {
            output += animation.getLength();
        }
        return output;
    }
}
