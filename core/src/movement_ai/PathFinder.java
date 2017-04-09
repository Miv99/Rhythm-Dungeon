package movement_ai;

import java.awt.Point;
import java.util.Comparator;
import java.util.PriorityQueue;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.miv.ComponentMappers;
import com.miv.EntityActions;
import com.miv.EntityActions.Direction;

import data.HitboxData.HitboxType;
import dungeons.Tile;
import utils.MapUtils;

public class PathFinder {
	public static class NoPathsException extends Exception {
		private static final long serialVersionUID = 2300779561134374157L;

		public NoPathsException() {
			super();
		}
	}
	
	private static class TileCell {
		int heuristicCost = 0;
        int finalCost = 0;
        
		private int x;
		private int y;
		private TileCell parent;
		
		TileCell(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static class TileComparator implements Comparator<TileCell> {
        public int compare(TileCell nodeFirst, TileCell nodeSecond) {
            if(nodeFirst.finalCost > nodeSecond.finalCost) {
            	return 1;
            }
            if(nodeSecond.finalCost > nodeFirst.finalCost) {
            	return -1;
            }
            return 0;
        }
    }
		
	/**
	 * Uses A*
	 * Takes into account the hitboxes of the mover and target
	 * @return - the direction of the first step in the calculated best path
	 */
	public static Direction calculateBestPathFirstStep(Tile[][] tiles, int maxSteps, Entity mover, Entity target) throws NoPathsException {
		HitboxType[][] moverHitbox = ComponentMappers.hitboxMapper.get(mover).getHitbox();
		HitboxType[][] targetHitbox = ComponentMappers.hitboxMapper.get(target).getHitbox();
		Point start = new Point(ComponentMappers.hitboxMapper.get(mover).getMapPosition().x + moverHitbox.length/2, ComponentMappers.hitboxMapper.get(mover).getMapPosition().y + moverHitbox[0].length/2);
		Point end = new Point(ComponentMappers.hitboxMapper.get(target).getMapPosition().x + targetHitbox.length/2, ComponentMappers.hitboxMapper.get(target).getMapPosition().y + targetHitbox[0].length/2);
		
		// Create and populate TileCell array
		int searchSquareXStart = Math.max(0, start.x - maxSteps);
		int searchSquareYStart = Math.max(0, start.y - maxSteps);
		int searchSquareXEnd = Math.min(tiles.length, start.x + maxSteps);
		int searchSquareYEnd = Math.min(tiles[0].length, start.y + maxSteps);
		TileCell[][] tileCells = new TileCell[searchSquareXEnd - searchSquareXStart][searchSquareYEnd - searchSquareYStart];
		if(end.x - start.x >= maxSteps
				|| end.y - start.y >= maxSteps
				|| end.x - searchSquareXStart < 0
				|| end.y - searchSquareYStart < 0) {
			throw new NoPathsException();
		}
		for(int x = 0; x < tileCells.length; x++) {
			for(int y = 0; y < tileCells[x].length; y++) {
				if(!tiles[searchSquareXStart + x][searchSquareYStart + y].getHitboxType().isTangible()) {
					tileCells[x][y] = new TileCell(x, y);
					tileCells[x][y].heuristicCost = Math.abs(x - (end.x - searchSquareXStart)) + Math.abs(y - (end.y - searchSquareYStart));
				}
			}
		}
		
		PriorityQueue<TileCell> open = new PriorityQueue<TileCell>(11, new TileComparator()); 
		open.add(tileCells[start.x - searchSquareXStart][start.y - searchSquareYStart]);
		boolean[][] closed = new boolean[tileCells.length][tileCells[0].length];
		TileCell current;
		
		while(true) {
			current = open.poll();
			if(current == null) {
				break;
			}
			closed[current.x][current.y] = true;
			if((current.x == end.x && current.y == end.y)
					|| MapUtils.boundingRectsIntersect(moverHitbox, current.x, current.y, targetHitbox, end.x, end.y)) {
				break;
			}
			
			TileCell t;
			if(current.x - 1 >= 0 
					&& EntityActions.isValidMovement(tiles, mover, current.x + searchSquareXStart, current.y + searchSquareYStart, Direction.LEFT)) {
                t = tileCells[current.x - 1][current.y];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1);
			}
			if(current.y - 1 >= 0
					&& EntityActions.isValidMovement(tiles, mover, current.x + searchSquareXStart, current.y + searchSquareYStart, Direction.DOWN)) {
                t = tileCells[current.x][current.y - 1];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1);
            }
            if(current.y + 1 < tileCells[0].length
            		&& EntityActions.isValidMovement(tiles, mover, current.x + searchSquareXStart, current.y + searchSquareYStart, Direction.UP)) {
                t = tileCells[current.x][current.y + 1];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1);
            }
            if(current.x + 1 < tileCells.length
            		&& EntityActions.isValidMovement(tiles, mover, current.x + searchSquareXStart, current.y + searchSquareYStart, Direction.RIGHT)) {
                t = tileCells[current.x + 1][current.y];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1);
            }
		}
		
		if(closed[end.x - searchSquareXStart][end.y - searchSquareYStart]) {
			//Trace back the path 
	        TileCell cell = tileCells[end.x - searchSquareXStart][end.y - searchSquareXStart];
	        TileCell lastCell = null;
	        while(cell.parent != null) {
	        	lastCell = cell;
	        	cell = cell.parent;
	        } 
	        System.out.println(lastCell.x + ", " + lastCell.y + ", " + cell.x + ", " + cell.y);
	        for(Direction d : Direction.values()) {
	        	if(lastCell.x - cell.x == d.getDeltaX()
	        			&& lastCell.y - cell.y == d.getDeltaY()) {
	        		return d;
	        	}
	        }
		}
		System.out.println(closed[end.x - searchSquareXStart][end.y - searchSquareXStart]);
		throw new NoPathsException();
	}
	
	/**
	 * Uses A*
	 * The path found in this method assumes the mover has a 1x1 hitbox
	 * @return - the direction of the first step in the calculated best path
	 */
	public static Direction calculateBestPathFirstStep(Tile[][] tiles, int maxSteps, Point start, Point end) throws NoPathsException {
		// Create and populate TileCell array
		int searchSquareXStart = Math.max(0, start.x - maxSteps);
		int searchSquareYStart = Math.max(0, start.y - maxSteps);
		int searchSquareXEnd = Math.min(tiles.length, start.x + maxSteps);
		int searchSquareYEnd = Math.min(tiles[0].length, start.y + maxSteps);
		TileCell[][] tileCells = new TileCell[searchSquareXEnd - searchSquareXStart][searchSquareYEnd - searchSquareYStart];
		if(end.x - start.x >= maxSteps
				|| end.y - start.y >= maxSteps
				|| end.x - searchSquareXStart < 0
				|| end.y - searchSquareYStart < 0) {
			throw new NoPathsException();
		}
		for(int x = 0; x < tileCells.length; x++) {
			for(int y = 0; y < tileCells[x].length; y++) {
				if(!tiles[searchSquareXStart + x][searchSquareYStart + y].getHitboxType().isTangible()) {
					tileCells[x][y] = new TileCell(x, y);
					tileCells[x][y].heuristicCost = Math.abs(x - (end.x - searchSquareXStart)) + Math.abs(y - (end.y - searchSquareYStart));
				}
			}
		}
		
		PriorityQueue<TileCell> open = new PriorityQueue<TileCell>(11, new TileComparator());
		open.add(tileCells[start.x - searchSquareXStart][start.y - searchSquareYStart]);
		boolean[][] closed = new boolean[tileCells.length][tileCells[0].length];
		TileCell current;
		
		while(true) {
			current = open.poll();
			if(current == null) {
				break;
			}
			closed[current.x][current.y] = true;
			if(current.x == end.x && current.y == end.y) {
				break;
			}
			
			TileCell t;
			if(current.x - 1 >= 0) {
                t = tileCells[current.x - 1][current.y];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1); 
			}
			if(current.y - 1 >= 0) {
                t = tileCells[current.x][current.y - 1];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1); 
            }
            if(current.y + 1 < tileCells[0].length) {
                t = tileCells[current.x][current.y + 1];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1); 
            }
            if(current.x + 1 < tileCells.length) {
                t = tileCells[current.x + 1][current.y];
                checkAndUpdateCost(open, closed, current, t, current.finalCost + 1); 
            }
		}
		
		if(closed[end.x - searchSquareXStart][end.y - searchSquareYStart]) {
			//Trace back the path 
            TileCell cell = tileCells[end.x - searchSquareXStart][end.y - searchSquareXStart];
            TileCell lastCell = null;
            while(cell.parent != null) {
            	lastCell = cell;
            	cell = cell.parent;
            } 
            
            for(Direction d : Direction.values()) {
            	if(lastCell.x - cell.x == d.getDeltaX()
            			&& lastCell.y - cell.y == d.getDeltaY()) {
            		return d;
            	}
            }
		}
		
		throw new NoPathsException();
	}
	
	private static void checkAndUpdateCost(PriorityQueue<TileCell> open, boolean[][] closed, TileCell current, TileCell cell, int cost) {
		if(cell == null || closed[cell.x][cell.y]) {
			return;
		}
        int tFinalCost = cell.heuristicCost + cost;
        
        boolean inOpen = open.contains(cell);
        if(!inOpen || tFinalCost < cell.finalCost){
            cell.finalCost = tFinalCost;
            cell.parent = current;
            if(!inOpen) {
            	open.add(cell);
            }
        }
	}
}
