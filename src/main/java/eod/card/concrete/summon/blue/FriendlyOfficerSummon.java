package eod.card.concrete.summon.blue;

import eod.Party;
import eod.card.abstraction.Card;
import eod.card.abstraction.summon.SummonCard;
import eod.card.abstraction.summon.SummonCardType;
import eod.effect.Summon;
import eod.warObject.character.concrete.blue.FriendlyOfficer;

import static eod.effect.EffectFunctions.Summon;

public class FriendlyOfficerSummon extends SummonCard {
    public FriendlyOfficerSummon() {
        super(4, SummonCardType.NORMAL);
    }

    @Override
    public Summon summonEffect() {
        return Summon(new FriendlyOfficer(player)).onOnePointOf(player, player.getBaseEmpty());
    }

    @Override
    public Card copy() {
        Card c = new FriendlyOfficerSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "召喚 和善的警官";
    }

    @Override
    public Party getParty() {
        return Party.BLUE;
    }
}
