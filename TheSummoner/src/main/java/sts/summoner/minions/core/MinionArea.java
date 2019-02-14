package sts.summoner.minions.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

import sts.summoner.util.BattleHelper;

public class MinionArea {
	private static HashSet<UUID> spawnedMinions;
	private static MinionGrid minionGrid;
	static private final int PLAYER_MINION_BUFFER = (int) (22f * Settings.scale);
	static private final int MINION_MONSTER_BUFFER = (int) (50f * Settings.scale);
	
	static public int X_START_COORDINATE;
	static public int Y_TOP_COORDINATE;
	static public int X_END_COORDINATE;
	static public int Y_BOT_COORDINATE;
	static public int MAX_WIDTH;
	static public int MAX_HEIGHT;
	
	public static final Logger logger = LogManager.getLogger(MinionArea.class);
	
	
	public static void atBattleStart() {
		if (!RoomPhase.COMBAT.equals(AbstractDungeon.getCurrRoom().phase)) return;
		
		logger.info("(AbstractDungeon.player.drawX + AbstractDungeon.player.hb_w) + PLAYER_MINION_BUFFER;");
		logger.info(AbstractDungeon.player.drawX +"+"+ AbstractDungeon.player.hb_w +"+"+ PLAYER_MINION_BUFFER);
		
		
		X_START_COORDINATE = (int) (AbstractDungeon.player.drawX + AbstractDungeon.player.hb_w) + PLAYER_MINION_BUFFER;
		Y_TOP_COORDINATE = (int) (410.0f * Settings.scale);
		Y_BOT_COORDINATE = (int) (135.0f * Settings.scale);
		
		ArrayList<AbstractMonster> monsters = BattleHelper.getCurrentBattleMonstersSortedOnX(false);
		X_END_COORDINATE = (int) ((monsters != null && monsters.size() > 0)? 
				(monsters.get(0).drawX - MINION_MONSTER_BUFFER ) : X_START_COORDINATE);
		
		MAX_WIDTH = Math.abs(X_END_COORDINATE - X_START_COORDINATE);
		MAX_HEIGHT = Math.abs(Y_TOP_COORDINATE - Y_BOT_COORDINATE);
		
		logger.info("Minion Area XXXX Start/end coordinates: " + X_START_COORDINATE + " , " + X_END_COORDINATE);
		logger.info("Minion Area YYYY Top/Bot coordinates: " + Y_TOP_COORDINATE + " , " + Y_BOT_COORDINATE);
		
		logger.info("Width x Height: " + MAX_WIDTH + " , " + MAX_HEIGHT);
		
		spawnedMinions = new HashSet<UUID>();
		minionGrid = new MinionGrid();
	}
	
	public static boolean placeMinionInGrid(EnhancedFriendlyMinion minion) {
		MinionBox minionBox = new MinionBox(minion.uuid, new Vector2(minion.textureScaledWidth(), minion.textureScaledHeight()));
		boolean added = minionGrid.addMinion(minionBox);
		if (added) spawnedMinions.add(minion.uuid);
		logger.info("Minion as added?: " + added);
		if (added) {
			Vector2 pos = getMinionGridPosition(minion);
			logger.info("Added at coords  x,y: " + pos.x + " , " + pos.y);
		}
		return added;
	}
	
	public static Vector2 getMinionGridPosition(EnhancedFriendlyMinion minion) {
		if (!spawnedMinions.contains(minion.uuid)) return null;
		Vector2 minionDraw = minionGrid.getMinionRelativeDrawVector(minion.uuid);
		minionDraw.x += X_START_COORDINATE;
		minionDraw.y += Y_BOT_COORDINATE;
		return minionDraw;
	}
}

class MinionBox {
	public UUID uuid;
	public Vector2 dimensions;
	
	public MinionBox(UUID uuid, Vector2 dimensions) {
		this.uuid = uuid;
		this.dimensions = dimensions;
	}
	
	public int width() {
		return (int) dimensions.x;
	}
	
	public int height() {
		return (int) dimensions.y;
	}

	@Override
	public boolean equals(Object otherMinion) {
		// TODO Auto-generated method stub
		if (!(otherMinion instanceof MinionBox)) return false;
		return this == otherMinion ||
				(this != null && otherMinion != null && this.uuid.equals(((MinionBox)otherMinion).uuid));
	}
}

class MinionRow implements Iterable<MinionBox>{
	public static final Logger logger = LogManager.getLogger(MinionRow.class);
	public ArrayList<MinionBox> minions;
	private int width;
	private int height;
	public static final int HORIZ_BUFFER_SPACE = (int) (25f * Settings.scale);
	
	MinionRow(){
		minions = new ArrayList<MinionBox>();
		width = 0;
		height = 0;
	}
	
