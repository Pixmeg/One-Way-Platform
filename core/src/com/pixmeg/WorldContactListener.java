package com.pixmeg;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {

    private boolean movingOut;
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int collide = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (collide){
            case Constants.HERO_BIT | Constants.PLATFORM_BIT:

                int pointCount = contact.getWorldManifold().getNumberOfContactPoints();
                Vector2[] points = contact.getWorldManifold().getPoints();

                for(int i = 0;i<pointCount;i++){
                    points[i].scl(Constants.PPM);
                    Vector2 heroVel,plfVel;
                    if(fixA.getFilterData().categoryBits == Constants.HERO_BIT){
                        heroVel = fixA.getBody().getLinearVelocityFromWorldPoint(points[i]);
                        plfVel = fixB.getBody().getLinearVelocityFromWorldPoint(points[i]);
                    }
                    else {
                        heroVel = fixB.getBody().getLinearVelocityFromWorldPoint(points[i]);
                        plfVel = fixA.getBody().getLinearVelocityFromWorldPoint(points[i]);
                    }


                    Vector2 relVel = new Vector2(heroVel.x-plfVel.x,heroVel.y-plfVel.y);
                    if(relVel.y < 0)
                        return;
                }

                movingOut = true;

                break;
        }

    }

    @Override
    public void endContact(Contact contact) {

        movingOut = false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        if(movingOut){
            contact.setEnabled(false);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
