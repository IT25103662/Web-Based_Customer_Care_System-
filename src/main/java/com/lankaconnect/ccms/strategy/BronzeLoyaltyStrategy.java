package com.lankaconnect.ccms.strategy;

/**
 * Concrete Strategy for Bronze Loyalty Tier (Customers subscribed for < 6 months).
 */
public class BronzeLoyaltyStrategy implements LoyaltyTierStrategy {
    @Override
    public String getTierName() {
        return "BRONZE";
    }

    @Override
    public double getDiscountPercentage() {
        return 0.0;
    }

    @Override
    public int getPriorityScore() {
        return 1;
    }
}
