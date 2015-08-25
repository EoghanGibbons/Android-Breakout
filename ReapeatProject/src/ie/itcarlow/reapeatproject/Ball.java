package ie.itcarlow.reapeatproject;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public abstract class Ball extends AnimatedSprite {
	private Body body;
	
	public Ball(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld)
	{
	    super(pX, pY, ResourceManager.getInstance().ball_region, vbo);
	    createPhysics(physicsWorld);
	}
	
	private void createPhysics(PhysicsWorld physicsWorld)
    {        
        body = PhysicsFactory.createCircleBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));//createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));

        body.setUserData("ball");
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
