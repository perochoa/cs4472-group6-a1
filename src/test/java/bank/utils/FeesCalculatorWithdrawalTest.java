package bank.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Black-box tests for FeesCalculator#calculateWithdrawalFee.
 * Technique: Robust Worst Case Boundary Value Analysis.
 * Spec: Assignment 1, "Withdrawal" section.
 * Test IDs (W1-W136) match the tables in docs/assignment1.md.
 * Day of week uses java.util.Calendar constants (1 = Sunday ... 7 = Saturday).
 */
class FeesCalculatorWithdrawalTest {

	private static final double TOLERANCE = 0.0001;

	private final FeesCalculator calculator = new FeesCalculator();

	@ParameterizedTest(name = "{0}: student={1}, amount={2}, balance={3}, day={4} -> fee {5} ({6})")
	@DisplayName("Withdrawal fee, valid inputs")
	@CsvSource({
		// id, student, amount, balance, dayOfWeek, expectedFee, spec rule
		"W9, true, 1, 3000, 1, 0.001, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W10, true, 1, 3000, 2, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W11, true, 1, 3000, 4, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W12, true, 1, 3000, 6, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W13, true, 1, 3000, 7, 0.001, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W16, true, 2, 3000, 1, 0.002, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W17, true, 2, 3000, 2, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W18, true, 2, 3000, 4, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W19, true, 2, 3000, 6, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W20, true, 2, 3000, 7, 0.002, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W23, true, 500, 3000, 1, 0.500, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W24, true, 500, 3000, 2, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W25, true, 500, 3000, 4, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W26, true, 500, 3000, 6, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W27, true, 500, 3000, 7, 0.500, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W30, true, 999, 3000, 1, 0.999, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W31, true, 999, 3000, 2, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W32, true, 999, 3000, 4, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W33, true, 999, 3000, 6, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W34, true, 999, 3000, 7, 0.999, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W37, true, 1000, 3000, 1, 1.000, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W38, true, 1000, 3000, 2, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W39, true, 1000, 3000, 4, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W40, true, 1000, 3000, 6, 0.000, 'Spec Withdrawal: student, weekday -> no fee'",
		"W41, true, 1000, 3000, 7, 1.000, 'Spec Withdrawal: student, weekend -> 0.1%'",
		"W63, false, 1, 0, 4, 0.003, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W64, false, 1, 0.01, 4, 0.003, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W65, false, 1, 500, 4, 0.003, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W66, false, 1, 999.99, 4, 0.003, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W67, false, 1, 1000, 4, 0.001, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W68, false, 1, 1000.01, 4, 0.001, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W69, false, 1, 3000, 4, 0.001, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W70, false, 1, 4999.99, 4, 0.001, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W71, false, 1, 5000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W72, false, 1, 5000.01, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W73, false, 1, 1000000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W75, false, 2, 0, 4, 0.006, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W76, false, 2, 0.01, 4, 0.006, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W77, false, 2, 500, 4, 0.006, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W78, false, 2, 999.99, 4, 0.006, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W79, false, 2, 1000, 4, 0.002, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W80, false, 2, 1000.01, 4, 0.002, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W81, false, 2, 3000, 4, 0.002, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W82, false, 2, 4999.99, 4, 0.002, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W83, false, 2, 5000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W84, false, 2, 5000.01, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W85, false, 2, 1000000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W87, false, 500, 0, 4, 1.500, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W88, false, 500, 0.01, 4, 1.500, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W89, false, 500, 500, 4, 1.500, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W90, false, 500, 999.99, 4, 1.500, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W91, false, 500, 1000, 4, 0.500, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W92, false, 500, 1000.01, 4, 0.500, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W93, false, 500, 3000, 4, 0.500, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W94, false, 500, 4999.99, 4, 0.500, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W95, false, 500, 5000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W96, false, 500, 5000.01, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W97, false, 500, 1000000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W99, false, 999, 0, 4, 2.997, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W100, false, 999, 0.01, 4, 2.997, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W101, false, 999, 500, 4, 2.997, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W102, false, 999, 999.99, 4, 2.997, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W103, false, 999, 1000, 4, 0.999, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W104, false, 999, 1000.01, 4, 0.999, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W105, false, 999, 3000, 4, 0.999, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W106, false, 999, 4999.99, 4, 0.999, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W107, false, 999, 5000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W108, false, 999, 5000.01, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W109, false, 999, 1000000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W111, false, 1000, 0, 4, 3.000, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W112, false, 1000, 0.01, 4, 3.000, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W113, false, 1000, 500, 4, 3.000, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W114, false, 1000, 999.99, 4, 3.000, 'Spec Withdrawal: not student, balance < 1000 -> 0.3%'",
		"W115, false, 1000, 1000, 4, 1.000, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W116, false, 1000, 1000.01, 4, 1.000, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W117, false, 1000, 3000, 4, 1.000, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W118, false, 1000, 4999.99, 4, 1.000, 'Spec Withdrawal: not student, 1000 <= balance < 5000 -> 0.1%'",
		"W119, false, 1000, 5000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W120, false, 1000, 5000.01, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'",
		"W121, false, 1000, 1000000, 4, 0.000, 'Spec Withdrawal: not student, balance >= 5000 -> no fee'"
	})
	void validInputs(String id, boolean student, double amount, double balance, int dayOfWeek,
			double expectedFee, String specRule) {
		assertEquals(expectedFee, calculator.calculateWithdrawalFee(amount, balance, student, dayOfWeek),
				TOLERANCE, id + " failed. " + specRule);
	}

