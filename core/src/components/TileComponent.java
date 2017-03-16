package components;

import com.badlogic.ashley.core.Component;

/**
 * Used to create the illusion of breakable tiles
 * Breakable tiles are intangible, but have size 1x1 tangible entities on them that block movement
 * Breaking tiles is simply killing the entity to allow movement
 */
public class TileComponent implements Component {

}
