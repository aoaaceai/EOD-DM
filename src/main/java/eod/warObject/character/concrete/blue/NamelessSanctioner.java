package eod.warObject.character.concrete.blue;

import eod.GameObject;
import eod.Party;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.blue.NamelessSanctionerSummon;
import eod.effect.EffectExecutor;
import eod.event.Event;
import eod.event.RoundStartEvent;
import eod.event.relay.EventReceiver;
import eod.specifier.WarObjectSpecifier;
import eod.warObject.WarObject;
import eod.warObject.character.abstraction.Character;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static eod.effect.EffectFunctions.RequestRegionalAttack;
import static eod.effect.EffectFunctions.Summon;
import static eod.specifier.condition.Conditions.Being;
import static eod.specifier.condition.Conditions.OwnedBy;

public class NamelessSanctioner extends Character {
    public NamelessSanctioner(Player player) {
        super(player, 2, 3, Party.BLUE);
        registerReceiver(new OwnedAbilities());
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new NamelessSanctionerSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "無名制裁者";
    }

    @Override
    public ArrayList<Point> getAttackRange() {
        return player.getBackEdge(position);
    }

    @Override
    public void attack(EffectExecutor executor) {
        super.attack(executor);

        executor.tryToExecute(
                RequestRegionalAttack(attack).from(this).to(getAttackRange())
        );
    }

    private class OwnedAbilities implements EventReceiver {

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            if(event instanceof RoundStartEvent) {
                RoundStartEvent e = (RoundStartEvent) event;
                if(e.getStartedRound().getPlayer().isPlayerA() == player.isPlayerA()) {
                    WarObject[] officers = WarObjectSpecifier.WarObject(player.getBoard()).which(OwnedBy(player)).which(Being(FriendlyOfficer.class)).get();
                    if(officers.length == 0) {
                        return;
                    }
                    Player previousOwner = player;
                    ((FriendlyOfficer) officers[new Random().nextInt(officers.length)]).die();
                    die();
                    // the die process will remove the player, so it's necessary to declare a previousOwner.
                    Morriad morriad = new Morriad(previousOwner);
                    previousOwner.tryToExecute(
                            Summon(morriad).onOnePointOf(previousOwner, previousOwner.getConflictEmpty())
                    );
                    morriad.attack(previousOwner);
                    // no need to teardown cuz die() already does that.
                }
            }
        }

        @Override
        public ArrayList<Class<? extends Event>> supportedEventTypes() {
            return new ArrayList<Class<? extends Event>>(){{
                add(RoundStartEvent.class);
            }};
        }

        @Override
        public void teardown() {
            NamelessSanctioner.this.unregisterReceiver(this);
        }
    }
}
