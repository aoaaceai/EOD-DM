package eod.card.concrete.summon.blue;

import eod.Party;
import eod.card.abstraction.Card;
import eod.card.abstraction.summon.SummonCard;
import eod.card.abstraction.summon.SummonCardType;
import eod.effect.Summon;
import eod.warObject.character.concrete.blue.NamelessSanctioner;

import static eod.effect.EffectFunctions.Summon;

public class NamelessSanctionerSummon extends SummonCard {
    public NamelessSanctionerSummon() {
        super(4, SummonCardType.NORMAL);
    }

    @Override
    public Summon summonEffect() {
        return Summon(new NamelessSanctioner(player)).onOnePointOf(player, player.getBaseEmpty());
    }

    @Override
    public Card copy() {
        Card c = new NamelessSanctionerSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "召喚 無名制裁者";
    }

    @Override
    public Party getParty() {
        return Party.BLUE;
    }
}
