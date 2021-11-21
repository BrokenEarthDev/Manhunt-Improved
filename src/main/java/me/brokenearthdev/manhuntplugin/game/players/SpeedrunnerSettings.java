package me.brokenearthdev.manhuntplugin.game.players;

import me.brokenearthdev.manhuntplugin.main.Manhunt;

import java.util.HashMap;
import java.util.Map;

public class SpeedrunnerSettings {
    
    public final int extraHearts;
    public final int extraDamage;
    public final int speedboost;
    public final int speedboostDuration;
    public final int alertProximityRadius;
    public final int autoSmeltProbability;
    
    private final Map<String, Object> settings = new HashMap<>();
    
    public Map<String, Object> settingsCopy() {
        return new HashMap<>(settings);
    }
    
    private SpeedrunnerSettings(int extraHearts, int extraDamage, int speedboost, int speedboostDuration,
                        int alertProximityRadius, int autoSmeltProbability) {
        this.extraHearts = extraHearts;
        this.extraDamage = extraDamage;
        this.speedboost = speedboost;
        this.speedboostDuration = speedboostDuration;
        this.alertProximityRadius = alertProximityRadius;
        this.autoSmeltProbability = autoSmeltProbability;
        createMap();
    }
    
    public SpeedrunnerSettings() {
        DefaultSettings def = new DefaultSettings();
        this.extraHearts = def.extraHearts;
        this.extraDamage = def.extraDamage;
        this.speedboost = def.speedboost;
        this.speedboostDuration = def.speedboostDuration;
        this.alertProximityRadius = def.alertProximityRadius;
        this.autoSmeltProbability = def.autoSmeltProbability;
        createMap();
    }
    
    private void createMap() {
        settings.put("extraHearts", extraHearts);
        settings.put("extraDamage", extraDamage);
        settings.put("speedboost", speedboost);
        settings.put("speedboostDuration", speedboostDuration);
        settings.put("alertProximityRadius", alertProximityRadius);
        settings.put("autoSmeltProbability", autoSmeltProbability + "%");
    }
    
    public static class DefaultSettings {
    
        private final int extraHearts = Manhunt.getInstance().getConfigManager().getLastCaptured().EXTRA_HEARTS.get();
        private final int extraDamage = Manhunt.getInstance().getConfigManager().getLastCaptured().EXTRA_DAMAGE.get();
        private final int speedboost = Manhunt.getInstance().getConfigManager().getLastCaptured().SPEED_BOOST.get();
        private final int speedboostDuration = Manhunt.getInstance().getConfigManager().getLastCaptured().SPEED_BOOST_DURATION.get();
        private final int alertProximityRadius = Manhunt.getInstance().getConfigManager().getLastCaptured().ALERT_PROXIMITY_RADIUS.get();
        private final int autoSmeltProbability = Manhunt.getInstance().getConfigManager().getLastCaptured().AUTO_SMELT_PROBABILITY.get();
        
    }
    
    public static class Builder {
        
        private int extraHearts = 0;
        private int extraDamage = 0;
        private int speedboost = 0;
        private int speedboostDuration = 0;
        private int alertProximityRadius = -1;
        private int autoSmeltProbability = 0;
        
        public Builder() {
            DefaultSettings def = new DefaultSettings();
            this.extraHearts = def.extraHearts;
            this.extraDamage = def.extraDamage;
            this.speedboost = def.speedboost;
            this.speedboostDuration = def.speedboostDuration;
            this.alertProximityRadius = def.alertProximityRadius;
            this.autoSmeltProbability = def.autoSmeltProbability;
        }
        
        public Builder(SpeedrunnerSettings settings) {
            this.extraHearts = settings.extraHearts;
            this.extraDamage = settings.extraDamage;
            this.speedboost = settings.speedboost;
            this.speedboostDuration = settings.speedboostDuration;
            this.alertProximityRadius = settings.alertProximityRadius;
            this.autoSmeltProbability = settings.autoSmeltProbability;
        }
        
        public Builder setExtraHearts(int extraHearts) {
            this.extraHearts = Math.max(extraHearts, 0);
            return this;
        }
        
        public Builder setExtraDamage(int extraDamage) {
            this.extraDamage = Math.max(extraDamage, 0);
            return this;
        }
        
        public Builder setSpeedBoost(int speedboost) {
            this.speedboost = Math.max(speedboost, 0);
            return this;
        }
        
        public Builder setSpeedBoostDuration(int duration) {
            this.speedboostDuration = Math.max(duration, 0);
            return this;
        }
        
        public Builder setAlertProximityRadius(int rad) {
            this.alertProximityRadius = Math.max(rad, -1);
            return this;
        }
        
        public Builder setAutoSmeltProbability(int percent) {
            this.autoSmeltProbability = Math.max(percent, 0);
            return this;
        }
        
        public SpeedrunnerSettings build() {
            return new SpeedrunnerSettings(extraHearts, extraDamage, speedboost, speedboostDuration, alertProximityRadius,
                    autoSmeltProbability);
        }
        
    }
    
    
}
