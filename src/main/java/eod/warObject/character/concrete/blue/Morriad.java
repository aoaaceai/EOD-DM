package eod.warObject.character.concrete.blue;

import eod.GameObject;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.blue.MorriadSummon;
import eod.effect.Effect;
import eod.effect.EffectExecutor;
import eod.event.BeforeObjectDamageEvent;
import eod.event.Event;
import eod.event.ObjectDeadEvent;
import eod.event.RoundEndEvent;
import eod.event.relay.EventReceiver;
import eod.event.relay.StatusHolder;
import eod.specifier.Accessing;
import eod.warObject.Status;
import eod.warObject.WarObject;
import eod.warObject.character.abstraction.Character;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static eod.Party.BLUE;
import static eod.effect.EffectFunctions.*;
import static eod.specifier.WarObjectSpecifier.*;
import static eod.specifier.condition.Conditions.*;

public class Morriad extends Character {
    public Morriad(Player player) {
        super(player, 8, 4, BLUE);
        registerReceiver(new OwnedAbilities());
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new MorriadSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "雙面警官．莫里亞德";
    }

    @Override
    public ArrayList<Point> getAttackRange() {
        return new ArrayList<Point>(){{
            addAll(player.getFrontEdge(position));
            addAll(player.getBackEdge(position));
            addAll(player.getLREdge(position));
        }};
    }

    @Override
    public void attack(EffectExecutor executor) {
        super.attack(executor);

        Accessing characterInRange = WarObject(player.getBoard()).which(InRangeOf(this)).which(Being(Character.class));
        WarObject[] targets = new ArrayList<WarObject>(){{
            addAll(Arrays.asList(characterInRange.which(OwnedBy(player)).get()));
            addAll(Arrays.asList(characterInRange.which(OwnedBy(player.rival())).which(WithoutStatus(Status.SNEAK)).get()));
        }}.toArray(WarObject[]::new);

        Character target = (Character) player.selectObject(targets);

        if(target.getPlayer().isPlayerA() == player.isPlayerA()) {
            executor.tryToExecuteInSequence(new Effect[] {
                    IncreaseHealth(2).to(target),
                    IncreaseAttack(2).to(target)
            });
            target.registerReceiver(new Protection(target));
        } else {
            executor.tryToExecuteInSequence(new Effect[] {
                    RequestRegionalAttack(attack).from(this).to(target),
                    GiveStatus(Status.LOCKED, Effect.HandlerType.Owner).to(target),
            });
            target.registerStatusHolder(new Disturb(target));
        }


    }

    private class Protection implements EventReceiver {
        Character carrier;

        public Protection(Character carrier) {
            this.carrier = carrier;
        }

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            if(event instanceof RoundEndEvent) {
                RoundEndEvent e = (RoundEndEvent) event;
                if(e.getEndedRound().getPlayer().isPlayerA() != carrier.getPlayer().isPlayerA()) {
                    teardown();
                }
            }

            if(event instanceof BeforeObjectDamageEvent) {
                BeforeObjectDamageEvent e = (BeforeObjectDamageEvent) event;
                if(e.getVictim() == carrier) {
                    e.getParam().reduceDamage(2);
                    teardown();
                }
            }
        }

        @Override
        public ArrayList<Class<? extends Event>> supportedEventTypes() {
            return new ArrayList<Class<? extends Event>>(){{
                add(RoundEndEvent.class);
                add(BeforeObjectDamageEvent.class);
            }};
        }

        @Override
        public void teardown() {
            carrier.unregisterReceiver(this);
            carrier = null;
        }
    }

    private class Disturb extends StatusHolder {

        public Disturb(WarObject carrier) {
            super(carrier);
            carrier.registerReceiver(new EndDetection());
        }

        @Override
        public ArrayList<Status> holdingStatus() {
            return new ArrayList<Status>() {{
                add(Status.NO_ATTACK_INITIATION);
            }};
        }

        private class EndDetection implements EventReceiver {

            @Override
            public void onEventOccurred(GameObject sender, Event event) {
                if(event instanceof RoundEndEvent) {
                    RoundEndEvent e = (RoundEndEvent) event;
                    if(e.getEndedRound().getPlayer().isPlayerA() == getCarrier().getPlayer().isPlayerA()) {
                        teardown();
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
                getCarrier().unregisterReceiver(this);
                Disturb.this.teardown();
            }
        }
    }

    private class OwnedAbilities implements EventReceiver {

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            if(event instanceof ObjectDeadEvent) {
                ObjectDeadEvent e = (ObjectDeadEvent) event;
                if(e.getDeadObject() == Morriad.this) {
                    boolean friendly = new Random().nextBoolean();

                    if(friendly) {
                        player.tryToExecute(
                            Summon(new FriendlyOfficer(player)).onOnePointOf(player, player.getBaseEmpty())
                        );
                    } else {
                        player.tryToExecute(
                                Summon(new NamelessSanctioner(player)).onOnePointOf(player, player.getBaseEmpty())
                        );
                    }

                    teardown();
                }
            }
        }

        @Override
        public ArrayList<Class<? extends Event>> supportedEventTypes() {
            return new ArrayList<Class<? extends Event>>() {{
                add(ObjectDeadEvent.class);
            }};
        }

        @Override
        public void teardown() {
            Morriad.this.unregisterReceiver(this);
        }
    }
}
