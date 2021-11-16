package com.mapr.mgrweb.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TotpUtility {

    private SecretGenerator secretGenerator = new DefaultSecretGenerator(32);
    private CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1, 6);

    @Value("${totp.secret}")
    private String totpSecret;

    /*
    public static void main(String[] args) throws Exception {
        TotpUtility tutil = new TotpUtility();
        String secret = "4ECL72PO6YYV7RUVS2ZCMLWISEJ5VZ4C";
        tutil.setTotpSecret(secret);
        String code = tutil.generateCode();
        boolean successful = tutil.isValid(code);
        System.out.println("Secret Key is: "+secret+ " Code is: "+code+ " Verification: "+successful);
    }
*/
    public void setTotpSecret(String aSecret) {
        this.totpSecret = aSecret;
    }

    public String getTotpSecret() {
        return this.totpSecret;
    }

    public boolean isValid(String inputCode) {
        TimeProvider timeProvider = new SystemTimeProvider();
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        verifier.setAllowedTimePeriodDiscrepancy(3);
        boolean isValid = verifier.isValidCode(totpSecret, inputCode);
        return isValid;
    }

    public String generateCode() throws Exception {
        TimeProvider timeProvider = new SystemTimeProvider();
        long currentBucket = Math.floorDiv(timeProvider.getTime(), 30);
        String code = codeGenerator.generate(totpSecret, currentBucket);
        return code;
    }
}
