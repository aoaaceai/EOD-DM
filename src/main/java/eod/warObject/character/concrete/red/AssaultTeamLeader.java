package eod.warObject.character.concrete.red;

import eod.GameObject;
import eod.Party;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.red.AssaultTeamLeaderSummon;
import eod.event.*;
import eod.effect.EffectExecutor;
import eod.event.Event;
import eod.event.relay.EventReceiver;
import eod.event.RoundStartEvent;
import eod.event.StatusAcquiredEvent;
import eod.param.PointParam;
import eod.warObject.Status;
import eod.warObject.character.abstraction.Character;

import java.awt.*;
import java.util.ArrayList;

import static eod.effect.EffectFunctions.*;

public class AssaultTeamLeader extends Character {
    public AssaultTeamLeader(Player player) {
        super(player, 1, 1, Party.RED);
    }

    @Override
    public void attack(EffectExecutor executor) {
        super.attack(executor);
        executor.tryToExecute(
                RequestRegionalAttack(attack).from(this).to(getAttackRange())
        );

        afterAttack();
    }

    @Override
    public ArrayList<Point> getAttackRange() {
        PointParam param = new PointParam();
        param.range = 1;
        return player.getFront(position, param);
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new AssaultTeamLeaderSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "特攻隊長";
    }

    private class OwnedAbilities implements EventReceiver {

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            if(AssaultTeamLeader.this.hasStatus(Status.NO_EFFECT)) {
                return;
            }
            if(event instanceof RoundStartEvent) {
                RoundStartEvent e = (RoundStartEvent) event;
                if (e.getStartedRound().getPlayer().isPlayerA() == AssaultTeamLeader.this.getPlayer().isPlayerA()) {
                    PointParam param = new PointParam();
                    param.emptySpace = true;
                    param.range = 1;
                    ArrayList<Point> front = player.getFront(position, param);
                    if(front.size() != 0) {
                        moveTo(front.get(0));
                    }
                }
            }
            if(event instanceof StatusAcquiredEvent) {
                StatusAcquiredEvent e = (StatusAcquiredEvent) event;
                if(e.getObject() == AssaultTeamLeader.this && e.getStatus() == Status.SNEAK) {
                    Player owner = AssaultTeamLeader.this.getPlayer();
                    owner.tryToExecute(
                            IncreaseAttack(1).to(AssaultTeamLeader.this)
                    );
                    owner.tryToExecute(
                            IncreaseHealth(1).to(AssaultTeamLeader.this)
                    );
                }
            }
            if(event instanceof ObjectEnterEnemyBaseEvent) {
                ObjectEnterEnemyBaseEvent e = (ObjectEnterEnemyBaseEvent) event;
                if(e.getObject() == AssaultTeamLeader.this) {
                    Player owner = AssaultTeamLeader.this.getPlayer();
                    owner.tryToExecute(
                            IncreaseAttack(1).to(AssaultTeamLeader.this)
                    );
                    owner.tryToExecute(
                            IncreaseHealth(1).to(AssaultTeamLeader.this)
                    );
                }
            }
        }

        @Override
        public ArrayList<Class<? extends Event>> supportedEventTypes() {
            return new ArrayList<Class<? extends Event>>(){{
                add(RoundStartEvent.class);
                add(ObjectEnterEnemyBaseEvent.class);
                add(StatusAcquiredEvent.class);
            }};
        }

        @Override
        public void teardown() {
            AssaultTeamLeader.this.unregisterReceiver(this);
        }
    }
}
