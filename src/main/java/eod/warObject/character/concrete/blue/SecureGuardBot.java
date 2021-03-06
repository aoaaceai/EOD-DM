package eod.warObject.character.concrete.blue;

import eod.Party;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.blue.SecureGuardBotSummon;
import eod.effect.EffectExecutor;
import eod.param.PointParam;
import eod.warObject.CanAttack;
import eod.warObject.character.abstraction.Machine;

import java.awt.*;
import java.util.ArrayList;

import static eod.effect.EffectFunctions.RequestRegionalAttack;

public class SecureGuardBot extends Machine {
    public SecureGuardBot(Player player) {
        super(player, 4, 4, Party.BLUE);
    }

    @Override
    public String getName() {
        return "維安警備機械";
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard c = new SecureGuardBotSummon();
        c.setPlayer(player);
        return c;
    }

    @Override
    public CanAttack getAttacker() {
        return attacker;
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
        return player.getBoard().getSurrounding(this.position, param);
    }
}
