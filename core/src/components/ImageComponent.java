package components;

import java.awt.Point;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class ImageComponent implements Component {
	private Sprite sprite;
	private Point mapPosition;
	
	public ImageComponent(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public Point getMapPosition() {
		return mapPosition;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public void setMapPosition(int x, int y) {
		mapPosition.x = x;
		mapPosition.y = y;
	}
}
