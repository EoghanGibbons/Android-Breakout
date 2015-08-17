package ie.itcarlow.reapeatproject;

import java.util.Iterator;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.Color;

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
	
	private PhysicsWorld physicsWorld;
	
	private BitmapTextureAtlas mTexturePlayer;
	
	private ITextureRegion mPlayerTextureRegion;
	private ITextureRegion mBoundryTextureRegion;
	
	private Sprite player;

	private boolean canJump = true;
	public boolean gameOver = false;
	
	
	//---------------------------------------------
    // CONSTRUCTOR
    //---------------------------------------------
	
	@Override
	public void createScene(){
		ResourceManager.gameMusic.play();
    	ResourceManager.gameMusic.setLooping(true);
	    createBackground();
	    //createHUD();
	    createPhysics();
	    createPlayer();
	    createBoundry();
	    setOnSceneTouchListener(this);
	    //this.engine.registerUpdateHandler(this);
	}

	//---------------------------------------------
    // INITIALIZERS
    //---------------------------------------------
	
	private void createPhysics(){
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 9.81f), false); 
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
	}
	
	private void createHUD(){
	    gameHUD = new HUD();
	    
	    // CREATE SCORE TEXT
	    scoreText = new Text(20, 420, resourcesManager.font, "Score: 0123456789", vbom);
	    //scoreText.setAnchorCenter(0, 0);    
	    scoreText.setText("Click Character to jump");
	    gameHUD.attachChild(scoreText);
	    
	    camera.setHUD(gameHUD);
	}
	
	private void createBoundry(){
		final FixtureDef fixDef = PhysicsFactory.createFixtureDef(0f,0f, 1.0f);
		
		BitmapTextureAtlas mTextureBoundryFloor = new BitmapTextureAtlas(engine.getTextureManager(), 800, 10);
		mBoundryTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTextureBoundryFloor, this.activity, "floor.png", 0, 0);
		mTextureBoundryFloor.load();
		
		Sprite floor = new Sprite(0,470, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyFloor = PhysicsFactory.createBoxBody(physicsWorld, floor, BodyType.StaticBody, fixDef);
		bodyFloor.setUserData("floor");
		floor.setUserData(bodyFloor);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(floor, bodyFloor, true, true));
		attachChild(floor);
		
		Sprite roof = new Sprite(0,0, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyRoof = PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, fixDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(roof, bodyRoof, true, true));
		attachChild(roof);
		
		BitmapTextureAtlas mTextureBoundryWall = new BitmapTextureAtlas(engine.getTextureManager(), 10, 460);
		mBoundryTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTextureBoundryWall, this.activity, "wall.png", 0, 0);
		mTextureBoundryWall.load();
		
		Sprite wallLeft = new Sprite(0,10, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyWallLeft = PhysicsFactory.createBoxBody(physicsWorld, wallLeft, BodyType.StaticBody, fixDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(wallLeft, bodyWallLeft, true, true));
		attachChild(wallLeft);
		
		Sprite wallRight = new Sprite(790,10, mBoundryTextureRegion, engine.getVertexBufferObjectManager());
		Body bodyWallRight = PhysicsFactory.createBoxBody(physicsWorld, wallRight, BodyType.StaticBody, fixDef);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(wallRight, bodyWallRight, true, true));
		attachChild(wallRight);
	}
	
	public void createPlayer(){
		mTexturePlayer = new BitmapTextureAtlas(engine.getTextureManager(), 40, 40);  
		mPlayerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexturePlayer, this.activity, "player.png", 0, 0);
        mTexturePlayer.load();
        player = new Sprite(20, 430,  mPlayerTextureRegion, engine.getVertexBufferObjectManager());
        createPhysicsBodies();
        this.attachChild(player);
	}
	
	private void createPhysicsBodies(){
		final FixtureDef fixDef = PhysicsFactory.createFixtureDef(1.5f,0f, 0.3f);
		
		Body body = PhysicsFactory.createBoxBody(physicsWorld, player, BodyType.DynamicBody, fixDef);
		body.setFixedRotation(true);
		body.setUserData("player");
    	player.setUserData(body); 
    	physicsWorld.registerPhysicsConnector(new PhysicsConnector(player, body, true, true));
	 }
	
	private void createBackground()
	{
	    setBackground(new Background(Color.WHITE));
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
                    if (x2.getBody().getUserData().equals("player")|| (x1.getBody().getUserData().equals("player")))
                    {
                        canJump = true;
                    }
                }
            }

            public void endContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
                {
                    if (x2.getBody().getUserData().equals("player") || (x1.getBody().getUserData().equals("player")))
                    {
                        canJump = false;
                    }
                }
            }

            public void preSolve(Contact contact, Manifold oldManifold)
            {

            }

            public void postSolve(Contact contact, ContactImpulse impulse)
            {

            }
        };
        return contactListener;
    }
	
	//---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		float touchFromRight = pSceneTouchEvent.getX() - (player.getX() + player.getWidth());
		float touchFromLeft = pSceneTouchEvent.getX() - player.getX();
		float touchFromBottom = pSceneTouchEvent.getY() - (player.getY() + player.getHeight());
		float touchFromTop = pSceneTouchEvent.getY() - player.getY();
		Body bodyPlayer = (Body) player.getUserData();
		//Touch to the right of the player
		if ((touchFromRight > 0) && (touchFromRight < 800)){
			if (touchFromRight > 30){
				bodyPlayer.setLinearVelocity(5, bodyPlayer.getLinearVelocity().y);
			}
			else{
				bodyPlayer.setLinearVelocity(1, bodyPlayer.getLinearVelocity().y);
			}
		}
		
		//Touch to the left of the player
		else if ((touchFromLeft < 0) && (touchFromLeft > -800)){
			
			if (touchFromLeft < -30){
				bodyPlayer.setLinearVelocity(-5, bodyPlayer.getLinearVelocity().y);
			}
			else{
				bodyPlayer.setLinearVelocity(-1, bodyPlayer.getLinearVelocity().y);
			}
		}
	
		//Touch on the player
		else if ((touchFromLeft > 0) && (touchFromRight < 0) && (touchFromTop > 0) && (touchFromBottom < 0) && (canJump)){
			bodyPlayer.setLinearVelocity(bodyPlayer.getLinearVelocity().x, -9);
			ResourceManager.jumpSound.play();
		}
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
