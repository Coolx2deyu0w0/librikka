package coolx2deyu0w0.lib.client.model;

import com.google.common.collect.ImmutableSet;

/**
 * 变体类型
 */
public interface VariantType
{
    String ALL    = "all";
    String DOWN   = "down";
    String UP     = "up";
    String NORTH  = "north";
    String SOUTH  = "south";
    String WEST   = "west";
    String EAST   = "east";
    String SIDE   = "side";
    String BOTTOM = "bottom";
    String TOP    = "top";
    String END    = "end";

    /**
     * 方块变体
     * <p>
     * 只记录Minecraft自带的方块变体
     */
    enum Block implements VariantType
    {
        /**
         * 将支持不同纹理的面使用“/”分割，下同
         * <p>
         * 东/南/西/北/上/下（各个面都不同）
         */
        CUBE("minecraft:block/cube", DOWN, UP, NORTH, SOUTH, WEST, EAST),
        /**
         * 东南西北上下（六个面都相同）
         */
        CUBE_ALL("minecraft:block/cube_all", ALL),
        /**
         * 东南西北/上/下（四个侧面相同，上下各不相同）
         */
        CUBE_BOTTOM_TOP("minecraft:block/cube_bottom_top", SIDE, BOTTOM, TOP),
        /**
         * 东南西北/上下（四个侧面都相同，上下两面相同）
         */
        CUBE_COLUMN("minecraft:block/cube_column", SIDE, END),
        /**
         * 东/南/西/北/上/下（各个面都不同）
         * <p>
         * 和“CUBE”相同，但是这个模型是有朝向的，MC对材质资源做出了一些旋转让其看起来都是“正着放”的
         */
        CUBE_DIRECTIONAL("minecraft:block/cube_directional", DOWN, UP, NORTH, SOUTH, WEST, EAST),
        /**
         * 东/南/西/北/上/下（各个面都不同）
         * <p>
         * 和“CUBE”相同，但是这个模型是将材质翻转的。效果可以参考翻转过的TNT（大概）
         */
        CUBE_MIRRORED("minecraft:block/cube_mirrored", DOWN, UP, NORTH, SOUTH, WEST, EAST),
        /**
         * 东南西北上下（六个面都相同）
         * <p>
         * 是“MIRRORED”的简化版本，六个面使用同样的材质并全部翻转
         */
        CUBE_MIRRORED_ALL("minecraft:block/cube_mirrored_all", ALL),
        /**
         * 东南西北下/上（顶面和另外五个面不同）
         */
        CUBE_TOP("minecraft:block/cube_top", SIDE, TOP);

        private String modelPath;

        private ImmutableSet<String> key;

        Block(String modelPath, String... key) {
            this.modelPath = modelPath;
            this.key = ImmutableSet.copyOf(key);
        }

        @Override
        public String getModelPath() {
            return this.modelPath;
        }

        @Override
        public boolean test(String key) {
            return this.key.contains(key);
        }
    }

    enum Item implements VariantType
    {
        GENERATED("minecraft:item/generatedEasy");

        private String modelPath;

        Item(String modelPath) {
            this.modelPath = modelPath;
        }

        @Override
        public String getModelPath() {
            return this.modelPath;
        }

        @Override
        public boolean test(String key) {
            return true;
        }
    }

    String getModelPath();

    boolean test(String key);
}
