package ie.itcarlow.reapeatproject;

import ie.itcarlow.reapeatproject.SceneManager.SceneType;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;



public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int MENU_SINGLEPLAYER = 0;
	private final int MENU_MULTIPLAYER = 1;
	
	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(0, 0);
	    
	    final IMenuItem singlePlayerMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SINGLEPLAYER, resourcesManager.singlePlayer_region, vbom), 1.2f, 1);
	    final IMenuItem multiPlayerMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MULTIPLAYER, resourcesManager.multiPlayer_region, vbom), 1.2f, 1);
	    
	    menuChildScene.addMenuItem(singlePlayerMenuItem);
	    menuChildScene.addMenuItem(multiPlayerMenuItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    singlePlayerMenuItem.setPosition(singlePlayerMenuItem.getX(), singlePlayerMenuItem.getY() + 10);
	    multiPlayerMenuItem.setPosition(multiPlayerMenuItem.getX(), multiPlayerMenuItem.getY() + 60);
	    
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
	    switch(pMenuItem.getID())
	    {
	        case MENU_SINGLEPLAYER:
	            //Load Game Scene!
	        	ResourceManager.menuMusic.stop();
	            SceneManager.getInstance().loadGameScene(engine);
	            return true;
	        case MENU_MULTIPLAYER:
	            return true;
	        default:
	            return false;
	    }
	}
	
	@Override
	public void createScene() {
		ResourceManager.menuMusic.play();
    	ResourceManager.menuMusic.setLooping(true);
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	public void createBackground(){
		attachChild(new Sprite(0, 0, resourcesManager.menu_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}

	
}
