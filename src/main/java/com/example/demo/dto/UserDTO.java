package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserDTO {
    @Getter
    protected static class EmailMixin {
        @Email()
        protected String email;

        /**
         * 이메일 주소는 case-insensitive 하기 위해 반드시 lower-case 로 변환해서 저장합니다.
         *
         * @return lower case 로 변환된 이메일 주소.
         */
        public String getEmail() {
            return email.toLowerCase();
        }
    }

    @Getter
    public static class SignInRequest extends EmailMixin {
        @NotNull()
        private String password;
    }

    @Getter
    public static class EmailSignUpRequest extends EmailMixin {
        @NotNull()
        private String nickname;

        @NotNull()
        private String password;
    }

    @Setter
    @Getter
    public static class SignInResponse {
        public String token;
    }

    @Setter
    @Getter
    public static class UserInfo {
        public String id;
        public String nickname;
    }
}
