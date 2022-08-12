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

    public static OrderStatus convertToOrderStatus(String status) {
        if (status == null) {
            return null;
        } else if (status.equalsIgnoreCase("NEW_ORDER")) {
            return OrderStatus.NEW_ORDER;
        } else if (status.equalsIgnoreCase("PROCESS")) {
            return OrderStatus.PROCESS;
        } else if (status.equalsIgnoreCase("READY_TO_PICKUP")) {
            return OrderStatus.READY_TO_PICKUP;
        } else if (status.equalsIgnoreCase("DELIVERY")) {
            return OrderStatus.DELIVERY;
        } else if (status.equalsIgnoreCase("CLOSED")) {
            return OrderStatus.CLOSED;
        } else if (status.equalsIgnoreCase("PENDING")) {
            return OrderStatus.PENDING;
        } else if (status.equalsIgnoreCase("CANCELED")) {
            return OrderStatus.CANCELED;
        } else {
            return null;
        }
    }

}
