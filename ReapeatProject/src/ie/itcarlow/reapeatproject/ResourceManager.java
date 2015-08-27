package ie.itcarlow.reapeatproject;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

public class ResourceManager
{
    //---------------------------------------------
    // VARIABLES
    //---------------------------------------------
    
    private static final ResourceManager INSTANCE = new ResourceManager();
    
    public Engine engine;
    public MainActivity activity;
    public Camera camera;
    public VertexBufferObjectManager vbom;
    
    private BitmapTextureAtlas splashTextureAtlas;   
    private BitmapTextureAtlas gameOverTextureAtlas;
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    public Font font;
    
    //audio
    public static Music gameMusic;
    public static Music menuMusic;
    
    //---------------------------------------------
    // TEXTURES & TEXTURE REGIONS
    //---------------------------------------------
    
    //Splash Screen
    public ITextureRegion splash_region;
    //Gameover Screen
    public ITextureRegion gameOver_region;
    
    //Menu
    public ITextureRegion menu_background_region;
    public ITextureRegion singlePlayer_region;
    public ITextureRegion multiPlayer_region;
    
    //Game
    public BuildableBitmapTextureAtlas gameTextureAtlas;
    public ITextureRegion player_region;
    public ITextureRegion brick_region;
    public ITextureRegion HUD_region;
    public ITiledTextureRegion ball_region;
    public ITextureRegion game_background_region;
    public Font gameFont;
    
    //---------------------------------------------
    // CLASS LOGIC
    //---------------------------------------------

    public void loadMenuResources()
    {
    	loadMenuGraphics();
        loadMenuAudio();
        loadMenuFonts();
    }
    
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
    }
    
    private void loadMenuGraphics(){
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
    	menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
    	singlePlayer_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "SinglePlayer.png");
    	multiPlayer_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "MultiPlayer.png");
    	
    	try {
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e){
    	        Debug.e(e);
    	}
    }

    public void loadMenuTextures()
    {
        menuTextureAtlas.load();
    }
    
    public void unloadMenuTextures()
    {
        menuTextureAtlas.unload();
    }
    
    private void loadMenuAudio()
    {
    	MusicFactory.setAssetBasePath("");
        try {
        	menuMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "mainMenuMusic.mp3");
		}  catch (IOException e) {
			e.printStackTrace();
		}
    }

    private void loadMenuFonts(){
        FontFactory.setAssetBasePath("");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.BLACK);
        font.load();
    }
    
    public void unloadGameTextures()
    {
    }
    
    private void loadGameGraphics()
    {
    	gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048, TextureOptions.BILINEAR);
    	player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "player.png");
    	brick_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "brick.png");
    	game_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "BreakoutBackground.png");
    	ball_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "ball.png", 8, 4);
    	HUD_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "HUD.png");
    	
    	try 
        {
            this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.gameTextureAtlas.load();
        } 
        catch (final TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }
    
    private void loadGameFonts()
    {
    	FontFactory.setAssetBasePath("");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        gameFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 12, true, Color.WHITE, 2, Color.BLACK);
        gameFont.load();
    }
    
    private void loadGameAudio()
    {
    	MusicFactory.setAssetBasePath("");
        try {
			gameMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "music.mp3");
		}  catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void loadSplashScreen(){
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
    	splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
    	splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
    	splashTextureAtlas.load();
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
    	gameOverTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
    	gameOver_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameOverTextureAtlas, activity, "gameover.png", 0, 0);
    	gameOverTextureAtlas.load();
    }
    
    public void unloadSplashScreen(){
    	splashTextureAtlas.unload();
    	splash_region = null;
    }
    
    public void loadGameOverScreen(){
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("");
    	gameOverTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
    	gameOver_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameOverTextureAtlas, activity, "gameover.png", 0, 0);
    	gameOverTextureAtlas.load();
    }
    
    public void unloadGameOverScreen(){
    	gameOverTextureAtlas.unload();
    	gameOver_region = null;
    }
    
    /**
     * @param engine
     * @param activity
     * @param camera
     * @param vbom
     * <br><br>
     * We use this method at beginning of game loading, to prepare Resources Manager properly,
     * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
     */
    public static void prepareManager(Engine engine, MainActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }
    
    //---------------------------------------------
    // GETTERS AND SETTERS
    //---------------------------------------------
    
    public static ResourceManager getInstance()
    {
        return INSTANCE;
    }
}