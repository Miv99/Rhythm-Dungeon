package components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import utils.Point;

public class ImageComponent implements Component {
	private TextureRegion image;
	private Point position;
	
	public ImageComponent(TextureRegion image) {
		this.image = image;
	}
	
	public TextureRegion getImage() {
		return image;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public void setImage(TextureRegion image) {
		this.image = image;
	}
	
	public void setPosition(float x, float y) {
		position.setX(x);
		position.setY(y);
	}
}
