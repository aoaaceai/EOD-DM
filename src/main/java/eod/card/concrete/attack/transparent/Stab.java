package eod.card.concrete.attack.transparent;

import eod.Gameboard;
import eod.Party;
import eod.card.abstraction.Card;
import eod.card.abstraction.action.AttackCard;
import eod.effect.EffectExecutor;
import eod.exceptions.NotSupportedException;
import eod.param.AttackParam;
import eod.param.PointParam;
import eod.warObject.Status;
import eod.warObject.WarObject;
import eod.warObject.character.abstraction.assaulter.Assassin;

import java.awt.*;

import static eod.effect.EffectFunctions.RequestRegionalAttack;
import static eod.specifier.WarObjectSpecifier.*;
import static eod.specifier.condition.Conditions.*;

public class Stab extends AttackCard {
    public Stab() {
        super(1);
    }

    @Override
    public void attack(EffectExecutor executor) {
        Gameboard board = player.getBoard();
        PointParam param = new PointParam();
        param.range = 1;
        Assassin assassin = (Assassin) player.selectObject(
                WarObject(player.getBoard())
                .which(OwnedBy(player))
                .which(WithoutStatus(Status.NO_ATTACK))
                .which(Being(Assassin.class)).get()
        );

        WarObject target;
        try {
            Point p = player.selectPosition(assassin.getAttackRange());
            target = board.getObjectOn(p.x, p.y);
        } catch(NotSupportedException e) {
            System.out.println("The selected assassin doesn't have its default attack range. Skipping.");
            return;
        } catch(IllegalArgumentException e) {
            System.out.println("There's no target on the selected point. Skipping.");
            return;
        }

        int hp = 2;
        try {
            Point t = target.getPlayer().getBack(target.position, param).get(0);
            if(board.getObjectOn(t.x, t.y) == assassin) {
                hp = 5;
            }
        } catch(IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("There's nothing behind the target. Continuing.");
        }

        executor.tryToExecute(
                RequestRegionalAttack(hp).from(assassin).to(target)
        );
    }

    @Override
    public Card copy() {
        Card c = new Stab();
        c.setPlayer(player);
        return c;
    }

    @Override
    public String getName() {
        return "刺殺";
    }

    @Override
    public Party getParty() {
        return Party.TRANSPARENT;
    }
}
