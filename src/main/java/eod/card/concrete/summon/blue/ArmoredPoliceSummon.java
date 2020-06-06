package eod.card.concrete.summon.blue;

import eod.Party;
import eod.card.abstraction.Card;
import eod.card.abstraction.summon.ShooterSummon;
import eod.card.abstraction.summon.SummonCardType;
import eod.effect.Summon;
import eod.warObject.character.concrete.blue.ArmoredPolice;

import static eod.effect.EffectFunctions.*;

public class ArmoredPoliceSummon extends ShooterSummon {
    public ArmoredPoliceSummon() {
        super(3, SummonCardType.NORMAL);
    }

    @Override
    public Summon summonEffect() {
        return Summon(new ArmoredPolice(player)).onOnePointOf(player, player.getBaseEmpty());
    }

    @Override
    public Card copy() {
        Card c = new ArmoredPoliceSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "召喚 機裝警備員";
    }

    @Override
    public Party getParty() {
        return Party.BLUE;
    }
}
