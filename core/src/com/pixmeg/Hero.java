package com.pixmeg;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Hero extends Sprite {
    private DemoScreen demoScreen;
    public Body body;
    private World world;

    public States currentState, previousState;

    private TextureRegion region;

    public boolean movingForward;

    public Hero(DemoScreen demoScreen, TextureRegion region){
        this.demoScreen = demoScreen;
        world = demoScreen.getWorld();

        currentState = States.IDLE;
        previousState = currentState;

        this.region = region;

        body = createBody();

        setRegion(region);

        setBounds(body.getPosition().x*Constants.PPM - getRegionWidth()/2,body.getPosition().y*Constants.PPM - getRegionHeight()/2+16,getRegionWidth(),getRegionHeight());


    }

    public void update(float delta){
        if(!movingForward){
            flip(true,false);
        }
        setBounds(body.getPosition().x*Constants.PPM - getRegionWidth()/2,body.getPosition().y*Constants.PPM - getRegionHeight()/2+16,getRegionWidth(),getRegionHeight());


    }

    public Body createBody(){
        Body body;
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(30/ Constants.PPM,80/Constants.PPM));
        body = world.createBody(bdef);
        body.setFixedRotation(true);


        PolygonShape shape = new PolygonShape();
        shape.setAsBox(Constants.HERO_WIDTH/Constants.PPM,Constants.HERO_HEIGHT/Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.friction = 1;
        fdef.filter.categoryBits = Constants.HERO_BIT;
        fdef.filter.maskBits = Constants.PLATFORM_BIT ;

        body.createFixture(fdef);

        shape.dispose();

        return body;

    }
}
