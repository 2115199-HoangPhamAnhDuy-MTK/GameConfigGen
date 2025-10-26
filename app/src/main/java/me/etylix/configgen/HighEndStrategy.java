package me.etylix.configgen;

public class HighEndStrategy implements GraphicsStrategy {
    @Override
    public GraphicsConfig getDefaultConfig() {
        return new GraphicsConfig(2, 2, 2, 2, 2, 2);
    }

    @Override
    public GraphicsConfig getAvailableConfig() {
        return new GraphicsConfig(2, 3, 3, 3, 3, 3);
    }
}
