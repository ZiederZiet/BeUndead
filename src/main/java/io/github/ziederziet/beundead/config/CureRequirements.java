package io.github.ziederziet.beundead.config;

public enum CureRequirements {
    NO_CURING(0, "No Curing"),
    GOLDEN_APPLE(1, "Golden Apple"),
    WEAKNESS_AND_APPLE(2, "Weakness & Golden Apple"),
    WEAKNESS_AND_TOTEM(3, "Weakness & Totem of Undying"),
    WEAKNESS_AND_ENCHANTED_APPLE(4, "Weakness & Enchanted Golden Apple");

    int id;
    String name;
    CureRequirements(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
