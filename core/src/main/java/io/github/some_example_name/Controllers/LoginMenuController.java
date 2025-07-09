package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Views.LoginMenuView;
import io.github.some_example_name.Views.MainMenuView;
import io.github.some_example_name.Views.RegisterMenuView;
import io.github.some_example_name.Views.SecurityQuestionView;

import java.util.ArrayList;
import java.util.List;

public class LoginMenuController {
    private LoginMenuView view;
    private final Main game;
    private final RegisterMenuController registerController;

    public LoginMenuController(Main game, RegisterMenuController registerController) {
        this.game = game;
        this.registerController = registerController;
    }

    public void setView(LoginMenuView view) {
        this.view = view;
    }

    public void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            if (view != null) view.showError("Username and password are required.");
            return;
        }

        List<Player> players = loadPlayers();

        for (Player player : players) {
            System.out.println("Checking against user: '" + player.getUsername() + "'");

            if (player.getUsername().equalsIgnoreCase(username.trim())) {
                if (player.getPassword().equals(password)) {
                    System.out.println("Password CORRECT. Login successful for " + username);

                    Player loggedInPlayer = player;
                    loggedInPlayer.initializeTransientFields();

                    Main.instance.setScreen(new MainMenuView(new MainMenuController(loggedInPlayer), GameAssetManager.getSkin(), loggedInPlayer));
                    return;
                } else {
                    if (view != null) view.showError("Incorrect password.");
                    return;
                }
            }
        }

        if (view != null) view.showError("User not found.");
    }

    public void handleBack() {
        game.setScreen(new RegisterMenuView(
            game,
            registerController,
            GameAssetManager.getSkin()
        ));
    }

    private List<Player> loadPlayers() {
        FileHandle userFile = Gdx.files.local("users.json");
        if (!userFile.exists() || userFile.length() == 0) {
            System.out.println("loadPlayers: users.json not found or is empty.");
            return new ArrayList<>();
        }
        try {
            Json json = new Json();
            return json.fromJson(ArrayList.class, Player.class, userFile);
        } catch (Exception e) {
            System.err.println("loadPlayers: Could not parse users.json.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public void handleForgotPassword(String username) {
        game.setScreen(new SecurityQuestionController(game));
    }
}
