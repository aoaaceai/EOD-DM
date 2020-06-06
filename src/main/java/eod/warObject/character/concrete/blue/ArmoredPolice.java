package eod.warObject.character.concrete.blue;

import eod.GameObject;
import eod.Party;
import eod.Player;
import eod.card.abstraction.Card;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.blue.ArmoredPoliceSummon;
import eod.card.concrete.summon.transparent.DroneSummon;
import eod.event.Event;
import eod.event.ObjectEnterEvent;
import eod.event.relay.EventReceiver;
import eod.param.PointParam;
import eod.warObject.character.abstraction.assaulter.Shooter;

import java.awt.*;
import java.util.ArrayList;

public class ArmoredPolice extends Shooter {
    public ArmoredPolice(Player player) {
        super(player, 2, 3, Party.BLUE);
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new ArmoredPoliceSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "機裝警備員";
    }

    @Override
    public ArrayList<Point> getAttackRange() {
        PointParam param = new PointParam();
        param.range = 1;
        return player.getBoard().get4Ways(position, param);
    }

    private class OwnedAbilities implements EventReceiver {

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            if(event instanceof ObjectEnterEvent) {
                ObjectEnterEvent e = (ObjectEnterEvent) event;
                if(e.getObject() == ArmoredPolice.this) {
                    player.handReceive(new ArrayList<Card>(){{
                        DroneSummon d = new DroneSummon();
                        d.setPlayer(player);
//                        if(player.isStable()) {
//                            d.setCost(0);
//                        }
                        add(d);
                    }});
                }
            }
        }

        @Override
        public ArrayList<Class<? extends Event>> supportedEventTypes() {
            return null;
        }

        @Override
        public void teardown() {

        }
    }
}
