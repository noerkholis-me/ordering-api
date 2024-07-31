package validator;

import com.hokeba.util.CommonFunction;

import dtos.merchant.MerchantRequest;

import java.util.Optional;

import models.Merchant;
import models.UserMerchant;
import repository.MerchantRepository;
import repository.UserMerchantRepository;

public class MerchantValidator {

    public static void validateCreate(MerchantRequest request) {
        try {
            if (request == null)
                throw new RuntimeException("Request is null or empty.");

            if (request.email == null)
                throw new RuntimeException("Email is null or empty.");

            if (!request.email.matches(CommonFunction.emailRegex))
                throw new RuntimeException("Invalid e-mail format.");

            if (request.provinceId == null || request.provinceId == 0)
                throw new RuntimeException("Province must selected.");

            if (request.cityId == null || request.cityId == 0)
                throw new RuntimeException("District must selected.");

            if (request.suburbId == null || request.suburbId == 0)
                throw new RuntimeException("Sub District must selected.");

            if (request.areaId == null ||request.areaId == 0)
                throw new RuntimeException("Sub Urban must selected.");

            Optional<Merchant> emailCheck = MerchantRepository.findByEmail(request.email);
            UserMerchant userMerchant = UserMerchantRepository.findByEmail(request.email);

            if (emailCheck.isPresent() || userMerchant != null)
                throw new RuntimeException("Email sudah terdaftar.");

            Optional<Merchant> nameCheck = MerchantRepository.findByName(request.name);
            
            if (nameCheck.isPresent())
                throw new RuntimeException("Merchant dengan nama yang sama sudah terdaftar.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (Error e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
    }
}