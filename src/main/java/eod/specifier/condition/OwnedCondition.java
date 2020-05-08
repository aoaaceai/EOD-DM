package eod.specifier.condition;

import eod.warObject.WarObject;
import eod.warObject.character.Character;
import eod.Player;

import java.util.Arrays;

public class OwnedCondition implements Condition {
    private Player player;

    public OwnedCondition(Player player) {
        this.player = player;
    }

    @Override
    public WarObject[] filter(WarObject[] objects) {
        return Arrays.stream(objects)
                .filter(object -> object.getPlayer() == player)
                .toArray(WarObject[]::new);
    }
}
