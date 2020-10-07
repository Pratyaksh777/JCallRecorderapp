package in.ac.vitbhopal.projects.callrecorder.helper;

public final class ScreenInfo {
    private final int width;
    private final int height;
    private final int density;

    public ScreenInfo(int width, int height, int density) {
        this.width = width;
        this.height = height;
        this.density = density;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDensity() {
        return density;
    }
}
