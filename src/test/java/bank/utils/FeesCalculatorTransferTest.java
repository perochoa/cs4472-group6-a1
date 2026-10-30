package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Black-box tests for FeesCalculator#calculateTransferFee.
 * Technique: Decision Table Analysis.
 * Spec: Assignment 1, "Transfer" section.
 */
class FeesCalculatorTransferTest {

    private static final double TOLERANCE = 1e-6;
    private final FeesCalculator calculator = new FeesCalculator();

    @ParameterizedTest
    @DisplayName("Transfer fee calculation")
    @CsvSource({
            "DT1,true,199,1999,999,0.1",
            "DT2,true,199,1999,1001,0.05",
            "DT3,true,199,2001,999,0.05",
            "DT4,true,199,2001,1001,0.025",
            "DT5,true,201,1999,999,0.05",
            "DT6,true,201,1999,1001,0.025",
            "DT7,true,201,2001,999,0.25",
            "DT8,true,201,2001,1001,0.125",
            "DT9,false,99,3999,1999,0.2",
            "DT10,false,99,3999,2001,0.1",
            "DT11,false,99,4001,1999,1.0",
            "DT12,false,99,4001,2001,0.5",
            "DT13,false,101,1999,999,0.2",
            "DT14,false,101,1999,1001,0.1",
            "DT15,false,101,2001,999,0.5",
            "DT16,false,101,2001,1001,0.25"
    })
    void transferTest(
        String name, 
        boolean student, 
        double amount, 
        double fromAccountBalance, 
        double toAccountBalance, 
        double expectedFee) {

        // Convert percentage to decimal
        expectedFee = (expectedFee/100); 
        assertEquals(amount*expectedFee, 
            calculator.calculateTransferFee(amount, fromAccountBalance, toAccountBalance, student), 
            TOLERANCE, 
            name + " failed.");
    }
}
