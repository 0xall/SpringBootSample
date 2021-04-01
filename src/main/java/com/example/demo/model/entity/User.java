package com.example.demo.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.password.PasswordEncoder;

@Setter
@Getter
@Document(collection = "user")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private String nickname;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String pwHash;

    /**
     * 패스워드를 설정합니다. 패스워드는 암호화되서 저장되기 때문에 `pwHash` 를
     * 직접 수정하지 않고, 이 메소드를 사용하여 패스워드를 변경해야합니다.
     *
     * @param passwordEncoder 패스워드 암호화를 위해 사용하는 encoder.
     * @param password plain 패스워드 값.
     */
    public void setPassword(PasswordEncoder passwordEncoder, String password) {
        this.pwHash = passwordEncoder.encode(password);
    }

    /**
     * 패스워드를 검증합니다.
     *
     * @param passwordEncoder 패스워드 검증을 위해 사용하는 encoder.
     * @param password plain 패스워드 값.
     *
     * @return 패스워드가 일치하면 true, 아니면 false 를 반환합니다.
     */
    public boolean matchPassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, pwHash);
    }
}
