package me.etylix.configgen;

public class HigherMidRangeStrategy implements GraphicsStrategy {
    @Override
    public GraphicsConfig getDefaultConfig() {
        return new GraphicsConfig(1, 1, 1, 1, 1, 0);
    }

    @Override
    public GraphicsConfig getAvailableConfig() {
        return new GraphicsConfig(2, 2, 3, 2, 3, 2);
    }
}
