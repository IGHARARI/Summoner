package sts.summoner.cards.skills;

import static sts.summoner.SummonerMod.makeCardPath;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import basemod.abstracts.CustomCard;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import sts.summoner.SummonerMod;
import sts.summoner.characters.TheSummoner;
import sts.summoner.minions.Wraithling;

public class SummonWraithling extends CustomCard {
    public static final String ID = SummonerMod.makeID("SummonWraithling");
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String IMG = makeCardPath("Skill.png");
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;

    private static final CardRarity RARITY = CardRarity.BASIC;
    private static final CardTarget TARGET = CardTarget.SELF;
    private static final CardType TYPE = CardType.SKILL;
    public static final CardColor COLOR = TheSummoner.Enums.THE_SUMMONER_COLOR;

    private static final int COST = 0;
    private static final int BASE_ATK = 5;
    private static final int UPG_TO_ATK = 2;


    // /STAT DECLARATION/


    public SummonWraithling() {
        super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
        this.isInnate = true;
        this.magicNumber = this.baseMagicNumber = BASE_ATK;
    }

    // Actions the card should do.
    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {
        if(abstractPlayer instanceof AbstractPlayerWithMinions) {
            AbstractPlayerWithMinions player = (AbstractPlayerWithMinions) abstractPlayer;
            player.addMinion(new Wraithling(magicNumber));
        }
    }

    //Upgraded stats.
    @Override
    public void upgrade() {
        if (!upgraded) {
            upgradeName();
            initializeDescription();
            upgradeMagicNumber(UPG_TO_ATK);
        }
    }
}
