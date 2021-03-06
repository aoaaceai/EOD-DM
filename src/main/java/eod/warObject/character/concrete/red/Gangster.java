package eod.warObject.character.concrete.red;

import eod.BoardPosition;
import eod.Gameboard;
import eod.Party;
import eod.Player;
import eod.card.abstraction.summon.SummonCard;
import eod.card.concrete.summon.red.GangsterSummon;
import eod.effect.EffectExecutor;
import eod.param.PointParam;
import eod.warObject.character.abstraction.Character;

import java.awt.*;

import static eod.effect.EffectFunctions.RequestRegionalAttack;

public class Gangster extends Character {
    public Gangster(Player player) {
        super(player, 1,1, Party.RED);
    }

    @Override
    public SummonCard getSummonCard() {
        SummonCard card = new GangsterSummon();
        card.setPlayer(player);
        return card;
    }

    @Override
    public String getName() {
        return "好戰分子";
    }

    @Override
    public void attack(EffectExecutor executor) {
        super.attack(executor);
        PointParam param = new PointParam();
        param.range = 1;
        executor.tryToExecute(
            RequestRegionalAttack(attack).from(this).to(player.getFront(position, param))
        );

        afterAttack();
    }

    @Override
    public void moveTo(Point point) {
        super.moveTo(point);
        if(player.getPosition(position) == BoardPosition.ENEMY_BASE && (position.x == 0 || position.x == Gameboard.boardSize - 1)) {
            transferTo(new ExperiencedWarrior(player));
        }
    }
}
