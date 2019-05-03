package com.pixmeg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class DemoScreen extends ScreenAdapter{

    private GameClass gameClass;
    private OrthographicCamera camera;
    private Viewport viewport;

    private SpriteBatch batch;
    private AssetManager manager;

    private World world;
    private Box2DDebugRenderer b2dr;

    private Body box;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private States state;
    private Array<TextureRegion> idleArray,runArray,jumpArray;
    private Animation<TextureRegion> idleAnime,runAnime,jumpAnime;

    private Hero hero;

    private float stateTimer;

    public DemoScreen(GameClass gameClass) {
        this.gameClass = gameClass;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(Constants.V_WIDTH,Constants.V_HEIGHT,camera);

        batch = gameClass.batch;
        manager = gameClass.manager;

        world = new World(new Vector2(0,-10f),true);
        b2dr = new Box2DDebugRenderer();

        map = new TmxMapLoader().load("map/level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map,1);

        new TileMapParser().ParseMap(world,map.getLayers().get("objects").getObjects());

        hero = new Hero(this,manager.get("images/texAtlas.atlas",TextureAtlas.class).findRegion("idle",1));

    }


    @Override
    public void show() {
        initAnimationArrays();

      //  box = createGround(240,0,480,20,true, Constants.GROUND_BIT);

        world.setContactListener(new WorldContactListener());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        moveHero();
        updateHeroTexture(delta);
        hero.update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        hero.draw(batch);
        batch.end();

      //  b2dr.render(world,camera.combined.scl(Constants.PPM));
        camera.update();

        world.step(1/60f,6,2);
    }


    private void moveHero(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            hero.body.applyLinearImpulse(new Vector2(0,40),hero.body.getWorldCenter(),true);
            hero.currentState = States.JUMP;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && hero.body.getLinearVelocity().x <= 2 && hero.currentState != States.JUMP){
            hero.body.applyLinearImpulse(new Vector2(2,0),hero.body.getWorldCenter(),true);
            hero.currentState = States.RUN;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && hero.body.getLinearVelocity().x >= -2 && hero.currentState != States.JUMP){
            hero.body.applyLinearImpulse(new Vector2(-2,0),hero.body.getWorldCenter(),true);
            hero.currentState = States.RUN;
        }
        else if(hero.body.getLinearVelocity().len() == 0 && hero.currentState != States.JUMP){
            hero.currentState = States.IDLE;
        }

        if(hero.currentState == States.JUMP && jumpAnime.isAnimationFinished(stateTimer)){
            hero.currentState = States.IDLE;
        }

    }


    private void updateHeroTexture(float delta){

        if(hero.body.getLinearVelocity().x < 0){
            hero.movingForward = false;
        }
        else if(hero.body.getLinearVelocity().x > 0){
            hero.movingForward = true;
        }

        stateTimer = hero.previousState == hero.currentState ? stateTimer+delta : 0;
        if(hero.previousState != hero.currentState){
            hero.previousState = hero.currentState;
        }

        if(hero.currentState == States.IDLE){
            TextureRegion region = idleAnime.getKeyFrame(stateTimer,true);
            hero.setRegion(region);
        }
        else if(hero.currentState == States.RUN){
            TextureRegion region = runAnime.getKeyFrame(stateTimer,true);
            hero.setRegion(region);
        }
        else if(hero.currentState == States.JUMP){
            TextureRegion region = jumpAnime.getKeyFrame(stateTimer,false);
            hero.setRegion(region);
        }

    }

    private void initAnimationArrays(){
        idleArray = new Array<TextureRegion>();
        runArray = new Array<TextureRegion>();
        jumpArray = new Array<TextureRegion>();

        TextureAtlas atlas = manager.get("images/texAtlas.atlas",TextureAtlas.class);

        for(int i = 1;i<=12;i++){
            idleArray.add(atlas.findRegion("idle",i));
        }

        for(int i = 1; i<=8;i++){
            runArray.add(atlas.findRegion("run",i));
        }

        for(int i = 1; i<=9;i++){
            jumpArray.add(atlas.findRegion("jump",i));
        }

        idleAnime = new Animation<TextureRegion>(1/10f,idleArray, Animation.PlayMode.LOOP);
        runAnime = new Animation<TextureRegion>(1/12f,runArray, Animation.PlayMode.LOOP);
        jumpAnime = new Animation<TextureRegion>(1/6f,jumpArray, Animation.PlayMode.NORMAL);

    }

    private Body createGround(float x, float y, float w, float h, boolean isStatic, Short categoryBits){
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
        fdef.filter.categoryBits = categoryBits;
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

    public World getWorld() {
        return world;
    }
}
