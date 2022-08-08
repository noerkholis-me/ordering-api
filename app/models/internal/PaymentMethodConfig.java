package models.internal;

public enum PaymentMethodConfig {

    QRCODE,
    VIRTUALACCOUNT,
    CASH,
    DEBITCREDIT;

    private String paymentMethod;

    private PaymentMethodConfig() {
        this.paymentMethod = this.name();
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public static PaymentMethodConfig convertToPaymentMethodConfig(String paymentMethod) {
        if (paymentMethod == null) {
            return null;
        } else if (paymentMethod.equalsIgnoreCase("qr_code")) {
            return PaymentMethodConfig.QRCODE;
        } else if (paymentMethod.equalsIgnoreCase("virtual_account")) {
            return PaymentMethodConfig.VIRTUALACCOUNT;
        } else if (paymentMethod.equalsIgnoreCase("cash")) {
            return PaymentMethodConfig.CASH;
        } else if (paymentMethod.equalsIgnoreCase("debit_credit")) {
            return PaymentMethodConfig.DEBITCREDIT;
        } else {
            return null;
        }
    }

}
