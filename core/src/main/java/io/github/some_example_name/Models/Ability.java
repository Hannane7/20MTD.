package io.github.some_example_name.Models;

public class Ability {
    private final AbilityType type;
    private final String name;
    private final String description;

    public Ability(AbilityType type, String name, String description) {
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public AbilityType getType() { return type; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    // Predefined abilities
    public static final Ability VITALITY = new Ability(AbilityType.VITALITY, "VITALITY", "افزایش ماکسیمم HP به اندازه یک واحد");
    public static final Ability DAMAGER = new Ability(AbilityType.DAMAGER, "DAMAGER", "افزایش ۲۵ درصدی دمیج سلاح به مدت ۱۰ ثانیه");
    public static final Ability PROCREASE = new Ability(AbilityType.PROCREASE, "PROCREASE", "افزایش یک واحدی شلیک Projectile سلاح");
    public static final Ability AMOCREASE = new Ability(AbilityType.AMOCREASE, "AMOCREASE", "افزایش ۱ واحدی حداکثر تعداد گلوله‌های سلاح");
    public static final Ability SPEEDY = new Ability(AbilityType.SPEEDY, "SPEEDY", "۲ برابر شدن سرعت حرکت بازیکن به مدت ۱۰ ثانیه");

    public static final Ability[] ALL_ABILITIES = {VITALITY, DAMAGER, PROCREASE, AMOCREASE, SPEEDY};

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ability)) return false;
        Ability ab = (Ability) o;
        return type == ab.type;
    }

    @Override
    public int hashCode() { return type.hashCode(); }
}
