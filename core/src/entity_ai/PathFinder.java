package entity_ai;

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
            /**
             * return c1.finalCost<c2.finalCost?-1:
                        c1.finalCost>c2.finalCost?1:0;
             */
            return 0;
        }
    }
	
	public static Array<Direction> calculateBestPath(Tile[][] tiles, int maxSteps, Entity mover, Entity target) {
		Array<Direction> path = new Array<Direction>();
		Point start = ComponentMappers.hitboxMapper.get(mover).getMapPosition();
		Point end = ComponentMappers.hitboxMapper.get(target).getMapPosition();
		
		// Create and populate TileCell array
		int searchSquareXStart = Math.max(0, start.x - maxSteps);
		int searchSquareYStart = Math.max(0, start.y - maxSteps);
		int searchSquareXEnd = Math.min(tiles.length, start.x + maxSteps);
		int searchSquareYEnd = Math.min(tiles[0].length, start.y + maxSteps);
		TileCell[][] tileCells = new TileCell[searchSquareXEnd - searchSquareXStart][searchSquareYEnd - searchSquareYStart];
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
		
		HitboxType[][] moverHitbox = ComponentMappers.hitboxMapper.get(mover).getHitbox();
		HitboxType[][] targetHitbox = ComponentMappers.hitboxMapper.get(target).getHitbox();
		
		while(true) {
			current = open.poll();
			if(current == null) {
				break;
			}
			closed[current.x][current.y] = true;
			if(current.equals(tileCells[end.x - searchSquareXStart][end.y - searchSquareYStart])
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
		
		if(closed[end.x - searchSquareXStart][end.y - searchSquareXStart]) {
			//Trace back the path 
            System.out.println("Path: ");
            TileCell c = tileCells[end.x - searchSquareXStart][end.y - searchSquareXStart];
            System.out.print("(" + c.x + ", " + c.y + ")");
            int steps = 0;
            while(c.parent != null) {
                System.out.println(" -> " + "(" + c.parent.x + ", " + c.parent.y + ")");
                c = c.parent;
                
                steps++;
                if(steps >= maxSteps) {
                	path.clear();
                	return path;
                }
            } 
		}
		
		return path;
	}
	
	/**
	 * The path found using this method assumes the mover has a 1x1 hitbox
	 */
	public static Array<Direction> calculateBestPath(Tile[][] tiles, int maxSteps, Point start, Point end) {
		Array<Direction> path = new Array<Direction>();
		
		// Create and populate TileCell array
		int searchSquareXStart = Math.max(0, start.x - maxSteps);
		int searchSquareYStart = Math.max(0, start.y - maxSteps);
		int searchSquareXEnd = Math.min(tiles.length, start.x + maxSteps);
		int searchSquareYEnd = Math.min(tiles[0].length, start.y + maxSteps);
		TileCell[][] tileCells = new TileCell[searchSquareXEnd - searchSquareXStart][searchSquareYEnd - searchSquareYStart];
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
			if(current.equals(tileCells[end.x - searchSquareXStart][end.y - searchSquareYStart])) {
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
		
		if(closed[end.x - searchSquareXStart][end.y - searchSquareXStart]) {
			//Trace back the path 
            System.out.println("Path: ");
            TileCell c = tileCells[end.x - searchSquareXStart][end.y - searchSquareXStart];
            System.out.print("(" + c.x + ", " + c.y + ")");
            int steps = 0;
            while(c.parent != null && steps < maxSteps) {
                System.out.println(" -> " + "(" + c.parent.x + ", " + c.parent.y + ")");
                c = c.parent;
                steps++;
            } 
            
            if(steps >= maxSteps) {
            	path.clear();
            	return path;
            }
		}
		
		return path;
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
