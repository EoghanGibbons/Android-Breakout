package ie.itcarlow.reapeatproject;

import java.util.Iterator;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;

import org.andengine.engine.camera.Camera;
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
import org.andengine.opengl.util.GLState;
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
	private ITextureRegion mBoundryTextureRegion;
	
	private Player player;
	private Ball ball;
	private Brick[][] bricks;
	
	private final int NROWS = 5;
	private final int NCOLS = 5;
	
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
	    createLevel();
	    setOnSceneTouchListener(this);
	    //this.engine.registerUpdateHandler(this);
	}

	//---------------------------------------------
    // INITIALIZERS
    //---------------------------------------------
	
	private void createPhysics(){
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0.0f, 0.0f), false); 
		registerUpdateHandler(physicsWorld);
		physicsWorld.setContactListener(createContactListener());
	}
	
	private void createHUD(){
	    gameHUD = new HUD();
	    
	    // CREATE SCORE TEXT
	    scoreText = new Text(20, 420, resourceManager.font, "Score: 0123456789", vbom);
	    //scoreText.setAnchorCenter(0, 0);    
	    scoreText.setText("Click Character to jump");
	    gameHUD.attachChild(scoreText);
	    
	    camera.setHUD(gameHUD);
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
		
		ball = new Ball(400, 300, vbom, physicsWorld);
		
		attachChild(ball);
		
		/*
		for (int i=0; i < NROWS; i++) {
	    	for (int j=0; j < NCOLS; j++) {
	      		bricks[i][j] = new Brick( (i) * (10 +80) , (1+j) * (10 + 20), vbom, physicsWorld, i+j , 1);
	      		attachChild(bricks[i][j]);
	    	}
	  	}
	  	*/
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
                		ball.bounce(true);
                	}
                	
                	if (((x1.getBody().getUserData() == "wallLeft") || (x1.getBody().getUserData() == "wallRight")) && (x2.getBody().getUserData() == "ball")){
                		ball.bounce(false);
                	}
                	
                	if (((x1.getBody().getUserData() == "roof") || (x1.getBody().getUserData() == "ball")) 
                	&& ((x2.getBody().getUserData() == "ball") || (x2.getBody().getUserData() == "roof") ) ){
                		ball.bounce(true);
                	}
                }
            }

            public void endContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
                {
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
