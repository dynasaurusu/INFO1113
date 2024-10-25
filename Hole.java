import processing.core.PApplet;
import processing.core.PVector;
import processing.core.PImage;

public class Hole {
    PApplet p;
    float x, y;
    PImage image;

    public Hole(PApplet p, float x, float y, PImage image) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void render() {
        p.image(image, x, y);
    }

    public boolean checkCollision(Ball ball) {
        float dist = PVector.dist(new PVector(x, y), new PVector(ball.position.x, ball.position.y));
        return dist < ball.image.width / 2; // Ball radius approximated by image width
    }
}
