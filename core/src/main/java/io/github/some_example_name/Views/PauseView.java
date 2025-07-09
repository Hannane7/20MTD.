package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.some_example_name.Controllers.GameController;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.*;
import java.util.stream.Collectors;

public class PauseView implements Screen {
    private final Screen gameScreen;
    private final GameController gameController;
    private final Skin skin;
    private final Stage stage;
    private final Label saveMessageLabel;
    private final Texture overlayTexture;
    private final Texture separatorTexture;

    public PauseView(Screen gameScreen, GameController gameController, Skin skin) {
        this.gameScreen = gameScreen;
        this.gameController = gameController;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.saveMessageLabel = new Label("", skin);


        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(0, 0, 0, 0.7f);
        overlayPixmap.fill();
        this.overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();

        Pixmap separatorPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        separatorPixmap.setColor(Color.GRAY);
        separatorPixmap.fill();
        this.separatorTexture = new Texture(separatorPixmap);
        separatorPixmap.dispose();

        buildUI();
    }

    private void buildUI() {
        Gdx.input.setInputProcessor(stage);
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        mainTable.add(new Label("GAME PAUSED", skin, "title")).padBottom(20).row();

        Table contentTable = new Table(skin);
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        TextButton resumeBtn = new TextButton("Resume", skin);
        TextButton saveBtn = new TextButton("Save Game", skin);
        TextButton giveupBtn = new TextButton("Give Up & Exit to Menu", skin);
        giveupBtn.getLabel().setColor(Color.RED);

        contentTable.add(resumeBtn).width(300).pad(5).row();
        contentTable.add(saveBtn).width(300).pad(5).row();
        contentTable.add(giveupBtn).width(300).pad(5).row();
        contentTable.add(saveMessageLabel).pad(10).row();

        contentTable.add(new Image(separatorTexture)).width(300).height(2).pad(15).row();

        final CheckBox grayscaleCheckbox = new CheckBox(" Grayscale Mode", skin);
        grayscaleCheckbox.setChecked(SettingsManager.isGrayscale());
        contentTable.add(grayscaleCheckbox).left().row();

        contentTable.add(new Image(separatorTexture)).width(300).height(2).pad(15).row();

        contentTable.add(new Label("Cheat Codes:", skin)).left().row();
        contentTable.add("GODMODE, LEVELUP, MONEY").left().padBottom(15).row();

        contentTable.add(new Label("Acquired Abilities:", skin)).left().row();
        Player player = gameController.getPlayerController().getPlayer();
        if (player.getUnlockedAbilities().isEmpty()) {
            contentTable.add("None").left().row();
        } else {
            for (Ability ab : player.getUnlockedAbilities()) {
                contentTable.add("- " + ab.getName()).left().padBottom(2).row();
            }
        }

        mainTable.add(scrollPane).grow().width(Gdx.graphics.getWidth() * 0.7f).row();

        resumeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.gameState = Main.GameState.PLAYING;
                Main.instance.setScreen(gameScreen);
            }
        });

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveGame();
            }
        });

        giveupBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Player currentPlayer = gameController.getPlayerController().getPlayer();
                Main.instance.setScreen(new GameOverView(currentPlayer, false)); // false یعنی بازیکن برنده نشده
            }
        });

        grayscaleCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.setGrayscale(grayscaleCheckbox.isChecked());
            }
        });
    }

    private void saveGame() {
        try {
            GameSaveData saveData = new GameSaveData();
            Player player = gameController.getPlayerController().getPlayer();
            saveData.player = new GameSaveData.PlayerData();
            saveData.player.name = player.getUsername();
            saveData.player.health = player.getPlayerHealth();
            saveData.player.kills = player.getKills();
            saveData.player.x = player.getPosX();
            saveData.player.y = player.getPosY();
            saveData.weapon = new GameSaveData.WeaponData();
            saveData.weapon.name = gameController.getWeaponController().getWeapon().getName();
            saveData.weapon.ammo = gameController.getWeaponController().getWeapon().getAmmo();
            saveData.surviveTime = player.getSurviveTime();
            saveData.gameDuration = gameController.getGameDurationSeconds();
            saveData.isBossSpawned = gameController.isBossSpawned();
            saveData.enemySpawnInterval = gameController.getEnemySpawnInterval();
            saveData.isBoundaryShieldActive = gameController.isBoundaryShieldActive();

            saveData.enemies = gameController.getEnemies().stream().map(enemy -> {
                GameSaveData.EnemyData enemyData = new GameSaveData.EnemyData();
                enemyData.type = enemy.getClass().getSimpleName();
                enemyData.health = enemy.getHealth();
                enemyData.x = enemy.getX();
                enemyData.y = enemy.getY();
                return enemyData;
            }).collect(Collectors.toList());


            Json json = new Json();
            String jsonText = json.prettyPrint(saveData);
            FileHandle file = Gdx.files.local("save.json");
            file.writeString(jsonText, false);

            saveMessageLabel.setText("Game Saved Successfully!");
            saveMessageLabel.setColor(Color.GREEN);
        } catch (Exception e) {
            saveMessageLabel.setText("Error: Could not save game.");
            saveMessageLabel.setColor(Color.RED);
            e.printStackTrace();
        }
    }
    @Override
    public void render(float delta) {
        com.badlogic.gdx.utils.ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // یک رنگ آبی تیره به جای سیاهی مطلق
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
        if (separatorTexture != null) separatorTexture.dispose();
    }

    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
