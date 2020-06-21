package io.github.marcuscastelo.quartus.circuit_logic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class QuartusOutput extends QuartusNode {
    List<Direction> DIRECTIONS_NONE = new ArrayList<>();

    @Override
    public List<Direction> getRelativeOutputDirections() {
        return DIRECTIONS_NONE;
    }

    @Override
    public List<Direction> getRelativeInputDirections() {
        return CircuitUtils.getHorizontalDirections();
    }

    @Override
    public String getNodeType() {
        return "Output";
    }
}
