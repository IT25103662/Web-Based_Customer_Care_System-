package com.lankaconnect.ccms.strategy;

/**
 * Concrete Strategy for Gold Loyalty Tier (Customers subscribed for >= 12 months).
 */
public class GoldLoyaltyStrategy implements LoyaltyTierStrategy {
    @Override
    public String getTierName() {
        return "GOLD";
    }

    @Override
    public double getDiscountPercentage() {
        return 15.0;
    }

    @Override
    public int getPriorityScore() {
        return 10;
    }
}
