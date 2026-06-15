package at.fhtechnikum.energyapi.dto;

public class TotalEnergyBetweenDates {

    private double totalCommunityProduced;
    private double totalCommunityUsed;
    private double totalGridUsed;

    public TotalEnergyBetweenDates(double totalCommunityProduced, double totalCommunityUsed, double totalGridUsed) {
        this.totalCommunityProduced = totalCommunityProduced;
        this.totalCommunityUsed = totalCommunityUsed;
        this.totalGridUsed = totalGridUsed;
    }

    public double getTotalCommunityProduced() {
        return totalCommunityProduced;
    }

    public double getTotalCommunityUsed() {
        return totalCommunityUsed;
    }

    public double getTotalGridUsed() {
        return totalGridUsed;
    }
}
