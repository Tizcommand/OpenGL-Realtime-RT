package scene;

import java.util.ArrayList;

import render.TextRenderer;

/**
 * Stores an {@link ArrayList} of {@link Scene} objects and
 * provides methods to switch between which of these {@link Scene} objects issues rendering.
 * 
 * @author Tizian Kirchner
 */
public class SceneCollection {
	/**
	 * Displayed by the {@link TextRenderer} when this {@link SceneCollection}
	 * is the {@link SceneCollectionStorage}'s currentSceneCollection.
	 */
	String name = "Unnamed SceneCollection";
	
	/**
	 * Stores the {@link Scene} objects that can be switched between.
	 */
	private ArrayList<Scene> scenes;
	
	/**
	 * Stores the {@link Scene} of the {@link SceneCollection} that is issuing rendering,
	 * while this {@link SceneCollection} is active. 
	 */
	private Scene currentScene;
	
	/**
	 * Stores the index of the {@link #currentScene} within the {@link #scenes scene list}. 
	 */
	private int currentSceneIndex;
	
	/**
	 * Constructs a new {@link SceneCollection}.
	 * 
	 * @param name See {@link SceneCollection#name}.
	 */
	public SceneCollection(String name) {
		this.name = name;
		scenes = new ArrayList<>();
		currentSceneIndex = 0;
	}
	
	/**
	 * @param scene The {@link Scene} that is to be added to this {@link SceneCollection}'s {@link #scenes scene list}.
	 */
	public void addScene(Scene scene) {
		if(scenes.isEmpty()) currentScene = scene;
		scenes.add(scene);
	}
	
	/**
	 * Switches to the next {@link Scene} of the {@link #scenes scene list}.
	 * Wraps around to the first {@link Scene} of the {@link #scenes scene list}
	 * if the {@link #currentScene} is the last {@link Scene} of the {@link #scenes scene list}.
	 */
	public void goToNextScene() {
		if (currentSceneIndex < scenes.size() - 1) {
			currentSceneIndex++;
		} else {
			currentSceneIndex = 0;
		}
		
		currentScene = scenes.get(currentSceneIndex);
		currentScene.prepareLoad();
	}
	
	/**
	 * Switches to the previous {@link Scene} of the {@link #scenes scene list}.
	 * Wraps around to the last {@link Scene} of the {@link #scenes scene list}
	 * if the {@link #currentScene} is the first {@link Scene} of the {@link #scenes scene list}.
	 */
	public void goToPreviousScene() {
		if (currentSceneIndex > 0) {
			currentSceneIndex--;
		} else {
			currentSceneIndex = scenes.size() - 1;
		}
		
		currentScene = scenes.get(currentSceneIndex);
		currentScene.prepareLoad();
	}
	
	/**
	 * Calls the {@link Scene#update(float) update method} of the {@link #currentScene}.
	 * 
	 * @param delta See {@link Scene#update(float)}.
	 */
	public void updateCurrentScene(float delta) {
		currentScene.update(delta);
	}
	
	/**
	 * Calls the {@link Scene#safeRender() safeRender method} of the {@link #currentScene}.
	 */
	public void renderCurrentScene() { 
		currentScene.safeRender();
	}
	
	/**
	 * See {@link SceneCollection#name}.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * See {@link #currentScene}.
	 */
	public Scene getCurrentScene() {
		return currentScene;
	}
}
