package com.orderfleet.webapp.service.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 * 
 * @author Shaheer
 * @since May 06, 2016
 */
public final class RandomUtil {

	private static final int PASSWORD_COUNT = 6;
    private static final int DEF_COUNT = 20;
    private static final int PID_DEF_COUNT = 10;
   

    private RandomUtil() {
    }

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(PASSWORD_COUNT);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    /**
    * Generates a reset key.
    *
    * @return the generated reset key
    */
    public static String generateResetKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }
    
    /**
     * Generate a unique series to validate a persistent token, used in the
     * authentication remember-me mechanism.
     *
     * @return the generated series data
     */
     public static String generateSeriesData() {
         return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
     }

     /**
     * Generate a persistent token, used in the authentication remember-me mechanism.
     *
     * @return the generated token data
     */
     public static String generateTokenData() {
         return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
     }
    
    /**
     * Generates a public id.
     *
     * @return the generated public id
     */
     public static String generatePid() {
         return RandomStringUtils.randomAlphanumeric(PID_DEF_COUNT) + System.currentTimeMillis();
     }
     
     /**
      * Generates a client document number.
      *
      * @return the generated documentNo
      */
      public static String generateServerDocumentNo() {
     	 return RandomStringUtils.randomAlphanumeric(DEF_COUNT);  
      }
}
