package eod.card.concrete.summon.blue;

import eod.Party;
import eod.card.abstraction.Card;
import eod.card.abstraction.summon.SummonCard;
import eod.card.abstraction.summon.SummonCardType;
import eod.effect.Summon;
import eod.warObject.character.concrete.blue.Morriad;

import static eod.effect.EffectFunctions.*;

public class MorriadSummon extends SummonCard {
    public MorriadSummon() {
        // also a token card. TODO
        super(8, SummonCardType.SPECIAL);
    }

    @Override
    public Summon summonEffect() {
        return Summon(new Morriad(player)).onOnePointOf(player, player.getBaseEmpty());
    }

    @Override
    public Card copy() {
        Card c = new MorriadSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "召喚 雙面警官．莫里亞德";
    }

    @Override
    public Party getParty() {
        return Party.BLUE;
    }
}
