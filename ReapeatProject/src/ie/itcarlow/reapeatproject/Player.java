package ie.itcarlow.reapeatproject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Player extends Sprite {
	private Body body;
	private int lives;
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld){
	    super(pX, pY, ResourceManager.getInstance().player_region, vbo);
	    lives = 1;
	    createPhysics(physicsWorld);
	}
    
    private void createPhysics(PhysicsWorld physicsWorld){        
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
        body.setLinearDamping(5);
        body.setUserData("player");
        body.setFixedRotation(true);
        
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
    }
    
    public void setX(float pX){
    	body.setLinearVelocity(pX, 0.0f);
    }
    
    public int getLives(){
    	return lives;
    }
    
    public void loseLife(){
    	lives += -1;
    }
}
