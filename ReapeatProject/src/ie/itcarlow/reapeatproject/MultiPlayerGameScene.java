package ie.itcarlow.reapeatproject;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MultiPlayerGameScene extends BaseScene implements IOnSceneTouchListener {
	//---------------------------------------------
    // VARIABLES
    //---------------------------------------------
	private AndroidWebSocketClient mWebSocketClient;
	private HUD gameHUD;
	private Text scoreLine;
	private Text enemyScoreLine;
	private PhysicsWorld physicsWorld;
	private Player player;
	private Player iosPlayer;
	private Ball ball;
	
	private boolean resetBall;
	private boolean gameStarted = false;
	
	private int myScore;
	private int enemyScore;
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float touchFromRight = pSceneTouchEvent.getX() - (player.getX() + player.getWidth());
		float touchFromLeft = pSceneTouchEvent.getX() - player.getX();
		
		//Touch to the right of the player
		if ((touchFromRight > 0) && (touchFromRight < 800)){
			player.setX(touchFromRight/10);
		}
		
		//Touch to the left of the player
		else if ((touchFromLeft < 0) && (touchFromLeft > -800)){
			player.setX(touchFromLeft/10);
		}
		return false;
	}

	@Override
	public void createScene() {
		mWebSocketClient = new AndroidWebSocketClient();
		mWebSocketClient.connectToServer();
		
		//createBackground();
	    //createPhysics();
	    //createHUD();
	    //createLevel();
	    //setOnSceneTouchListener(this);
	    //this.engine.registerUpdateHandler(this);
	}

	private void createLevel() {
		final FixtureDef fixDef = PhysicsFactory.createFixtureDef(0f,0f, 1.0f);
		
		Sprite roof = new Sprite(0,-10, resourceManager.boundry_region, engine.getVertexBufferObjectManager());
		Body bodyRoof = PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, fixDef);
		bodyRoof.setUserData("roof");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(roof, bodyRoof, true, true));
		attachChild(roof);
		
		Sprite floor = new Sprite(0,500, resourceManager.boundry_region, engine.getVertexBufferObjectManager());
		Body bodyFloor = PhysicsFactory.createBoxBody(physicsWorld, floor, BodyType.StaticBody, fixDef);
		bodyFloor.setUserData("floor");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(floor, bodyFloor, true, true));
		attachChild(floor);
		
		Sprite wallLeft = new Sprite(-10,0, resourceManager.wall_region, engine.getVertexBufferObjectManager());
		Body bodyWallLeft = PhysicsFactory.createBoxBody(physicsWorld, wallLeft, BodyType.StaticBody, fixDef);
		bodyWallLeft.setUserData("wallLeft");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(wallLeft, bodyWallLeft, true, true));
		attachChild(wallLeft);
		
		Sprite wallRight = new Sprite(800,0, resourceManager.wall_region, engine.getVertexBufferObjectManager());
		Body bodyWallRight = PhysicsFactory.createBoxBody(physicsWorld, wallRight, BodyType.StaticBody, fixDef);
		bodyWallRight.setUserData("wallRight");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(wallRight, bodyWallRight, true, true));
		attachChild(wallRight);
		
		player = new Player(360, 450, vbom, physicsWorld);
		attachChild(player);
		
		ball = new Ball(390, 200, vbom, physicsWorld);
		attachChild(ball);
		ball.getBody().setLinearVelocity(new Vector2(0,0));
	}

	private void createHUD() {
		 gameHUD = new HUD();
		    //HUDSprite = new Sprite(0, 460, resourceManager.HUD_region, vbom);
		    //gameHUD.attachChild(HUDSprite);
		    camera.setHUD(gameHUD);
		    // CREATE SCORE TEXT
		    scoreLine = new Text(720, 460, resourceManager.gameFont, "Score: 123456", vbom);
		    scoreLine.setTextOptions(new TextOptions());
		    enemyScoreLine = new Text(5, 460, resourceManager.gameFont, "Lives: 123456", vbom);
		    enemyScoreLine.setTextOptions(new TextOptions());
		    
		    //scoreText.setAnchorCenter(0, 0);    
		    scoreLine.setText("Score: " + myScore);
		    enemyScoreLine.setText("Score: " + enemyScore);
		    gameHUD.attachChild(scoreLine);
		    gameHUD.attachChild(enemyScoreLine);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0.0f, 0.0f), false){
			@Override
			public void onUpdate(float pSecondsElapsed){
				super.onUpdate(pSecondsElapsed);
				sendUpdates();
				if (resetBall){
					resetBall();
				}
			}
		}; 
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
	}

	private void sendUpdates() {
		mWebSocketClient.sendBallPositions((int) ball.getX(), (int) ball.getY());
		mWebSocketClient.sendPlayerPosition((int) player.getX());
	}

	private ContactListener createContactListener() {
		ContactListener contactListener = new ContactListener(){
            public void beginContact(Contact contact){
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null){
                	if ((x1.getBody().getUserData() == "player") && (x2.getBody().getUserData() == "ball")){
                		ball.setXVelocity((ball.getX() + (ball.getWidth()/2)) - (player.getX() + (player.getWidth()/2)));
                		ball.bounce(true);
                	}
                	
                	if (((x1.getBody().getUserData() == "wallLeft") || (x1.getBody().getUserData() == "wallRight")) && (x2.getBody().getUserData() == "ball")){
                		ball.bounce(false);
                	}
                	
                	if ((x1.getBody().getUserData() == "roof") || (x1.getBody().getUserData() == "ball")){
                		myScore += 1;
                		resetBall = true;
                	}
                	
                	if (( x1.getBody().getUserData() == "floor") && (x2.getBody().getUserData() == "ball")){
                		enemyScore += 1;
                		resetBall = true;
                	}
                }
            }

			@Override
			public void endContact(Contact contact) {}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {}
        };
		return contactListener;
	}

	protected void resetBall() {
		this.engine.runOnUpdateThread(new Runnable(){
			public void run() {
				PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(ball);
				// Unregister the physics connector
				physicsWorld.unregisterPhysicsConnector(physicsConnector);
				physicsWorld.destroyBody(physicsConnector.getBody());
			}
		});
		resetBall = false;
		ball = new Ball(390, 200, vbom, physicsWorld);
		attachChild(ball);
	}

	private void createBackground() {
		attachChild(new Sprite(0, 0, resourceManager.game_background_region, vbom){
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MULTIPLAYER;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
		

}
