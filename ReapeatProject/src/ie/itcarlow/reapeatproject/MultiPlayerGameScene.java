package ie.itcarlow.reapeatproject;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ContactListener;

public class MultiPlayerGameScene extends BaseScene implements IOnSceneTouchListener {
	//---------------------------------------------
    // VARIABLES
    //---------------------------------------------
	private AndroidWebSocketClient mWebSocketClient;
	private HUD gameHUD;
	private PhysicsWorld physicsWorld;
	private Player player;
	private Player iosPlayer;
	
	private boolean resetBall;
	
	private int myScore;
	private int enemyScore;
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createScene() {
		mWebSocketClient = new AndroidWebSocketClient();
		mWebSocketClient.connectToServer();
		
		createBackground();
	    createPhysics();
	    createHUD();
	    createLevel();
	    setOnSceneTouchListener(this);
	    this.engine.registerUpdateHandler(this);
	}

	private void createLevel() {
		// TODO Auto-generated method stub
		
	}

	private void createHUD() {
		// TODO Auto-generated method stub
		
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0.0f, 0.0f), false){
			@Override
			public void onUpdate(float pSecondsElapsed){
				super.onUpdate(pSecondsElapsed);
				//removeObjectsSetForDestruction();
				removeBricksSetForDestruction();
				if (resetBall){
					resetBall();
				}
			}
		}; 
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
	}

	private ContactListener createContactListener() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void resetBall() {
		// TODO Auto-generated method stub
		
	}

	protected void removeBricksSetForDestruction() {
		// TODO Auto-generated method stub
		
	}

	private void createBackground() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SceneType getSceneType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
		

}
