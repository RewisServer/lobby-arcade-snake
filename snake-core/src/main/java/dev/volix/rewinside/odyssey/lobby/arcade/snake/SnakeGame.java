package dev.volix.rewinside.odyssey.lobby.arcade.snake;

import dev.volix.rewinside.odyssey.lobby.arcade.nbs.song.SongPlayer;
import java.awt.*;
import dev.volix.rewinside.odyssey.common.frames.alignment.Alignment;
import dev.volix.rewinside.odyssey.common.frames.color.ColorTransformer;
import dev.volix.rewinside.odyssey.common.frames.component.ImageComponent;
import dev.volix.rewinside.odyssey.common.frames.component.TextComponent;
import dev.volix.rewinside.odyssey.lobby.arcade.*;
import dev.volix.rewinside.odyssey.lobby.arcade.helper.RandomHelpersKt;
import dev.volix.rewinside.odyssey.common.frames.helper.MathHelpersKt;
import dev.volix.rewinside.odyssey.common.frames.resource.font.FontAdapter;
import dev.volix.rewinside.odyssey.common.frames.resource.image.ImageAdapter;
import dev.volix.rewinside.odyssey.common.frames.resource.image.SpriteSheet;
import dev.volix.rewinside.odyssey.lobby.arcade.snake.component.FoodComponent;
import dev.volix.rewinside.odyssey.lobby.arcade.snake.component.SnakeComponent;

/**
 * @author Benedikt Wüller
 */
public class SnakeGame extends SongPlayerFrameGame {

    private static final int MIN_POINTS = 1;
    private static final int MAX_POINTS = 10;

    private final int boardSize;
    private final Font font;

    private final FoodComponent food;
    protected final SnakeComponent snake;
    private final TextComponent scoreDisplay;

    private Direction nextDirection = Direction.EAST;

    private long lastFoodConsumedAt;

    private int requiredStepsToFood;
    private int stepsTaken;

    protected long score;

    public SnakeGame(final Dimension viewportDimension, final ColorTransformer transformer, final int boardSize,
                     final int initialSnakeSize, final ImageAdapter imageAdapter, final FontAdapter fontAdapter, final SongPlayer songPlayer) {
        super(fontAdapter, new Dimension(256, 256), viewportDimension, 200L, transformer, songPlayer);

        this.setInputDescription(InputKey.LEFT, "Links");
        this.setInputDescription(InputKey.RIGHT, "Rechts");
        this.setInputDescription(InputKey.UP, "Hoch");
        this.setInputDescription(InputKey.DOWN, "Runter");

        this.setAllowSimultaneousInputs(false);

        this.boardSize = boardSize;
        final Dimension boardDimensions = new Dimension(boardSize, boardSize);

        final SpriteSheet spriteSheet = imageAdapter.getSheet("snake", 32);
        this.snake = new SnakeComponent(this.getCanvasDimensions(), boardDimensions, initialSnakeSize, spriteSheet);
        this.food = new FoodComponent(new Point(), this.snake.getTileDimensions(), spriteSheet);

        this.getBaseComponent().addComponent(new ImageComponent(new Point(), this.getCanvasDimensions(), imageAdapter.get("background")));

        this.font = fontAdapter.get("JetBrainsMono-ExtraBold", 25.0f);
        this.scoreDisplay = new TextComponent(new Point(this.getCanvasDimensions().width - 23, 12), "0", new Color(255, 0, 0, 255), this.font, Alignment.TOP_RIGHT);

        this.getBaseComponent().addComponent(this.snake);
        this.getBaseComponent().addComponent(this.food);
        this.getBaseComponent().addComponent(this.scoreDisplay);

        this.getIdleComponent().addComponent(new ImageComponent(new Point(), this.getCanvasDimensions(), imageAdapter.get("idle")));

        this.getGameOverComponent().addComponent(new ImageComponent(new Point(), this.getCanvasDimensions(), imageAdapter.get("game-over")));

        this.snake.getSnakeTiles().add(new Point());
        while (this.snake.getSnakeTiles().size() < this.snake.length - 1) {
            this.snake.addPoint(this.snake.calculateNextPoint());
        }

        this.updateFood();
        this.requiredStepsToFood = this.calculateStepsToFood();
    }

    @Override
    protected boolean onUpdate(final long currentTime, final long delta) {
        if (!super.onUpdate(currentTime, delta)) return false;

        this.snake.direction = this.nextDirection;
        final Point nextPoint = this.snake.calculateNextPoint();
        if (nextPoint == null) return false;

        final boolean outOfBounds = nextPoint.x < 0 || nextPoint.y < 0 || nextPoint.x >= this.boardSize || nextPoint.y >= this.boardSize;
        if (outOfBounds) {
            this.setGameOver();
            return false;
        }

        this.snake.addPoint(nextPoint);
        this.stepsTaken += 1;

        final boolean hitSelf = this.snake.getSnakeTiles().stream().anyMatch(point -> point != nextPoint && point.equals(nextPoint));
        if (hitSelf) {
            this.setGameOver();
            return true;
        }

        if (nextPoint.equals(MathHelpersKt.tiled(this.food.getPosition(), this.food.getDimensions(), new Point()))) {
            this.snake.length++;
            this.updateFood();

            // Update score
            final int points = Math.max(MIN_POINTS, MAX_POINTS - (this.stepsTaken - this.requiredStepsToFood));

            this.score += points;
            this.scoreDisplay.setText(String.valueOf(this.score));

            this.requiredStepsToFood = this.calculateStepsToFood();
            this.stepsTaken = 0;
        }

        return false;
    }

    private int calculateStepsToFood() {
        final Point foodTile = MathHelpersKt.tiled(this.food.getPosition(), this.food.getDimensions(), new Point());
        final Point snakeTile = this.snake.getSnakeTiles().get(this.snake.getSnakeTiles().size() - 1);
        return Math.abs(foodTile.x - snakeTile.x) + Math.abs(foodTile.y - snakeTile.y);
    }

    private void setGameOver() {
        this.getGameOverComponent().addComponent(new TextComponent(
                new Point(this.getCanvasDimensions().width / 2, this.getCanvasDimensions().height / 2 + 38),
                String.valueOf(this.score),
                Color.WHITE,
                this.font,
                Alignment.TOP_CENTER
        ));

        this.setState(GameState.DONE);
    }

    private void updateFood() {
        final Point newTile = RandomHelpersKt.getRandomPoint(0, 0, this.boardSize, this.boardSize, this.snake.getSnakeTiles());
        this.food.getPosition().move(newTile.x * this.food.getDimensions().width, newTile.y * this.food.getDimensions().height);
        this.food.setType(Math.random() < 0.5 ? FoodComponent.Type.APPLE : FoodComponent.Type.BANANA);
    }

    @Override
    protected void onKeyDown(final InputKey key, final long currentTime) {
        super.onKeyDown(key, currentTime);
        if (!this.getStarted()) return;

        final Direction direction = key.getDirection();
        if (direction == null) return;

        if (!this.snake.direction.isPerpendicularTo(direction)) return;
        this.nextDirection = direction;
    }
}
