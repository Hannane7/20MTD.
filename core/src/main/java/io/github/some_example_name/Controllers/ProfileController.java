package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import io.github.some_example_name.Models.Player;

public class ProfileController {
    private final FileHandle userFile;
    private final Json json;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    public ProfileController() {
        this.userFile = Gdx.files.local("users.json");
        this.json = new Json();
    }

    private List<Player> loadPlayers() {
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
        json.setUsePrototypes(false);
        try {
            userFile.writeString(json.prettyPrint(players), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeUsername(Player currentPlayer, String newUsername) {
        List<Player> allPlayers = loadPlayers();
        for (Player p : allPlayers) {
            if (p.getUsername().equals(currentPlayer.getUsername())) {
                p.setUsername(newUsername);
                break;
            }
        }
        savePlayers(allPlayers);
    }

    public void changePassword(Player currentPlayer, String newPassword) {
        List<Player> allPlayers = loadPlayers();
        for (Player p : allPlayers) {
            if (p.getUsername().equals(currentPlayer.getUsername())) {
                p.setPassword(newPassword);
                break;
            }
        }
        savePlayers(allPlayers);
    }

    public void changeAvatar(Player currentPlayer, String newAvatarPath) {
        List<Player> allPlayers = loadPlayers();
        for (Player p : allPlayers) {
            if (p.getUsername().equals(currentPlayer.getUsername())) {
                p.setAvatarPath(newAvatarPath);
                break;
            }
        }
        savePlayers(allPlayers);
    }

    public void deleteAccount(Player playerToDelete) {
        List<Player> allPlayers = loadPlayers();
        allPlayers.removeIf(p -> p.getUsername().equals(playerToDelete.getUsername()));
        savePlayers(allPlayers);
    }

    public boolean isUsernameDuplicate(String newUsername, Player currentPlayer) {
        List<Player> allPlayers = loadPlayers();
        for (Player p : allPlayers) {
            if (!p.getUsername().equals(currentPlayer.getUsername()) && p.getUsername().equalsIgnoreCase(newUsername)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return UPPERCASE_PATTERN.matcher(password).find() &&
            DIGIT_PATTERN.matcher(password).find() &&
            SPECIAL_CHAR_PATTERN.matcher(password).find();
    }
}
