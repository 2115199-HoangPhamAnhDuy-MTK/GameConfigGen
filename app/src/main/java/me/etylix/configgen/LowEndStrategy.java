package me.etylix.configgen;

public class LowEndStrategy implements GraphicsStrategy {
    @Override
    public GraphicsConfig getDefaultConfig() {
        return new GraphicsConfig(0, 0, 0, 0, 0, 0);
    }

    @Override
    public GraphicsConfig getAvailableConfig() {
        return new GraphicsConfig(1, 2, 1, 1, 2, 0);
    }
}
