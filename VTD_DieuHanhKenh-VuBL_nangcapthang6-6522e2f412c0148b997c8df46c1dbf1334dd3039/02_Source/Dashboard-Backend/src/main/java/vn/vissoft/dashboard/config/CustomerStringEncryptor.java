package vn.vissoft.dashboard.config;

import com.viettel.security.PassTranformer;
import org.jasypt.encryption.StringEncryptor;

public class CustomerStringEncryptor implements StringEncryptor {

    private String secretkey;

    public CustomerStringEncryptor(String secretkey)
    {
        this.secretkey = secretkey;
        PassTranformer.setInputKey(secretkey);
    }

    @Override
    public String encrypt(String s) {
        return PassTranformer.encrypt(s);
    }

    @Override
    public String decrypt(String s) {
        return PassTranformer.decrypt(s);
    }
}
