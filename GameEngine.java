import processing.core.PApplet;
import processing.core.PImage;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class GameEngine {
    PApplet p;
    PImage wallImages[], ballImages[], holeImages[], entryPointImage, tileImage;
    List<Ball> balls;
    List<Line> lines;
    List<Wall> walls;
    List<Hole> holes;
    List<Brick> bricks;
    List<EntryPoint> entryPoints;
    Spawner spawner;
    int score;
    boolean isPaused;
    int currentLevelIndex;
    JSONArray levelsConfig;
    int levelTime;
    int spawnInterval;
    float timer;
    float spawnTimer;
    int totalBalls = 5;  // Set this to the total number of balls to be displayed

    public GameEngine(PApplet p) {
        this.p = p;
        this.balls = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.walls = new ArrayList<>();
        this.holes = new ArrayList<>();
        this.bricks = new ArrayList<>();
        this.entryPoints = new ArrayList<>();
        this.score = 0;
        this.isPaused = false;

        loadImages(); // Load images for the game
        loadConfig(); // Load config with all level information
        currentLevelIndex = 0; // Start from the first level
        loadLevel(currentLevelIndex);
    }

    private void loadImages() {
        // Load wall, ball, hole, and entry point images
        wallImages = new PImage[5];
        wallImages[0] = p.loadImage("wall0.png");
        wallImages[1] = p.loadImage("wall1.png");
        wallImages[2] = p.loadImage("wall2.png");
        wallImages[3] = p.loadImage("wall3.png");
        wallImages[4] = p.loadImage("wall4.png");
        
        ballImages = new PImage[5];
        ballImages[0] = p.loadImage("ball0.png");
        ballImages[1] = p.loadImage("ball1.png");
        ballImages[2] = p.loadImage("ball2.png");
        ballImages[3] = p.loadImage("ball3.png");
        ballImages[4] = p.loadImage("ball4.png");

        holeImages = new PImage[5];
        holeImages[0] = p.loadImage("hole0.png");
        holeImages[1] = p.loadImage("hole1.png");
        holeImages[2] = p.loadImage("hole2.png");
        holeImages[3] = p.loadImage("hole3.png");
        holeImages[4] = p.loadImage("hole4.png");

        entryPointImage = p.loadImage("entrypoint.png");
        tileImage = p.loadImage("tile.png");

        // Add checks to ensure images are loaded correctly
        for (int i = 0; i < 5; i++) {
            if (ballImages[i] == null) System.out.println("Failed to load ball image " + i);
            if (holeImages[i] == null) System.out.println("Failed to load hole image " + i);
            if (wallImages[i] == null) System.out.println("Failed to load wall image " + i);
        }
        if (entryPointImage == null) System.out.println("Failed to load entry point image");
        if (tileImage == null) System.out.println("Failed to load tile image");
    }

    public void update() {
        if (!isPaused) {
            timer += 1 / p.frameRate; // Use frameRate to approximate delta time
            spawnTimer += 1 / p.frameRate;

            if (spawnTimer >= spawnInterval) {
                spawnBall();
                spawnTimer = 0;
            }

            for (Ball ball : balls) {
                ball.update();
                checkCollisions(ball);
            }

            updateBricks();

            if (balls.isEmpty() && spawnTimer == 0) {
                nextLevel(); // Go to the next level if current level is done
            }
        }
    }

    public void render() {
        renderBackground(); // Render the background grid
        for (Wall wall : walls) {
            wall.render();
        }
        for (Hole hole : holes) {
            hole.render();
        }
        for (Ball ball : balls) {
            ball.render();
        }
        for (Line line : lines) {
            line.render();
        }
        for (Brick brick : bricks) {
            brick.render();
        }
        for (EntryPoint entryPoint : entryPoints) {
            entryPoint.render();
        }

        renderHUD();  // Render the HUD
    }

    public void handleKeyPress(char key) {
        if (key == 'r') {
            resetLevel(false); // Reset and reload the level
        } else if (key == ' ') {
            isPaused = !isPaused; // Pause/unpause the game
        }
    }

    public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == PApplet.LEFT) {
            lines.add(new Line(p, mouseX, mouseY));  // Start a new free-form line
        }
    }

    public void handleMouseDrag(int mouseX, int mouseY) {
        if (!lines.isEmpty()) {
            lines.get(lines.size() - 1).addPoint(mouseX, mouseY);  // Add points to the current line
        }
    }

    private void loadConfig() {
        try (BufferedReader br = new BufferedReader(new FileReader("config.json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONObject config = new JSONObject(jsonContent.toString());
            levelsConfig = config.getJSONArray("levels"); // Get all levels config
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLevel(int levelIndex) {
        // Ensure that you only load if within bounds
        if (levelIndex >= levelsConfig.length()) {
            p.println("Game Completed!");
            return; // No more levels to load, game completed
        }

        // Clear the previous level's data
        resetLevel(true);

        JSONObject levelConfig = levelsConfig.getJSONObject(levelIndex);
        String layoutFile = levelConfig.getString("layout");
        levelTime = levelConfig.getInt("time");
        spawnInterval = levelConfig.getInt("spawn_interval");

        // Load the level layout from the file
        loadLevelLayout(layoutFile);
    }

    private void nextLevel() {
        currentLevelIndex++;
        loadLevel(currentLevelIndex); // Load the next level
    }

    private void loadLevelLayout(String levelFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(levelFile))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char tile = line.charAt(col);
                    float x = col * 32;
                    float y = row * 32 + 64;  // Shift map down by 2 rows (64 pixels)
                    switch (tile) {
                        case 'X':
                            walls.add(new Wall(p, x, y, wallImages[0])); // Add wall0 by default
                            break;
                        case '1': case '2': case '3': case '4':
                            int wallIndex = Character.getNumericValue(tile);
                            walls.add(new Wall(p, x, y, wallImages[wallIndex])); // Load corresponding wall image
                            break;
                        case 'H':
                            char colorDigit = line.charAt(col + 1);
                            int holeColorIndex = Character.getNumericValue(colorDigit);
                            holes.add(new Hole(p, x, y, holeImages[holeColorIndex]));
                            col++; // Skip the next character since it represents the hole's color
                            break;
                        case 'B':
                            char ballColorDigit = line.charAt(col + 1);
                            int ballColorIndex = Character.getNumericValue(ballColorDigit);
                            balls.add(new Ball(p, x, y, ballColorIndex, ballImages[ballColorIndex]));
                            col++; // Skip the next character as it's part of the ball definition
                            break;
                        case 'S': // Case for entry point
                            entryPoints.add(new EntryPoint(p, x, y, entryPointImage)); // Add entry point
                            break;
                    }
                }
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void spawnBall() {
        if (entryPoints.isEmpty()) return; // Ensure there is at least one entry point

        EntryPoint entryPoint = entryPoints.get(0); // Get the first entry point
        int randomColorIndex = p.floor(p.random(0, 5)); // Randomly choose a color index
        PImage ballImage = ballImages[randomColorIndex];
        
        Ball ball = new Ball(p, entryPoint.getX(), entryPoint.getY(), randomColorIndex, ballImage); 
        balls.add(ball);
    }

    private void checkCollisions(Ball ball) {
        for (Wall wall : walls) {
            wall.checkCollision(ball);  // Ball bounces off walls
        }
        for (Line line : lines) {
            if (line.checkCollision(ball)) {
                ball.velocity.y *= -1;  // Reflect velocity when hitting a line (for simplicity)
            }
        }
    }

    private void updateBricks() {
        bricks.removeIf(brick -> brick.hitsLeft <= 0);
    }

    private void resetLevel(boolean isLoadingLevel) {
        balls.clear();
        lines.clear(); 
        walls.clear();
        holes.clear();
        bricks.clear();
        entryPoints.clear();
        timer = 0;
        spawnTimer = 0;

        if (!isLoadingLevel) {
            loadLevel(currentLevelIndex);
        }
    }

    private void removeLine(int mouseX, int mouseY) {
        lines.removeIf(line -> line.isCloseTo(mouseX, mouseY)); // Remove line if close to the mouse click
    }

    private void renderBackground() {
        // Render background grid
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) { 
                p.image(tileImage, i * 32, j * 32 + 64); // Shift grid down by 2 rows (64 pixels)
            }
        }
    }

    private void renderHUD() {
        // Draw the bar at the top
        p.fill(200); // Light gray background for the bar
        p.rect(0, 0, p.width, 50); // Draw the bar
        p.fill(0);

        p.textSize(16);

        // Display score and time at top right
        p.text("Score: " + score, p.width - 150, 20);
        p.text("Time: " + Math.max(0, levelTime - (int) timer), p.width - 150, 40);

        // Display unspawned balls at top left
        for (int i = 0; i < totalBalls; i++) {
            p.image(ballImages[i % 5], 10 + i * 32, 10);
        }

        // Display spawn timer in the center
        p.text(String.format("%.1f", spawnTimer), p.width / 2, 30);
    }

    // Updated method for moving unspawned balls left by 1px/frame
    public void moveUnspawnedBallsLeft() {
        for (Ball ball : balls) {
            ball.setPosition(ball.getX() - 1, ball.getY());  // Move left by 1px
        }
    }
}
