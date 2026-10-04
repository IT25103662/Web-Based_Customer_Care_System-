package com.lankaconnect.ccms.strategy;

/**
 * Strategy Pattern Interface for Customer Loyalty Tier calculation.
 * Part of SLIIT SE2030 Software Engineering Design Patterns implementation.
 */
public interface LoyaltyTierStrategy {
    String getTierName();
    double getDiscountPercentage();
    int getPriorityScore();
}
