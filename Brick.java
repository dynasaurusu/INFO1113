import processing.core.PApplet;
import processing.core.PImage;

public class Brick {
    PApplet p;
    float x, y;
    int hitsLeft;
    int color;
    PImage image;

    public Brick(PApplet p, float x, float y, PImage image, int color) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.image = image;
        this.color = color;
        this.hitsLeft = 1;
    }

    public void render() {
        p.image(image, x, y);
    }

    public void checkCollision(Ball ball) {
        if (hitsLeft > 0 && ball.colorIndex == this.color) {
            hitsLeft--; // Decrease hits left when the ball with matching color hits the brick
        }
    }
}
