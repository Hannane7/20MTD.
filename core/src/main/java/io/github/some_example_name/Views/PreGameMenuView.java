package io.github.some_example_name.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Controllers.MainMenuController;
import io.github.some_example_name.Controllers.PreGameMenuController;
import io.github.some_example_name.Main;
import io.github.some_example_name.Models.GameAssetManager;
import io.github.some_example_name.Models.Player;
import io.github.some_example_name.Models.Weapon;
import io.github.some_example_name.Controllers.GameController; // برای دسترسی به لیست سلاح‌ها

public class PreGameMenuView implements Screen {

    private final Stage stage;
    private final PreGameMenuController controller;
    private final Skin skin;
    private final Player player;
    private Texture backgroundTexture;

    private final Label heroHpLabel;
    private final Label heroSpeedLabel;
    private final Label weaponDamageLabel;
    private final Label weaponAmmoLabel;
    private final SelectBox<String> heroSelectBox;
    private final SelectBox<Weapon> weaponSelectBox;

    public PreGameMenuView(PreGameMenuController controller, Skin skin, Player player) {
        this.controller = controller;
        this.skin = skin;
        this.player = player;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.backgroundTexture = new Texture("Images/Backgrounds/Menus.png");

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        table.add(new Label("Pre-Game Setup", skin, "title")).colspan(4).padBottom(20).row();

        Image avatarImage = new Image();
        if (player.getAvatarPath() != null && !player.getAvatarPath().isEmpty()) {
            try {
                Texture avatarTexture = GameAssetManager.getManager().get(player.getAvatarPath(), Texture.class);
                avatarImage.setDrawable(new TextureRegionDrawable(avatarTexture));
            } catch (Exception e) { Gdx.app.error("PreGameView", "Avatar not found for path: " + player.getAvatarPath()); }
        }

        table.add(avatarImage).size(64, 64).colspan(4).row();
        table.add(new Label("Player: " + player.getUsername(), skin)).colspan(4).padBottom(20).row();

        heroSelectBox = new SelectBox<>(skin);
        heroSelectBox.setItems("shana", "diamond", "scarlet", "lilith", "dasher");
        heroSelectBox.setSelected(player.getSelectedCharacter());
        heroHpLabel = new Label("HP: -", skin);
        heroSpeedLabel = new Label("Speed: -", skin);

        table.add(new Label("Select Hero:", skin)).left().padRight(10);
        table.add(heroSelectBox).width(150).left();
        table.add(heroHpLabel).padLeft(10);
        table.add(heroSpeedLabel).padLeft(10).row();

        weaponSelectBox = new SelectBox<>(skin);
        weaponSelectBox.setItems(GameController.getAllWeapons().toArray(new Weapon[0])); // گرفتن لیست سلاح‌ها از کنترلر
        weaponDamageLabel = new Label("Damage: -", skin);
        weaponAmmoLabel = new Label("Ammo: -", skin);

        table.add(new Label("Select Weapon:", skin)).left().padTop(10).padRight(10);
        table.add(weaponSelectBox).width(150).left().padTop(10);
        table.add(weaponDamageLabel).padLeft(10).padTop(10);
        table.add(weaponAmmoLabel).padLeft(10).padTop(10).row();

        SelectBox<String> durationSelectBox = new SelectBox<>(skin);
        durationSelectBox.setItems("2.5", "5", "10", "20");
        table.add(new Label("Duration (min):", skin)).left().padTop(10).padRight(10);
        table.add(durationSelectBox).width(150).left().padTop(10).colspan(3).row();

        TextButton playButton = new TextButton("Start Game", skin);
        TextButton backButton = new TextButton("Back", skin);
        Table buttonRow = new Table();
        buttonRow.add(playButton).pad(20);
        buttonRow.add(backButton).pad(20);
        table.add(buttonRow).colspan(4).padTop(30);


        heroSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateHeroDetails();
            }
        });

        weaponSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateWeaponDetails();
            }
        });

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.handleStartGame(
                    player,
                    heroSelectBox.getSelected(),
                    weaponSelectBox.getSelected().getName(), // ارسال نام سلاح
                    Float.parseFloat(durationSelectBox.getSelected())
                );
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.handleBack(player);
            }
        });

        updateHeroDetails();
        updateWeaponDetails();
    }

    private void updateHeroDetails() {
        String selectedHero = heroSelectBox.getSelected().toLowerCase();
        int hp = 0, speed = 0;
        switch (selectedHero) {
            case "shana":   hp = 4; speed = 4; break;
            case "diamond": hp = 7; speed = 1; break;
            case "scarlet": hp = 3; speed = 5; break;
            case "lilith":  hp = 5; speed = 3; break;
            case "dasher":  hp = 2; speed = 10; break;
        }
        heroHpLabel.setText("HP: " + hp);
        heroSpeedLabel.setText("Speed: " + speed);
    }

    private void updateWeaponDetails() {
        Weapon selectedWeapon = weaponSelectBox.getSelected();
        weaponDamageLabel.setText("Damage: " + (int)selectedWeapon.getDamage());
        weaponAmmoLabel.setText("Ammo: " + selectedWeapon.getMagazineSize());
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
        if (backgroundTexture != null) backgroundTexture.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
