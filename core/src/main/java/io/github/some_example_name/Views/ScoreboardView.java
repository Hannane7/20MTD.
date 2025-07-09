package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final List<Player> players;
    private final Player currentPlayer;
    private String sortBy = "score";
    private Texture backgroundTexture;

    public ScoreboardView(List<Player> players, Player currentPlayer) {
        this.skin = GameAssetManager.getSkin();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.players = new ArrayList<>(players);
        this.currentPlayer = currentPlayer;
        this.backgroundTexture = new Texture("Images/Backgrounds/Menus.png");

        buildUI();
    }

    private void buildUI() {
        stage.clear();
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        table.add(new Label("Scoreboard", skin, "title")).colspan(5).padBottom(30).row();


        Table sortButtonsTable = new Table();
        TextButton sortScore = new TextButton("Sort by Score", skin);
        TextButton sortUsername = new TextButton("Sort by Username", skin);
        TextButton sortKills = new TextButton("Sort by Kills", skin);
        TextButton sortSurvive = new TextButton("Sort by Time", skin);

        sortButtonsTable.add(sortScore).pad(5);
        sortButtonsTable.add(sortUsername).pad(5);
        sortButtonsTable.add(sortKills).pad(5);
        sortButtonsTable.add(sortSurvive).pad(5);
        table.add(sortButtonsTable).colspan(5).padBottom(20).row();

        Table headerTable = new Table(skin);
        headerTable.add(new Label("Rank", skin)).pad(10).expandX();
        headerTable.add(new Label("Username", skin)).pad(10).expandX();
        headerTable.add(new Label("Kills", skin)).pad(10).expandX();
        headerTable.add(new Label("Time", skin)).pad(10).expandX();
        headerTable.add(new Label("Score", skin)).pad(10).expandX();
        table.add(headerTable).colspan(5).width(Gdx.graphics.getWidth() * 0.8f).row();

        players.sort((a, b) -> {
            switch (sortBy) {
                case "username": return a.getUsername().compareToIgnoreCase(b.getUsername());
                case "kills": return Integer.compare(b.getKills(), a.getKills());
                case "survive": return Float.compare(b.getSurviveTime(), a.getSurviveTime());
                default: return Integer.compare(b.getScore(), a.getScore());
            }
        });

        int max = Math.min(10, players.size());
        for (int i = 0; i < max; i++) {
            Player p = players.get(i);
            Table playerRow = new Table(skin);

            Label rankLabel = new Label("" + (i + 1), skin);
            Label userLabel = new Label(p.getUsername(), skin);
            Label killsLabel = new Label("" + p.getKills(), skin);
            Label timeLabel = new Label(String.format("%.0f s", p.getSurviveTime()), skin);
            Label scoreLabel = new Label("" + p.getScore(), skin);

            if (i == 0) { rankLabel.setColor(Color.GOLD); userLabel.setColor(Color.GOLD); }
            else if (i == 1) { rankLabel.setColor(Color.SLATE); userLabel.setColor(Color.SLATE); }
            else if (i == 2) { rankLabel.setColor(Color.valueOf("#CD7F32")); userLabel.setColor(Color.valueOf("#CD7F32")); }

            if (currentPlayer != null && p.getUsername().equals(currentPlayer.getUsername())) {
                userLabel.setColor(Color.LIME);
            }

            playerRow.add(rankLabel).pad(5).expandX();
            playerRow.add(userLabel).pad(5).expandX();
            playerRow.add(killsLabel).pad(5).expandX();
            playerRow.add(timeLabel).pad(5).expandX();
            playerRow.add(scoreLabel).pad(5).expandX();
            table.add(playerRow).colspan(5).width(Gdx.graphics.getWidth() * 0.8f).row();
        }

        TextButton backBtn = new TextButton("Back", skin);
        table.add(backBtn).colspan(5).padTop(30);

        sortScore.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { sortBy = "score"; buildUI(); }});
        sortUsername.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { sortBy = "username"; buildUI(); }});
        sortKills.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { sortBy = "kills"; buildUI(); }});
        sortSurvive.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { sortBy = "survive"; buildUI(); }});

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.instance.setScreen(new MainMenuView(new MainMenuController(currentPlayer), skin, currentPlayer));
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        Main.getBatch().begin();
        Main.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Main.getBatch().end();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
