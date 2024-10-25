import processing.core.PApplet;

public class Spawner {
    PApplet p;
    float x, y;

    public Spawner(PApplet p) {
        this.p = p;
        this.x = p.width / 2;
        this.y = p.height / 2; // Default spawn position (center of the screen)
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}
