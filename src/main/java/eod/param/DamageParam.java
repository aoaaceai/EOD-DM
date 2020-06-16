package eod.param;

public class DamageParam {
    public int damage;
    public boolean realDamage;

    public DamageParam(int damage) {
        this.damage = damage;
    }

    public void reduceDamage(int amount) {
        if(realDamage) return;
        damage -= amount;
        if(damage < 0) {
            damage = 0;
        }
    }
}
