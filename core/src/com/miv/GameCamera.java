package com.miv;

import java.awt.Point;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import dungeons.Dungeon;
import systems.RenderSystem;

public class GameCamera extends OrthographicCamera {
	private final float lerpSpeed = 0.08f;
	
	private Dungeon dungeon;
	private RenderSystem renderSystem;
	private Entity focus;
	
	public GameCamera(float viewportWidth, float viewportHeight) {
		super(viewportWidth, viewportHeight);
	}
		
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	public void setRenderSystem(RenderSystem renderSystem) {
		this.renderSystem = renderSystem;
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y, 0);
		update();
	}
	
	public void translatePosition(float x, float y) {
		setPosition(position.x + x, position.y + y);
	}
	
	/**
	 * Focus the camera on an entity with a HitboxComponent
	 */
	public void setFocus(Entity focus) {
		this.focus = focus;
	}
	
	/**
	 * Unfocus the camera on an entity
	 */
	public void unfocus() {
		focus = null;
	}
	
	public void frameUpdate(float deltaTime) {
		if(focus != null) {
			Point focusMapPosition = ComponentMappers.hm.get(focus).getMapPosition();
			lerp(lerpSpeed * (deltaTime/(1/60f)), focusMapPosition.x * Options.TILE_SIZE, focusMapPosition.y * Options.TILE_SIZE);
		}
	}
	
	@Override
	public void update() {
		if(dungeon != null) {
			dungeon.getTileRenderSystem().getBatch().setProjectionMatrix(combined);
		}
		if(renderSystem != null) {
			renderSystem.getBatch().setProjectionMatrix(combined);
		}
		update(true);
	}
	
	private void lerp(float lerpSpeed, float xTarget, float yTarget) {
        Vector3 target = new Vector3(xTarget, yTarget, 0);
        
        // Translate camera by (oldPosition * 0.9) + (target * 0.1)
        Vector3 cameraPosition = position;
        cameraPosition.scl(1f - lerpSpeed);
        target.scl(lerpSpeed);
        cameraPosition.add(target);
        setPosition(cameraPosition.x, cameraPosition.y);
	}
}
