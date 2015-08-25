package ie.itcarlow.reapeatproject;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;

public class GameOverScene extends BaseScene {

private Sprite gameOver;
	
    @Override
    public void createScene(){
    	gameOver = new Sprite(0, 0, resourceManager.gameOver_region, vbom)
    	{
    	    @Override
    	    protected void preDraw(GLState pGLState, Camera pCamera) 
    	    {
    	       super.preDraw(pGLState, pCamera);
    	       pGLState.enableDither(); 	//This blends the colours slightly to improve image quality
    	    }
    	};
    	        
    	gameOver.setScale(1.5f);
    	gameOver.setPosition(400-128, 240-128);
    	attachChild(gameOver);
    }

    @Override
    public void onBackKeyPressed()
    {

    }

    @Override
    public SceneType getSceneType(){
    	return SceneType.SCENE_GAMEOVER;
    }

    @Override
    public void disposeScene(){
    	gameOver.detachSelf();
    	gameOver.dispose();
        this.detachSelf();
        this.dispose();
    }

}
