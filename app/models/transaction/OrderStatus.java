package models.transaction;

public enum OrderStatus {
    NEW_ORDER,
    PROCESS,
    READY_TO_PICKUP,
    DELIVERY,
    CLOSED,
    PENDING,
    CANCELED;

    private String status;

    private OrderStatus() {
        this.status = this.name();
    }

    public String getStatus() {
        return this.status;
    }

}
