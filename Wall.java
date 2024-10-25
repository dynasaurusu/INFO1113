import processing.core.PApplet;
import processing.core.PImage;

public class Wall {
    PApplet p;
    float x, y;
    PImage image;

    public Wall(PApplet p, float x, float y, PImage image) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void render() {
        p.image(image, x, y);
    }

    public void checkCollision(Ball ball) {
        if (ball.position.x + ball.image.width > x && ball.position.x < x + image.width &&
            ball.position.y + ball.image.height > y && ball.position.y < y + image.height) {
            ball.velocity.x *= -1;
            ball.velocity.y *= -1;
        }
    }
}