	@ParameterizedTest(name = "{0}: student={1}, amount={2}, balance={3}, day={4} -> invalid ({5})")
	@DisplayName("Withdrawal fee, invalid inputs")
	@CsvSource({
		// id, student, amount, balance, dayOfWeek, spec rule
		"W1, true, 0, 3000, 0, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W2, true, 0, 3000, 1, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W3, true, 0, 3000, 2, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W4, true, 0, 3000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W5, true, 0, 3000, 6, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W6, true, 0, 3000, 7, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W7, true, 0, 3000, 8, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W8, true, 1, 3000, 0, 'Assumption: day outside 1-7 is invalid'",
		"W14, true, 1, 3000, 8, 'Assumption: day outside 1-7 is invalid'",
		"W15, true, 2, 3000, 0, 'Assumption: day outside 1-7 is invalid'",
		"W21, true, 2, 3000, 8, 'Assumption: day outside 1-7 is invalid'",
		"W22, true, 500, 3000, 0, 'Assumption: day outside 1-7 is invalid'",
		"W28, true, 500, 3000, 8, 'Assumption: day outside 1-7 is invalid'",
		"W29, true, 999, 3000, 0, 'Assumption: day outside 1-7 is invalid'",
		"W35, true, 999, 3000, 8, 'Assumption: day outside 1-7 is invalid'",
		"W36, true, 1000, 3000, 0, 'Assumption: day outside 1-7 is invalid'",
		"W42, true, 1000, 3000, 8, 'Assumption: day outside 1-7 is invalid'",
		"W43, true, 1001, 3000, 0, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W44, true, 1001, 3000, 1, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W45, true, 1001, 3000, 2, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W46, true, 1001, 3000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W47, true, 1001, 3000, 6, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W48, true, 1001, 3000, 7, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W49, true, 1001, 3000, 8, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W50, false, 0, -0.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W51, false, 0, 0, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W52, false, 0, 0.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W53, false, 0, 500, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W54, false, 0, 999.99, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W55, false, 0, 1000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W56, false, 0, 1000.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W57, false, 0, 3000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W58, false, 0, 4999.99, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W59, false, 0, 5000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W60, false, 0, 5000.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W61, false, 0, 1000000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W62, false, 1, -0.01, 4, 'Assumption: negative balance is invalid'",
		"W74, false, 2, -0.01, 4, 'Assumption: negative balance is invalid'",
		"W86, false, 500, -0.01, 4, 'Assumption: negative balance is invalid'",
		"W98, false, 999, -0.01, 4, 'Assumption: negative balance is invalid'",
		"W110, false, 1000, -0.01, 4, 'Assumption: negative balance is invalid'",
		"W122, false, 1001, -0.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W123, false, 1001, 0, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W124, false, 1001, 0.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W125, false, 1001, 500, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W126, false, 1001, 999.99, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W127, false, 1001, 1000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W128, false, 1001, 1000.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W129, false, 1001, 3000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W130, false, 1001, 4999.99, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W131, false, 1001, 5000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W132, false, 1001, 5000.01, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W133, false, 1001, 1000000, 4, 'Assumption: amount outside $1-$1,000 is invalid'",
		"W134, true, 500, -0.01, 4, 'Assumption: negative balance is invalid'",
		"W135, false, 500, 3000, 0, 'Assumption: day outside 1-7 is invalid'",
		"W136, false, 500, 3000, 8, 'Assumption: day outside 1-7 is invalid'"
	})
	void invalidInputs(String id, boolean student, double amount, double balance, int dayOfWeek,
			String specRule) {
		assertThrows(IllegalArgumentException.class,
				() -> calculator.calculateWithdrawalFee(amount, balance, student, dayOfWeek),
				id + " failed. " + specRule);
	}
}
