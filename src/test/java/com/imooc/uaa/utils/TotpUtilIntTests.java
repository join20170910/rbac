package com.imooc.uaa.utils;

import com.imooc.uaa.UaaApplicationTests;
import com.imooc.uaa.util.TotpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.InvalidKeyException;
import java.security.Key;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TotpUtilIntTests extends UaaApplicationTests {
    @Autowired
    private TotpUtil totp;
    @Test
    public void givenSameKeyAndTotp_whenValidateTwice_thenFail() throws InvalidKeyException {
        Instant now = Instant.now();
        Instant validFuture = now.plus(totp.getTimeStep());
        Key key = totp.generateKey();
        String first = totp.createTotp(key, now);
        Key newKey = totp.generateKey();
      //  assertTrue(totp.verifyTotp(key,first),"第一次验证应该成功");

        String second = this.totp.createTotp(key, Instant.now());
        assertEquals(first,second,"时间间隔内生成的两个 TOTP是一致的");

        String afterTimeStep = this.totp.createTotp(key, validFuture);
        assertNotEquals(first,afterTimeStep,"过期之后和原来的 TOTP 比较应该不致");
        assertFalse(totp.validateTotp(newKey,first),"使用新的 key 验证原来的 TOTP 应该失败");
    }
}
