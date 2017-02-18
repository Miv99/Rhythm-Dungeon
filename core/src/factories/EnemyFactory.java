package factories;

import com.badlogic.ashley.core.Engine;

import graphics.Images;

public class EnemyFactory {
	private Images images;
	private Engine engine;
	
	public EnemyFactory(Images images, Engine engine) {
		this.images = images;
		this.engine = engine;
	}
}
