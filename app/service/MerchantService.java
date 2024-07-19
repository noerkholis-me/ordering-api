package service;

import java.math.BigDecimal;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.Constant;
import com.hokeba.util.Encryption;
import com.hokeba.util.MailConfig;

import dtos.merchant.MerchantRequest;

import models.Merchant;
import models.Role;
import models.ShipperArea;
import models.ShipperCity;
import models.ShipperProvince;
import models.ShipperSuburb;
import models.internal.PaymentMethod;
import models.merchant.MerchantPayment;
import repository.RoleRepository;

public class MerchantService {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Registers a new merchant with the provided data and creates corresponding merchant payments based on the selected payment methods.
     *
     * @param  data  the merchant request data containing merchant information and selected payment methods
     * @return       the registered merchant object
     */
    public static Merchant registerMerchant(MerchantRequest data) {
        Transaction txn = Ebean.beginTransaction();

        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String jsonString = objectMapper.writeValueAsString(data);
            Merchant merchant = objectMapper.readValue(jsonString, Merchant.class);
            
            Role role = RoleRepository.getByKey("admin_merchant");

            if (role == null) {
                role = RoleRepository.getById(2L);
            }

            merchant.province = ShipperProvince.find.byId(data.provinceId);
            merchant.city = ShipperCity.find.byId(data.cityId);
            merchant.suburb = ShipperSuburb.find.byId(data.suburbId);
            merchant.area = ShipperArea.find.byId(data.areaId);
            merchant.fullName = data.name;
            merchant.totalActiveBalance = BigDecimal.ZERO;
            merchant.merchantCode = merchant.generateMerchantCode();
            merchant.merchantQrCode = Constant.getInstance().getFrontEndUrl().concat("merchant/"+merchant.merchantCode);
            merchant.role = role;
            merchant.isActive = false;

            merchant.save();

            PaymentMethod pMethod = null;

            if (merchant.isKiosk) {
                if (data.isCashKiosk) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "cash").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentCashKiosk = new MerchantPayment();
                        merchPaymentCashKiosk.merchant = merchant;
                        merchPaymentCashKiosk.paymentMethod = pMethod;
                        merchPaymentCashKiosk.device = "KIOSK";
                        merchPaymentCashKiosk.isActive = Boolean.TRUE;
                        merchPaymentCashKiosk.isDeleted = Boolean.FALSE;
                        merchPaymentCashKiosk.typePayment = data.typeCashKiosk;
                        merchPaymentCashKiosk.save();
                    }
                }

                if (data.isDebitKiosk) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "debit").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentDebitCreditKiosk = new MerchantPayment();
                        merchPaymentDebitCreditKiosk.merchant = merchant;
                        merchPaymentDebitCreditKiosk.paymentMethod = pMethod;
                        merchPaymentDebitCreditKiosk.device = "KIOSK";
                        merchPaymentDebitCreditKiosk.isActive = Boolean.TRUE;
                        merchPaymentDebitCreditKiosk.isDeleted = Boolean.FALSE;
                        merchPaymentDebitCreditKiosk.typePayment = data.typeDebitKiosk;
                        merchPaymentDebitCreditKiosk.save();
                    }
                }

                if (data.isCreditKiosk) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "credit").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentDebitCreditKiosk = new MerchantPayment();
                        merchPaymentDebitCreditKiosk.merchant = merchant;
                        merchPaymentDebitCreditKiosk.paymentMethod = pMethod;
                        merchPaymentDebitCreditKiosk.device = "KIOSK";
                        merchPaymentDebitCreditKiosk.isActive = Boolean.TRUE;
                        merchPaymentDebitCreditKiosk.isDeleted = Boolean.FALSE;
                        merchPaymentDebitCreditKiosk.typePayment = data.typeCreditKiosk;
                        merchPaymentDebitCreditKiosk.save();
                    }
                }

                if (data.isQrisKiosk) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "qr_code").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentQrisKiosk = new MerchantPayment();
                        merchPaymentQrisKiosk.merchant = merchant;
                        merchPaymentQrisKiosk.paymentMethod = pMethod;
                        merchPaymentQrisKiosk.device = "KIOSK";
                        merchPaymentQrisKiosk.isActive = Boolean.TRUE;
                        merchPaymentQrisKiosk.isDeleted = Boolean.FALSE;
                        merchPaymentQrisKiosk.typePayment = data.typeQrisKiosk;
                        merchPaymentQrisKiosk.save();
                    }
                }

                if (data.isVaKiosk) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "virtual_account").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentVaKiosk = new MerchantPayment();
                        merchPaymentVaKiosk.merchant = merchant;
                        merchPaymentVaKiosk.paymentMethod = pMethod;
                        merchPaymentVaKiosk.device = "KIOSK";
                        merchPaymentVaKiosk.isActive = Boolean.TRUE;
                        merchPaymentVaKiosk.isDeleted = Boolean.FALSE;
                        merchPaymentVaKiosk.typePayment = data.typeQrisKiosk;
                        merchPaymentVaKiosk.save();
                    }
                }
            }

            if (merchant.isMobileQr) {
                if (data.isCashMobileQr) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "cash").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();

                    if (pMethod != null) {
                        MerchantPayment merchPaymentCashMQ = new MerchantPayment();
                        merchPaymentCashMQ.merchant = merchant;
                        merchPaymentCashMQ.paymentMethod = pMethod;
                        merchPaymentCashMQ.device = "MOBILEQR";
                        merchPaymentCashMQ.isActive = Boolean.TRUE;
                        merchPaymentCashMQ.isDeleted = Boolean.FALSE;
                        merchPaymentCashMQ.typePayment = data.typeCashMobileQr;
                        merchPaymentCashMQ.save();
                    }
                }

                if (data.isDebitMobileQr) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "debit").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentDebitCreditMQ = new MerchantPayment();
                        merchPaymentDebitCreditMQ.merchant = merchant;
                        merchPaymentDebitCreditMQ.paymentMethod = pMethod;
                        merchPaymentDebitCreditMQ.device = "MOBILEQR";
                        merchPaymentDebitCreditMQ.isActive = Boolean.TRUE;
                        merchPaymentDebitCreditMQ.isDeleted = Boolean.FALSE;
                        merchPaymentDebitCreditMQ.typePayment = data.typeDebitMobileQr;
                        merchPaymentDebitCreditMQ.save();
                    }
                }

                if (data.isCreditMobileQr) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "credit").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentDebitCreditMQ = new MerchantPayment();
                        merchPaymentDebitCreditMQ.merchant = merchant;
                        merchPaymentDebitCreditMQ.paymentMethod = pMethod;
                        merchPaymentDebitCreditMQ.device = "MOBILEQR";
                        merchPaymentDebitCreditMQ.isActive = Boolean.TRUE;
                        merchPaymentDebitCreditMQ.isDeleted = Boolean.FALSE;
                        merchPaymentDebitCreditMQ.typePayment = data.typeCreditMobileQr;
                        merchPaymentDebitCreditMQ.save();
                    }
                }

                if (data.isQrisMobileQr) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "qr_code").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentQrisMQ = new MerchantPayment();
                        merchPaymentQrisMQ.merchant = merchant;
                        merchPaymentQrisMQ.paymentMethod = pMethod;
                        merchPaymentQrisMQ.device = "MOBILEQR";
                        merchPaymentQrisMQ.isActive = Boolean.TRUE;
                        merchPaymentQrisMQ.isDeleted = Boolean.FALSE;
                        merchPaymentQrisMQ.typePayment = data.typeQrisMobileQr;
                        merchPaymentQrisMQ.save();
                    }
                }

                if (data.isVaMobileQr) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "virtual_account").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentVaMQ = new MerchantPayment();
                        merchPaymentVaMQ.merchant = merchant;
                        merchPaymentVaMQ.paymentMethod = pMethod;
                        merchPaymentVaMQ.device = "MOBILEQR";
                        merchPaymentVaMQ.isActive = Boolean.TRUE;
                        merchPaymentVaMQ.isDeleted = Boolean.FALSE;
                        merchPaymentVaMQ.typePayment = data.typeQrisMobileQr;
                        merchPaymentVaMQ.save();
                    }
                }
            }

            if (merchant.isPos) {
                if (data.isCashPos) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "cash").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentCashPos = new MerchantPayment();
                        merchPaymentCashPos.merchant = merchant;
                        merchPaymentCashPos.paymentMethod = pMethod;
                        merchPaymentCashPos.device = "MINIPOS";
                        merchPaymentCashPos.isActive = Boolean.TRUE;
                        merchPaymentCashPos.isDeleted = Boolean.FALSE;
                        merchPaymentCashPos.typePayment = data.typeCashPos;
                        merchPaymentCashPos.save();
                    }
                }

                if (data.isDebitPos) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "debit").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentDebitCreditPos = new MerchantPayment();
                        merchPaymentDebitCreditPos.merchant = merchant;
                        merchPaymentDebitCreditPos.paymentMethod = pMethod;
                        merchPaymentDebitCreditPos.device = "MINIPOS";
                        merchPaymentDebitCreditPos.isActive = Boolean.TRUE;
                        merchPaymentDebitCreditPos.isDeleted = Boolean.FALSE;
                        merchPaymentDebitCreditPos.typePayment = data.typeDebitPos;
                        merchPaymentDebitCreditPos.save();
                    }
                }

                if (data.isCreditPos) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "credit").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentDebitCreditPos = new MerchantPayment();
                        merchPaymentDebitCreditPos.merchant = merchant;
                        merchPaymentDebitCreditPos.paymentMethod = pMethod;
                        merchPaymentDebitCreditPos.device = "MINIPOS";
                        merchPaymentDebitCreditPos.isActive = Boolean.TRUE;
                        merchPaymentDebitCreditPos.isDeleted = Boolean.FALSE;
                        merchPaymentDebitCreditPos.typePayment = data.typeCreditPos;
                        merchPaymentDebitCreditPos.save();
                    }
                }

                if (data.isQrisPos) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "qr_code").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentQrisPos = new MerchantPayment();
                        merchPaymentQrisPos.merchant = merchant;
                        merchPaymentQrisPos.paymentMethod = pMethod;
                        merchPaymentQrisPos.device = "MINIPOS";
                        merchPaymentQrisPos.isActive = Boolean.TRUE;
                        merchPaymentQrisPos.isDeleted = Boolean.FALSE;
                        merchPaymentQrisPos.typePayment = data.typeQrisPos;
                        merchPaymentQrisPos.save();
                    }
                }

                if (data.isVaPos) {
                    pMethod = PaymentMethod.find.where().eq("t0.payment_code", "virtual_account").eq("t0.is_available", true).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    
                    if (pMethod != null) {
                        MerchantPayment merchPaymentVaPos = new MerchantPayment();
                        merchPaymentVaPos.merchant = merchant;
                        merchPaymentVaPos.paymentMethod = pMethod;
                        merchPaymentVaPos.device = "MINIPOS";
                        merchPaymentVaPos.isActive = Boolean.TRUE;
                        merchPaymentVaPos.isDeleted = Boolean.FALSE;
                        merchPaymentVaPos.typePayment = data.typeQrisPos;
                        merchPaymentVaPos.save();
                    }
                }
            }

            merchant.activationCode = Encryption.EncryptAESCBCPCKS5Padding(String.valueOf(merchant.id) + String.valueOf(System.currentTimeMillis()));
            merchant.update();

            Thread thread = new Thread(() -> {
                try {
                    MailConfig.sendmail(
                        merchant.email,
                        MailConfig.subjectActivation,
                        MailConfig.renderMailSendCreatePasswordCMSTemplate(
                            merchant.activationCode,
                            merchant.name
                        )
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
            txn.commit();

            return merchant;
        } catch (Exception e) {
            e.printStackTrace();
            txn.rollback();
            throw new RuntimeException(e);
        }
    }
}
