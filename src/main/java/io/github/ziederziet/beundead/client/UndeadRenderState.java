package io.github.ziederziet.beundead.client;

public interface UndeadRenderState {
    void setChestExtension(boolean chestExtension);
    boolean hasChestExtension();
    void setType(String type);
    String getType();
    boolean isHuman();
    boolean isShaking();
    void setShaking(boolean shaking);
}
