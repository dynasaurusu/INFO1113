import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Ball {
    PApplet p;
    PVector position; // The position of the ball
    PVector velocity; // Velocity of the ball
    PImage image;     // The image representing the ball
    float radius;     // Radius of the ball (size)
    int colorIndex;   // Color of the ball, identified by an index

    public Ball(PApplet p, float x, float y, int color, PImage image) {
        this.p = p;
        this.position = new PVector(x, y); // Initial position
        this.velocity = new PVector(2, 2); // Example velocity
        this.image = image;
        this.radius = image.width / 2; // Assuming the image width is the diameter of the ball
        this.colorIndex = color;
    }

    public void update() {
        // Move the ball based on its velocity
        position.add(velocity);
    }

    public void render() {
        p.image(image, position.x - radius, position.y - radius);
    }

    public PVector getVelocity() {
        return velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }
}
