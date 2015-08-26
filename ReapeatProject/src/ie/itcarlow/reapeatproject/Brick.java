package ie.itcarlow.reapeatproject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Brick extends Sprite {
	private Body body;
	private int hp;
	private int idX;
	private int idY;
	
	public Brick(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld, int pIdX, int pIdY, int health)
	{
	    super(pX, pY, ResourceManager.getInstance().brick_region, vbo);
	    hp = health;
	    idX = pIdX;
	    idY = pIdY;
	    createPhysics(physicsWorld);
	}
	
	private void createPhysics(PhysicsWorld physicsWorld)
    {        
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));

        body.setUserData("brick");
        body.setFixedRotation(true);
        
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false)
        {
            @Override
            public void onUpdate(float pSecondsElapsed)
            {
                super.onUpdate(pSecondsElapsed);
            }
        });
    }
	
	public Vector2 getId(){
		Vector2 returnVect = new Vector2(idX, idY);
		return returnVect;
	}
	
	public void reduceHP(){
		hp += -1;
	}
	
	public int getHP(){
		return hp;
	}
}
