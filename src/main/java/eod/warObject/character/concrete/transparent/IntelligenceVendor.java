package eod.warObject.character.concrete.transparent;

import eod.GameObject;
import eod.Party;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.transparent.IntelligenceVendorSummon;
import eod.effect.Effect;
import eod.effect.EffectExecutor;
import eod.event.Event;
import eod.event.RoundEndEvent;
import eod.event.relay.EventReceiver;
import eod.event.relay.StatusHolder;
import eod.param.PointParam;
import eod.warObject.Status;
import eod.warObject.WarObject;
import eod.warObject.character.abstraction.Character;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static eod.effect.EffectFunctions.GiveStatus;

public class IntelligenceVendor extends Character {
    public IntelligenceVendor(Player player) {
        super(player, 3, 0, Party.TRANSPARENT);
        registerReceiver(new OwnedAbilities());
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new IntelligenceVendorSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "情報販";
    }

    @Override
    public ArrayList<Point> getAttackRange() {
        return player.getBoard().allSpaces(new Point(-1, 0), new PointParam());
    }

    @Override
    public void attack(EffectExecutor executor) {
        super.attack(executor);
        try {
            Point p = player.selectPosition(getAttackRange());
            WarObject object = player.getBoard().getObjectOn(p.x, p.y);

            object.registerStatusHolder(new AttackEffectLock(object));
        } catch (IllegalArgumentException e) {
            System.out.println("There's no object on the selected point. Skipping.");
        }

        afterAttack();
    }


    private class AttackEffectLock extends StatusHolder {

        public AttackEffectLock(WarObject carrier) {
            super(carrier);
            carrier.registerReceiver(new EndDetection());
        }

        @Override
        public ArrayList<Status> holdingStatus() {
            return new ArrayList<Status>() {{
                add(Status.NO_EFFECT);
                add(Status.NO_ATTACK);
            }};
        }

        private class EndDetection implements EventReceiver {
            @Override
            public void onEventOccurred(GameObject sender, Event event) {
                if(((RoundEndEvent) event).getEndedRound().getPlayer().isPlayerA() != player.isPlayerA()) {
                    teardown();
                }
            }

            @Override
            public ArrayList<Class<? extends Event>> supportedEventTypes() {
                return new ArrayList<Class<? extends Event>>(){{
                    add(RoundEndEvent.class);
                }};
            }

            @Override
            public void teardown() {
                getCarrier().unregisterReceiver(this);
                AttackEffectLock.this.teardown();
            }
        }
    }



    private class OwnedAbilities implements EventReceiver {

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            if(IntelligenceVendor.this.hasStatus(Status.NO_EFFECT)) {
                return;
            }
            if(event instanceof RoundEndEvent) {
                RoundEndEvent e = (RoundEndEvent) event;
                if(e.getEndedRound().getPlayer().isPlayerA() == player.isPlayerA()) {
                    if(player.getHand().size() <= 2) {
                        player.drawFromDeck(2);
                    } else {
                        player.drawFromDeck(1);
                    }
                }
            }
        }

        @Override
        public ArrayList<Class<? extends Event>> supportedEventTypes() {
            return new ArrayList<Class<? extends Event>>(){{
                add(RoundEndEvent.class);
            }};
        }

        @Override
        public void teardown() {
            IntelligenceVendor.this.unregisterReceiver(this);
        }
    }
}
