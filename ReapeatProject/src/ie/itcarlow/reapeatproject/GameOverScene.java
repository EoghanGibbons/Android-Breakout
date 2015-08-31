package ie.itcarlow.reapeatproject;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;

public class GameOverScene extends BaseScene {

private Sprite gameOver;
	
    @Override
    public void createScene(){
    	gameOver = new Sprite(0, 0, resourceManager.menu_background_region, vbom)
    	{
    	    @Override
    	    protected void preDraw(GLState pGLState, Camera pCamera) 
    	    {
    	       super.preDraw(pGLState, pCamera);
    	       pGLState.enableDither(); 	//This blends the colours slightly to improve image quality
    	    }
    	};
    	attachChild(gameOver);
    	attachChild(new Text(250, 20, ResourceManager.font, "Game Over", vbom));
    	if (resourceManager.new_highscore){
    		attachChild(new Text(200, 240, ResourceManager.font, "New Highscore: " + resourceManager.getHighScore(), vbom));
    		resourceManager.new_highscore = false;
    	}
    	else{
    		attachChild(new Text(150, 240, ResourceManager.font, "Current Highscore: " + resourceManager.getHighScore(), vbom));
    	}
    }

    @Override
    public void onBackKeyPressed(){
    	SceneManager.getInstance().loadMenuScene(engine);
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
