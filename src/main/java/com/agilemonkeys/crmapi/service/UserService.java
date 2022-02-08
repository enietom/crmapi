package com.agilemonkeys.crmapi.service;

import com.agilemonkeys.crmapi.dto.AuthUserDto;
import com.agilemonkeys.crmapi.dto.CustomUserPrincipal;
import com.agilemonkeys.crmapi.dto.UserDto;
import com.agilemonkeys.crmapi.entity.UserEntity;
import com.agilemonkeys.crmapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException(username)
        );
        return new CustomUserPrincipal(user);
    }

    public AuthUserDto registerUser(AuthUserDto authUserDto) {

        if (!authUserDto.getPassword().equals(authUserDto.getMatchingPassword())) {
            throw new BadCredentialsException("Confirmation Password doesn't match");
        }
        if (isUserDuplicated(authUserDto.getUsername())) {
            throw new BadCredentialsException("Username is already used");
        }

        UserEntity userEntity = mapAuthUserDtoToUserEntity(authUserDto);
        if (userEntity.getRoles() == null) {
            userEntity.setRoles("ROLE_USER");
        }
        userEntity.setPassword(passwordEncoder().encode(userEntity.getPassword()));

        userEntity = userRepository.save(userEntity);
        log.debug("User {} registered successfully", userEntity.getUsername());

        return mapAuthUserEntityToAuthUserDto(userEntity);
    }

    private boolean isUserDuplicated(String username) {
        Optional<UserEntity> duplicated = userRepository.findByUsername(username);
        return duplicated.isPresent();
    }

    public List<UserDto> getUsers() {
        log.debug("Getting users");
        List<UserEntity> userEntities = userRepository.findAll();

        List<UserDto> result = userEntities.stream().map(
            this::mapUserEntityToUserDto
        ).collect(Collectors.toList());

        log.debug("{} users found", result.size());
        return result;
    }

    public UserDto getUser(Long userId) {
        log.debug("Getting user with id: {}", userId);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow();

        UserDto userDto = mapUserEntityToUserDto(userEntity);

        log.debug("User found {} {}", userId, userDto.getUsername());

        return userDto;
    }

    public UserDto createUser(UserDto userDto) {
        log.debug("Creating new user");

        if (isUserDuplicated(userDto.getUsername())) {
            throw new BadCredentialsException("Username is already used");
        }

        UserEntity userEntity = mapUserDtoToUserEntity(userDto);

        UserEntity newUserEntity = userRepository.save(userEntity);

        UserDto newUserDto = mapUserEntityToUserDto(newUserEntity);

        log.debug("User created successfully {}", newUserDto);
        return newUserDto;
    }

    public UserDto updateUser(Long userId, UserDto userDto) {
        log.debug("Updating user with id {}", userId);

        UserEntity userEntity = userRepository.findById(userId).orElseThrow();

        userEntity.setUsername(userDto.getUsername());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setRoles(userDto.getRoles());

        userRepository.save(userEntity);

        UserDto updatedUserDto = mapUserEntityToUserDto(userEntity);

        log.debug("User updated successfully {}", updatedUserDto);
        return updatedUserDto;
    }

    public UserDto deleteUser(Long userId) {
        log.debug("Deleting user with id {}", userId);
        UserEntity userEntity = userRepository.findById(userId).orElseThrow();

        UserDto deletedUserDto = mapUserEntityToUserDto(userEntity);

        userRepository.delete(userEntity);

        log.debug("User deleted successfully {}", deletedUserDto);
        return deletedUserDto;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private UserDto mapUserEntityToUserDto(UserEntity userEntity) {
        return UserDto.builder()
            .id(userEntity.getId())
            .username(userEntity.getUsername())
            .firstName(userEntity.getFirstName())
            .lastName(userEntity.getLastName())
            .roles(userEntity.getRoles())
            .lastModifiedBy(userEntity.getLastModifiedBy())
            .lastModifiedDate(userEntity.getLastModifiedDate())
            .build();
    }

    private UserEntity mapUserDtoToUserEntity(UserDto userDto) {
        return UserEntity.builder()
            .id(userDto.getId())
            .username(userDto.getUsername())
            .firstName(userDto.getFirstName())
            .lastName(userDto.getLastName())
            .roles(userDto.getRoles())
            .build();
    }

    private UserEntity mapAuthUserDtoToUserEntity(AuthUserDto authUserDto) {
        return UserEntity.builder()
            .username(authUserDto.getUsername())
            .firstName(authUserDto.getFirstName())
            .lastName(authUserDto.getLastName())
            .password(authUserDto.getPassword())
            .roles(authUserDto.getRoles())
            .build();
    }

    private AuthUserDto mapAuthUserEntityToAuthUserDto(UserEntity userEntity) {
        return AuthUserDto.builder()
            .username(userEntity.getUsername())
            .firstName(userEntity.getFirstName())
            .lastName(userEntity.getLastName())
            .roles(userEntity.getRoles())
            .build();
    }

}
