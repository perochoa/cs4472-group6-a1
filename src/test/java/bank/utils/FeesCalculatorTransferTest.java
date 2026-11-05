package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Black-box tests for FeesCalculator#calculateTransferFee.
 * Technique: Decision Table Analysis.
 * Spec: Assignment 1, "Transfer" section.
 * Test IDs (DT1-DT16) match the decision table rules and the tables in docs/assignment1.md.
 */
class FeesCalculatorTransferTest {

	private static final double TOLERANCE = 1e-6;

	private final FeesCalculator calculator = new FeesCalculator();

	@ParameterizedTest(name = "{0}: student={1}, amount={2}, from={3}, to={4} -> rate {5}% ({6})")
	@DisplayName("Transfer fee, decision table rules")
	@CsvSource({
		// id, student, amount, fromAccountBalance, toAccountBalance, fee rate (%), spec rule
		"DT1,true,199,1999,999,0.1,'student, amount < 200, fromAccountBalance < 2000, toAccountBalance < 1000 → 0.1%'",
		"DT2,true,199,1999,1001,0.05,'student, amount < 200, fromAccountBalance < 2000, toAccountBalance >= 1000 → 0.05%'",
		"DT3,true,199,2001,999,0.05,'student, amount < 200, fromAccountBalance >= 2000, toAccountBalance < 1000 → 0.05%'",
		"DT4,true,199,2001,1001,0.025,'student, amount < 200, fromAccountBalance >= 2000, toAccountBalance >= 1000 → 0.025%'",
		"DT5,true,201,1999,999,0.05,'student, amount >= 200, fromAccountBalance < 2000, toAccountBalance < 1000 → 0.05%'",
		"DT6,true,201,1999,1001,0.025,'student, amount >= 200, fromAccountBalance < 2000, toAccountBalance >= 1000 → 0.025%'",
		"DT7,true,201,2001,999,0.25,'student, amount >= 200, fromAccountBalance >= 2000, toAccountBalance < 1000 → 0.25%'",
		"DT8,true,201,2001,1001,0.125,'student, amount >= 200, fromAccountBalance >= 2000, toAccountBalance >= 1000 → 0.125%'",
		"DT9,false,99,3999,1999,0.2,'non-student, amount < 100, fromAccountBalance < 4000, toAccountBalance < 2000 → 0.2%'",
		"DT10,false,99,3999,2001,0.1,'non-student, amount < 100, fromAccountBalance < 4000, toAccountBalance >= 2000 → 0.1%'",
		"DT11,false,99,4001,1999,1.0,'non-student, amount < 100, fromAccountBalance >= 4000, toAccountBalance < 2000 → 1.0%'",
		"DT12,false,99,4001,2001,0.5,'non-student, amount < 100, fromAccountBalance >= 4000, toAccountBalance >= 2000 → 0.5%'",
		"DT13,false,101,1999,999,0.2,'non-student, amount >= 100, fromAccountBalance < 2000, toAccountBalance < 1000 → 0.2%'",
		"DT14,false,101,1999,1001,0.1,'non-student, amount >= 100, fromAccountBalance < 2000, toAccountBalance >= 1000 → 0.1%'",
		"DT15,false,101,2001,999,0.5,'non-student, amount >= 100, fromAccountBalance >= 2000, toAccountBalance < 1000 → 0.5%'",
		"DT16,false,101,2001,1001,0.25,'non-student, amount >= 100, fromAccountBalance >= 2000, toAccountBalance >= 1000 → 0.25%'"
	})
	void transferTest(String id, boolean student, double amount, double fromAccountBalance,
			double toAccountBalance, double feeRatePercent, String specRule) {
		// Convert percentage to decimal
		double expectedFee = amount * (feeRatePercent / 100);
		assertEquals(expectedFee,
				calculator.calculateTransferFee(amount, fromAccountBalance, toAccountBalance, student),
				TOLERANCE, id + " failed. " + specRule);
	}
}
