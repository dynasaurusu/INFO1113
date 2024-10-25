import processing.core.PApplet;

public class App extends PApplet {
    GameEngine gameEngine;

    public static void main(String[] args) {
        PApplet.main("App");
    }

    public void settings() {
        size(576, 640); // 18x18 grid with 32x32 tiles
    }

    public void setup() {
        gameEngine = new GameEngine(this);
        frameRate(30); // Set frame rate to 30 fps
    }

    public void draw() {
        background(255);
        gameEngine.update();
        gameEngine.render();
    }

    public void keyPressed() {
        gameEngine.handleKeyPress(key);
    }

    public void mousePressed() {
        gameEngine.handleMouseClick(mouseX, mouseY, mouseButton);
    }

    public void mouseDragged() {
        gameEngine.handleMouseDrag(mouseX, mouseY);
    }
}
