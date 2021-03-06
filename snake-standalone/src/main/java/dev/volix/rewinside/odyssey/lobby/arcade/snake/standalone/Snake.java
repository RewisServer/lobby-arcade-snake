package dev.volix.rewinside.odyssey.lobby.arcade.snake.standalone;

import dev.volix.rewinside.odyssey.lobby.arcade.nbs.song.Song;
import dev.volix.rewinside.odyssey.lobby.arcade.nbs.song.SongParser;
import dev.volix.rewinside.odyssey.lobby.arcade.nbs.song.SongPlayer;
import java.awt.*;
import dev.volix.rewinside.odyssey.common.frames.color.MinecraftColorPalette;
import dev.volix.rewinside.odyssey.common.frames.color.PaletteColorTransformer;
import dev.volix.rewinside.odyssey.lobby.arcade.FrameGameCreator;
import dev.volix.rewinside.odyssey.lobby.arcade.standalone.FrameGameApplication;
import dev.volix.rewinside.odyssey.common.frames.resource.font.FontAdapter;
import dev.volix.rewinside.odyssey.common.frames.resource.font.FontFileAdapter;
import dev.volix.rewinside.odyssey.common.frames.resource.image.ImageAdapter;
import dev.volix.rewinside.odyssey.common.frames.resource.image.ImageFileAdapter;
import dev.volix.rewinside.odyssey.lobby.arcade.snake.SnakeGame;
import java.io.File;
import java.nio.file.Paths;
import tv.dev.volix.rewinside.odyssey.lobby.arcade.standalone.SonicSongPlayer;

/**
 * @author Benedikt Wüller
 */
public class Snake {

    public static void main(String[] args) {
        final File[] directories = new File[] {
                new File("./resources"),
                new File("../resources")
        };

        File directory = null;
        for (final File file : directories) {
            if (!file.exists()) continue;
            directory = file.getAbsoluteFile();
        }

        if (directory == null) {
            throw new IllegalStateException("Unable to find resources directory.");
        }

        final Song themeSong = new SongParser().parse(Paths.get(directory.getAbsolutePath(), "Ob-La-Di_Ob-La-Da.nbs").toFile());

        final PaletteColorTransformer transformer = new MinecraftColorPalette();
        final ImageAdapter imageAdapter = new ImageFileAdapter("resources");
        final FontAdapter fontAdapter = new FontFileAdapter("resources");
        final FrameGameCreator creator = () -> {
            final SongPlayer songPlayer = new SonicSongPlayer(themeSong);
            return new SnakeGame(new Dimension(512, 512), transformer, 8, 3, imageAdapter, fontAdapter, songPlayer);
        };

        new FrameGameApplication("Snake", creator).start();
    }

}
