package io.github.ziederziet.beundead.config;

public enum InventoryState {
    ONE_SLOT(0, "One Slot"),
    HOTBAR(1, "Hotbar"),
    FULL(2, "Full Inventory");
    int id;
    String name;
    InventoryState(int id, String name){
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
