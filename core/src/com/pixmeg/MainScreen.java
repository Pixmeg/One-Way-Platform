package com.pixmeg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainScreen extends ScreenAdapter {
    private GameClass gameClass;
    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Body hero, platform;

    public MainScreen(GameClass gameClass) {
        this.gameClass = gameClass;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Constants.V_WIDTH,Constants.V_HEIGHT,camera);

        world = new World(new Vector2(0,0f),true);
        b2dr = new Box2DDebugRenderer();
    }


    @Override
    public void show() {
        hero = createHero(240,380,50,50,false);
        platform = createPlatform(240,300,200,20,true);

      //  world.setContactListener(new WorldContactListener());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            hero.applyForceToCenter(new Vector2(0,20),true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            hero.applyForceToCenter(new Vector2(0,-20),true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            hero.applyForceToCenter(new Vector2(-20,0),true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            hero.applyForceToCenter(new Vector2(20,0),true);
        }

        b2dr.render(world,camera.combined.scl(Constants.PPM));
        camera.update();

        world.step(1/60f,6,2);
    }

    private Body createHero(float x, float y, float w, float h, boolean isStatic){
        Body body;
        BodyDef bdef = new BodyDef();
        if(isStatic){
            bdef.type = BodyDef.BodyType.StaticBody;
        }
        else {
            bdef.type = BodyDef.BodyType.DynamicBody;
        }

        bdef.position.set(x/Constants.PPM,y/Constants.PPM);

        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/(2*Constants.PPM),h/(2*Constants.PPM));

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.restitution = 0.3f;
        fdef.filter.categoryBits = Constants.HERO_BIT;
        fdef.filter.maskBits = Constants.PLATFORM_BIT;

        body.createFixture(fdef);

        shape.dispose();

        return body;
    }

    private Body createPlatform(float x, float y, float w, float h, boolean isStatic){
        Body body;
        BodyDef bdef = new BodyDef();
        if(isStatic){
            bdef.type = BodyDef.BodyType.StaticBody;
        }
        else {
            bdef.type = BodyDef.BodyType.DynamicBody;
        }

        bdef.position.set(x/Constants.PPM,y/Constants.PPM);

        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/(2*Constants.PPM),h/(2*Constants.PPM));

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.filter.categoryBits = Constants.PLATFORM_BIT;
        fdef.filter.maskBits = Constants.HERO_BIT;

        body.createFixture(fdef);

        shape.dispose();

        return body;
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width,height,true);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
