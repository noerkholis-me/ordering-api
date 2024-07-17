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
        if (request == null)
            throw new RuntimeException("Request is null or empty.");

        if (request.getEmail() == null)
            throw new RuntimeException("Email is null or empty.");

        if (!request.getEmail().matches(CommonFunction.emailRegex))
            throw new RuntimeException("Invalid e-mail format.");

        if (request.getProvinceId() == 0 || request.getProvinceId() == null)
            throw new RuntimeException("Province must selected.");

        if (request.getCityId() == 0 || request.getCityId() == null)
            throw new RuntimeException("District must selected.");

        if (request.getSuburbId() == 0 || request.getSuburbId() == null)
            throw new RuntimeException("Sub District must selected.");

        if (request.getAreaId() == 0 || request.getAreaId() == null)
            throw new RuntimeException("Sub Urban must selected.");

        Optional<Merchant> emailCheck = MerchantRepository.findByEmail(request.getEmail());
        UserMerchant userMerchant = UserMerchantRepository.findByEmail(request.getEmail());

        if (emailCheck.isPresent() || userMerchant != null)
            throw new RuntimeException("Email sudah terdaftar.");

        Optional<Merchant> nameCheck = MerchantRepository.findByName(request.getName());
        
        if (nameCheck.isPresent())
            throw new RuntimeException("Merchant dengan nama yang sama sudah terdaftar.");
        
    }
}