package io.github.marcuscastelo.quartus.circuit_logic.native_programs;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusCircuit;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ExtensorGateNode extends QuartusNode {
    public ExtensorGateNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public String getNodeType() {
        return "ExtensorGate";
    }
}
