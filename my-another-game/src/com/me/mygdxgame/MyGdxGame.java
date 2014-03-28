package com.me.mygdxgame;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MyGdxGame implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture textureBackGround;
	public boolean availableAccelerometer = false;
	public boolean availableVibrator = false;
	Texture sphereTexture;//texture of sphere
	Rectangle boundingRectangle;//rectangle of collision
	float accelX, accelY;//data from accelerometer
	float k = 0.999f;//friction constant
	float speedX = 0, speedY = 0;//speed
	int screenWidth, screenHeight;
	float eps = 0.01f;//minimal number for extra small values 

	@Override
	public void create() {
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		availableAccelerometer = Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer);
		availableVibrator = Gdx.input.isPeripheralAvailable(Peripheral.Vibrator);
		
		camera = new OrthographicCamera();
		camera.setToOrtho(true, screenWidth, screenHeight);

		batch = new SpriteBatch();

		textureBackGround = new Texture(Gdx.files.internal("data/wood.png"));
		textureBackGround.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		/* Initializing our sphere */
		sphereTexture = new Texture(Gdx.files.internal("data/square.png"));
		sphereTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		boundingRectangle = new Rectangle();
		boundingRectangle.x = screenWidth / 2;
		boundingRectangle.y = 20;
		boundingRectangle.width = 16;
		boundingRectangle.height = 16;

	}

	@Override
	public void dispose() {
		batch.dispose();
		textureBackGround.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(textureBackGround, 0, 0);
		batch.draw(sphereTexture, boundingRectangle.x, boundingRectangle.y);
		batch.end();

		if(availableAccelerometer) {
			accelX = ((float) Math.round(Gdx.input.getAccelerometerX())) / 10;//hack for small amount of speed
			accelY = ((float) Math.round(Gdx.input.getAccelerometerY())) / 10;//hack for small amount of speed
			moveRectangle(accelY, accelX);
		} else {
			accelX = 0;
			accelY = 0;
			if(Gdx.input.isKeyPressed(Keys.RIGHT)) {
				accelX += 1;
			}
			if(Gdx.input.isKeyPressed(Keys.LEFT)) {
				accelX -= 1;
			}
			if(Gdx.input.isKeyPressed(Keys.UP)) {
				accelY -= 1;
			}
			if(Gdx.input.isKeyPressed(Keys.DOWN)) {
				accelY += 1;
			}
			moveRectangle(accelX, accelY);
		}
		
	}

	private void moveRectangle(float dx, float dy) {
		speedX += dx;
		speedY += dy;
		speedX *= k;
		speedY *= k;
		
		// checking for extra small values
		if (Math.abs(speedX) < eps)
			speedX = 0;		
		if (Math.abs(speedY) < eps)
			speedY = 0;
		
		
		if (boundingRectangle.x + speedX > 0 //checking boundaries
				&& boundingRectangle.x + speedX + boundingRectangle.width < screenWidth) {
			boundingRectangle.x += speedX;
		} else { //if boundary is intersected - revert speed
			if(availableVibrator)
				Gdx.input.vibrate(100);
			speedX = -speedX;
			if (boundingRectangle.x + speedX < 0) {
				boundingRectangle.x = 0;
			}
			if (boundingRectangle.x + speedX + boundingRectangle.width > screenWidth) {
				boundingRectangle.x = screenWidth - boundingRectangle.width;
			}
		}
		
		
		if (boundingRectangle.y + speedY > 0 //checking boundaries
				&& boundingRectangle.y + speedY + boundingRectangle.height < screenHeight) {
			boundingRectangle.y += speedY;
		} else {//if boundary is intersected - revert speed
			if(availableVibrator)
				Gdx.input.vibrate(100);			
			speedY = -speedY;
			if (boundingRectangle.y + speedY < 0) {
				boundingRectangle.y = 0;
			}
			if (boundingRectangle.y + speedY + boundingRectangle.height > screenHeight) {
				boundingRectangle.y = screenHeight - boundingRectangle.height;
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
