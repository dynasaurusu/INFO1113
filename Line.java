import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

public class Line {
    PApplet p;
    List<PVector> points;  // List of points to form a free-drawn curve

    public Line(PApplet p, float startX, float startY) {
        this.p = p;
        this.points = new ArrayList<>();
        this.points.add(new PVector(startX, startY));  // Initial starting point
    }

    // Add points as the mouse drags
    public void addPoint(float x, float y) {
        this.points.add(new PVector(x, y));
    }

    public void render() {
        p.strokeWeight(10);
        for (int i = 0; i < points.size() - 1; i++) {
            PVector start = points.get(i);
            PVector end = points.get(i + 1);
            p.line(start.x, start.y, end.x, end.y);  // Draw segments between points
        }
    }

    // Check collision by calculating distance to every segment of the curve
    public boolean checkCollision(Ball ball) {
        for (int i = 0; i < points.size() - 1; i++) {
            PVector start = points.get(i);
            PVector end = points.get(i + 1);
            PVector ballPosition = new PVector(ball.getX(), ball.getY());
            float distance = distToSegment(ballPosition, start, end);

            if (distance < ball.radius) {
                return true;  // Collision detected
            }
        }
        return false;
    }

    // Compute distance from the ball's position to the closest point on a line segment
    private float distToSegment(PVector point, PVector start, PVector end) {
        float l2 = PVector.dist(start, end) * PVector.dist(start, end);
        if (l2 == 0) return PVector.dist(point, start);
        float t = PVector.sub(point, start).dot(PVector.sub(end, start)) / l2;
        t = Math.max(0, Math.min(1, t));  // Clamp t to the segment bounds
        PVector projection = PVector.add(start, PVector.mult(PVector.sub(end, start), t));
        return PVector.dist(point, projection);
    }

    // Check if the line is close to the given mouse coordinates
    public boolean isCloseTo(int mouseX, int mouseY) {
        PVector mousePos = new PVector(mouseX, mouseY);
        for (PVector point : points) {
            if (PVector.dist(mousePos, point) < 15) {  // Check if close to any point
                return true;
            }
        }
        return false;
    }
}
