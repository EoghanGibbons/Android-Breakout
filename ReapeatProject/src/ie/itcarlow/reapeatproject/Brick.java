package ie.itcarlow.reapeatproject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Brick extends Sprite {
	private Body body;
	private int hp;
	
	public Brick(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld, int number, int health)
	{
	    super(pX, pY, ResourceManager.getInstance().brick_region, vbo);
	    hp = health;
	    createPhysics(physicsWorld, number);
	}
	
	private void createPhysics(PhysicsWorld physicsWorld, int number)
    {        
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));

        body.setUserData("brick" + String.valueOf(number));
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
}
