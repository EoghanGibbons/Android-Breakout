package ie.itcarlow.reapeatproject;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;

import java.util.Iterator;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameScene extends BaseScene implements IOnSceneTouchListener {
	//---------------------------------------------
    // VARIABLES
    //---------------------------------------------
	private Text scoreText;
	private HUD gameHUD;
	private Sprite HUDSprite;
	
	private PhysicsWorld physicsWorld;
	private ITextureRegion mBoundryTextureRegion;
	
	private Player player;
	private Ball ball;
	private boolean resetBall;
	
	private final int NROWS = 9;
	private final int NCOLS = 5;
	private Brick[][] bricks;
	
	public boolean gameOver = false;
	
	//---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------
	
	@Override
	public void createScene(){
		ResourceManager.gameMusic.play();
    	ResourceManager.gameMusic.setLooping(true);
	    createBackground();
	    createPhysics();
	    createLevel();
	    createHUD();
	    setOnSceneTouchListener(this);
	    this.engine.registerUpdateHandler(this);
	}

	//---------------------------------------------
    // INITIALIZERS
    //---------------------------------------------
	
	private void createPhysics(){
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0.0f, 0.0f), false){
			@Override
			public void onUpdate(float pSecondsElapsed){
				super.onUpdate(pSecondsElapsed);
				removeObjectsSetForDestruction();
				removeBricksSetForDestruction();
				if (resetBall){
					removeBallPhysicsConnector();
				}
			}
		}; 
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
	}
	
	private void createHUD(){
	    gameHUD = new HUD();
	    //HUDSprite = new Sprite(0, 460, resourceManager.HUD_region, vbom);
	    //gameHUD.attachChild(HUDSprite);
	    camera.setHUD(gameHUD);
	    // CREATE SCORE TEXT
	    scoreText = new Text(0, 440, resourceManager.font, "Score: 0123456789", vbom);
	    scoreText.setTextOptions(new TextOptions());
	    //scoreText.setAnchorCenter(0, 0);    
	    scoreText.setText("fml");
	    gameHUD.attachChild(scoreText);
	}
	
	private void createLevel(){
		final FixtureDef fixDef = PhysicsFactory.createFixtureDef(0f,0f, 1.0f);
		
		BitmapTextureAtlas mTextureBoundryFloor = new BitmapTextureAtlas(engine.getTextureManager(), 800, 10);
		mBoundryTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTextureBoundryFloor, this.activity, "floor.png", 0, 0);
		Sprite roof = new Sprite(0,-10, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyRoof = PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, fixDef);
		bodyRoof.setUserData("roof");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(roof, bodyRoof, true, true));
		attachChild(roof);
		
		Sprite floor = new Sprite(0,500, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyFloor = PhysicsFactory.createBoxBody(physicsWorld, floor, BodyType.StaticBody, fixDef);
		bodyFloor.setUserData("floor");
		floor.setUserData(bodyFloor);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(floor, bodyFloor, true, true));
		attachChild(floor);
		
		BitmapTextureAtlas mTextureBoundryWall = new BitmapTextureAtlas(engine.getTextureManager(), 10, 480);
		mBoundryTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTextureBoundryWall, this.activity, "wall.png", 0, 0);
		mTextureBoundryWall.load();
		
		Sprite wallLeft = new Sprite(-10,0, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyWallLeft = PhysicsFactory.createBoxBody(physicsWorld, wallLeft, BodyType.StaticBody, fixDef);
		bodyWallLeft.setUserData("wallLeft");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(wallLeft, bodyWallLeft, true, true));
		attachChild(wallLeft);
		
		Sprite wallRight = new Sprite(800,0, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyWallRight = PhysicsFactory.createBoxBody(physicsWorld, wallRight, BodyType.StaticBody, fixDef);
		bodyWallRight.setUserData("wallRight");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(wallRight, bodyWallRight, true, true));
		attachChild(wallRight);
		
		player = new Player(360, 450, vbom, physicsWorld){
			@Override
			public void onDie(){
        	}
		};
		attachChild(player);
		
		ball = new Ball(390, 200, vbom, physicsWorld);
		
		attachChild(ball);
		
		bricks = new Brick[NROWS][NCOLS];
		for (int i=0; i < NROWS; i++) {
	    	for (int j=0; j < NCOLS; j++) {
	    		bricks[i][j] = new Brick( (i) * (10 +80) , (1+j) * (10 + 20), vbom, physicsWorld, i, j, 1);
	      		attachChild(bricks[i][j]);
	    	}
	  	}
	}
	
	private void createBackground()
	{
		//setBackground(new Background(Color.WHITE));
		attachChild(new Sprite(0, 0, resourceManager.game_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	private ContactListener createContactListener()
    {
        ContactListener contactListener = new ContactListener()
        {
            public void beginContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
                {
                	if ((x1.getBody().getUserData() == "player") && (x2.getBody().getUserData() == "ball")){
                		ball.setXVelocity((ball.getX() + (ball.getWidth()/2)) - (player.getX() + (player.getWidth()/2)));
                		ball.bounce(true);
                	}
                	
                	if (((x1.getBody().getUserData() == "wallLeft") || (x1.getBody().getUserData() == "wallRight")) && (x2.getBody().getUserData() == "ball")){
                		ball.bounce(false);
                	}
                	
                	if ((x1.getBody().getUserData() == "roof") || (x1.getBody().getUserData() == "ball")){
                		ball.bounce(true);
                	}
                	
                	if (( x1.getBody().getUserData() == "floor") && (x2.getBody().getUserData() == "ball")){
                		x2.getBody().setUserData("destroy");
                		player.loseLife();
                		resetBall = true;
                	}
                	
                	if (( x1.getBody().getUserData() == "brick") && (x2.getBody().getUserData() == "ball")){
                		ball.bounce(true);
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
	
	//---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------
	
	private void removeObjectsSetForDestruction()
	{
		for (Iterator<Body> iter = physicsWorld.getBodies(); iter.hasNext();){
			final Body currentBody = iter.next();
			if (currentBody.getUserData() == "destroy"){
				this.engine.runOnUpdateThread(new Runnable(){
					public void run() {
						physicsWorld.destroyBody(currentBody);
						currentBody.setUserData(null);
					}
				});
			}
		}
	}
	
	private void removeBallPhysicsConnector(){
		this.engine.runOnUpdateThread(new Runnable(){
			public void run() {
					// Find the physics connector associated with the sprite mPiglet
					PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(ball);
					// Unregister the physics connector
					physicsWorld.unregisterPhysicsConnector(physicsConnector);
				}
			});
		resetBall = false;
		ball.reset(physicsWorld);
	}
	
	private void removeBricksSetForDestruction(){
		for (int i=0; i < NROWS; i++) {
			final int x = i;
	    	for (int j=0; j < NCOLS; j++) {
	    		final int y = j;
	    		if (bricks[i][j].getHP() < 1){
	    			this.engine.runOnUpdateThread(new Runnable(){
	    				public void run() {
	    						// Find the physics connector associated with the sprite mPiglet
	    						PhysicsConnector physicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(bricks[x][y]);
	    						// Unregister the physics connector
	    						physicsWorld.unregisterPhysicsConnector(physicsConnector);
	    						physicsWorld.destroyBody(physicsConnector.getBody());
	    					}
	    				});
	    		}
	    	}
		} 	
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float touchFromRight = pSceneTouchEvent.getX() - (player.getX() + player.getWidth());
		float touchFromLeft = pSceneTouchEvent.getX() - player.getX();
		//Touch to the right of the player
		if ((touchFromRight > 0) && (touchFromRight < 800)){
			if (touchFromRight> 100){
				player.setX(20f);
			}
			else if (touchFromRight > 30){
				player.setX(5f);
			}
			else{
				player.setX(1f);
			}
		}
		
		//Touch to the left of the player
		else if ((touchFromLeft < 0) && (touchFromLeft > -800)){
			if(touchFromLeft < -100){
				player.setX(-20f);
			}
			else if (touchFromLeft < -30){
				player.setX(-5f);
			}
			else{
				player.setX(-1f);
			}
		}
		//float touchedX = pSceneTouchEvent.getX();
		//float distance = touchedX-player.getX() + 40;
		//player.setX(distance);
		return false;
	}
	
	@Override
	public void disposeScene()
	{
	    camera.setHUD(null);
	    camera.setCenter(400, 240);
	    ResourceManager.gameMusic.stop();
	}
	
	@Override
	public void onBackKeyPressed() {
		 SceneManager.getInstance().loadMenuScene(engine);
	}
	
	//---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
	
	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}
	
	//---------------------------------------------
    // LEVEL CREATION
    //---------------------------------------------
	
	
	
}
