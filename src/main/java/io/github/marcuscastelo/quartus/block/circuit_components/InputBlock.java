package io.github.marcuscastelo.quartus.block.circuit_components;

import io.github.marcuscastelo.quartus.circuit_logic.QuartusInput;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusInputConvertible;
import io.github.marcuscastelo.quartus.circuit_logic.QuartusNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class InputBlock extends AbstractNodeBlock implements QuartusInputConvertible {
    public InputBlock() {
        super(Settings.copy(Blocks.LEVER));
    }

    @Override
    public QuartusInput createQuartusInput(World world, BlockPos pos) throws QuartusNode.QuartusWrongNodeBlockException {
        return new QuartusInput(world, pos) {
            @Override
            public boolean getInGameValue() {
                BlockState bs = world.getBlockState(pos);
                return bs.get(Properties.POWERED);
            }
        };
    }
}
