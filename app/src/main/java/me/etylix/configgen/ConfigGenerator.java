package me.etylix.configgen;

public class ConfigGenerator {
    private GraphicsStrategy strategy;

    public void setStrategy(GraphicsStrategy strategy) {
        this.strategy = strategy;
    }

    public String generateConfigFileContent() {
        if (strategy == null) {
            return "unset!";
        }
        GraphicsConfig defaultConfig = strategy.getDefaultConfig();
        GraphicsConfig availConfig = strategy.getAvailableConfig();

        StringBuilder sb = new StringBuilder();
        sb.append("[Default]\n");
        sb.append(defaultConfig.toFileString());
        sb.append("\n"); // Thêm một dòng trống cho dễ đọc
        sb.append("[Available]\n");
        sb.append(availConfig.toFileString());
        return sb.toString();
    }
}
