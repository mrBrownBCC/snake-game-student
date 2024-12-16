package bcc.snakegame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

public class Main extends InputAdapter implements ApplicationListener {
    
    // Grid dimensions
    private static final int GRID_SIZE = 20;
    private static final int CELL_SIZE = 24; // since 20 * 24 = 480
    private static final float MOVE_TIME = 0.2f; // time per cell move

    // Directions enum
    enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    // Current and previous snake states
    private ArrayList<GridPosition> snake;
    private ArrayList<GridPosition> prevSnake;

    private Direction curDir;
    private Direction nextDir;

    // Apple position
    private GridPosition apple;

    // Timing variables for animation
    private float moveTimer = 0f;

    private boolean gameOver = false;
    private int score = 0;

    @Override
    public void create() {
        camera = new OrthographicCamera(480, 480);
        camera.setToOrtho(false, 480, 480);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();

        Gdx.input.setInputProcessor(this);

        // Initialize snake in the center of the grid
        snake = new ArrayList<>();
        snake.add(new GridPosition(GRID_SIZE / 2, GRID_SIZE / 2));
        curDir = Direction.EAST;
        nextDir = Direction.EAST;

        // prevSnake starts as a copy of snake
        prevSnake = copySnake(snake);

        // Place initial apple
        placeApple();
    }

    private ArrayList<GridPosition> copySnake(ArrayList<GridPosition> source) {
        ArrayList<GridPosition> copy = new ArrayList<>();
        for (GridPosition gp : source) {
            copy.add(new GridPosition(gp));
        }
        return copy;
    }

    private void placeApple() {
        apple = new GridPosition(MathUtils.random(GRID_SIZE-1), MathUtils.random(GRID_SIZE-1));
        // Ensure apple is not on snake
        while (onSnake(apple)) {
            apple.x = MathUtils.random(GRID_SIZE-1);
            apple.y = MathUtils.random(GRID_SIZE-1);
        }
    }

    private boolean onSnake(GridPosition pos) {
        for (GridPosition s : snake) {
            if (s.x == pos.x && s.y == pos.y) return true;
        }
        return false;
    }

    @Override
    public void render() {
        if (gameOver) {
            renderGameOver();
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        moveTimer += delta;
        if (moveTimer >= MOVE_TIME) {
            moveTimer -= MOVE_TIME;
            // Complete the move to the next cell
            doMove();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        float alpha = moveTimer / MOVE_TIME;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw apple
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(apple.x * CELL_SIZE, apple.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Interpolate and draw the snake
        shapeRenderer.setColor(Color.GREEN);

        // Interpolate all segments (including head and tail)
        // prevSnake is the previous state, snake is the current state
        for (int i = 0; i < snake.size(); i++) {
            GridPosition current;
            GridPosition previous;
            if(i >= prevSnake.size()){
                current = snake.get(i);
                previous = snake.get(i-1);

            } else {
                current = snake.get(i);
                previous = prevSnake.get(i);
            }
            float segX = previous.x + (current.x - previous.x)*alpha;
            float segY = previous.y + (current.y - previous.y)*alpha;
            shapeRenderer.rect(segX * CELL_SIZE, segY * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        font.draw(batch, "Score: " + score, 10, 470);
        batch.end();
    }

    private void doMove() {
        // Before we move, copy the current snake state to prevSnake
        prevSnake = copySnake(snake);

        // Update current direction to next direction once we finish a cell
        curDir = nextDir;
        moveSnake();
        checkDeath();
        checkApple();
    }

    private void moveSnake() {
        // Compute new head position based on current direction
        GridPosition head = snake.get(snake.size()-1);
        GridPosition newHead = new GridPosition(head);

        switch (curDir) {
            case NORTH: newHead.y += 1; break;
            case SOUTH: newHead.y -= 1; break;
            case EAST:  newHead.x += 1; break;
            case WEST:  newHead.x -= 1; break;
        }

        // Add the new head
        snake.add(newHead);
    }

    private void checkApple() {
        GridPosition head = snake.get(snake.size()-1);
        if (head.x == apple.x && head.y == apple.y) {
            // Ate apple, increase score and place a new one
            score++;
            placeApple();
            // Do not remove tail - snake grows
        } else {
            // Remove tail segment
            snake.remove(0);
        }
    }

    private void checkDeath() {
        GridPosition head = snake.get(snake.size()-1);
        // Check out of bounds
        if (head.x < 0 || head.x >= GRID_SIZE || head.y < 0 || head.y >= GRID_SIZE) {
            gameOver = true;
            return;
        }

        // Check hitting itself
        for (int i=0; i<snake.size()-1; i++) {
            GridPosition seg = snake.get(i);
            if (seg.x == head.x && seg.y == head.y) {
                gameOver = true;
                return;
            }
        }
    }

    private void renderGameOver() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        font.draw(batch, "Game Over!", 180, 250);
        font.draw(batch, "Score: " + score, 190, 220);
        font.draw(batch, "Press ENTER to Restart", 100, 190);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            restart();
        }
    }

    private void restart() {
        gameOver = false;
        score = 0;
        snake.clear();
        snake.add(new GridPosition(GRID_SIZE / 2, GRID_SIZE / 2));
        curDir = Direction.EAST;
        nextDir = Direction.EAST;
        prevSnake = copySnake(snake);
        placeApple();
        moveTimer = 0f;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (gameOver) return false;
        // Update next direction based on arrow keys
        if (keycode == Keys.UP && curDir != Direction.SOUTH) {
            nextDir = Direction.NORTH;
        } else if (keycode == Keys.DOWN && curDir != Direction.NORTH) {
            nextDir = Direction.SOUTH;
        } else if (keycode == Keys.LEFT && curDir != Direction.EAST) {
            nextDir = Direction.WEST;
        } else if (keycode == Keys.RIGHT && curDir != Direction.WEST) {
            nextDir = Direction.EAST;
        }
        return true;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void resize(int width, int height) { }
}
