package sme.tech.innovators.sme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sme.tech.innovators.sme.dto.response.BusinessDto;
import sme.tech.innovators.sme.dto.response.UserDto;
import sme.tech.innovators.sme.dto.response.UserWithBusinessDto;
import sme.tech.innovators.sme.entity.Business;
import sme.tech.innovators.sme.entity.User;
import sme.tech.innovators.sme.repository.BusinessRepository;
import sme.tech.innovators.sme.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminQueryService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this::toUserDto)
                .toList();
    }

    public List<BusinessDto> getAllBusinesses() {
        return businessRepository.findAllByIsDeletedFalse()
                .stream()
                .map(this::toBusinessDto)
                .toList();
    }

    public List<UserWithBusinessDto> getAllUsersWithBusinesses() {
        return userRepository.findAllByIsDeletedFalse()
                .stream()
                .map(user -> UserWithBusinessDto.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .accountStatus(user.getAccountStatus())
                        .role(user.getRole())
                        .userCreatedAt(user.getCreatedAt())
                        .business(user.getBusiness() != null ? toBusinessDto(user.getBusiness()) : null)
                        .build())
                .toList();
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .accountStatus(user.getAccountStatus())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private BusinessDto toBusinessDto(Business business) {
        return BusinessDto.builder()
                .id(business.getId())
                .name(business.getName())
                .slug(business.getSlug())
                .publicLink(business.getPublicLink())
                .description(business.getDescription())
                .createdAt(business.getCreatedAt())
                .updatedAt(business.getUpdatedAt())
                .build();
    }
}
