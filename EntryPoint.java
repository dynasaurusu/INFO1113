import processing.core.PApplet;
import processing.core.PImage;

public class EntryPoint {
    PApplet p;
    float x, y;
    PImage image;

    public EntryPoint(PApplet p, float x, float y, PImage image) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void render() {
        p.image(image, x, y);
    }

    // Accessor methods for x and y
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

