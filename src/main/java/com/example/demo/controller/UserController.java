package com.example.demo.controller;

import com.example.demo.annotation.RequestUser;
import com.example.demo.dto.UserDTO;
import com.example.demo.error.APIError;
import com.example.demo.model.entity.User;
import com.example.demo.model.repository.UserRepository;
import com.example.demo.service.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Email 및 Password 로 로그인하는 API.
     *
     * @return JWT Token.
     */
    @RequestMapping(value="/sign-in", method=RequestMethod.POST)
    @ResponseBody
    public UserDTO.SignInResponse signIn(
            @RequestBody @Valid UserDTO.SignInRequest userSignInRequest
    ) {
        UserDTO.SignInResponse response = new UserDTO.SignInResponse();

        Optional<User> user = userRepository.findByEmail(userSignInRequest.getEmail());

        // 해당 이메일을 가진 유저가 존재하지 않거나 패스워드가 일치하지 않는 경우
        if (user.isEmpty() || !user.get().matchPassword(passwordEncoder, userSignInRequest.getPassword())) {
            throw new APIError("AUTHENTICATE_FAIL", "Failed to Authenticate.", HttpStatus.UNAUTHORIZED);
        }

        // 이메일 및 패스워드 인증이 통과하면 JWT Token 을 발급하고 리턴합니다.
        response.setToken(jwtTokenProvider.createToken(user.get().getId()));
        return response;
    }

    /**
     * Email 로 회원가입하는 API.
     *
     * @return 생성된 유저정보.
     */
    @RequestMapping(value="/sign-up", method=RequestMethod.POST)
    @ResponseBody
    public UserDTO.UserInfo signUp(
            @RequestBody @Valid UserDTO.EmailSignUpRequest signUpRequest
    ) {
        User user = new User();

        user.setEmail(signUpRequest.getEmail());
        user.setNickname(signUpRequest.getNickname());
        user.setPassword(passwordEncoder, signUpRequest.getPassword());

        Optional<User> emailUser = userRepository.findByEmail(signUpRequest.getEmail());

        if (emailUser.isPresent()) {
            throw new APIError("EMAIL_EXISTS", "The email does already exist.", HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            userRepository.insert(user);
        } catch (DuplicateKeyException ex) {
            // 닉네임이 이미 존재하는 경우
            throw new APIError("NICKNAME_EXISTS", "The nickname does already exist.", HttpStatus.NOT_ACCEPTABLE);
        }

        return modelMapper.map(user, UserDTO.UserInfo.class);
    }

    /**
     * 현재 로그인한 유저의 정보를 가져오는 API.
     *
     * @return 현재 로그인한 유저의 유저 정보.
     */
    @RequestMapping(value="/user")
    @ResponseBody
    public UserDTO.UserInfo getMyInfo(
            @RequestUser Optional<User> user
    ) {
        return modelMapper.map(user.get(), UserDTO.UserInfo.class);
    }

    /**
     * 유저 ID 를 통해 Public 한 유저 정보를 가져오는 API.
     *
     * @param userId 유저 아이디.
     * @return 퍼블릭한 유저 정보.
     */
    @RequestMapping(value="/user/{id}")
    @ResponseBody
    public UserDTO.UserInfo getUserInfo(
            @PathVariable("id") String userId
    ) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new APIError("USER_NOT_FOUND", "User is not found", HttpStatus.NOT_FOUND);
        }

        return modelMapper.map(user.get(), UserDTO.UserInfo.class);
    }
}
