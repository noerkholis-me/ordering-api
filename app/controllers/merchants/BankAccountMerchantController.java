package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.bankaccount.BankAccountMerchantModel;
import dtos.bankaccount.ChangePrimaryAccount;
import models.Merchant;
import models.merchant.BankAccountMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.BankAccountMerchantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankAccountMerchantController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(BankAccountMerchantController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final BaseResponse response = new BaseResponse();

    public static Result addBankAccount() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                BankAccountMerchantModel request = objectMapper.readValue(json.toString(), BankAccountMerchantModel.class);
                String message = validateRequest(request);
                if (message != null) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " " + message, null);
                    return notFound(Json.toJson(response));
                }
                List<BankAccountMerchant> bankAccountMerchants = BankAccountMerchantRepository.findAll(merchant);
                if (!bankAccountMerchants.isEmpty()) {
                    for (int i = 0; i < bankAccountMerchants.size(); i++) {
                        if (bankAccountMerchants.get(i).getIsPrimary() == Boolean.TRUE && request.getIsPrimary() == Boolean.TRUE) {
                            response.setBaseResponse(0, 0, 0, inputParameter + " Primary account has been set", null);
                            return badRequest(Json.toJson(response));
                        }
                    }
                }

                Optional<BankAccountMerchant> bankAccountNumberMerchant = BankAccountMerchantRepository.findByAccountNumber(request.getAccountNumber());
                if (bankAccountNumberMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "account number "+ request.getAccountNumber() +" already exist", null);
                    return badRequest(Json.toJson(response));
                }

                Transaction trx = Ebean.beginTransaction();
                try {
                    BankAccountMerchant bankAccountMerchant = new BankAccountMerchant();
                    bankAccountMerchant.setMerchant(merchant);
                    bankAccountMerchant.setBankName(request.getBankName());
                    bankAccountMerchant.setAccountNumber(request.getAccountNumber());
                    bankAccountMerchant.setAccountName(request.getAccountName());
                    bankAccountMerchant.setIsPrimary(request.getIsPrimary());
                    bankAccountMerchant.save();

                    trx.commit();

                    response.setBaseResponse(1, offset, 1, success + " Create Bank Account", bankAccountMerchant.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while creating bank account", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }

            } catch (Exception ex) {
                logger.error("Error while creating add bank account", ex);
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result editBankAccount(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                BankAccountMerchantModel request = objectMapper.readValue(json.toString(), BankAccountMerchantModel.class);
                String message = validateRequest(request);
                if (message != null) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " " + message, null);
                    return notFound(Json.toJson(response));
                }
                List<BankAccountMerchant> bankAccountMerchants = BankAccountMerchantRepository.findAll(merchant);
                if (!bankAccountMerchants.isEmpty()) {
                    for (int i = 0; i < bankAccountMerchants.size(); i++) {
                        if (bankAccountMerchants.get(i).getIsPrimary() == Boolean.TRUE && request.getIsPrimary() == Boolean.TRUE) {
                            response.setBaseResponse(0, 0, 0, inputParameter + " Primary account has been set", null);
                            return badRequest(Json.toJson(response));
                        }
                    }
                }
                Optional<BankAccountMerchant> bankAccountMerchant = BankAccountMerchantRepository.findById(id);
                if (!bankAccountMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, inputParameter + id + " not found", null);
                    return badRequest(Json.toJson(response));
                }

                Optional<BankAccountMerchant> bankAccountNumberMerchant = BankAccountMerchantRepository.findByAccountNumber(request.getAccountNumber());
                if (bankAccountNumberMerchant.isPresent()) {
                    if (!bankAccountNumberMerchant.get().id.equals(id)) {
                        response.setBaseResponse(0, 0, 0, "account number "+ request.getAccountNumber() +" already exist", null);
                        return badRequest(Json.toJson(response));
                    }
                }

                Transaction trx = Ebean.beginTransaction();
                try {
                    BankAccountMerchant getBankAccount = bankAccountMerchant.get();
                    getBankAccount.setMerchant(merchant);
                    getBankAccount.setBankName(request.getBankName());
                    getBankAccount.setAccountNumber(request.getAccountNumber());
                    getBankAccount.setAccountName(request.getAccountName());
                    getBankAccount.setIsPrimary(request.getIsPrimary());
                    getBankAccount.update();

                    trx.commit();

                    response.setBaseResponse(1, offset, 1, success + " Update Bank Account", getBankAccount.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while update bank account", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }

            } catch (Exception ex) {
                logger.error("Error while update bank account", ex);
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteBankAccount(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Optional<BankAccountMerchant> bankAccountMerchant = BankAccountMerchantRepository.findById(id);
                if (!bankAccountMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, inputParameter + id + " not found", null);
                    return badRequest(Json.toJson(response));
                }

                if (bankAccountMerchant.get().getIsPrimary() == Boolean.TRUE) {
                    response.setBaseResponse(0, 0, 0, inputParameter + id + " can't delete, because this bank account is primary", null);
                    return badRequest(Json.toJson(response));
                }

                Transaction trx = Ebean.beginTransaction();
                try {
                    BankAccountMerchant getBankAccount = bankAccountMerchant.get();
                    getBankAccount.isDeleted = Boolean.TRUE;
                    getBankAccount.update();

                    trx.commit();

                    response.setBaseResponse(1, offset, 1, "Success Delete Bank Account", getBankAccount.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while delete bank account", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }

            } catch (Exception ex) {
                logger.error("Error while delete bank account", ex);
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getBankAccountById(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Optional<BankAccountMerchant> bankAccountMerchant = BankAccountMerchantRepository.findById(id);
                if (!bankAccountMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, inputParameter + id + " not found", null);
                    return badRequest(Json.toJson(response));
                }

                BankAccountMerchant getBankAccount = bankAccountMerchant.get();
                BankAccountMerchantModel model = new BankAccountMerchantModel();
                model.setBankName(getBankAccount.getBankName());
                model.setAccountNumber(getBankAccount.getAccountNumber());
                model.setAccountName(getBankAccount.getAccountName());
                model.setIsPrimary(getBankAccount.getIsPrimary());

                response.setBaseResponse(1, offset, 1, success + " Get data bank account", model);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                logger.error("Error while get bank account by id", ex);
                ex.printStackTrace();
            }

        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getAllBankAccount(String filter, String sort, int offset, int limit) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Query<BankAccountMerchant> bankAccountQuery = BankAccountMerchantRepository.findAllTablesQuery(merchant);
                List<BankAccountMerchant> getTotalPage = BankAccountMerchantRepository.getTotalPage(bankAccountQuery);
                List<BankAccountMerchant> bankAccounts = BankAccountMerchantRepository.findAllWithPaging(bankAccountQuery, sort, filter, offset, limit);
                List<BankAccountMerchantModel> bankAccountMerchantModels = new ArrayList<>();
                for (BankAccountMerchant bankAccountMerchant : bankAccounts) {
                    BankAccountMerchantModel model = new BankAccountMerchantModel();
                    model.setId(bankAccountMerchant.id);
                    model.setBankName(bankAccountMerchant.getBankName());
                    model.setAccountNumber(bankAccountMerchant.getAccountNumber());
                    model.setAccountName(bankAccountMerchant.getAccountName());
                    model.setIsPrimary(bankAccountMerchant.getIsPrimary());
                    bankAccountMerchantModels.add(model);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? getTotalPage.size() : bankAccountMerchantModels.size(), offset, limit, success + " Showing data bank account", bankAccountMerchantModels);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                logger.error("Error while getting list bank account", ex);
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result changePrimaryBankAccount() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                ChangePrimaryAccount changePrimaryAccount = objectMapper.readValue(json.toString(), ChangePrimaryAccount.class);
                Transaction trx = Ebean.beginTransaction();
                try {
                    List<BankAccountMerchant> bankAccountMerchants = BankAccountMerchantRepository.findAll(merchant);
                    if (bankAccountMerchants.isEmpty()) {
                        response.setBaseResponse(0, 0, 0, notFound, null);
                        return notFound(Json.toJson(response));
                    }
                    bankAccountMerchants.stream().forEach(o -> {
                        if (changePrimaryAccount.getId() == o.id && o.getIsPrimary() != Boolean.TRUE) {
                            o.setIsPrimary(changePrimaryAccount.getIsPrimary());
                            o.update();
                        } else {
                            o.setIsPrimary(Boolean.FALSE);
                            o.update();
                        }
                    });

                    trx.commit();

                    response.setBaseResponse(1, offset, 1, success + " Change Is Priamry Bank Account", changePrimaryAccount.getId());
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while creating bank account", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                logger.error("Error while change primary account bank account", ex);
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static String validateRequest(BankAccountMerchantModel request) {
        if (request.getAccountNumber() == null || request.getAccountNumber().equalsIgnoreCase(""))
            return "Account Number is not null or empty";
        if (request.getAccountName() == null || request.getAccountName().equalsIgnoreCase(""))
            return "Account Name is not null or empty";
        if (request.getIsPrimary() == null)
            return "Is Primary is not null";
        if (request.getAccountName().length() > 50)
            return "Nama tidak boleh lebih dari 50 karakter";
        return null;
    }

}
