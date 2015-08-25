package ie.itcarlow.reapeatproject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class Player extends Sprite {
	private Body body;
	private int lives;
	
	public Player(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld)
	{
	    super(pX, pY, ResourceManager.getInstance().player_region, vbo);
	    lives = 3;
	    createPhysics(physicsWorld);
	}
    
    public abstract void onDie();
    
    private void createPhysics(PhysicsWorld physicsWorld)
    {        
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
        body.setLinearDamping(10);
        body.setUserData("player");
        body.setFixedRotation(true);
        
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                super.onUpdate(pSecondsElapsed);
                //camera.onUpdate(0.1f);
                
                if (getY() <= 0)
                {                    
                    onDie();
                }
            }
        });
    }
    
    public void setX(float pX){
    	//body.setLinearVelocity((float) (Math.sqrt(20 * pX)), 0.0f);
    }
}
