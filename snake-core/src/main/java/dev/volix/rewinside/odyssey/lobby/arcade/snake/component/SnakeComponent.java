package dev.volix.rewinside.odyssey.lobby.arcade.snake.component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.volix.rewinside.odyssey.common.frames.component.DummyComponent;
import dev.volix.rewinside.odyssey.common.frames.component.TiledComponent;
import dev.volix.rewinside.odyssey.lobby.arcade.Direction;
import dev.volix.rewinside.odyssey.common.frames.resource.image.SpriteSheet;
import dev.volix.rewinside.odyssey.lobby.arcade.snake.SnakeType;

/**
 * @author Benedikt Wüller
 */
public class SnakeComponent extends TiledComponent<DummyComponent> {

    private final SpriteSheet spriteSheet;

    @Getter private final List<Point> snakeTiles = new LinkedList<>();

    public Direction direction;
    public int length;

    public SnakeComponent(final Dimension dimensions, final Dimension tiles, final int initialLength, final SpriteSheet spriteSheet) {
        super(new Point(), dimensions, tiles);
        this.length = initialLength;
        this.spriteSheet = spriteSheet;
        this.direction = Direction.EAST;
    }

    public void addPoint(final Point point) {
        this.setDirty(point, this.snakeTiles.get(this.snakeTiles.size() - 1));
        this.snakeTiles.add(point);

        if (this.snakeTiles.size() > this.length) {
            this.setDirty(this.snakeTiles.remove(0), this.snakeTiles.get(0));
        }
    }

    public Point calculateNextPoint() {
        final Point current = this.snakeTiles.get(this.snakeTiles.size() - 1);
        if (current == null) return null;

        return this.direction.move(current, 1);
    }

    @Nullable
    protected DummyComponent getTileComponent(@NotNull final Point tile, @NotNull final Point position, @NotNull final Dimension dimensions) {
        return new DummyComponent(position, dimensions);
    }

    @Override
    protected void onRender(@NotNull final Graphics2D context, @NotNull final Rectangle bounds) {
        super.onRender(context, bounds);

        final int amount = this.snakeTiles.size();
        for (int i = 0; i < amount; i++) {
            final Point point = this.snakeTiles.get(i);

            final Dimension tileDimensions = this.getTileDimensions();

            final Rectangle section = new Rectangle(
                point.x * tileDimensions.width, point.y * tileDimensions.height,
                tileDimensions.width, tileDimensions.height
            );

            if (!section.intersects(bounds)) continue;
            if (!this.calculateBounds().contains(section)) continue;

            final Direction from = i == 0 ? null : Direction.getDirection(point, this.snakeTiles.get(i - 1));
            final Direction to = (i == this.snakeTiles.size() - 1) ? null : Direction.getDirection(point, this.snakeTiles.get(i + 1));

            final SnakeType type = SnakeType.getBodyIndex(from, to);
            if (type == null) continue;

            final BufferedImage sprite = this.spriteSheet.getSprite(type.index);
            context.drawImage(sprite, section.x, section.y, section.width, section.height, null);
        }
    }

}
