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
    public void create() {// DONT EDIT HERE
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

    //checkpoint 1
    @Override
    public boolean keyDown(int keycode) {
        if (gameOver) return false;
        // Update next direction based on arrow keys
        //need to make sure that it won't be doing a 180! For example, we can't go North if curDir is SOUTH
        if (keycode == Keys.UP) {
            nextDir = Direction.NORTH;
        } else if (keycode == Keys.DOWN) {
            nextDir = Direction.SOUTH;
        } else if (keycode == Keys.LEFT) {
            nextDir = Direction.WEST;
        } else if (keycode == Keys.RIGHT) {
            nextDir = Direction.EAST;
        }
        return true;
    }

    //checkpoint 1
    private void checkApple() {
        //check if the head of the snake is on the apple
        //if it is, increase score, and use your placeApple() method

        snake.remove(0);//only run this code if there is NO APPLE - this deletes the last snake segment. 
    }
    

    //CHECKPOINT 2
    //should return true if the position is located on the snake
    //useful for determining if we have a legal apple position
    private boolean onSnake(GridPosition pos) {

        return false;
    }

    //checkpoint 2 - need to update apple position creation!
    private void placeApple() {
        apple = new GridPosition(5,9);
        //You will need to use random numbers and a while loop to set the apple position.
        //generate candidate position
        //check that its not on the snake
        //if it is on the snake, try again
        //if not on the snake, set the apple position

    }
    
    //checkpoint 2
    private void checkDeath() {
        //check if the head of the snake is an illegal position

        // Check out of bounds

        // Check hitting itself

        // set gameOver to true if we die
    }


    //helper function for animation, can be safely ignored. DONT EDIT
    private ArrayList<GridPosition> copySnake(ArrayList<GridPosition> source) {
        ArrayList<GridPosition> copy = new ArrayList<>();
        for (GridPosition gp : source) {
            copy.add(new GridPosition(gp));
        }
        return copy;
    }
    
    

    @Override
    public void render() {//READ, but don't modify! (unless you are done with the core requirements.)
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

    private void doMove() {//again, don't change this one
        // Before we move, copy the current snake state to prevSnake
        prevSnake = copySnake(snake);

        // Update current direction to next direction once we finish a cell
        curDir = nextDir;
        moveSnake();
        checkDeath();
        checkApple();
    }

    private void moveSnake() {//DONT need to change
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


    private void renderGameOver() {// no need to change
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
