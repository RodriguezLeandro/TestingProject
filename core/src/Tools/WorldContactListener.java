package Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.megamangame.MegamanMainClass;

import Sprites.Fireball;
import Sprites.Lava;
import Sprites.Megaman;
import Sprites.Zero;

/**
 * Created by Leandro on 03/01/2017.
 */

public class WorldContactListener implements ContactListener {


    Fixture fixtureBody;
    Fixture fixtureObject;

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int categoryDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (categoryDefinition){

            case MegamanMainClass.MEGAMAN_SENSOR_BIT | MegamanMainClass.COIN_BIT :

                fixtureBody = fixtureA.getUserData().getClass() == Megaman.class ? fixtureA : fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB : fixtureA;

                if (fixtureObject.getUserData() instanceof InteractiveTileObject){
                    ((InteractiveTileObject) fixtureObject.getUserData()).onBodyHit();
                }

                break;

            case MegamanMainClass.MEGAMAN_SENSOR_BIT | MegamanMainClass.LAVA_BIT :

                fixtureBody = fixtureA.getUserData().getClass() == Megaman.class ? fixtureA: fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB: fixtureA;

                if (fixtureObject.getUserData() instanceof  InteractiveTileObject){
                    ((Lava) fixtureObject.getUserData()).onBodyHit();
                }

                break;

            case MegamanMainClass.ZERO_SENSOR_BIT | MegamanMainClass.FIREBALL_MEGAMAN_SENSOR_BIT:

                fixtureBody = fixtureA.getUserData().getClass() == Zero.class ? fixtureA: fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB: fixtureA;

                //Basicamente, si la bola de fuego va hacia la derecha manda true, sino false.
                    if (((Fireball)fixtureObject.getUserData()).fireToRight) {
                        ((Zero) fixtureBody.getUserData()).onBodyHit(true);
                    }else{
                        ((Zero) fixtureBody.getUserData()).onBodyHit(false);
                    }
                //Luego, que la bola no puedo volver a pegarle por unos 3 segundos.
                ((Zero)fixtureBody.getUserData()).setZeroUntouchableDot5Seconds();

                break;

            case MegamanMainClass.MEGAMAN_SENSOR_BIT | MegamanMainClass.FIREBALL_ZERO_SENSOR_BIT:
                fixtureBody = fixtureA.getUserData().getClass() == Megaman.class ? fixtureA: fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB: fixtureA;

                //Basicamente, si la bola de fuego va hacia la derecha manda true, sino false.
                if (((Fireball)fixtureObject.getUserData()).fireToRight) {
                    ((Megaman) fixtureBody.getUserData()).onBodyHit();
                }else{
                    ((Megaman) fixtureBody.getUserData()).onBodyHit();
                }
                break;

            case MegamanMainClass.FIREBALL_MEGAMAN_SENSOR_BIT | MegamanMainClass.ZERO_SENSOR_BIT_2:
                fixtureBody = fixtureA.getUserData().getClass() == Zero.class ? fixtureA: fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB: fixtureA;

                ((Zero)fixtureBody.getUserData()).setZeroShouldJump();

                break;

            case MegamanMainClass.MEGAMAN_SENSOR_BIT | MegamanMainClass.ZERO_SENSOR_BIT:
                fixtureBody = fixtureA.getUserData().getClass() == Megaman.class ? fixtureA: fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB: fixtureA;
                ((Megaman) fixtureBody.getUserData()).onBodyHit();

                break;

            case MegamanMainClass.MEGAMAN_SENSOR_BIT | MegamanMainClass.ENEMY_BIT:

                fixtureBody = fixtureA.getUserData().getClass() == Megaman.class ? fixtureA: fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB: fixtureA;

                //Si megaman es golpeado por la derecha, entonces impulso por la derecha,
                //De lo contrario, impulso por la izquierda.
                if (((Megaman) fixtureBody.getUserData()).body.getPosition().x < ((Enemy) fixtureObject.getUserData()).body.getPosition().x) {
                    //Mandamos true si es impulso a  la izquierda, de lo contrario mandamos false.
                    //Tambien le voy a mandar el body del enemigo que lo choco, para que se separen.
                    ((Megaman) fixtureBody.getUserData()).onBodyHitLower(true);
                }
                else {
                    ((Megaman) fixtureBody.getUserData()).onBodyHitLower(false);
                }
                break;

            case MegamanMainClass.FIREBALL_MEGAMAN_SENSOR_BIT| MegamanMainClass.ENEMY_BIT:

                fixtureBody = fixtureA.getUserData().getClass() == Fireball.class ? fixtureA: fixtureB;
                fixtureObject = fixtureBody == fixtureA ? fixtureB: fixtureA;
                ((Enemy) fixtureObject.getUserData()).onBodyHit();
                ((Fireball)fixtureBody.getUserData()).destroy();

                break;

            default:

                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int categoryDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch(categoryDefinition){

            case MegamanMainClass.MEGAMAN_SENSOR_BIT | MegamanMainClass.LAVA_BIT :
                if (fixtureObject.getUserData() instanceof  InteractiveTileObject){
                    ((Lava) fixtureObject.getUserData()).onBodyStopHit();
                }
                break;

            default:

                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
