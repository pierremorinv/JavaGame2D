package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.character.Character;
import com.mygdx.character.Monster;
import com.mygdx.character.Player;
import com.mygdx.character.Stat;
import com.mygdx.game.room.Room;
import com.mygdx.interfaces.ChestInterface;
import com.mygdx.game.room.Tile;
import com.mygdx.interfaces.MenuInterface;
import com.mygdx.interfaces.TextureType;
import com.mygdx.item.*;
import com.mygdx.item.Supplies.Potion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyGdxGame extends ApplicationAdapter implements ApplicationListener {
    SpriteBatch batch;
    List<Tile> tileList;
    static Room actualRoom;
    Animation<TextureRegion> animation;
    float elapsed;
    Player player;
    public boolean moneyWon = false;
    Monster monster;
    Game game;
    ChestInterface chestInterface;

    float timeSeconds = 0f;
    float period = 2f;
    float timeSeconds2 = 0f;
    float timeSeconds3 = 0f;
    float period2 = 1f;
    boolean attacker = true;
    BitmapFont font;

    int dammageDeal;
    Sprite heroSprite;

    Sprite womenSprite;
    Sprite monsterSprite;

    Sprite littlePotion;
    Sprite bigPotion;
    List<Sprite> chestSprites;
    List<Sprite> itemsSprites;

    List<Integer> playerAttacks = new ArrayList<>();

    List<Integer> monsterAttacks = new ArrayList<>();
    List<BitmapFont> itemsPrices = new CopyOnWriteArrayList<>();
    Chest chest;

    Color itemSelectedColor = Color.GREEN;
    Boolean youWillDieAudioPlayed = false;
    Boolean heroDiedAudioPlayed = false;

    boolean monsterDiedAudioPlayed = false;
    boolean womenAudioPlayed = false;
    boolean musicWinPlayed = false;

    boolean isRoomLoreDisplayed, isMonsterLoreDisplayed;
    float timeBeforeDeath = 0f;
    float deathDelay = 1f;
    float delayWomen = 3f;

    InputAdapter inputAdapter;
    FreeTypeFontGenerator generator;
    MenuInterface menu;

    boolean up, down, left, right;

    boolean potionTook = false;
    Potion potion;
    Sprite spritePotion;
    BitmapFont fontMonsterLore;


    Music musicTheme;

    Music musicDungeon;
    Music musicLava;
    Music musicWin;

    //    FreeTypeFontGenerator.FreeTypeFontParameter parameter;
//    BitmapFont fontHP;
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        game = new Game(6);
        actualRoom = game.getRooms().get(0);
        game.play(actualRoom);
        System.out.println("Yo o" + game.getPlayer());
        player = game.getPlayer();
        player.setX(player.getPosition().getX());
        player.setY(player.getPosition().getY());
        heroSprite = new Sprite(new Texture(player.getPathToAsset()));


        musicTheme = Gdx.audio.newMusic(Gdx.files.internal("soundEffects/forestMusic.mp3"));
        musicTheme.setLooping(true);
        musicTheme.setVolume(0.3f);
        musicTheme.play();


        womenSprite = new Sprite(new Texture(Gdx.files.internal("character/women.png")));
        womenSprite.setPosition(actualRoom.getNeighborsWalkable(player.getPosition(), 1).get(0).getX(), actualRoom.getNeighborsWalkable(player.getPosition(), 1).get(0).getY());
        womenSprite.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight());

        monster = actualRoom.getMonster();
        monsterSprite = new Sprite(new Texture(actualRoom.getMonster().getPathToAsset()));

        chest = new Chest(actualRoom.getRoomNumber(), 10);
        chestInterface = new ChestInterface(this.player, batch, 2, this.chest);
        chestSprites = chestInterface.displayChestInterface();

        menu = new MenuInterface(player, batch);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("hpFont.ttf"));

        potion = actualRoom.generateAPotion();

        Timer.schedule(new Timer.Task() {
                           @Override
                           public void run() {
                               if (!player.isInFight() && !monster.isDead()) {
                                   monster.move(actualRoom, monster.getPosition());
                               }
                           }
                       }
                , 1        //    (delay)
                , monster.getVivacity() //    (seconds)
        );
        inputAdapter = new InputAdapter() {

            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                player.move(actualRoom, player.getPosition(), x, Gdx.graphics.getHeight() - y);
                heroSprite = new Sprite(new Texture(player.getPathToAsset()));
                if (player.isInChest() && chestInterface.handleClick(x, y)) {
                    itemSelectedColor = Color.GREEN;
                } else {
                    itemSelectedColor = Color.RED;
                }
                if (player.isInMenu()) {
                    menu.handleClick(x, Gdx.graphics.getHeight() - y);
                }
                chestSprites = chestInterface.displayChestInterface();
                return true;
            }

            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                        up = true;
                        break;

                    case Input.Keys.A:
                        left = true;
                        break;
                    case Input.Keys.S:
                        down = true;
                        break;
                    case Input.Keys.D:
                        right = true;
                        break;
                    case Input.Keys.I:
                        player.setInMenu(!player.isInMenu());
                        break;
                    default:
                        System.out.println("not the right key");
                }


                heroSprite = new Sprite(new Texture(player.getPathToAsset()));
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                        up = false;

                        break;
                    case Input.Keys.A:
                        left = false;
                        break;
                    case Input.Keys.S:
                        down = false;
                        break;
                    case Input.Keys.D:
                        right = false;
                        break;
                    case Input.Keys.C:
                        if (monster.isDead()) {
                            player.setInChest(!player.isInChest());
                        }
                        break;
                    default:
                        break;
                }
                return super.keyUp(keycode);
            }
        };

    }

    public void drawFloor() {
        for (Tile tile : actualRoom.getTiles()) {
            Sprite sprite = new Sprite(new Texture(tile.getPathToAsset()));
            sprite.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight());
            sprite.setPosition(tile.getX(), tile.getY());
            sprite.draw(batch);
            //            Sprite bigPotion = new Sprite(new Texture("item/supplies/bigPotion.png"));
            //            Sprite littlePotion = new Sprite(new Texture("item/supplies/smallPotion.png"));

        }
    }

    @Override
    public void render() {
        batch.begin();
        Gdx.input.setInputProcessor(inputAdapter);
        drawFloor();
        Sprite chatSprite = new Sprite(new Texture("chat.png"));
        chatSprite.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()/8);
        chatSprite.setPosition(0,Gdx.graphics.getHeight()-Gdx.graphics.getHeight()/8);
        chatSprite.draw(batch);
        FreeTypeFontGenerator.FreeTypeFontParameter parameterLore = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterLore.color = Color.WHITE;
        parameterLore.size = 13;
        BitmapFont fontRoomLore = generator.generateFont(parameterLore);
        fontRoomLore.draw(batch, actualRoom.getMessage(), Gdx.graphics.getWidth()/4, chatSprite.getY()+60);
        monsterSprite.draw(batch);
        heroSprite.draw(batch);
        heroSprite.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight());
        heroSprite.setPosition(player.getX(), player.getY());

        if (actualRoom.getRoomNumber() == game.getDifficulty()) {
            monsterSprite.setSize(actualRoom.getRelativeWidth() * 2, actualRoom.getRelativeHeight() * 2);
        } else {
            monsterSprite.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight());

        }
        monsterSprite.setPosition(monster.getPosition().getX(), monster.getPosition().getY());

        player.canFight(monster.getPosition().isNeighbor(actualRoom, player.getPosition()) && !monster.isDead() && !player.isDead());
        if (player.isInFight()) {
            BitmapFont fontMonsterLore = generator.generateFont(parameterLore);
            fontMonsterLore.draw(batch, actualRoom.getMonster().getMessage(), Gdx.graphics.getWidth()/4, chatSprite.getY()+30);
            if (!youWillDieAudioPlayed && actualRoom.getRoomNumber() == game.getDifficulty()) {
                Sound youWillDieAudio = Gdx.audio.newSound(Gdx.files.internal("soundEffects/youWillDie.wav"));
                youWillDieAudio.play(1.0f);
                youWillDieAudioPlayed = true;

            } else if (!youWillDieAudioPlayed) {
                Sound randomMonsterAudio = Gdx.audio.newSound(Gdx.files.internal("soundEffects/randomMonster.mp3"));
                randomMonsterAudio.play(1.0f);
                youWillDieAudioPlayed = true;
            }
            FreeTypeFontGenerator.FreeTypeFontParameter parameterMonster = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameterMonster.color = Color.RED;
            parameterMonster.size = 25 + monster.getStat().get(Stat.HP);
            BitmapFont fontMonster = generator.generateFont(parameterMonster);
            fontMonster.draw(batch, String.valueOf(monster.getStat().get(Stat.HP)), monster.getPosition().getX() - actualRoom.getRelativeWidth() / 2, monster.getPosition().getY() + actualRoom.getRelativeHeight());

            FreeTypeFontGenerator.FreeTypeFontParameter paramaterPlayer = new FreeTypeFontGenerator.FreeTypeFontParameter();
            paramaterPlayer.size = 25;
            BitmapFont fontPlayerHP = generator.generateFont(paramaterPlayer);
            fontPlayerHP.draw(batch, String.valueOf(player.getStat().get(Stat.HP)), player.getPosition().getX() - actualRoom.getRelativeWidth() / 2, player.getPosition().getY() + actualRoom.getRelativeHeight());


            Sprite heroLifeBar = new Sprite(new Texture("character/blueBar" + player.calculateLifeDividedBy4() + ".png"));
            Sprite monsterLifeBar = new Sprite(new Texture("character/redBar" + monster.calculateLifeDividedBy4() + ".png"));

            heroLifeBar.setPosition(player.getPosition().getX(), player.getPosition().getY() + actualRoom.getRelativeHeight());
            monsterLifeBar.setPosition(monster.getPosition().getX(), monster.getPosition().getY() + actualRoom.getRelativeWidth());
            heroLifeBar.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight() / 2);
            monsterLifeBar.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight() / 2);
            heroLifeBar.draw(batch);
            monsterLifeBar.draw(batch);
            timeSeconds += Gdx.graphics.getDeltaTime();
            if (timeSeconds > period) {
                timeSeconds -= period;
                dammageDeal = fightRound(player, monster, attacker);
                attacker = !attacker;
            }
        }
        mooveCharacter();
        //TODO loose and win management
        if (monster.isDead()) {
            int money = 10;
            if (!moneyWon) {
                System.out.println("vous avez  " + player.getMoney());
                int moneyEarned = money + money * (actualRoom.getRoomNumber() / 3);
                player.addMoney(moneyEarned);
                System.out.println("vous etes dans la salle " + actualRoom.getRoomNumber());
                moneyWon = true;
                System.out.println("vous avez obtenu : " + moneyEarned + " vous avez maitenant " + player.getMoney());
            }
            monsterSprite = new Sprite(new Texture("character/death.png"));
            monsterSprite.setPosition(monster.getPosition().getX(), monster.getPosition().getY());
            monsterSprite.setSize(actualRoom.getRelativeWidth() / 2, actualRoom.getRelativeHeight() / 2);
            player.setInFight(false);

            actualRoom.setDoorOpen(monster.getPosition());
            Item dropedItem = monster.getDroped();
            if (dropedItem != null) {
                Sprite dropSprite = new Sprite(new Texture(dropedItem.getPathToAsset()));
                dropSprite.setPosition(monster.getX() + actualRoom.getRelativeWidth() / 2, monster.getY() + actualRoom.getRelativeWidth() / 2);
                dropSprite.setSize(actualRoom.getRelativeWidth() / 2, actualRoom.getRelativeHeight() / 2);
                dropSprite.draw(batch);
            }

            Sprite chestSprite = new Sprite(new Texture("chest_1.png"));
            chestSprite.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight());
            chestSprite.setPosition(actualRoom.getChestTile().getX(), actualRoom.getChestTile().getY());
            chestSprite.draw(batch);
            if (!potionTook) {
                spritePotion = new Sprite(new Texture(potion.getPathToAsset()));
                spritePotion.setSize(actualRoom.getRelativeWidth() / 2, actualRoom.getRelativeHeight() / 2);
                spritePotion.setPosition(actualRoom.getPotion().getPosition().getX() + actualRoom.getRelativeWidth() / 4, actualRoom.getPotion().getPosition().getY() + actualRoom.getRelativeWidth() / 4);
                spritePotion.draw(batch);

            }
            if (actualRoom.getPotion().getPosition().isNeighbor(actualRoom, player.getPosition()) && !potionTook) {
                player.setStat(potion.getStat(), player.getStat().get(Stat.HP) + potion.getNumber());
                System.out.println("you took a potion" + potion.getStat() + " " + potion.getNumber());
                potionTook = true;
            }

            Sound monsterDied = Gdx.audio.newSound(Gdx.files.internal("soundEffects/monsterDied.wav"));
            actualRoom.setDoorOpen(monster.getPosition());

            timeBeforeDeath += Gdx.graphics.getDeltaTime();
            if (timeBeforeDeath > deathDelay) {
                if (!monsterDiedAudioPlayed) {
                    monsterDied.play(1.0f);
                    monsterDiedAudioPlayed = true;
                }
                timeBeforeDeath = 0f;
            }
            youWillDieAudioPlayed = false;
        }
        if (player.isDead()) {
            heroSprite = new Sprite(new Texture("character/heroDied.png"));
            heroSprite.setPosition(player.getPosition().getX(), player.getPosition().getY());
            heroSprite.setSize(actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight());
            player.setInFight(false);
            timeBeforeDeath += Gdx.graphics.getDeltaTime();
            if (timeBeforeDeath > deathDelay) {
                if (!heroDiedAudioPlayed) {
                    Sound heroDied = Gdx.audio.newSound(Gdx.files.internal("soundEffects/heroDied.wav"));
                    heroDied.play(1.0f);
                    heroDiedAudioPlayed = true;
                }
                timeBeforeDeath = 0f;
            }

        }
        if (game.isWin()) {
            //TODO : add a win screen
            womenSprite.draw(batch);

           if(!musicWinPlayed) {
               musicTheme.stop();
               musicWin = Gdx.audio.newMusic(Gdx.files.internal("soundEffects/winMusic.mp3"));
               musicWin.setLooping(true);
               musicWin.setVolume(0.3f);
               musicWin.play();
               musicWinPlayed = true;

           }


            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (!womenAudioPlayed) {
                        Sound womenAudio = Gdx.audio.newSound(Gdx.files.internal("soundEffects/womenAudio2.wav"));
                        womenAudio.play(1.0f);
                        womenAudioPlayed = true;
                    }
                }
            }, delayWomen);

        } else if (actualRoom.isDoorOpen() && actualRoom.getNeighbors(actualRoom.getExitTile(), 1).contains(player.getPosition())) {
            player.setInChest(false);
            moneyWon = false;
            actualRoom = game.nextRoom(actualRoom);



                int nbChange = Game.getDifficulty()/3;
                if(actualRoom.getRoomNumber()>2*nbChange){
                    musicTheme.stop();
                    musicTheme = Gdx.audio.newMusic(Gdx.files.internal("soundEffects/lavaDungeonMusic.mp3"));

                }else if(actualRoom.getRoomNumber()>nbChange){
                    musicTheme.stop();
                    musicTheme = Gdx.audio.newMusic(Gdx.files.internal("soundEffects/dungeonMusic.mp3"));

                }
                else{
                    System.out.println("heloo");
                }
            musicTheme.setLooping(true);
            musicTheme.play();




            potion = actualRoom.generateAPotion();
            potionTook = false;
            game.play(actualRoom);
            System.out.println(actualRoom.getRoomNumber());
            chest = new Chest(actualRoom.getRoomNumber(), 10);
            chestInterface = new ChestInterface(this.player, batch, 2, this.chest);
            chestSprites = chestInterface.displayChestInterface();
            itemsSprites = chestInterface.getItemSprites();
            monster = actualRoom.getMonster();
            monsterSprite = new Sprite(new Texture(actualRoom.getMonster().getPathToAsset()));
            monsterDiedAudioPlayed = false;
            moneyWon = false;

        }
        //TODO: implement the chest interaction in game (set true for dev mode)
        if (player.isInChest()) {
            for (Sprite allChestSprite : chestSprites) {
                allChestSprite.draw(batch);
            }
            itemsPrices = chestInterface.displayWeaponCost();
            int[] coords = chestInterface.coordItemSelected(chestInterface.getSpriteSelected());
            chestInterface.displayAllMoney();
            Sprite easterEgs = new Sprite(new Texture("nem.jpg"));
            easterEgs.setSize(0, 0);
            easterEgs.draw(batch);
            if (coords != null) {
                ShapeRenderer shapeRenderer = new ShapeRenderer();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(itemSelectedColor);
                shapeRenderer.rect(coords[0], coords[1], coords[2], coords[3]);
                shapeRenderer.end();
            }
        }
        if (player.isInMenu()) {
            menu.draw();
        }
        batch.end();
    }

    public void mooveCharacter() {

        int speed = actualRoom.getRelativeWidth() / 8;
        int newX = player.getX();
        int newY = player.getY();
        if (up) {
            player.move(actualRoom, newX, newY, newX, newY + speed);
        }
        if (down) {
            player.move(actualRoom, newX, newY, newX, newY - speed);
        }
        if (left) {
            player.move(actualRoom, newX, newY, newX - speed, newY);
        }
        if (right) {
            player.move(actualRoom, newX, newY, newX + speed, newY);
        }
    }

    public void fightHistory() {
        FreeTypeFontGenerator.FreeTypeFontParameter parameterAttacks = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameterAttacks.color = Color.WHITE;
        parameterAttacks.size = 16;
        BitmapFont fontAttacks = generator.generateFont(parameterAttacks);
        BitmapFont fontName = generator.generateFont(parameterAttacks);
        if (monsterAttacks != null && playerAttacks != null) {
            int index = 0;
            for (int att : monsterAttacks) {
                fontName.draw(batch, monster.getName(), actualRoom.getRelativeWidth(), actualRoom.getRelativeHeight() * (actualRoom.getHeight() - 2) - (index * actualRoom.getRelativeHeight() / 2));
                fontAttacks.draw(batch, String.valueOf(att), (int) actualRoom.getRelativeWidth() * 2, actualRoom.getRelativeHeight() * (actualRoom.getHeight() - 2) - (index * actualRoom.getRelativeHeight() / 2));
                index++;
            }
            index = 0;
            for (int att : playerAttacks) {
                fontName.draw(batch, player.getName(), actualRoom.getRelativeWidth() * 3, actualRoom.getRelativeHeight() * (actualRoom.getHeight() - 2) - (index * actualRoom.getRelativeHeight() / 2));
                fontAttacks.draw(batch, String.valueOf(att), (int) actualRoom.getRelativeWidth() * 3 * 1.5f, actualRoom.getRelativeHeight() * (actualRoom.getHeight() - 2) - (index * actualRoom.getRelativeHeight() / 2));
                index++;
            }
        }
    }

    public int fightRound(Character char1, Character char2, boolean isChar1) {
        int damage;
        if (isChar1) {
            damage = char1.attack(char2);
            playerAttacks.add(damage);
        } else {
            damage = char2.attack(char1);
            monsterAttacks.add(damage);
        }
        return damage;
    }

    public static int getActualRoomLevel() {
        return actualRoom.getRoomNumber();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}

