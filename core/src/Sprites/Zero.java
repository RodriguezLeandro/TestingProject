package Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.megamangame.MegamanMainClass;

import Screen.MainGameScreen;

/**
 * Created by Leandro on 04/01/2017.
 */

public class Zero extends Sprite {

    public World world;
    public Body body;

    public enum State {STANDING, WALKING, CROUCHING, FALLING, GETTINGHIT, JUMPING, HITTING, DYING};
    public State currentState;
    public State previousState;


    private TextureRegion zeroStand;
    private TextureRegion textureRegion;


    private Animation zeroWalking;
    private Animation zeroCrouching;
    private Animation zeroGettingHit;
    private Animation zeroJumping;
    private Animation zeroHitting;
    private Animation zeroDying;

    private float stateTimer;
    private boolean runningRight;
    private boolean shouldBeJumping;
    private boolean zeroIsDead;


    public Zero(MainGameScreen mainGameScreen){

        //Supuestamente con esto seleccionamos la region de nuestro MegamanAndEnemies SpriteSheet.
        //En realidad, deberiamos agarrar la region solo del ninja?(No estaria funcionando?).
        super(mainGameScreen.getTextureAtlas().findRegion("advnt_full"));

        //Obtenemos el mundo en el que el enemigo principal vivira.
        world = mainGameScreen.getWorld();

        //Inicializamos los estados de nuestro personaje.
        currentState = State.STANDING;

        //Inicializamos tambien el estado previo.
        previousState = State.STANDING;

        //Inicializamos el timer del estado de las animaciones.
        stateTimer = 0;

        //Decimos que zero no esta muerto al iniciar el juego.
        zeroIsDead = false;

        //Decimos que cuando arranca el juego el personaje mira a la izquierda.
        //Nota: como es el enemigo, debe estar mirando siempre hacia donde esta nuestro mc(main character).
        runningRight = false;

        //Creamos las animaciones de el malvado personaje contrario.
        crearAnimaciones();

        //Definimos y creamos las caracteristicas del body del enemigo.
        defineZero();

        //Buscamos la textura inicial, en la que nuestro enemigo se encuentra en Standing.
        zeroStand = new TextureRegion(getTexture(),1,1,32,64);

        //Definimos la posicion inicial de nuestro personaje enemigo.
        //En realidad, en update esto deberia de sobreescribirse siempre, no deberia ser necesario.
        setBounds(0,0,64 / MegamanMainClass.PixelsPerMeters ,128 / MegamanMainClass.PixelsPerMeters);

        //Con el metodo setRegion se dibujara nuestro personaje.
        setRegion(zeroStand);

    }

    public void update(float delta){
        //Aqui actualizamos la posicion de nuestro enemigo principal, para que se ...
        //corresponda con la posicion del fixture(y body) de nuestro personaje.

        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 + 7 / MegamanMainClass.PixelsPerMeters);

