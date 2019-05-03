package com.pixmeg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameClass extends Game {
	public SpriteBatch batch;
	public AssetManager manager;

	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("images/texAtlas.atlas", TextureAtlas.class);
		manager.finishLoading();
		setScreen(new MainScreen(this));

	}

	@Override
	public void dispose () {
		batch.dispose();
		manager.dispose();
	}
}
