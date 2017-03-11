package components;

import java.awt.Point;
import java.util.HashMap;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.miv.Movement.Direction;

public class ImageComponent implements Component {
	// Name of the sprite without any animations and without "_[direction]"
	private String spriteName;
	// The original sprites facing each direction (left/right), without any animations
	private HashMap<Direction, Sprite> directionalSprites;
	private Direction facing;
	
	// Sprite currently being used
	private Sprite sprite;
	private Point mapPosition;
	
	public ImageComponent(String spriteName, HashMap<Direction, Sprite> directionalSprites, Point mapPosition) {
		this.spriteName = spriteName;
		this.directionalSprites = directionalSprites;
		this.mapPosition = mapPosition;
		
		sprite = directionalSprites.get(Direction.Right);
		facing = Direction.Right;
	}
	
	/**
	 * Used for animation entities (entities whose sole purpose is to show an animation)
	 */
	public ImageComponent(Point mapPosition) {
		this.mapPosition = mapPosition;
		facing = Direction.Right;
	}
	
	public void faceDirection(Direction direction) {
		if(direction.equals(Direction.Left)
				|| direction.equals(Direction.Right)) {
			facing = direction;
			sprite = directionalSprites.get(direction);
		}
	}
	
	public Direction getFacing() {
		return facing;
	}
	
	/**
	 * Returns the sprite for the direction the entity is currently facing
	 */
	public Sprite getDirectionalSprite() {
		return directionalSprites.get(facing);
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public Point getMapPosition() {
		return mapPosition;
	}
	
	public String getSpriteName() {
		return spriteName;
	}
	
	public HashMap<Direction, Sprite> getDirectionalSprites() {
		return directionalSprites;
	}
	
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public void setMapPosition(int x, int y) {
		mapPosition.x = x;
		mapPosition.y = y;
	}
}
