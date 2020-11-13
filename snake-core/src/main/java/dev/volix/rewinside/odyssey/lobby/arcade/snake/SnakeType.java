package dev.volix.rewinside.odyssey.lobby.arcade.snake;

import lombok.RequiredArgsConstructor;
import dev.volix.rewinside.odyssey.lobby.arcade.Direction;

/**
 * @author Benedikt Wüller
 */
@RequiredArgsConstructor
public enum SnakeType {

    HEAD_NORTH(6),
    HEAD_EAST(3),
    HEAD_SOUTH(2),
    HEAD_WEST(7),

    REAR_NORTH(12),
    REAR_EAST(9),
    REAR_SOUTH(8),
    REAR_WEST(13),

    CORNER_NORTH_EAST(4),
    CORNER_EAST_SOUTH(0),
    CORNER_SOUTH_WEST(1),
    CORNER_WEST_NORTH(5),

    STRAIGHT_VERTICAL(10),
    STRAIGHT_HORIZONTAL(11);

    public final int index;

    public static SnakeType getBodyIndex(final Direction from, final Direction to) {
        if (to == null) { // Head
            if (from == Direction.NORTH) return HEAD_NORTH;
            if (from == Direction.EAST) return HEAD_EAST;
            if (from == Direction.SOUTH) return HEAD_SOUTH;
            if (from == Direction.WEST) return HEAD_WEST;
            return null;
        }

        if (from == null) { // Rear
            if (to == Direction.NORTH) return REAR_NORTH;
            if (to == Direction.EAST) return REAR_EAST;
            if (to == Direction.SOUTH) return REAR_SOUTH;
            if (to == Direction.WEST) return REAR_WEST;
            return null;
        }

        if (from.isOppositeOf(to)) { // Straight connections.
            if (from == Direction.NORTH || from == Direction.SOUTH) return STRAIGHT_VERTICAL;
            if (from == Direction.EAST || from == Direction.WEST) return STRAIGHT_HORIZONTAL;
            return null;
        }

        // Corners.
        if (from == Direction.NORTH) return to == Direction.WEST ? CORNER_WEST_NORTH : CORNER_NORTH_EAST;
        if (from == Direction.EAST) return to == Direction.NORTH ? CORNER_NORTH_EAST : CORNER_EAST_SOUTH;
        if (from == Direction.SOUTH) return to == Direction.EAST ? CORNER_EAST_SOUTH : CORNER_SOUTH_WEST;
        if (from == Direction.WEST) return to == Direction.SOUTH ? CORNER_SOUTH_WEST : CORNER_WEST_NORTH;
        return null;
    }

}
