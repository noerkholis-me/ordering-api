package models.internal;

public enum DeviceType {

    MOBILEQR,
    KIOSK,
    MINIPOS;

    private String device;

    private DeviceType() {
        this.device = this.name();
    }

    public String getDevice() {
        return this.device;
    }

}
