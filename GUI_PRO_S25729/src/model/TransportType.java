package model;

public enum TransportType {
    PLANE,
    TRAIN,
    BOAT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
