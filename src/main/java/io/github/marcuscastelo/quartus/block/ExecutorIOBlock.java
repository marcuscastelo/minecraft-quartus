package io.github.marcuscastelo.quartus.block;

import io.github.marcuscastelo.quartus.registry.QuartusItems;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

/**
 * Classe dos blocos de Input e Output (entrada e saída) do executor
 * Por meio dele será possível colocar as entradas do circuito
 * Do lado oposto das entradas estarão as saídas do circuito
 */
public class ExecutorIOBlock extends HorizontalFacingBlock {

	/**
	 * Construtor padrão da Classe ExecutorIOBlock
	 * Setta as características do bloco para não opaco (evita bugs de visibilidade através do bloco)
	 * Por padrão, o bloco é inicialmente não energizada (não apresenta saída igual a 1 - verdadeiro)
	 */
    public ExecutorIOBlock() {
        super(Settings.of(Material.PART).nonOpaque());
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(EXTENSOR_STATE, ExecutorIOState.VOID).with(Properties.POWERED, false));
	}
	
	/**
	 * Método que define qual tipo de bloco será o ExecutorIOBlock
	 * O sprite (aparência) do bloco muda conforme seu tipo
	 * Possibilidades:
	 * 					- VOID - bloco vazio
	 * 					- VOID_END - bloco vazio e final da sequência
	 * 					- INPUT - Contém somente entrada
	 * 					- OUTPUT - Contém somente saída
	 * 					- IO - Contém entrada e saída
	 */
    public enum ExecutorIOState implements StringIdentifiable {
        VOID("void"), VOID_END("void_end"), INPUT("input"), OUPUT("output"), IO("io");

        String identifier;
        ExecutorIOState(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public String asString() {
            return identifier;
        }
    }

	//Variáveis auxiliares para definição dos estados dos blocos IO (Input Output)
    public static final EnumProperty<ExecutorIOState> EXTENSOR_STATE = EnumProperty.of("executor_io_state", ExecutorIOState.class);

	/**
	 * Método que informa o jogo que esse bloco é um bloco que pode emitir redstone
	 * @param state estado do bloco
	 * @return true
	 */
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return getStrongRedstonePower(state, view, pos, facing);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        Direction outputDir = state.get(FACING);
        if (facing != outputDir) return 0;
        return state.get(Properties.POWERED)? 15: 0;
    }

	/**
	 * Método que define as propriedades que o bloco designado terá.
	 * @param builder		Especifica que o bloco em questão terá as propriedades
	 * 						de FACING (orientação), EXTENSOR_STATE (void, input, input/output, etc)
	 * 						e se está POWERED (energizado)
	 */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENSOR_STATE, Properties.POWERED);
    }

	/**
	 * Método que retorna o estado do bloco (blockState) quando posicionado no mundo.
	 * No caso do ExecutorIOBlock, ele se encontrará sempre à direita do bloco Executor
	 * A verificação é feita verificando o bloco da esquerda (deve ser um Executor ou outro ExecutorIOBlock)
	 * @param ctx		Contexto do bloco posicionado
	 * @return		Retorna o BlockState 
	 */
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction futureFacing = ctx.getPlayerFacing().getOpposite();
        Direction left;
        left = futureFacing.rotateYClockwise();

        World world = ctx.getWorld();

        BlockState leftBlockState = world.getBlockState(ctx.getBlockPos().offset(left));

        if (!(leftBlockState.getBlock() instanceof HorizontalFacingBlock) || leftBlockState.get(FACING) != futureFacing || (!leftBlockState.getBlock().equals(this)  && !(leftBlockState.getBlock() instanceof ExecutorBlock))) return Blocks.AIR.getDefaultState();
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(EXTENSOR_STATE, ExecutorIOState.VOID_END);
    }

    /**
     * Método que retorna uma lista de itens que foram derrubados
     * @param state		Estado do bloco
     * @param builder		Builder que configura as propriedades dos blocos
     * @return		Lista com os itens a serem derrubados
     */
    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        return Collections.singletonList(new ItemStack(state.getBlock().asItem()));
    }

	/**
	 * Define o comportamento de mudança de estado e propagação de informação pela corrente de ExecutorIO
	 * Faz a verificação dos blocos vizinho e determina o estado de cada bloco da sequência
	 * Caso a sequência seja desfeita (ExecutorIOBlock ou Executor destruído ou movido), todos blocos à direita serão destruídos
	 * @param state		Identifica o estado do bloco (energizado, dureza, etc)
	 * @param world		Mundo em que está sendo jogado
	 * @param pos		Posição do bloco no mundo
	 * @param previousBlock		Bloco anterior ao analisado
	 * @param neighborPos		Posição do bloco vizinho
	 * @param moved		Boolean que verifica se o bloco foi movido ou de fato destruído
	 */
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block previousBlock, BlockPos neighborPos, boolean moved) {
        if (neighborPos == pos) return;

        //Obtém informações do blockstate atual
        ExecutorIOState executorIOState = state.get(EXTENSOR_STATE);
        Direction facingDirection = world.getBlockState(pos).get(FACING);

        //Obtém o blockstate à direita do bloco atual
        BlockPos rightBlockPos = pos.offset(facingDirection.rotateYClockwise());
        BlockState rightBlockState = world.getBlockState(rightBlockPos);

        //Obtém o blockstate à esquerda do bloco atual
        BlockPos leftBlockPos = pos.offset(facingDirection.rotateYCounterclockwise());
        BlockState leftBlockState = world.getBlockState(leftBlockPos);

        //Se o bloco a direita não for outra porta IO ou o próprio executor, quebra o bloco atual
        if (!rightBlockState.getBlock().equals(this) && !(rightBlockState.getBlock() instanceof ExecutorBlock)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            dropStack(world, pos, new ItemStack(QuartusItems.EXTENSOR_IO, 1));
            return;
        }

        //Se um bloco do tipo ExecutorIO for quebrado à esquerda, propague a informação na cadeia até o executor (de modo que ele possa reagir quanto à adição ou remoção de I/O)
        if (previousBlock.equals(Blocks.END_PORTAL) ||
            (       previousBlock.equals(this) &&
                    neighborPos.equals(leftBlockPos) &&
                    (executorIOState == ExecutorIOState.INPUT || executorIOState == ExecutorIOState.OUPUT || executorIOState == ExecutorIOState.IO)
            )) {
            world.updateNeighbor(rightBlockPos, Blocks.END_PORTAL, pos);
        }

        //Se o estado atual for algum tipo de void, mantenha o void_end apenas no fim da cadeia de ExecutorIO (apenas estética)
        if (executorIOState == ExecutorIOState.VOID_END || executorIOState == ExecutorIOState.VOID) {
            //Se o bloco à esquerda for outro
            if (leftBlockState.getBlock().equals(this) && leftBlockState.get(FACING) == facingDirection) {
                world.setBlockState(pos, state.with(EXTENSOR_STATE, ExecutorIOState.VOID));
            } else {
                world.setBlockState(pos, state.with(EXTENSOR_STATE, ExecutorIOState.VOID_END));
            }
        }
    }
}