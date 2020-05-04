package eod.specifier;

import eod.Character;
import eod.specifier.condition.Condition;
import eod.specifier.timing.Timing;

public class Accessing {

    private Character[] objects;

    public Accessing(Character[] objects) {
        this.objects = objects;
    }

    public Accessing which(Condition condition) {
        objects = condition.filter(objects);
        return this;
    }

    public Accessing in(Timing timing) {
        objects = timing.filter(objects);
        return this;
    }

    public Character[] get() {
        return objects;
    }
}
