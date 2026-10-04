package com.lankaconnect.ccms.strategy;

/**
 * Concrete Strategy for Silver Loyalty Tier (Customers subscribed for 6 to 12 months).
 */
public class SilverLoyaltyStrategy implements LoyaltyTierStrategy {
    @Override
    public String getTierName() {
        return "SILVER";
    }

    @Override
    public double getDiscountPercentage() {
        return 10.0;
    }

    @Override
    public int getPriorityScore() {
        return 5;
    }
}
