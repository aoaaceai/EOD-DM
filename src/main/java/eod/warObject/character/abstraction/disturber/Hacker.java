package eod.warObject.character.abstraction.disturber;

import eod.Party;
import eod.Player;
import eod.warObject.character.abstraction.Character;

public abstract class Hacker extends Character {
    public Hacker(Player player, int x, int y, int hp, int range, Party party) {
        super(player, x, y, hp, range, party);
    }
}
