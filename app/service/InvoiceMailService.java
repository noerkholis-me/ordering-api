package service;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.hokeba.util.Constant;
import com.hokeba.util.MailConfig;
import models.Store;
import models.transaction.Order;
import models.transaction.OrderPayment;
import play.Logger;
import repository.OrderRepository;

import java.util.Optional;

public class InvoiceMailService {

    private final static Logger.ALogger logger = Logger.of(InvoiceMailService.class);

    private static InvoiceMailService instance;

    public static InvoiceMailService getInstance() {
        if (instance == null) {
            instance = new InvoiceMailService();
        }
        return instance;
    }

    public static void handleCallbackAndSendEmail(String request) {

        logger.info(">>> incoming requet :  " + request);
        String orderNumber = request;
        Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);

        System.out.println("order >>> " + order.get().id);

        System.out.println("order member " + order.get().getMember().id);

        String email = order.get().getMember().email;
        System.out.println("email >>> " + email);
        Store store = order.get().getStore();

        String urlLogo = store.getMerchant().logo;
        String urlEmailLogo = Constant.getInstance().getImageUrl() + "/" + "assets/images/email.png";

        Thread thread = new Thread(() -> {
            try {
                MailConfig.sendmail(email, MailConfig.subjectInvoice, MailConfig.renderMailInvoiceTemplate(urlLogo, urlEmailLogo, order.get()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

        Transaction trx = Ebean.beginTransaction();
        try {
            OrderPayment orderPayment = OrderRepository.findDataOrderPayment(order.get().id);
            orderPayment.setMailStatusCode("200");
            orderPayment.setMailStatus("Success");
            orderPayment.setMailMessage("SENT");
            orderPayment.update();

            trx.commit();
        } catch (Exception e) {
            logger.error("Error saat mengirim invoice ke customer", e);
            e.printStackTrace();
            trx.rollback();
        } finally {
            trx.end();
        }
    }

}
