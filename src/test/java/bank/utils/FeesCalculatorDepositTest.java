package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Black-box tests for FeesCalculator#calculateDepositInterest.
 * Technique: Weak Robust Equivalence Class Analysis.
 * Spec: Assignment 1, "Deposit" section.
 * Test IDs (D1-D14) match the tables in docs/assignment1.md.
 */
class FeesCalculatorDepositTest {

	private static final double TOLERANCE = 0.0001;

	private final FeesCalculator calculator = new FeesCalculator();

	@ParameterizedTest(name = "{0}: student={1}, amount={2}, balance={3} -> fee {4} ({5})")
	@DisplayName("Deposit fee, valid equivalence classes")
	@CsvSource({
		// id,  student, amount, balance, expectedFee, spec rule
		"D1,  true,  100, 1000,  0.50, 'Spec Deposit: student, amount > 50, balance > 500 -> 0.5%'",
		"D2,  true,  100, 300,   0.25, 'Spec Deposit: student, amount > 50, balance <= 500 -> 0.25%'",
		"D3,  true,  20,  6000,  0.10, 'Spec Deposit: student, amount <= 50, balance > 5000 -> 0.5%'",
		"D4,  true,  20,  300,   0.00, 'Spec Deposit: student, amount <= 50, balance <= 500 -> no fee'",
		"D5,  true,  20,  2000,  0.00, 'Spec Deposit: student, amount <= 50, 500 < balance <= 5000 -> no fee'",
		"D6,  false, 500, 3000,  4.00, 'Spec Deposit: not student, amount > 250, balance > 2500 -> 0.8%'",
		"D7,  false, 500, 1000,  2.00, 'Spec Deposit: not student, amount > 250, balance <= 2500 -> 0.4%'",
		"D8,  false, 100, 20000, 0.00, 'Spec Deposit: not student, amount <= 250, balance > 10000 -> no fee'",
		"D9,  false, 100, 5000,  0.10, 'Spec Deposit: not student, amount <= 250, 2500 < balance <= 10000 -> 0.1%'",
		"D10, false, 100, 1000,  0.10, 'Spec Deposit: not student, amount <= 250, balance <= 2500 -> 0.1%'"
	})
	void validClasses(String id, boolean student, double amount, double balance,
			double expectedFee, String specRule) {
		assertEquals(expectedFee, calculator.calculateDepositInterest(amount, balance, student), TOLERANCE,
				id + " failed. " + specRule);
	}

	@ParameterizedTest(name = "{0}: student={1}, amount={2}, balance={3} -> invalid ({4})")
	@DisplayName("Deposit fee, invalid equivalence classes")
	@CsvSource({
		// id,  student, amount, balance, spec rule
		"D11, true,  -10, 1000, 'Assumption: negative amount is invalid input'",
		"D12, true,  100, -1,   'Assumption: negative balance is invalid input'",
		"D13, false, -10, 1000, 'Assumption: negative amount is invalid input'",
		"D14, false, 100, -1,   'Assumption: negative balance is invalid input'"
	})
	void invalidClasses(String id, boolean student, double amount, double balance, String specRule) {
		assertThrows(IllegalArgumentException.class,
				() -> calculator.calculateDepositInterest(amount, balance, student),
				id + " failed. " + specRule);
	}
}
