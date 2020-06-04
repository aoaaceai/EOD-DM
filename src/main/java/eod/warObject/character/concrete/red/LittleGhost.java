package eod.warObject.character.concrete.red;

import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.red.LittleGhostSummon;
import eod.warObject.character.abstraction.other.Ghost;

public class LittleGhost extends Ghost {
    public LittleGhost(Player player) {
        super(player, 1);
        // TODO: ask Spacezipper about the details of LittleGhost
    }

    @Override
    public String getName() {
        return "小亡靈";
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new LittleGhostSummon();
        c.setPlayer(player);
        return c;
    }
}
