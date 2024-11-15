package controllers.kitchen.order;

import java.util.Optional;

// import javax.naming.spi.DirStateFactory.Result;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.Play;
import repository.*;

import com.fasterxml.jackson.databind.JsonNode;

import com.hokeba.api.BaseResponse;


import controllers.BaseController;
import controllers.shop.order.ShopOrderController;
import models.Merchant;
import models.Store;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderDetailStatus;
import models.transaction.OrderStatus;
import repository.OrderDetailRepository;
import repository.OrderDetailStatusRepository;
import repository.OrderRepository;

public class KitchenOrderController extends BaseController {
  
  @SuppressWarnings("rawtypes")
  private static BaseResponse response = new BaseResponse();
  private final static Logger.ALogger logger = Logger.of(KitchenOrderController.class);

  // @SuppressWarnings("deprecation")
//   public static Result changeStatus(String orderNumber, Long orderDetailId) throws Exception {
//     Merchant merchant = checkMerchantAccessAuthorization();
//     if(merchant != null) {
//       try {
//         JsonNode jsonNode = request().body().asJson();
  
//         String status_code = jsonNode.get("status_code").asText();
//         Boolean isActive = jsonNode.get("is_active").asBoolean();
  
//         // Updated condition to check for null and zero values correctly
//         if (orderDetailId == null || orderDetailId <= 0 || orderNumber.isEmpty()) {
//           throw new Exception("order number, and order detail id cannot be empty or zero");
//         }
  
//         Optional<Order> orderData = OrderRepository.findByOrderNumber(orderNumber);
  
//         if (orderData.isPresent()) {
//           logger.info("Order found with order number: " + orderNumber);
//           System.out.println("Order found with order number: " + orderNumber);
  
//           Order orders = OrderRepository.find.where().eq("t0.order_number", orderNumber).findUnique();
  
//           if (orders != null && orders.getStatus().equalsIgnoreCase("PROCESS")) {
//             logger.info("Order status is PROCESS");
//             OrderDetail orderDetail = OrderDetailRepository.findById(orderDetailId);
//             System.out.println("PROCESS");
//             System.out.println( "OrderDEtail" + orderDetail != null);
  
//             if (orderDetail != null) {
//               // Optional<OrderDetailStatus> orderDetailStatus = 
//               //     Optional.ofNullable(OrderDetailStatusRepository.findByCodeAndOrderDetailId(status_code, orderDetailId));
//               // System.out.println("orderDetailStatus : " + orderDetailStatus);
//               // System.out.println("Order detail found with order detail id: " + orderDetailId);
  
//               // if (orderDetailStatus != null) {
//               //   logger.info("Order detail status found with status code: " + status_code);
//               //   System.out.println("Order detail status found with status code: " + status_code);
//               //   // Logic to update OrderDetailStatus
//               //   orderDetailStatus.setIsActive(isActive);
//               //   orderDetailStatus.update();
//               // } else {
//               //   logger.info("Order detail status not found with status code: " + status_code);
//               //   System.out.println("Order detail status not found with status code: " + status_code);
//               //   // Logic to create and save new OrderDetailStatus
//               //   OrderDetailStatus newOrderDetailStatus = new OrderDetailStatus();
//               //   newOrderDetailStatus.setOrderDetail(orderDetail);
//               //   newOrderDetailStatus.setCode(status_code);
//               //   newOrderDetailStatus.setName(status_code.equals("COMPLETED") ? "Selesai Dibuat" : "Dikirim ke Customer");
//               //   newOrderDetailStatus.setDescription(status_code.equals("COMPLETED") ? "Order selesai dibuat" : "Order dikirim ke customer");
//               //   newOrderDetailStatus.setIsActive(isActive);
//               //   newOrderDetailStatus.save();
//               //   OrderDetailStatusRepository.save(newOrderDetailStatus);
//               // }

//               Optional<OrderDetailStatus> orderDetailStatusOpt = 
//                   Optional.ofNullable(OrderDetailStatusRepository.findByCodeAndOrderDetailId(status_code, orderDetailId));
// System.out.println("orderDetailStatus : " + orderDetailStatusOpt);
// System.out.println("Order detail found with order detail id: " + orderDetailId);

// if (orderDetailStatusOpt.isPresent()) {
//     OrderDetailStatus orderDetailStatus = orderDetailStatusOpt.get();
//     System.out.println("Retrieved OrderDetailStatus: " + orderDetailStatus); // Print the retrieved object
//     logger.info("Order detail status found with status code: " + status_code);
//     System.out.println("Order detail status found with status code: " + status_code);
//     // Logic to update OrderDetailStatus
//     orderDetailStatus.setIsActive(isActive);
//     orderDetailStatus.update();
// } else {
//     logger.info("Order detail status not found with status code: " + status_code);
//     System.out.println("Order detail status not found with status code: " + status_code);
//     // Logic to create and save new OrderDetailStatus
//     OrderDetailStatus newOrderDetailStatus = new OrderDetailStatus();
//     newOrderDetailStatus.setOrderDetail(orderDetail);
//     newOrderDetailStatus.setCode(status_code);
//     newOrderDetailStatus.setName(status_code.equals("COMPLETED") ? "Selesai Dibuat" : "Dikirim ke Customer");
//     newOrderDetailStatus.setDescription(status_code.equals("COMPLETED") ? "Order selesai dibuat" : "Order dikirim ke customer");
//     newOrderDetailStatus.setIsActive(isActive);
//     newOrderDetailStatus.save();
//     OrderDetailStatusRepository.save(newOrderDetailStatus);
// }
  
//               // return response
//               response.setBaseResponse(0, 0, 0, "success", null);
//               return ok(Json.toJson(response));
//             }
//           } else {
//             System.out.println("Order status is not PROCESS or order is null");
//             logger.error("Order status is not PROCESS or order is null with order number: " + orderNumber);
//             response.setBaseResponse(0, 0, 0, "Order status is not PROCESS or order is null", null);
//             return badRequest(Json.toJson(response));
//           }
//         } else {
//           System.out.println("Order not found");
//           logger.error("Order not found with order number: " + orderNumber);
//           response.setBaseResponse(0, 0, 0, "Order not found", null);
//           return badRequest(Json.toJson(response));
//         }
//       } catch (Exception e) {
//         logger.error("Error when getting list data orders", e);
//         response.setBaseResponse(0, 0, 0, e.getMessage(), null);
//         return badRequest(Json.toJson(response));
//       }
//     }
//     response.setBaseResponse(0, 0, 0, unauthorized, null);
//     return badRequest(Json.toJson(response));
//   }

public static Result changeStatus(String orderNumber, Long orderDetailId) throws Exception {
  Merchant merchant = checkMerchantAccessAuthorization();
  if (merchant != null) {
      try {
          JsonNode jsonNode = request().body().asJson();

          String status_code = jsonNode.get("status_code").asText();
          Boolean isActive = jsonNode.get("is_active").asBoolean();

          // Updated condition to check for null and zero values correctly
          if (orderDetailId == null || orderDetailId <= 0 || orderNumber.isEmpty()) {
              throw new Exception("Order number and order detail ID cannot be empty or zero");
          }

          Optional<Order> orderData = OrderRepository.findByOrderNumber(orderNumber);

          if (orderData.isPresent()) {
              logger.info("Order found with order number: " + orderNumber);
              System.out.println("Order found with order number: " + orderNumber);

              Order orders = OrderRepository.find.where().eq("t0.order_number", orderNumber).findUnique();

              if (orders != null && (orders.getStatus().equalsIgnoreCase("PROCESS") || orders.getStatus().equalsIgnoreCase("READY_TO_PICKUP"))) {
                  logger.info("Order status is PROCESS");
                  Optional<OrderDetail> orderDetail = OrderDetailRepository.findById(orderDetailId);
                  System.out.println("PROCESS");
                  System.out.println("OrderDetail exists: " + orderDetail.isPresent());

                  if (orderDetail.isPresent()) {
                      Optional<OrderDetailStatus> orderDetailStatusOpt =
                              Optional.ofNullable(OrderDetailStatusRepository.findByCodeAndOrderDetailId(status_code, orderDetailId));
                      System.out.println("Order detail found with order detail id: " + orderDetailId);

                      if (orderDetailStatusOpt.isPresent()) {
                          OrderDetailStatus orderDetailStatus = orderDetailStatusOpt.get();
                          logger.info("Order detail status found with status code: " + status_code);
                          System.out.println("Order detail status found with status code: " + status_code);
                          // Logic to update OrderDetailStatus
                          orderDetailStatus.setIsActive(isActive);
                          orderDetailStatus.update();
                      } else {
                          logger.info("Order detail status not found with status code: " + status_code);
                          System.out.println("Order detail status not found with status code: " + status_code);
                          // Logic to create and save new OrderDetailStatus
                          OrderDetailStatus newOrderDetailStatus = new OrderDetailStatus(
                              orderDetail.get(), // Pass the OrderDetail object
                              status_code,       // Code
                              status_code.equals("COMPLETED") ? "Selesai Dibuat" : "Dikirim ke Customer", // Name
                              status_code.equals("COMPLETED") ? "Order selesai dibuat" : "Order dikirim ke customer", // Description
                              isActive           // Is Active
                          );
                          OrderDetailStatusRepository.save(newOrderDetailStatus);
                      }

                      // Return response
                      response.setBaseResponse(0, 0, 0, "success", null);
                      return ok(Json.toJson(response));
                  }
              } else {
                  logger.error("OrderDetail not found with ID: " + orderDetailId);
                  System.out.println("OrderDetail not found with ID: " + orderDetailId);
                  response.setBaseResponse(0, 0, 0, "OrderDetail not found", null);
                  return badRequest(Json.toJson(response));
              }
          } else {
              System.out.println("Order not found");
              logger.error("Order not found with order number: " + orderNumber);
              response.setBaseResponse(0, 0, 0, "Order not found", null);
              return badRequest(Json.toJson(response));
          }
      } catch (Exception e) {
          logger.error("Error when getting list data orders", e);
          response.setBaseResponse(0, 0, 0, e.getMessage(), null);
          return badRequest(Json.toJson(response));
      }
  }
  response.setBaseResponse(0, 0, 0, "Unauthorized", null);
  return badRequest(Json.toJson(response));
}
}
