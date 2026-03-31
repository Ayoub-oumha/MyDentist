package com.example.mydentist2.model;

public enum DentalServiceType {

    CONSULTATION("Consultation", 30),
    CLEANING("Teeth Cleaning", 45),
    FILLING("Dental Filling", 60),
    EXTRACTION("Tooth Extraction", 45),
    ROOT_CANAL("Root Canal Treatment", 90),
    CROWN("Crown Placement", 75),
    WHITENING("Teeth Whitening", 60),
    ORTHODONTIC_CHECKUP("Orthodontic Check-up", 30),
    IMPLANT_CONSULTATION("Implant Consultation", 45),
    EMERGENCY("Emergency Care", 30);

    private final String label;
    private final int durationMinutes;

    DentalServiceType(String label, int durationMinutes) {
        this.label = label;
        this.durationMinutes = durationMinutes;
    }

    public String getLabel() { return label; }
    public int getDurationMinutes() { return durationMinutes; }
}
