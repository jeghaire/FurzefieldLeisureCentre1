package com.flc.furzefieldleisurecentre;

public enum ExerciseType {

    YOGA("Yoga", 10.0),
    ZUMBA("Zumba", 12.0),
    AQUACISE("Aquacise", 15.0),
    BOX_FIT("Box Fit", 14.0),
    CALISTHENICS("Calisthenics", 24.0),
    BODY_BLITZ("Body Blitz", 13.0);

    private final String displayName;
    private final double price;

    ExerciseType(String displayName, double price) {
        this.displayName = displayName;
        this.price = price;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPrice() {
        return price;
    }
}