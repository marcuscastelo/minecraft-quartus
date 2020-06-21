package io.github.marcuscastelo.quartus.circuit_logic.real_nodes;

import net.minecraft.util.math.Direction;

import java.util.List;

public class NotGateNode extends QuartusAbstract2To1GateNode {
    @Override
    public List<Direction> getRelativeInputDirections() {
        return null;
    }

    @Override
    public List<Direction> getRelativeOutputDirections() {
        return null;
    }

    @Override
    public String getNodeType() {
        return "NotGate";
    }
}
