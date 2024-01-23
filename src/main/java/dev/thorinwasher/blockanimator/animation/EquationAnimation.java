package dev.thorinwasher.blockanimator.animation;

import dev.thorinwasher.blockanimator.structure.Structure;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class EquationAnimation implements Animation{


    private final int length;
    private final Expression expression;

    EquationAnimation(String equation, Structure structure, int ticks){
        this.expression = new ExpressionBuilder(equation).build();
        expression.validate();
        this.length = ticks;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void run() {

    }
}
