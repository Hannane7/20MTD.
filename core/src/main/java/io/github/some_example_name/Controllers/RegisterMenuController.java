package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Views.LoginMenuView;
import io.github.some_example_name.Views.MainMenuView;
import io.github.some_example_name.Views.RegisterMenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegisterMenuController {
    private RegisterMenuView registerView;
    private final FileHandle userFile;
    private final Json json;

    private final String[] avatarPaths = {
        "Images/avatar/avatar1.png",
        "Images/avatar/avatar2.png",
        "Images/avatar/avatar3.png",
        "Images/avatar/avatar4.png"
    };

    public RegisterMenuController() {
        this.userFile = Gdx.files.local("users.json");
        this.json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public void setView(RegisterMenuView view) {
        this.registerView = view;
    }

    public void handleRegister(String username, String password, String selectedQuestion, String answer) {
        if (username.isEmpty() || password.isEmpty() || selectedQuestion.isEmpty() || answer.isEmpty()) {
            if (registerView != null) registerView.showError("All fields are required.");
            return;
        }

        if (!isPasswordStrong(password)) {
            if (registerView != null) registerView.showError("Password must be stronger.");
            return;
        }

        List<Player> players = loadPlayers();

        if (isUsernameDuplicate(players, username)) {
            if (registerView != null) registerView.showError("This username already exists.");
            return;
        }

        Player newPlayer = new Player(username, password);
        newPlayer.setAvatarPath(getRandomAvatarPath());
        newPlayer.setSecurityQuestion(selectedQuestion);
        newPlayer.setSecurityAnswer(answer);
        newPlayer.initializeTransientFields();

        players.add(newPlayer);
        savePlayers(players);

        if (registerView != null) registerView.showSuccess("Registration successful!");

        Main.instance.setScreen(new MainMenuView(new MainMenuController(newPlayer), GameAssetManager.getSkin(), newPlayer));
    }

    public void handleGuestLogin() {
        String guestName = "Guest_" + UUID.randomUUID().toString().substring(0, 6);
        Player guestPlayer = new Player(guestName, "");
        guestPlayer.setAvatarPath("Images/avatar/avatar5.png");
        guestPlayer.initializeTransientFields();
        Main.instance.setScreen(new MainMenuView(new MainMenuController(guestPlayer), GameAssetManager.getSkin(), guestPlayer));
    }

    public void handleBack() {
        Gdx.app.exit();
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 &&
            password.matches(".*[A-Z].*") &&
            password.matches(".*[0-9].*") &&
            password.matches(".*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/\\\\|].*");
    }

    private boolean isUsernameDuplicate(List<Player> players, String username) {
        for (Player player : players) {
            if (player.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }


    private List<Player> loadPlayers() {
        if (!userFile.exists() || userFile.length() == 0) {
            return new ArrayList<>();
        }
        try {
            return json.fromJson(ArrayList.class, Player.class, userFile);
        } catch (Exception e) {
            System.err.println("Could not parse users.json, returning new list.");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    private void savePlayers(List<Player> players) {
        json.setUsePrototypes(false);
        try {
            userFile.writeString(json.prettyPrint(players), false);
            System.out.println("Players saved successfully! Total users: " + players.size());
        } catch (Exception e) {
            System.err.println("!!! FAILED TO SAVE PLAYERS !!!");
            e.printStackTrace();
        }
    }

    private String getRandomAvatarPath() {
        int index = (int) (Math.random() * avatarPaths.length);
        return avatarPaths[index];
    }
}
