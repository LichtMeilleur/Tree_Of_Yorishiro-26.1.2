package com.licht_meilleur.tree_of_yorishiro.block;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BudOfYorishiroBlock extends Block {

    public static final BooleanProperty WATERED = BooleanProperty.of("watered");

    private static final VoxelShape SHAPE = Block.createCuboidShape(
            4.0, 0.0, 4.0,
            12.0, 10.0, 12.0
    );

    public BudOfYorishiroBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WATERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERED);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        return belowState.isSideSolidFullSquare(world, below, net.minecraft.util.math.Direction.UP);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(WATERED);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient && player.isCreative() && player.isSneaking()) {
            growNow((ServerWorld) world, pos);
            return ActionResult.SUCCESS;
        }

        ItemStack stack = player.getStackInHand(hand);

        if (!state.get(WATERED) && stack.getItem() instanceof BucketItem) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(WATERED, true), Block.NOTIFY_ALL);
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(WATERED)) return;

        if (random.nextInt(1) == 0) {
            growNow(world, pos);
        }
    }

    private void growNow(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, ModBlocks.TREE_OF_YORISHIRO.getDefaultState(), Block.NOTIFY_ALL);

        if (world.getBlockEntity(pos) instanceof TreeOfYorishiroBlockEntity be) {
            be.initDefaultChibisIfNeeded();
            be.cleanupOrphanChibishiros();
            be.ensureChibishiros();
        }
    }

    private void spawnFiveChibis(ServerWorld world, BlockPos pos) {
        ChibishiroColor[] colors = new ChibishiroColor[] {
                ChibishiroColor.RED,
                ChibishiroColor.BLUE,
                ChibishiroColor.YELLOW,
                ChibishiroColor.PURPLE,
                ChibishiroColor.WHITE
        };

        double[][] offsets = new double[][] {
                { 1.5,  0.5},
                {-1.5,  0.5},
                { 0.5,  1.5},
                { 0.5, -1.5},
                {-1.2, -1.2}
        };

        for (int i = 0; i < colors.length; i++) {
            ChibishiroEntity entity = new ChibishiroEntity(ModEntities.CHIBISHIRO, world);
            entity.setColor(colors[i]);
            entity.refreshPositionAndAngles(
                    pos.getX() + 0.5 + offsets[i][0],
                    pos.getY() + 1.0,
                    pos.getZ() + 0.5 + offsets[i][1],
                    world.random.nextFloat() * 360.0f,
                    0.0f
            );
            world.spawnEntity(entity);
        }
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {

        if (!world.isClient) {
            dropStack(world, pos,
                    new net.minecraft.item.ItemStack(
                            com.licht_meilleur.tree_of_yorishiro.registry.ModItems.RAINBOW_SEED
                    )
            );
        }

        super.onBreak(world, pos, state, player);
    }
}