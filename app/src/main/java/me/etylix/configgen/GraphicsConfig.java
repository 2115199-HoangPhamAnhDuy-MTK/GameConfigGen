package me.etylix.configgen;

public class GraphicsConfig {
    int resolution;
    int textureQuality;
    int particleQuality;
    int shadowQuality;
    int framerate;
    int antiAliasing;

    public GraphicsConfig(int resolution, int textureQuality, int particleQuality, int shadowQuality, int framerate, int antiAliasing) {
        this.resolution = resolution;
        this.textureQuality = textureQuality;
        this.particleQuality = particleQuality;
        this.shadowQuality = shadowQuality;
        this.framerate = framerate;
        this.antiAliasing = antiAliasing;
    }

    public String toFileString() {
        return "resolution=" + resolution + "\n" +
                "textureQuality=" + textureQuality + "\n" +
                "particleQuality=" + particleQuality + "\n" +
                "shadowQuality=" + shadowQuality + "\n" +
                "framerate=" + framerate + "\n" +
                "antiAliasing=" + antiAliasing + "\n";
    }
}
