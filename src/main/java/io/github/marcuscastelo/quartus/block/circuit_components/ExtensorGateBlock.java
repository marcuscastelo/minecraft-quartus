package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusWorldNode;
import io.github.marcuscastelo.quartus.circuit_logic.resolvable_nodes.ExtensorGateNode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class ExtensorGateBlock extends AbstractGateBlock {
    @Override
    public List<Direction> getPossibleOutputDirections(World world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Direction facingDir = bs.get(FACING);
        return Arrays.asList(facingDir);
    }

    @Override
    public List<Direction> getPossibleInputDirections(World world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Direction facingDir = bs.get(FACING);
        return Arrays.asList(facingDir.rotateYClockwise(), facingDir.rotateYCounterclockwise(), facingDir.getOpposite());
    }

    @Override
    public QuartusNode createQuartusNode() {
        return new ExtensorGateNode();
    }
}
