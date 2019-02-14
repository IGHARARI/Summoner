package sts.summoner.patches.minions;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import sts.summoner.minions.core.MinionArea;
import sts.summoner.util.BattleHelper;

@SpirePatch(
		clz=AbstractDungeon.class,
		method="nextRoomTransition",
		paramtypez= {SaveFile.class}
	)
public class OnBattleStartMinionSetupPatch {
	
	public static final Logger logger = LogManager.getLogger(OnBattleStartMinionSetupPatch.class);
	
	@SpirePostfixPatch
	public static void moveCharacter(AbstractDungeon __instance, SaveFile savefile) {
		ArrayList<AbstractMonster> monsterList = BattleHelper.getCurrentBattleMonstersSortedOnX(false);
		logger.info("ENTERING MinionArea Insert patch");
		if (!monsterList.isEmpty()) {
			logger.info("Monster list is not empty");
			AbstractPlayer p = AbstractDungeon.player;
			//If there's no monster on the left of the player, move the player to the left...
			int bufferSpace = (int) (60.0f * Settings.scale);
			int leftmostAvailableX = 0;
			//If it's a "surrounded" battle
			if (p.hb_x > monsterList.get(0).hb_x) {
				for(AbstractMonster monster : monsterList) {
					if (monster.hb_x < p.hb_x) leftmostAvailableX = (int) (monster.hb_x + monster.hb_w);
				}
			}
			leftmostAvailableX += bufferSpace;
			logger.info("Moving player to : " + leftmostAvailableX + " , " + AbstractDungeon.floorY);
			AbstractDungeon.player.movePosition(leftmostAvailableX, AbstractDungeon.floorY);
			
			MinionArea.atBattleStart();
		} else {
			logger.info("Monster list is EMPTY OOH NOO");
		}
	}
	
}

