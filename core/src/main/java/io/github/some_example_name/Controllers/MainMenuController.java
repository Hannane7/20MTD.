package io.github.some_example_name.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.google.gson.Gson;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.*;
import io.github.some_example_name.Views.*;

import java.util.ArrayList;
import java.util.List;

public class MainMenuController {
    private final Player currentPlayer;

    public MainMenuController(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void handleNewGame() {

        Player newPlayer = new Player(currentPlayer.getUsername(), currentPlayer.getPassword());

        Main.instance.setScreen(
            new PreGameMenuView(
                new PreGameMenuController(),
                GameAssetManager.getSkin(),
                newPlayer
            )
        );
    }

    public void handleLoadGame() {
        FileHandle file = Gdx.files.local("save.json");
        if (!file.exists()) {
            System.out.println("No saved game found.");
            return;
        }

        Json json = new Json();
        GameSaveData saveData;
        try {
            saveData = json.fromJson(GameSaveData.class, file);
        } catch (Exception e) {
            System.err.println("Could not load save file: " + e.getMessage());
            return;
        }


        Player loadedPlayer = new Player(saveData.player.name, "");
        loadedPlayer.setPlayerHealth(saveData.player.health);
        loadedPlayer.setKills(saveData.player.kills);
        loadedPlayer.setPos(saveData.player.x, saveData.player.y);
        loadedPlayer.addSurviveTime(saveData.surviveTime);


        PreGame preGameInfo = new PreGame(
            loadedPlayer.getUsername(),
            "Shana",
            saveData.weapon.name,
            saveData.gameDuration / 60
        );

        GameView gameView = new GameView(loadedPlayer, preGameInfo);

        GameController loadedController = gameView.getController();
        loadedController.setBossSpawned(saveData.isBossSpawned);
        loadedController.setEnemySpawnInterval(saveData.enemySpawnInterval);
        if(saveData.isBoundaryShieldActive){
            loadedController.activateBoundaryShield();
        }


        gameView.getController().getEnemies().clear();
        for (GameSaveData.EnemyData enemyData : saveData.enemies) {
            Enemy enemy = createEnemyFromData(enemyData);
            gameView.getController().getEnemies().add(enemy);
        }
        Main.gameState = Main.GameState.PLAYING;
        Main.instance.setScreen(gameView);
    }

    private Enemy createEnemyFromData(GameSaveData.EnemyData data) {
        Enemy enemy;
        switch (data.type) {
            case "Elder":           enemy = new Elder(new com.badlogic.gdx.math.Vector2(data.x, data.y)); break;
            case "EyeBat":          enemy = new EyeBat(new com.badlogic.gdx.math.Vector2(data.x, data.y)); break;
            case "TentacleMonster": enemy = new TentacleMonster(new com.badlogic.gdx.math.Vector2(data.x, data.y)); break;
            case "Tree":            enemy = new Tree(new com.badlogic.gdx.math.Vector2(data.x, data.y)); break;
            default:                return null; // یا یک دشمن پیش‌فرض
        }
        enemy.setHealth(data.health);
        return enemy;
    }

    public void handleSettings() {
        Main.instance.setScreen(new SettingsView(GameAssetManager.getSkin(), this.currentPlayer));
    }

    public void handleCredits() {
        Main.instance.setScreen(
            new CreditsView(
                GameAssetManager.getSkin(),
                currentPlayer
            )
        );
    }


    public void handleProfile(Player currentPlayer) {
        Main.instance.setScreen(new ProfileView(currentPlayer, GameAssetManager.getSkin()));
    }


    public void handleScoreboard(Player currentPlayer) {
        List<Player> allPlayers = loadAllPlayers();
        Main.instance.setScreen(new ScoreboardView(allPlayers, currentPlayer));
    }

    private List<Player> loadAllPlayers() {
        FileHandle userFile = Gdx.files.local("users.json");
        Gson gson = new Gson();
        if (!userFile.exists()) return new ArrayList<>();
        try {

            String json = userFile.readString();
            Player[] playersArray = gson.fromJson(json, Player[].class);
            List<Player> list = new ArrayList<>();
            if (playersArray != null) {
                for (Player p : playersArray) list.add(p);
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void handleTalent(Player currentPlayer) {
        Main.instance.setScreen(new TalentMenuView(currentPlayer));
    }


    public void handleExit() {
        Gdx.app.exit();
    }
}
