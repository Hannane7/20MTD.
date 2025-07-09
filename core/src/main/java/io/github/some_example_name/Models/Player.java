package io.github.some_example_name.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Main;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String username;
    private String password;
    private String avatarPath;
    private String securityQuestion;
    private String securityAnswer;
    private String selectedCharacter;
    private List<Ability> unlockedAbilities = new ArrayList<>();
    private int score;
    private int kills = 0;
    private float surviveTime = 0;
    private int level = 1;
    private int xp = 0;
    private int xpToNextLevel = 20;

    private transient Sprite playerSprite;
    private transient CollisionRect rect;
    private transient float posX = 0;
    private transient float posY = 0;
    private transient int playerHealth;
    private transient float speed = 4;
    private transient boolean moveUp = false, moveDown = false, moveLeft = false, moveRight = false;
    private transient CharacterAnimator animator;
    private transient CharacterAnimator.State animState = CharacterAnimator.State.IDLE;
    private transient float invincibleTimer = 0f;
    private transient float originalSpeed;
    private transient Ability currentAbility;
    private transient float damageMultiplier = 1f;
    private transient Weapon weapon;
    private transient float damagerTimer = 0f;
    private transient float speedyTimer = 0f;
    private transient int projectileBonus = 0;

    public Player() {
        // این کانستراктор خالی برای خواندن از JSON ضروری است
    }

    public Player(String username, String password) {
        this(username, password, "Shana");
    }

    public Player(String username, String password, String charName) {
        this.selectedCharacter = charName;
        this.animator = new CharacterAnimator(charName);
        this.posX = Gdx.graphics.getWidth() / 2f;
        this.posY = Gdx.graphics.getHeight() / 2f;
        this.username = username;
        this.password = password;
        this.avatarPath = "avatar/avatar5.png";
        this.playerHealth = 4;
        this.speed = 4;
        this.originalSpeed = this.speed;
        this.playerSprite = new Sprite(GameAssetManager.getCharacterTexture());
        this.playerSprite.setPosition(posX, posY);
        this.playerSprite.setSize(64, 64);
        this.rect = new CollisionRect(posX, posY, playerSprite.getWidth(), playerSprite.getHeight());
        this.originalSpeed = speed;
    }

    public void setPlayerHealth(int playerHealth) {
        this.playerHealth = playerHealth;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        this.originalSpeed = speed;
    }

    public void setSelectedCharacter(String charName) {
        this.selectedCharacter = charName;
        this.animator = new CharacterAnimator(charName);
    }

    public void update(float delta) {
        if (invincibleTimer > 0) invincibleTimer -= delta;
        boolean isMoving = moveUp || moveDown || moveLeft || moveRight;
        animState = isMoving ? CharacterAnimator.State.RUN : CharacterAnimator.State.IDLE;

        if (moveUp) posY += speed;
        if (moveDown) posY -= speed;
        if (moveLeft) posX -= speed;
        if (moveRight) posX += speed;

        if (animator != null) animator.update(delta, animState);
        surviveTime += delta;

        if (damagerTimer > 0) damagerTimer -= delta;
        if (speedyTimer > 0) {
            speedyTimer -= delta;
            if (speedyTimer <= 0) {
                speed = originalSpeed;
            }
        }
    }

    public void render(float centerX, float centerY) {
        if (animator != null) {
            float spriteWidth = animator.getWidth();
            float spriteHeight = animator.getHeight();
            animator.render(Main.getBatch(), centerX - spriteWidth / 2, centerY - spriteHeight / 2);

        } else if (playerSprite != null) {
            playerSprite.setPosition(centerX - playerSprite.getWidth() / 2, centerY - playerSprite.getHeight() / 2);
            playerSprite.draw(Main.getBatch());
        }

        rect.move(posX, posY);
    }

    public Vector2 getPositionCenter() {
        return new Vector2(posX + playerSprite.getWidth() / 2f,
            posY + playerSprite.getHeight() / 2f);
    }

    public void setMoveUp(boolean value) { this.moveUp = value; }
    public void setMoveDown(boolean value) { this.moveDown = value; }
    public void setMoveLeft(boolean value) { this.moveLeft = value; }
    public void setMoveRight(boolean value) { this.moveRight = value; }
    public void takeDamage(float dmg) { if (invincibleTimer <= 0) { playerHealth -= dmg; if (playerHealth < 0) playerHealth = 0; invincibleTimer = 1f; } }
    public void addKill() { kills++; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getAvatarPath() { return avatarPath; }
    public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    public void setScore(int score) { this.score = score; }
    public float getSurviveTime() { return surviveTime; }
    public void addSurviveTime(float delta) { surviveTime += delta; }
    public Sprite getPlayerSprite() { return playerSprite; }
    public void setPlayerSprite(Sprite playerSprite) { this.playerSprite = playerSprite; }
    public float getPosX() { return posX; }
    public void setPosX(float posX) { this.posX = posX; }
    public float getPosY() { return posY; }
    public void setPosY(float posY) { this.posY = posY; }
    public int getPlayerHealth() { return playerHealth; }
    public CollisionRect getRect() { return rect; }
    public void setRect(CollisionRect rect) { this.rect = rect; }
    public float getSpeed() { return speed; }
    public void addKills(int value) { this.kills += value; }
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public String getSelectedCharacter() { return selectedCharacter; }
    public Vector2 getPosition() { return new Vector2(posX, posY); }
    public void gainXP(int amount) { xp += amount; while (xp >= xpToNextLevel) { xp -= xpToNextLevel; level++; xpToNextLevel = 20 * level; grantRandomAbility();} }
    public int getLevel() { return level; }
    public int getXP() { return xp; }
    public int getXPToNextLevel() { return xpToNextLevel; }
    public Rectangle getBounds() { return new Rectangle(posX, posY, playerSprite.getWidth(), playerSprite.getHeight()); }
    public boolean isInvincible() { return invincibleTimer > 0; }
    public void setPos(float x, float y) { this.posX = x; this.posY = y; }
    public int getScore() {
        return (int) (surviveTime * kills);
    }
    public void setInvincible(float sec) { invincibleTimer = sec; }
    public boolean isDead() { return playerHealth <= 0; }
    public List<Ability> getUnlockedAbilities() { return unlockedAbilities; }
    public void addAbility(Ability ability) { if (!unlockedAbilities.contains(ability)) unlockedAbilities.add(ability); }
    public void setCurrentAbility(Ability ability) { this.currentAbility = ability; }
    public Ability getCurrentAbility() { return currentAbility; }

    private void grantRandomAbility() {
        List<Ability> candidates = new ArrayList<>();
        for (Ability ab : Ability.ALL_ABILITIES) {
            if (!unlockedAbilities.contains(ab)) {
                candidates.add(ab);
            }
        }
        if (candidates.isEmpty()) return;
        Ability chosen = candidates.get((int)(Math.random() * candidates.size()));
        unlockedAbilities.add(chosen);
        applyAbilityEffect(chosen);
        System.out.println("New ability unlocked: " + chosen.getName());
    }

    public void applyAbilityEffect(Ability ab) {
        switch (ab.getType()) {
            case VITALITY:
                playerHealth += 1;
                break;
            case DAMAGER:
                damagerTimer = 10f;
                break;
            case PROCREASE:
                projectileBonus++;
                break;
            case AMOCREASE:
                if (weapon != null) {
                    weapon.increaseMagazineSize(5);
                }
                break;
            case SPEEDY:
                speedyTimer = 10f;
                speed = originalSpeed * 2;
                break;
        }
    }

    public boolean isDamagerActive() {
        return this.damagerTimer > 0;
    }

    public int getProjectileBonus() {
        return this.projectileBonus;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }


    public void initializeTransientFields() {
        this.animator = new CharacterAnimator(this.selectedCharacter != null ? this.selectedCharacter : "SHANA");
        this.posX = Gdx.graphics.getWidth() / 2f;
        this.posY = Gdx.graphics.getHeight() / 2f;
        this.playerHealth = 4;
        this.speed = 4;
        this.originalSpeed = this.speed;
        this.playerSprite = new Sprite(GameAssetManager.getCharacterTexture());
        this.playerSprite.setPosition(posX, posY);
        this.playerSprite.setSize(64, 64);
        this.rect = new CollisionRect(posX, posY, playerSprite.getWidth(), playerSprite.getHeight());
    }


}
