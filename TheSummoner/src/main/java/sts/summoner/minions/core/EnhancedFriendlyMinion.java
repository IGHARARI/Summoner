package sts.summoner.minions.core;


import java.util.UUID;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.Hitbox;

import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import kobting.friendlyminions.monsters.MinionMoveGroup;

public class EnhancedFriendlyMinion extends AbstractFriendlyMonster {
	public final UUID uuid;
	public boolean isSummoned;

	public EnhancedFriendlyMinion(String name, String id, int maxHealth, String imgUrl) {
		super(name, id, maxHealth, Settings.WIDTH/2, Settings.HEIGHT/2, 100, 100, imgUrl, 0, 0);
		uuid = UUID.randomUUID();
		isSummoned = attemptToAddToGrid();
	}
	
	public boolean attemptToAddToGrid() {
		boolean added = MinionArea.placeMinionInGrid(this);
		if (added) {
			Vector2 drawPosition = MinionArea.getMinionGridPosition(this);
			this.drawX = drawPosition.x;
			this.drawY = drawPosition.y;
	        this.hb_w = textureScaledWidth();
	        this.hb_h = textureScaledHeight();
	        this.hb_x = 0;
	        this.hb_y = 15f * Settings.scale;
	        this.hb = new Hitbox(this.hb_w, this.hb_h);
	        this.healthHb = new Hitbox(this.hb_w, 60.0f * Settings.scale);
	        this.refreshHitboxLocation();
	        this.refreshIntentHbLocation();
	        moves = new MinionMoveGroup(this.drawX - 15.0f * Settings.scale, this.drawY - 15 * Settings.scale);
		}
		return added;
	}
	
	@Override
	public void render(SpriteBatch sb) {
		if (isSummoned) {
			super.render(sb);
		}
	}
	
	public int textureScaledWidth() {
		return (int) (this.img.getWidth() * Settings.scale);
	}
	public int textureScaledHeight() {
		return (int) (this.img.getHeight() * Settings.scale);
	}

}
