package me.brokenearthdev.manhuntplugin.tracker;

public enum  TrackerType {

    SIMPLISTIC("simplistic tracker"),
    SIMPLE_W_COORDINATES("simplistic tracker (with special features)"),
    COMPLEX("advanced tracker");
    
    private final String name;
    
    TrackerType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
