package eod.warObject.character.concrete.red;

import eod.GameObject;
import eod.Gameboard;
import eod.Party;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.red.CrazyBomberSummon;
import eod.effect.EffectExecutor;
import eod.effect.RegionalAttack;
import eod.event.Event;
import eod.event.ObjectDeadEvent;
import eod.event.relay.EventReceiver;
import eod.param.DamageParam;
import eod.param.PointParam;
import eod.warObject.Damageable;
import eod.warObject.Status;
import eod.warObject.character.abstraction.Character;

import java.awt.*;
import java.util.ArrayList;

import static eod.effect.EffectFunctions.Damage;
import static eod.effect.EffectFunctions.RequestRegionalAttack;

public class CrazyBomber extends Character {
    public CrazyBomber(Player player) {
        super(player, 1, 2, Party.RED);
        registerReceiver(new OwnedAbilities());
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new CrazyBomberSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "瘋狂炸彈客";
    }

    @Override
    public ArrayList<Point> getAttackRange() {
        return player.getBoard().allSpaces(new Point(Gameboard.firstLine, 0), new PointParam());
    }

    @Override
    public void attack(EffectExecutor executor) {
        super.attack(executor);
        Point p = player.selectPosition(getAttackRange());

        ArrayList<Point> singleTarget = new ArrayList<>();
        singleTarget.add(p);
        executor.tryToExecute(
                RequestRegionalAttack(attack).from(this).to(singleTarget)
        );

        PointParam param = new PointParam();
        param.range = 1;
        SpecialRegionalAttack SRA = new SpecialRegionalAttack(1);
        executor.tryToExecute(
            SRA.to(player.getBoard().getSurrounding(p, param))
        );

        afterAttack();
    }

    private class OwnedAbilities implements EventReceiver {

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            if(CrazyBomber.this.hasStatus(Status.NO_EFFECT)) {
                return;
            }
            if(event instanceof ObjectDeadEvent) {
                ObjectDeadEvent e = (ObjectDeadEvent) event;
                if(e.getDeadObject() == CrazyBomber.this) {
                    PointParam param = new PointParam();
                    param.range = 1;
                    SpecialRegionalAttack SRA = new SpecialRegionalAttack(3);
                    SRA.from(CrazyBomber.this).to(player.getBoard().getSurrounding(position, param));
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
            CrazyBomber.this.unregisterReceiver(this);
        }
    }

    private class SpecialRegionalAttack extends RegionalAttack {

        public SpecialRegionalAttack(int hp) {
            super(hp);
        }

        @Override
        public RegionalAttack to(ArrayList<Point> targets) {
            Gameboard board = player.getBoard();
            for(Point p:targets) {
                try {
                    Damageable target = board.getObjectOn(p.x, p.y);
                    Damage(new DamageParam(param.hp), HandlerType.Rival).on(target);
                } catch (IllegalArgumentException e) {
                    System.out.println("There's no object on the point, skipping.");
                }
            }
            return this;
        }
    }
}
