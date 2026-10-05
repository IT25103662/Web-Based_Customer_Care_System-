package com.lankaconnect.ccms.strategy;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Strategy Pattern Context Class.
 * Resolves and executes the appropriate LoyaltyTierStrategy based on customer subscription duration.
 */
public class LoyaltyStrategyContext {

    private LoyaltyTierStrategy strategy;

    public static LoyaltyTierStrategy determineStrategy(LocalDate subscriptionStartDate) {
        if (subscriptionStartDate == null) {
            return new BronzeLoyaltyStrategy();
        }
        long months = ChronoUnit.MONTHS.between(subscriptionStartDate, LocalDate.now());
        if (months >= 12) {
            return new GoldLoyaltyStrategy();
        } else if (months >= 6) {
            return new SilverLoyaltyStrategy();
        } else {
            return new BronzeLoyaltyStrategy();
        }
    }

    public LoyaltyStrategyContext(LoyaltyTierStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(LoyaltyTierStrategy strategy) {
        this.strategy = strategy;
    }

    public String getTierName() {
        return strategy != null ? strategy.getTierName() : "BRONZE";
    }

    public double getDiscountPercentage() {
        return strategy != null ? strategy.getDiscountPercentage() : 0.0;
    }

    public int getPriorityScore() {
        return strategy != null ? strategy.getPriorityScore() : 1;
    }
}
