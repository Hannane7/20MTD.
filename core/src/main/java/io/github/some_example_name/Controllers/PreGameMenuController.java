package io.github.some_example_name.Controllers;

import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Models.PreGame;
import io.github.some_example_name.Views.GameView;
import io.github.some_example_name.Views.MainMenuView;

public class PreGameMenuController {

    public void handleStartGame(Player player, String selectedHero, String selectedWeapon, float duration) {

        Main.gameState = Main.GameState.PLAYING;

        player.setSelectedCharacter(selectedHero);

        PreGame preGameInfo = new PreGame(player.getUsername(), selectedHero, selectedWeapon, duration);

        Main.instance.setScreen(new GameView(player, preGameInfo));
    }

    public void handleBack(Player player) {
        Main.instance.setScreen(
            new MainMenuView(
                new MainMenuController(player),
                GameAssetManager.getSkin(),
                player
            )
        );
    }
}
