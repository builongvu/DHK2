package vn.vissoft.dashboard.config;

import com.viettel.security.PassTranformer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

public class CustomPasswordEncoder implements PasswordEncoder {

    private String passwordSecretKey;

    public CustomPasswordEncoder(String passwordSecretKey)
    {
        this.passwordSecretKey = passwordSecretKey;
    }

    @Override
    public String encode(CharSequence charSequence) {
        PassTranformer.setInputKey(passwordSecretKey);
        String pass = charSequence == null?"":charSequence.toString();
        return PassTranformer.encrypt(pass);
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return Objects.equals(charSequence,s);
    }
}