	public void addMinion(MinionBox minion){
		minions.add(minion);
		width += minion.width() + HORIZ_BUFFER_SPACE;
		height = Math.max(minion.height(), height);
	}
	
	public void removeMinion(MinionBox minion){
		minions.remove(minion);
		width -= (minion.width() + HORIZ_BUFFER_SPACE);
		if (minion.height() == height) recalculateHeight();
	}
	
	public void addMinion(UUID uuid) {
		MinionBox minion = getMinion(uuid);
		if (minion != null) addMinion(minion);
	}
	public void removeMinion(UUID uuid) {
		MinionBox minion = getMinion(uuid);
		if (minion != null) removeMinion(minion);
	}
	
	public MinionBox getMinion(UUID uuid) {
		for (MinionBox minion : minions) {
			if (minion.uuid.equals(uuid)) return minion;
		}
		return null;
	}

	public int getMinionXOffset(UUID uuid) {
		int xOffset = 0;
		for (MinionBox minion : minions) {
			if (minion.uuid.equals(uuid)) {
				return xOffset;
			} else {
				xOffset += minion.width() + HORIZ_BUFFER_SPACE;
			}
		}
		return -1;
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	private void recalculateHeight() {
		height = 0;
		for (int i = 0; i < minions.size(); i++) height = Math.max(height, minions.get(i).height());
	}

	@Override
	public Iterator<MinionBox> iterator() {
		return minions.iterator();
	}

	public boolean canAddMinion(MinionBox minion) {
		logger.info("(width + HORIZ_BUFFER_SPACE + minion.width()) < MinionArea.MAX_WIDTH || translates to");
		logger.info("(" + width + " + " + HORIZ_BUFFER_SPACE + " + "  + minion.width() + ") < " + MinionArea.MAX_WIDTH);
		return (width + HORIZ_BUFFER_SPACE + minion.width()) < MinionArea.MAX_WIDTH;
	}
}

class MinionGrid {
	public static final Logger logger = LogManager.getLogger(MinionGrid.class);
	public static final int VERTICAL_BUFFER_SPACE = (int) (50.0f * Settings.scale);
	public static final int STATIC_Y_OFFSET = 0;
	public static final int STATIC_X_OFFSET = 0;
	
	//A list containing all current minion rows. get(0) is the top row.
	private ArrayList<MinionRow> minionRows;

	public MinionGrid() {
		minionRows = new ArrayList<MinionRow>();
	}
	
	/**
	 * 
	 * @param minion
	 * @return @true if the minion as successfully added, @false if the grid is full
	 */
	public boolean addMinion(MinionBox minion) {
		boolean added = false;
		//Check if the minion can be added into any existing row
		for (MinionRow row : minionRows) {
			if (row.canAddMinion(minion)) {
				if (row.height() <= minion.height()) {
					row.addMinion(minion);
					added = true;
					break;
				} else {
					int heightDifference = minion.height() - row.height();
					if (getGridCurrentHeight() + heightDifference < MinionArea.MAX_HEIGHT) {
						row.addMinion(minion);
						added = true;
						break;
					}
				}
			}
		}
		//If not, check if it can be added into a new row
		if (!added) {
			logger.info("NOT added to any existing Row, checking for new row...");
			logger.info("(getGridCurrentHeight() + VERTICAL_BUFFER_SPACE + minion.height() < MinionArea.MAX_HEIGHT)  || is then...");
			logger.info("(" + getGridCurrentHeight() + " + " + VERTICAL_BUFFER_SPACE + " + " + minion.height() + " <  " + MinionArea.MAX_HEIGHT + ")");
			if (getGridCurrentHeight() + VERTICAL_BUFFER_SPACE + minion.height() < MinionArea.MAX_HEIGHT) {
				MinionRow newRow = new MinionRow();
				newRow.addMinion(minion);
				minionRows.add(newRow);
				added = true;
			}
		}
		return added;
	}
	
	public Vector2 getMinionRelativeDrawVector(UUID uuid) {
		Vector2 drawXY = null;
		boolean isFirstRow = true;
		int gridVerticalOffset = MinionArea.MAX_HEIGHT;
		for(MinionRow row : minionRows) {
			int xOffset  = row.getMinionXOffset(uuid);
			if (!isFirstRow) gridVerticalOffset -= (row.height() + VERTICAL_BUFFER_SPACE);
			if (xOffset != -1) return new Vector2(xOffset, gridVerticalOffset);
			isFirstRow = false;
		}
		
		return drawXY;
	}

	private int getGridCurrentHeight() {
		int height = 0;
		for (MinionRow row : minionRows) {
			height += row.height();
		}
		return height;
	}
}
