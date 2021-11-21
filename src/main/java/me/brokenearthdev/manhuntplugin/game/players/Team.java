package me.brokenearthdev.manhuntplugin.game.players;

public enum Team {
    SPEEDRUNNER("SPEEDRUNNER"),
    HUNTER("HUNTER");
    
    private String name;
    Team(String name) {
        this.name = name;
    }
    
    public static Team getTeam(String name) {
        if (name == null)
            return null;
        if (name.equals(HUNTER.toString()))
            return HUNTER;
        else if (name.equals(SPEEDRUNNER.toString()))
            return SPEEDRUNNER;
        else return null;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
