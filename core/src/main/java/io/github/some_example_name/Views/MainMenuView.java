package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;

public class MainMenuView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final MainMenuController controller;
    private final Player currentPlayer;

    private Label messageLabel;
    private Texture backgroundTexture;


    public MainMenuView(MainMenuController controller, Skin skin, Player currentPlayer) {
        this.controller = controller;
        this.skin = skin;
        this.currentPlayer = currentPlayer;
        this.stage = new Stage(new ScreenViewport());
        this.backgroundTexture = GameAssetManager.getManager().get("Images/Backgrounds/Menus.png");
        Gdx.input.setInputProcessor(stage);

        buildUI();
    }


    private void buildUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);

        Label titleLabel = new Label("20 Minutes Till Dawn", skin, "title");
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        if (currentPlayer != null && currentPlayer.getAvatarPath() != null) {
            try {
                Image avatar = new Image(new com.badlogic.gdx.graphics.Texture(Gdx.files.internal(currentPlayer.getAvatarPath())));
                mainTable.add(avatar).size(64, 64).padBottom(5).row();
            } catch (Exception e) {
                Gdx.app.error("MainMenuView", "Avatar not found: " + currentPlayer.getAvatarPath());
            }
        }

        String displayName = currentPlayer != null ? currentPlayer.getUsername() : "Guest";
        Label playerLabel = new Label("Player: " + displayName, skin);
        mainTable.add(playerLabel).padBottom(10).row();

        if (currentPlayer != null) {
            Label scoreLabel = new Label("Score: " + currentPlayer.getScore(), skin);
            mainTable.add(scoreLabel).padBottom(20).row();
        }

        // --- بخش اسکرول‌شو برای دکمه‌ها ---
        Table buttonTable = new Table();
        ScrollPane scrollPane = new ScrollPane(buttonTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        TextButton continueBtn = new TextButton("Continue", skin);
        TextButton newGameBtn = new TextButton("New Game", skin);
        TextButton loadGameBtn = new TextButton("Load Game", skin);
        TextButton settingsBtn = new TextButton("Settings", skin);
        TextButton profileButton = new TextButton("Profile", skin);
        TextButton scoreboardBtn = new TextButton("Scoreboard", skin);
        TextButton talentBtn = new TextButton("Talent/Hints", skin);
        TextButton creditsBtn = new TextButton("Credits", skin);
        TextButton exitBtn = new TextButton("Exit", skin);

        float buttonWidth = 250;
        float buttonPad = 10;
        buttonTable.add(continueBtn).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(newGameBtn).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(loadGameBtn).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(settingsBtn).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(profileButton).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(scoreboardBtn).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(talentBtn).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(creditsBtn).width(buttonWidth).padBottom(buttonPad).row();
        buttonTable.add(exitBtn).width(buttonWidth).padBottom(buttonPad).row();

        mainTable.add(scrollPane).height(Gdx.graphics.getHeight() * 0.4f).fillX().padBottom(10).row();

        messageLabel = new Label("", skin);
        messageLabel.setAlignment(Align.center);
        mainTable.add(messageLabel).width(300).padTop(15);


        continueBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleLoadGame(); }});
        loadGameBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleLoadGame(); }});
        newGameBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleNewGame(); }});
        settingsBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleSettings(); }});

        profileButton.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) {
            if (currentPlayer != null && !currentPlayer.getUsername().startsWith("Guest")) {
                controller.handleProfile(currentPlayer);
            } else {
                messageLabel.setText("[ERROR] Profile is only for registered players.");
                messageLabel.setColor(com.badlogic.gdx.graphics.Color.RED);
            }
        }});

        scoreboardBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleScoreboard(currentPlayer); }});
        talentBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleTalent(currentPlayer); }});
        creditsBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleCredits(); }});

        exitBtn.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { controller.handleExit(); }});
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        com.badlogic.gdx.utils.ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        Main.getBatch().begin();
        Main.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Main.getBatch().end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
