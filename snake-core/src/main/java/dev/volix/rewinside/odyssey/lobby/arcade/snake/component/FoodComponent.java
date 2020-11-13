package dev.volix.rewinside.odyssey.lobby.arcade.snake.component;

import java.awt.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.volix.rewinside.odyssey.common.frames.component.SpriteComponent;
import dev.volix.rewinside.odyssey.common.frames.resource.image.SpriteSheet;

/**
 * @author Benedikt Wüller
 */
public class FoodComponent extends SpriteComponent {

    @RequiredArgsConstructor
    public enum Type {
        APPLE(14),
        BANANA(15);

        public final int index;
    }

    @Getter private Type type = Type.APPLE;

    public FoodComponent(final Point position, final Dimension dimensions, final SpriteSheet spriteSheet) {
        super(position, dimensions, spriteSheet, Type.APPLE.index);
    }

    public void setType(final Type type) {
        this.type = type;
        this.setSpriteIndex(type.index);
    }

}
