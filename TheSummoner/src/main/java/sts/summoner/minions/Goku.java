package sts.summoner.minions;

import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import kobting.friendlyminions.monsters.MinionMove;
import sts.summoner.minions.core.EnhancedFriendlyMinion;
import sts.summoner.util.TextureLoader;

public class Goku extends EnhancedFriendlyMinion {
    
    private static String NAME = "Wraithling";
    private static String ID = "WraithlingMinion";
    private AbstractMonster target;
    private int baseAttack;

    public Goku(int baseAttack) {
        super(NAME, ID, 9, "summoner/images/monsters/models/wraithling.png");
        addMoves();
        this.baseAttack = baseAttack;
    }
    
    private void addMoves(){
        this.moves.addMove(new MinionMove("Attack", this, TextureLoader.getTexture("summoner/images/monsters/intents/attack_monster_intent_1.png"), "Deal 5 damage", () -> {
            target = AbstractDungeon.getRandomMonster();
            DamageInfo info = new DamageInfo(this,baseAttack,DamageInfo.DamageType.NORMAL);
            info.applyPowers(this, target); // <--- This lets powers affect minions attacks
            AbstractDungeon.actionManager.addToBottom(new DamageAction(target, info));
        }));
        this.moves.addMove(new MinionMove("Defend", this, TextureLoader.getTexture("summoner/images/monsters/intents/attack_monster_intent_5.png"),"Gain 5 block", () -> {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(this,this, 5));
        }));
    }
}