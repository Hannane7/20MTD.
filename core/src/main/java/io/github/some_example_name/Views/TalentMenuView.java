package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.Ability;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Models.SettingsManager;

public class TalentMenuView implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final Player currentPlayer;
    private Texture backgroundTexture;

    public TalentMenuView(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        this.skin = GameAssetManager.getSkin();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.backgroundTexture = new Texture("Images/Backgrounds/Menus.png");
        buildUI();

    }

    private void buildUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        mainTable.add(new Label("Talent & Hints", skin, "title")).padBottom(20).row();

        Table contentTable = new Table(skin);
        contentTable.top().left();

        contentTable.add(new Label("Hero Tips:", skin)).left().padBottom(5).row();
        contentTable.add("- SHANA: Faster reload, unique starter weapon.").left().padBottom(2).row();
        contentTable.add("- DIAMOND: Double health, but very slow speed.").left().padBottom(2).row();
        contentTable.add("- LILITH: Can summon bats to fight for her, heals per kill.").left().padBottom(15).row();

        contentTable.add(new Label("Game Keys:", skin)).left().padBottom(5).row();
        String currentControls = SettingsManager.getControlType();
        contentTable.add("Move: " + currentControls).left().row();
        contentTable.add("Shoot: Left Mouse Click").left().row();
        contentTable.add("Reload: R").left().row();
        contentTable.add("Auto-Aim & Shoot: Space").left().row();
        contentTable.add("Pause Menu: ESC").left().padBottom(15).row();


        contentTable.add(new Label("Cheat Codes:", skin)).left().padBottom(5).row();
        contentTable.add("GODMODE: Infinite health").left().row();
        contentTable.add("MONEY: +10000 coins").left().row();
        contentTable.add("LEVELUP: Instant level up").left().padBottom(15).row();

        contentTable.add(new Label("Abilities:", skin )).left().padBottom(5).row();
        for (Ability ability : Ability.ALL_ABILITIES) {
            contentTable.add(ability.getName() + ": " + ability.getDescription()).left().padBottom(2).row();
        }

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        mainTable.add(scrollPane).grow().width(Gdx.graphics.getWidth() * 0.8f).padBottom(20).row();

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.instance.setScreen(new MainMenuView(new MainMenuController(currentPlayer), skin, currentPlayer));
            }
        });
        mainTable.add(backBtn).padTop(10);
    }

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
    public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
