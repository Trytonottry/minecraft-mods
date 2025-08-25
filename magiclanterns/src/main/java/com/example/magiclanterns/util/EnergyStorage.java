package com.example.magiclanterns.util;

public class EnergyStorage {
    private long energy;
    private final long maxEnergy;
    private final Runnable onChange;

    public EnergyStorage(long maxEnergy, Runnable onChange) {
        this.maxEnergy = maxEnergy;
        this.onChange = onChange;
        this.energy = 0;
    }

    public long getEnergy() { return energy; }
    public long getMaxEnergy() { return maxEnergy; }

    public boolean canInsert(long amount) {
        return amount > 0 && energy + amount <= maxEnergy;
    }

    public boolean canExtract(long amount) {
        return amount > 0 && energy >= amount;
    }

    public void insert(long amount) {
        if (canInsert(amount)) {
            energy += amount;
            onChange.run();
        }
    }

    public void extract(long amount) {
        if (canExtract(amount)) {
            energy -= amount;
            onChange.run();
        }
    }

    public double getFillRatio() {
        return (double) energy / maxEnergy;
    }
}