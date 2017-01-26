package Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.megamangame.MegamanMainClass;


import Sprites.Boss1;
import Sprites.Megaman;
import Tools.WorldCreator;

/**
 * Created by Leandro on 12/01/2017.
 */

public class Level2Screen extends MainGameScreen {

    private TiledMap tiledMap;

    private OrthogonalTiledMapRenderer mapRenderer;

    private WorldCreator worldCreator;

    private Boss1 boss1;


    public Level2Screen(MegamanMainClass game, LevelSelect levelSelect){
        super(game,levelSelect);

        tiledMap = tmxMapLoader.load("tiledmap/tiled2.tmx");

        //OrthogonalTiledMapRenderer se encarga del renderizado del mapa.
        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / game.PixelsPerMeters);

        if(MegamanMainClass.assetManager.isLoaded("audio/topman.mp3")) {
            Music music = MegamanMainClass.assetManager.get("audio/topman.mp3", Music.class);
            music.play();
            music.setLooping(true);

        }

        boss1 = new Boss1(this);

        worldCreator = new WorldCreator(this);

    }

    public void update(float delta){

        boss1.update(delta);

        //Si tocamos B, que el enemigo ataque con el hair(haier?);
        if (Gdx.input.isKeyJustPressed(Input.Keys.B)){
               boss1.createHairSpecialAttack();
        }
        //Si tocamos V, el enemigo ataca tambien con el haier.
        if (Gdx.input.isKeyJustPressed(Input.Keys.V)){
            boss1.createHairAttack();
        }

        //Si la pelea no se encuentra en la recta final.
        if (!stageInFinalBattle) {
            //Si el personaje sale de los limites izquierdos, no lo dejamos.
            if (megaman.body.getPosition().x < 10 / MegamanMainClass.PixelsPerMeters) {
                megaman.body.setTransform(new Vector2(10 / MegamanMainClass.PixelsPerMeters, megaman.body.getPosition().y), megaman.body.getAngle());
            }

            //Si el personaje se encuentra dentro de los limites del mundo, la camara lo sigue.
            if ((megaman.body.getPosition().x >= 400 / MegamanMainClass.PixelsPerMeters) && (megaman.body.getPosition().x <= 12525 / MegamanMainClass.PixelsPerMeters)) {

                //Hacemos que la camara tenga en el centro a nuestro personaje principal.
                mainCamera.position.x = megaman.body.getPosition().x;

            } else {
                //Logica: Si el cuerpo del personaje sale de los limites x del mundo, la camara queda fija.
                mainCamera.position.x = megaman.body.getPosition().x < (400 / MegamanMainClass.PixelsPerMeters) ? (399 / MegamanMainClass.PixelsPerMeters) : (12526 / MegamanMainClass.PixelsPerMeters);
               // stageInFinalBattle = true;
            }

            //Si megaman sale de los limites derechos de la pantalla, entramos en la batalla final.
            if (megaman.body.getPosition().x > 12896 / MegamanMainClass.PixelsPerMeters){
                megaman.body.setTransform(new Vector2(13100 / MegamanMainClass.PixelsPerMeters,megaman.body.getPosition().y),megaman.body.getAngle());
                mainCamera.position.x = 13332;
                stageInFinalBattle = true;
            }

            //De la misma manera deberiamos comprobar si el personaje sale del limite inferior del mapa...
            //y de esa manera eliminarlo(setToDead).
            if (megaman.body.getPosition().y <= 0) {
                megaman.setState(Megaman.State.DYING);
            }

        }else {
            //Si estamos en la batalla final.
            mainCamera.position.x = 13332 / MegamanMainClass.PixelsPerMeters;

        }

        mainCamera.update();

        mapRenderer.setView(mainCamera);
    }

    @Override
    public void setGravityModifyOn() {

    }

    @Override
    public void setGravityModifyOff() {

    }

    @Override
    public void setAddScore(Integer score) {

    }

    @Override
    public void show() {

    }

    public void render(float delta) {
        super.update(delta);

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        game.batch.setProjectionMatrix(mainCamera.combined);

        game.batch.begin();

        super.draw(game.batch);

        boss1.draw(game.batch);

        game.batch.end();

        box2DDebugRenderer.render(world, mainCamera.combined);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        hud.stage.draw();

        if (gameOver()) {
            levelSelectScreen.setLastLevelPlayed(2);
            game.setScreen(new GameOverScreen(game, hud.getScore(),levelSelectScreen));
            dispose();
        }

    }

    public World getWorld(){
        return world;
    }


    @Override
    public void dispose() {

    }

    @Override
    public TiledMap getTiledMap() {
        return tiledMap;
    }
}