        //Tambien seleccionamos la textureregion que veremos en cada ciclo de renderizado.
        setRegion(getTextureRegion(delta));
    }


    public void crearAnimaciones(){

        //Declaramos el array que contendra los frames para crear animaciones.
        Array<TextureRegion> textureRegionFrames = new Array<TextureRegion>();

        //Al array de textureregions lo llenamos con las imagenes correspondientes para...
        //asi crear las animaciones.
        for(int i = 1; i <7;i++){
            //Creamos objetos textureregions y los agregamos al arraylist.
            textureRegionFrames.add(new TextureRegion(getTexture(),i * 32, 1,32,64));
        }
        //Creamos la animacion y queda guardada en memoria.
        zeroWalking = new Animation(0.1f,textureRegionFrames);
        //Liberamos el textureregion para poder utilizarlo nuevamente.
        textureRegionFrames.clear();

        //Realizamos la misma operacion unas 4 veces mas(para cada animacion nueva).

        for (int i = 7; i < 10; i++){
            textureRegionFrames.add(new TextureRegion(getTexture(),i * 32, 1, 32, 64));
        }
        zeroCrouching = new Animation(0.1f,textureRegionFrames);
        textureRegionFrames.clear();

        for (int i = 1; i < 3; i++){
            textureRegionFrames.add(new TextureRegion(getTexture(),i * 32, 65, 32, 64));
        }
        zeroGettingHit = new Animation(0.1f,textureRegionFrames);
        textureRegionFrames.clear();

        for (int i = 7; i < 10; i++){
            textureRegionFrames.add(new TextureRegion(getTexture(),i * 32, 65, 32, 64));
        }
        zeroJumping = new Animation(0.1f,textureRegionFrames);
        textureRegionFrames.clear();

        for (int i = 7; i < 10; i++){
            textureRegionFrames.add(new TextureRegion(getTexture(),i * 32 + 2, 129, 32, 64));
        }
        zeroHitting = new Animation(0.1f,textureRegionFrames);
        textureRegionFrames.clear();

        for (int i = 1; i < 3; i++) {
            textureRegionFrames.add(new TextureRegion(getTexture(), i * 32 - 4, 321, 38, 64));
        }
        zeroDying = new Animation(0.1f,textureRegionFrames);
        textureRegionFrames.clear();
    }

    public void defineZero(){

        //Creamos el bodydef de nuestro personaje.
        BodyDef bodyDef = new BodyDef();

        //Establecemos la posicion que tendra nuestro personaje.
        bodyDef.position.set(2000 / MegamanMainClass.PixelsPerMeters ,200 / MegamanMainClass.PixelsPerMeters);
        //Decidimos si es StaticBody, DynamicBody o KinematicBody.
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        //Creamos el cuerpo de nuestro personaje enemigo en nuestro mundo principal.
        body = world.createBody(bodyDef);

        //Creamos el fixtureDef de nuestro fixture.
        FixtureDef fixtureDef = new FixtureDef();

        //Creamos el Shape que utilizara nuestro fixturedef.(Polygonshape, Circleshape).
        CircleShape circleShape = new CircleShape();

        //Establecemos el radio de nuestro circulo.
        circleShape.setRadius(20 / MegamanMainClass.PixelsPerMeters);

        //Establecemos la posicion inicial de nuestro circulo.
        circleShape.setPosition(new Vector2(0, 10 / MegamanMainClass.PixelsPerMeters ));

        //Agregamos el shape al fixturedef.
        fixtureDef.shape = circleShape;

        //Agregamos el filtro de categoria(quien es nuestro personaje) de box2d.
        fixtureDef.filter.categoryBits = MegamanMainClass.ZERO_BIT;

        //Agregamos el filtro de mascara(a quien puede colisionar nuestro personaje).
        fixtureDef.filter.maskBits = MegamanMainClass.DEFAULT_BIT | MegamanMainClass.COIN_BIT |
                MegamanMainClass.FLYINGGROUND_BIT | MegamanMainClass.FLOOR_BIT |
                MegamanMainClass.MEGAMAN_BIT | MegamanMainClass.LAVA_BIT;

        //Creamos el fixture de nuestro body(con el fixturedef).
        body.createFixture(fixtureDef);

        //Cambiamos la posicion de nuestro circleshape(Esto es porque crearemos otro ...
        //fixture con el mismo fixtureshape).
        circleShape.setPosition(new Vector2(0,-32 / MegamanMainClass.PixelsPerMeters));

        //Creamos otro fixture asi nuestro body contiene dos circulos y es mas grande la colision.
        body.createFixture(fixtureDef);

        //Creamos un polygonShape para utilizarla como sensor al chocar aliados???.
        PolygonShape polygonShape = new PolygonShape();

        //Creamos una caja del tamaño de los dos circulos(body del personaje).
        //Al final no lo hacemos, preferimos crear un rectangulo customizado.
        //Igualmente dejamos comentado la manera de hacer la caja.
        //polygonShape.setAsBox(20 / MegamanMainClass.PixelsPerMeters,40 / MegamanMainClass.PixelsPerMeters);

        //Creamos un array de vector2 para crear un poligono con forma rectangular.

        Vector2[] vertices = new Vector2[4];

        //Creamos cada vector del array y les asignamos las correspondientes posiciones.
        vertices[0] = new Vector2(-20f / MegamanMainClass.PixelsPerMeters ,30f / MegamanMainClass.PixelsPerMeters);
        vertices[1] = new Vector2(20f / MegamanMainClass.PixelsPerMeters,30f / MegamanMainClass.PixelsPerMeters);
        vertices[2] = new Vector2(20f / MegamanMainClass.PixelsPerMeters,-53f / MegamanMainClass.PixelsPerMeters);
        vertices[3] = new Vector2(-20f / MegamanMainClass.PixelsPerMeters,-53f / MegamanMainClass.PixelsPerMeters);

        //Ponemos los vertices del array que conforman la forma poligonal.
        polygonShape.set(vertices);

        //Liberamos la memoria utilizada por el array.
        vertices = null;

        //Ponemos el shape en el fixturedef.
        fixtureDef.shape = polygonShape;

        //Decimos que nuestro shape es un sensor.
        fixtureDef.isSensor = true;

        //Creamos nuestro sensor, y decimos que el fixture se llamara mainNinja(personajePrincipal).
        body.createFixture(fixtureDef).setUserData("mainSorcerer");

    }

    //Funcion para modificar el estado de nuestro personaje desde pantalla principal.
    public void setState(State state){
        previousState = currentState;
        currentState = state;
    }

    public TextureRegion getTextureRegion(float delta){

        //Obtenemos el estado actual de nuestro enemigo.
        currentState = getState();

        //Vemos en que estado estamos, y que haremos a partir de esta informacion.
        switch (currentState){
            case JUMPING:
                //Si no estaba saltando, reiniciamos el stateTimer.
                if (previousState != State.JUMPING) {
                    stateTimer = 0;
                }
                //Si esta saltando, prendemos la animacion de salto.
                textureRegion = zeroJumping.getKeyFrame(stateTimer);
                //Si termina la animacion de salto
                break;
            case WALKING:
                //Si no estaba caminando, reiniciamos el stateTimer.
                if (previousState != State.WALKING) {
                    stateTimer = 0;
                }
                //Si esta caminando, prendemos la animacion de caminar.
                //Notese que en el segundo parametro del metodo getkeyframe, decimos...
                //que cuando finalize la animacion, comienze de nuevo en un bucle.
                textureRegion = zeroWalking.getKeyFrame(stateTimer,true);
                break;
            case FALLING:
                //Si no estaba cayendo, reiniciamos el stateTimer.
                if (previousState != State.FALLING) {
                    stateTimer = 0;
                }
                //Si esta callendo, mostramos la animacion de Standing.
                textureRegion = zeroStand;
                break;
            case STANDING:
                //Si esta en standing, mostramos la animacion de Standing.
                textureRegion = zeroStand;
                break;
            case GETTINGHIT:

                //Si no estaba siendo golpeado, reiniciamos el stateTimer.
                if (previousState != State.GETTINGHIT) {
                    stateTimer = 0;
                }
                //Si esta siendo lastimado, mostramos la animacion de IsGettingHit.
                textureRegion = zeroGettingHit.getKeyFrame(stateTimer);
                //Si la animacion de ser golpeado finaliza...
                if (zeroGettingHit.isAnimationFinished(stateTimer)){
                    shouldBeJumping = false;
                    //Y volvemos a parar a nuestro personaje con State.Standing.
                    currentState = State.STANDING;
                }
                break;
            //En el caso de Dying, luego vemos que hacemos(porque debe finalizar el juego);
            case DYING:

                //Si no estaba muriendo, reiniciamos el stateTimer.
                if (previousState != State.DYING) {
                    stateTimer = 0;
                }
                textureRegion = zeroDying.getKeyFrame(stateTimer);
                if (zeroDying.isAnimationFinished(stateTimer)){
                    //End of the game.
                }
                break;
            //Realizamos lo mismo con cada uno de los estados.
            case HITTING:
                //Si no estaba pegando, reiniciamos el stateTimer.
                if (previousState != State.HITTING) {
                    stateTimer = 0;
                }
                textureRegion = zeroHitting.getKeyFrame(stateTimer);
                if (zeroHitting.isAnimationFinished(stateTimer)){
                    shouldBeJumping = false;
                    currentState = State.STANDING;
                }
                break;
            case CROUCHING:
                //Si no estaba agachandose, reiniciamos el stateTimer.
                if (previousState != State.CROUCHING) {
                    stateTimer = 0;
                }
                textureRegion = zeroCrouching.getKeyFrame(stateTimer);
                if (zeroCrouching.isAnimationFinished(stateTimer)){
                    shouldBeJumping = false;
                    currentState = State.STANDING;
                }
                break;
            default:
                textureRegion = zeroStand;
                break;
        }

        //Verificamos para que lado esta mirando el personaje y si nuestra imagen ve para el mismo lado...
        //De no ser asi, damos vuelta las imagenes de nuestro animacion para que se vea en el lado correcto.
        if ((body.getLinearVelocity().x < 0 || !runningRight) && (!textureRegion.isFlipX())){
            textureRegion.flip(true,false);
            //Acordarse que si cambiamos la orientacion, hay que cambiar el booleano tambien.
            runningRight = false;
        }
        //Lo mismo que arriba.
        else if((body.getLinearVelocity().x > 0 || runningRight) &&(textureRegion.isFlipX())){
            textureRegion.flip(true,false);
            runningRight = true;
        }

        //Si no hemos cambiado de estado, entonces le agregamos tiempo a la animacion...
        //De lo contrario, establecemos el timer en 0.
        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        //Devolvemos el textureRegion que corresponde.
        return textureRegion;
    }

    public Boolean isDead(){
        if (zeroIsDead){
            return true;
        }
        else {
            return false;
        }
    }

    //Funcion que devuelve el estado de nuestro personaje.
    private State getState(){

        if (currentState == State.CROUCHING){
            return State.CROUCHING;
        }
        else if(currentState == State.GETTINGHIT){
            return State.GETTINGHIT;
        }
        else if (currentState == State.HITTING){
            return State.HITTING;
        }
        else if (currentState == State.DYING){
            return State.DYING;
        }
        //Si el jugador estaba saltando, aunque luego caiga devolvemos el State.Jumping.
        else if ((body.getLinearVelocity().y > 0 && shouldBeJumping) || (body.getLinearVelocity().y < 0 && previousState == State.JUMPING)){
            return State.JUMPING;
        }
        //Si el jugador cae, devolvemos State.Falling.
        else if (body.getLinearVelocity().y < 0){
            shouldBeJumping = true;
            return State.FALLING;
        }
        //Si el jugador camina, devolvemos State.Walking
        else if (body.getLinearVelocity().x != 0) {
            return State.WALKING;
        }
        //Si el jugador esta parado, devolvemos State.Standing.
        else{
            return State.STANDING;
        }

    }
}
