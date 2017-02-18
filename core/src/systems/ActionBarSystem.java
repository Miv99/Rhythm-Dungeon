package systems;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import hud.ActionBar;
import hud.BeatLine;

/**
 * Updates and renders the Action Bar
 */
public class ActionBarSystem {
	private SpriteBatch batch;
	
	private float windowWidth;
	private ActionBar actionBar;
	// Time it takes in seconds for a BeatLine to travel the entire width of the window
	private float scrollIntervalInSeconds;
	private Array<BeatLine> beatLineAdditionQueue;
	
	private final float cursorLineXPos = 50f;
	
	public ActionBarSystem(float windowWidth, ActionBar actionBar, float bpm) {
		batch = new SpriteBatch();
		this.windowWidth = windowWidth;
		this.actionBar = actionBar;
		setScrollInterval(calculateScrollInterval(bpm));
	}
	
	public static float calculateScrollInterval(float bpm) {
		//TODO: tweak magic number
		return 160f/bpm;
	}
	
	public void setWindowWidth(float windowWidth) {
		this.windowWidth = windowWidth;
	}
	
	public void setScrollInterval(float scrollIntervalInSeconds) {
		this.scrollIntervalInSeconds = scrollIntervalInSeconds;
	}
	
	public void update(float deltaTime) {
		if(actionBar != null && !actionBar.isPaused()) {
			for(BeatLine b : actionBar.getBeatLines()) {
				b.setTimeUntilCursorLineInSeconds(b.getTimeUntilCursorLineInSeconds() - deltaTime);
				
				if(b.getTimeUntilCursorLineInSeconds() < scrollIntervalInSeconds) {
					float x = ((b.getTimeUntilCursorLineInSeconds()/scrollIntervalInSeconds) * windowWidth) + cursorLineXPos;
					//TODO: batch.draw the BeatLine image and the circle image
				}
				
				// Once the BeatLine crosses the cursor, a new BeatLine is spawned
				if(b.getTimeUntilCursorLineInSeconds() <= 0) {
					queueBeatLineAddition(new BeatLine(b.getTimeUntilCursorLineInSeconds() + scrollIntervalInSeconds, b.getStrongBeat()));
				}
			}
			
			fireBeatLineAdditionQueue();
		}
	}
	
	private void queueBeatLineAddition(BeatLine newBeatLine) {
		beatLineAdditionQueue.add(newBeatLine);
	}
	
	private void fireBeatLineAdditionQueue() {
		if(beatLineAdditionQueue.size > 0) {
			actionBar.getBeatLines().addAll(beatLineAdditionQueue);
			beatLineAdditionQueue.clear();
		}
	}
}