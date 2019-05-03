package com.pixmeg;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class TileMapParser {

    public static void ParseMap(World world, MapObjects objects){
        for(MapObject object:objects) {
            if (object instanceof RectangleMapObject) {
                Vector2 position = new Vector2();
                ((RectangleMapObject) object).getRectangle().getPosition(position);

                BodyDef bdef = new BodyDef();
                bdef.position.set((position.x + ((RectangleMapObject) object).getRectangle().getWidth() / 2) / Constants.PPM, (position.y + ((RectangleMapObject) object).getRectangle().getHeight() / 2) / Constants.PPM);
                bdef.type = BodyDef.BodyType.StaticBody;
                Body body = world.createBody(bdef);

                Shape shape = createRectangle((RectangleMapObject) object);

                FixtureDef fdef = new FixtureDef();
                fdef.shape = shape;
                fdef.density = 1;
                fdef.filter.categoryBits = Constants.PLATFORM_BIT;
                fdef.filter.maskBits = Constants.HERO_BIT;

                body.createFixture(fdef);
                shape.dispose();
            }
        }

    }

    private static PolygonShape createRectangle(RectangleMapObject object){

        float width = object.getRectangle().getWidth();
        float height = object.getRectangle().getHeight();

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width/(2*Constants.PPM),height/(2*Constants.PPM));

        return polygonShape;
    }
}
