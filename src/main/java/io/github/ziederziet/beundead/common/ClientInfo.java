package io.github.ziederziet.beundead.common;

import java.util.HashMap;
import java.util.Map;

public class ClientInfo {
    public static int zombieInvState;
    public static boolean canChestExtension;
    public static boolean zombieNightVision;
    public static boolean zombieJumpOnTheirOwn;
    public static int zombieMaxViewDistance = 4;
    public static double zombieWalkingSpeed;
    public static float zombieBreakingSpeed;
    public static boolean zombieSprintEnabled;
    public static Map<String, UndeadType> undeadTypes = new HashMap<>();
}
