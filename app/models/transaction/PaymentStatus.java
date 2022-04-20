package models.transaction;

public enum PaymentStatus {

    PENDING("PENDING"),
    UNPAID("UNPAID"),
    PAID("PAID"),
    CANCELLED("CANCELLED");

    private String status;

    private PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
