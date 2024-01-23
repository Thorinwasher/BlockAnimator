package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.structure.Structure;
import org.bukkit.plugin.Plugin;

public class AnimationBuilder {

    private final Plugin plugin;
    private Structure structure;
    private AnimationSequence animationSequence;

    public AnimationBuilder(Plugin plugin){
        this.plugin = plugin;
        this.animationSequence = new AnimationSequence(plugin);
    }

    public AnimationBuilder addAnimation(String equation, Structure structure, int ticks){
        Animation animation = new EquationAnimation(equation, structure, ticks);
        animationSequence.addAnimation(animation);
        return this;
    }

    public Animation build(){
        return animationSequence;
    }
}
