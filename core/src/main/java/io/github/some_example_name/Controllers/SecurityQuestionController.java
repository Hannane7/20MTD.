package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Views.LoginMenuView;
import io.github.some_example_name.Views.MainMenuView;
import io.github.some_example_name.Views.SecurityQuestionView;

import java.util.ArrayList;
import java.util.List;

public class SecurityQuestionController implements Screen {
    private final Main game;
    private final SecurityQuestionView view;
    private final Json json;

    public SecurityQuestionController(Main game) {
        this.game = game;
        this.json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        this.view = new SecurityQuestionView(this, GameAssetManager.getSkin());
    }


    public SecurityQuestionController(Main game, List<Player> allPlayers, Skin skin) {
        this.game = game;
        this.json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        this.view = new SecurityQuestionView(this, skin);
    }

    public Player findPlayerByUsername(String username) {
        List<Player> players = loadPlayers();
        for (Player player : players) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return player;
            }
        }
        return null;
    }

    public void handlePasswordReset(String username, String answer, String newPassword) {
        if (answer.isEmpty() || newPassword.isEmpty()) {
            view.showError("Answer and new password are required.");
            return;
        }

        if (!isPasswordStrong(newPassword)) {
            view.showError("New password must be stronger!");
            return;
        }

        List<Player> players = loadPlayers();
        Player playerToUpdate = null;

        for (Player p : players) {
            if (p.getUsername().equalsIgnoreCase(username)) {
                playerToUpdate = p;
                break;
            }
        }

        if (playerToUpdate == null) {
            view.showError("An unexpected error occurred. User not found.");
            return;
        }

        if (!playerToUpdate.getSecurityAnswer().equalsIgnoreCase(answer)) {
            view.showError("Incorrect security answer.");
            return;
        }

        playerToUpdate.setPassword(newPassword);

        savePlayers(players);

        view.showSuccess("Password changed successfully! Returning to menu...");


        playerToUpdate.initializeTransientFields();
        game.setScreen(new MainMenuView(new MainMenuController(playerToUpdate), GameAssetManager.getSkin(), playerToUpdate));
    }

    public void backToLogin() {
        game.setScreen(new LoginMenuView(new LoginMenuController(game, new RegisterMenuController()), GameAssetManager.getSkin()));
    }

    private List<Player> loadPlayers() {
        FileHandle userFile = Gdx.files.local("users.json");
        if (!userFile.exists() || userFile.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return json.fromJson(ArrayList.class, Player.class, userFile);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void savePlayers(List<Player> players) {
        FileHandle userFile = Gdx.files.local("users.json");
        json.setUsePrototypes(false);
        try {
            userFile.writeString(json.prettyPrint(players), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 &&
            password.matches(".*[A-Z].*") &&
            password.matches(".*[0-9].*") &&
            password.matches(".*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\|].*");
    }

    @Override public void show() { Gdx.input.setInputProcessor(view.getStage()); }
    @Override public void render(float delta) { view.render(delta); }
    @Override public void resize(int width, int height) { view.resize(width, height); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { view.dispose(); }
}
