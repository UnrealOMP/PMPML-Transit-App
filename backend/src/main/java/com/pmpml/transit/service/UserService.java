package com.pmpml.transit.service;

import com.pmpml.transit.dto.response.UserResponse;
import com.pmpml.transit.entity.KycProfile;
import com.pmpml.transit.entity.User;
import com.pmpml.transit.enums.KycStatus;
import com.pmpml.transit.exception.ResourceNotFoundException;
import com.pmpml.transit.repository.KycProfileRepository;
import com.pmpml.transit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KycProfileRepository kycProfileRepository;

    public UserResponse getProfile(Authentication authentication) {
        User user = getUserByEmail(authentication.getName());
        KycStatus kycStatus = user.getKycProfile() != null ? user.getKycProfile().getStatus() : KycStatus.NOT_STARTED;
        return UserResponse.builder()
                .id(user.getId()).email(user.getEmail()).fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber()).roles(user.getRoles())
                .kycStatus(kycStatus).createdAt(user.getCreatedAt()).build();
    }

    @Transactional
    public UserResponse submitKyc(Authentication authentication, String providerName, String providerReference) {
        User user = getUserByEmail(authentication.getName());
        KycProfile kyc = kycProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> KycProfile.builder().user(user).build());
        kyc.setProviderName(providerName);
        kyc.setProviderReference(providerReference);
        kyc.setStatus(KycStatus.PENDING);
        kycProfileRepository.save(kyc);
        user.setKycProfile(kyc);
        userRepository.save(user);
        return getProfile(authentication);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
