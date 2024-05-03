package com.qyd.service.user.service.help;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 密码加密器，后续接入SpringSecurity之后。 可以使用PasswordEncoder 进行替换
 *
 * @author 邱运铎
 * @date 2024-04-21 16:25
 */
@Component
public class UserPwdEncoder {

    /**
     * 密码加盐， 更推荐的做法是每个用户都是用级独立的盐， 提高安全性
     */
    @Value("${security.salt}")
    private String salt;

    @Value("${security.salt-index}")
    private Integer saltIndex;

    public boolean match(String plainPwd, String encPwd) {
        return Objects.equals(encPwd(plainPwd), encPwd);
    }

    /**
     * 明文密码处理
     *
     * @param plainPwd
     * @return
     */
    public String encPwd(String plainPwd) {
        if (plainPwd.length() > saltIndex) {
            plainPwd = plainPwd.substring(0, saltIndex) + salt + plainPwd.substring(saltIndex);
        } else {
            plainPwd = plainPwd + salt;
        }
        return DigestUtils.md5DigestAsHex(plainPwd.getBytes(StandardCharsets.UTF_8));
    }

    // 在自己更换密码的盐和加盐位置后，admin账户的密码admin需要自己重新生成加密一下放到数据库
    public static void main(String[] args) {
        String res = "c34c74cfbcccb8af0baedd02db9b4781";
        String paiRes = "df3a4143b663a086d1c006c8084db1b1";
        String plainPwd = "admin";
//        String salt = "simple_forum_salt";
        String salt = "tech_π";
        int saltIndex = 3;
        if (plainPwd.length() > saltIndex) {
            plainPwd = plainPwd.substring(0, saltIndex) + salt + plainPwd.substring(saltIndex);
        } else {
            plainPwd = plainPwd + salt;
        }
        System.out.println(DigestUtils.md5DigestAsHex(plainPwd.getBytes(StandardCharsets.UTF_8)));
    }
}
