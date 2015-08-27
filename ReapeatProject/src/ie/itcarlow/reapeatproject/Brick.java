package ie.itcarlow.reapeatproject;

import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Brick extends Sprite {
	private Body body;
	private int hp;
	private int idX;
	private int idY;
	private Color brickColor;
	
	public Brick(float pX, float pY, VertexBufferObjectManager vbo, PhysicsWorld physicsWorld, int pIdX, int pIdY, int health)
	{
	    super(pX, pY, ResourceManager.getInstance().brick_region, vbo);
	    hp = health;
	    idX = pIdX;
	    idY = pIdY;
	    switch(idY){
	    case 0:
	    	brickColor = Color.RED;
	    	break;
	    case 1:
	    	brickColor = new Color(255, 165, 0);
	    	break;
	    case 2:
	    	brickColor = Color.YELLOW;
	    	break;
	    case 3:
	    	brickColor = Color.GREEN;
	    	break;
	    case 4:
	    	brickColor = Color.BLUE;
	    	break;
	    case 5:
	    	brickColor = new Color(75,0,30);
	    	break;
	    case 6:
	    	brickColor = new Color(238,130,238);
	    	break;
	    }
	    super.setColor(brickColor);
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
