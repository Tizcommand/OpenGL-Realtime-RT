package scene;

import java.util.ArrayList;

import scene.rasterizing.showcase.BeachScene;
import scene.rasterizing.showcase.PyramidScene;
import scene.rasterizing.test.shader.ShaderTestScene;
import scene.rasterizing.test.shader.SpecularTestScene;
import scene.rasterizing.test.texture.FilteringTestScene;
import scene.rasterizing.test.texture.MipMappingTestScene;
import scene.rasterizing.test.texture.TransparencyTestScene;
import scene.rt.showcase.CsgScene;
import scene.rt.showcase.MirrorRoomScene;
import scene.rt.showcase.RandomSpheresScene;
import scene.rt.showcase.RefractionScene;
import scene.rt.test.MirrorTestScene;
import scene.rt.test.QuadricTestScene;
import scene.rt.test.SphereTestScene;
import scene.rt.test.TriangleTestScene;

/**
 * Stores {@link SceneCollection} objects and
 * provides methods for switching between
 * which {@link SceneCollection} is the {@link #currentSceneCollection} and
 * which {@link Scene}, of the {@link #currentSceneCollection}, is issuing rendering.
 * 
 * @author Tizian Kirchner
 */
public class SceneCollectionStorage {
	/**
	 * Stores the {@link SceneCollection} that is currently active.
	 */
	private static SceneCollection currentSceneCollection;
	
	/**
	 * Stores the {@link SceneCollection} objects that can be switched between.
	 */
	private static ArrayList<SceneCollection> sceneCollections;
	
	/**
	 * Stores the index of the {@link #currentSceneCollection} within {@link SceneCollectionStorage#sceneCollections}. 
	 */
	private static int currentSceneCollectionIndex = 0;
	
	/**
	 * Constructs all {@link SceneCollection} objects, assigns them their {@link Scene} objects
	 * and adds them to the {@link #sceneCollections SceneCollection list}.
	 */
	public static void init() {
		SceneCollection rtShowcaseScenes = new SceneCollection("Raytracing Showcase Scenes");
		rtShowcaseScenes.addScene(new MirrorRoomScene());
		rtShowcaseScenes.addScene(new RefractionScene());
		rtShowcaseScenes.addScene(new RandomSpheresScene());
		rtShowcaseScenes.addScene(new CsgScene());
		
		SceneCollection rtTestScenes = new SceneCollection("Raytracing Test Scenes");
		rtTestScenes.addScene(new SphereTestScene());
		rtTestScenes.addScene(new QuadricTestScene());
		rtTestScenes.addScene(new TriangleTestScene());
		rtTestScenes.addScene(new MirrorTestScene());
		
		SceneCollection showcaseScenes = new SceneCollection("Rasterizing Showcase Scenes");
		showcaseScenes.addScene(new PyramidScene());
		showcaseScenes.addScene(new BeachScene());
		
		SceneCollection shaderTestScenes = new SceneCollection("Rasterizing Shader Test Scenes");
		shaderTestScenes.addScene(new ShaderTestScene());
		shaderTestScenes.addScene(new SpecularTestScene());
		
		SceneCollection textureTestScenes = new SceneCollection("Rasterizing Texture Test Scenes");
		textureTestScenes.addScene(new FilteringTestScene());
		textureTestScenes.addScene(new MipMappingTestScene());
		textureTestScenes.addScene(new TransparencyTestScene());
		
		sceneCollections = new ArrayList<>();
		sceneCollections.add(rtShowcaseScenes);
		sceneCollections.add(rtTestScenes);
		sceneCollections.add(showcaseScenes);
		sceneCollections.add(shaderTestScenes);
		sceneCollections.add(textureTestScenes);
		
		currentSceneCollection = sceneCollections.get(currentSceneCollectionIndex);
		currentSceneCollection.getCurrentScene().prepareLoad();
	}
	
	/**
	 * Calls the {@link Scene#update update method} of the {@link #currentSceneCollection}'s current {@link Scene}.
	 * 
	 * @param delta See {@link Scene#update}.
	 */
	public static void update(float delta) {
		currentSceneCollection.updateCurrentScene(delta);
	}
	
	/**
	 * Calls the {@link Scene#render render method} of the {@link #currentSceneCollection}'s current {@link Scene}.
	 */
	public static void render() {
		currentSceneCollection.renderCurrentScene();
	}
	
	/**
	 * Calls the {@link SceneCollection#goToNextScene() goToNextScene method} of the
	 * {@link #currentSceneCollection}.
	 */
	public static void goToNextScene() {
		currentSceneCollection.goToNextScene();
	}
	
	/**
	 * Calls the {@link SceneCollection#goToNextScene() goToPreviousScene method} of the
	 * {@link #currentSceneCollection}. 
	 */
	public static void goToPreviousScene() {
		currentSceneCollection.goToPreviousScene();
	}
	
	/**
	 * Switches to the next {@link SceneCollection} of the {@link #sceneCollections SceneCollection list}.
	 * Wraps around to the first {@link SceneCollection} of the {@link #sceneCollections SceneCollection list}
	 * if the {@link #currentSceneCollection} is the last {@link SceneCollection}
	 * of the {@link #sceneCollections SceneCollection list}.
	 */
	public static void goToNextSceneCollection() {
		if (currentSceneCollectionIndex < sceneCollections.size() - 1) {
			currentSceneCollectionIndex++;
		} else {
			currentSceneCollectionIndex = 0;
		}
		
		currentSceneCollection = sceneCollections.get(currentSceneCollectionIndex);
		currentSceneCollection.getCurrentScene().prepareLoad();
	}
	
	/**
	 * Switches to the previous {@link SceneCollection} of the {@link #sceneCollections SceneCollection list}.
	 * Wraps around to the last {@link SceneCollection} of the {@link #sceneCollections SceneCollection list}
	 * if the {@link #currentSceneCollection} is the first {@link SceneCollection}
	 * of the {@link #sceneCollections SceneCollection list}.
	 */
	public static void goToPreviousSceneCollection() {
		if (currentSceneCollectionIndex > 0) {
			currentSceneCollectionIndex--;
		} else {
			currentSceneCollectionIndex = sceneCollections.size() - 1;
		}
		
		currentSceneCollection = sceneCollections.get(currentSceneCollectionIndex);
		currentSceneCollection.getCurrentScene().prepareLoad();
	}
	
	/**
	 * See {@link SceneCollectionStorage#currentSceneCollection}.
	 */
	public static SceneCollection getCurrentSceneCollection() {
		return currentSceneCollection;
	}
	
	/**
	 * @return The current {@link Scene} of the {@link #currentSceneCollection}.
	 */
	public static Scene getCurrentScene() {
		return currentSceneCollection.getCurrentScene();
	}
}