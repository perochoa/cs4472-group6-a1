package atm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import atm.dispatcher.MessageDispatcher;
import atm.exceptions.InvalidAmountException;
import atm.exceptions.InvalidPinFormatException;
import atm.ui.panels.MainPanel;
import atm.utils.CredentialsCheck;
import atm.utils.FormatChecker;
import bank.transactions.utils.AccountType;
import bank.transactions.utils.TransactionResult;
import bank.transactions.utils.TransactionType;

@ExtendWith(MockitoExtension.class)
public class SessionWithdrawalTest {
	ATM atm;
	MessageDispatcher dispatcher;
	

	@BeforeEach
	public void setUp() throws Exception {
		dispatcher = Mockito.mock(MessageDispatcher.class);
		FormatChecker formatCheck = new FormatChecker();
		CredentialsCheck credentialsCheck = new CredentialsCheck(dispatcher);
		atm = new ATM(formatCheck, credentialsCheck, dispatcher);
		
		MainPanel mainPanel = Mockito.mock(MainPanel.class);
		atm.setMainPanel(mainPanel);
		atm.createSession();
	}
	
	// PIN and amount are tested separately (instructor clarification): with one variable,
	// robust worst case BVA gives the same 7 values as single fault BVA (7^1 = 6*1 + 1)

	// PIN at its 7 robust boundary values, amount held at its nominal value ($500)
	@ParameterizedTest(name = "{0}: pin={1}, amount=500 -> {2} ({3})")
	@DisplayName("Session withdrawal, PIN format")
	@CsvSource({
		"TC01, 0000, PIN_ERROR, 'PIN must be 5 digits'",
		"TC02, 00000, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC03, 00001, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC04, 55555, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC05, 99998, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC06, 99999, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC07, 100000, PIN_ERROR, 'PIN must be 5 digits'",
	})
	public void sessionPinFormatTest(String id, String pin, String expected, String rule) throws Exception {
		runWithdrawal(pin, 500, expected);
	}

	// amount at its 7 robust boundary values, PIN held at its nominal value (55555)
	@ParameterizedTest(name = "{0}: pin=55555, amount={1} -> {2} ({3})")
	@DisplayName("Session withdrawal, amount")
	@CsvSource({
		"TC08, 0, AMOUNT_ERROR, 'Assumption 2: $0 is an invalid amount'",
		"TC09, 20, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC10, 40, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC11, 500, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC12, 980, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC13, 1000, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC14, 1020, AMOUNT_ERROR, 'Amount over the $1,000 daily limit'",
	})
	public void sessionAmountTest(String id, int amount, String expected, String rule) throws Exception {
		runWithdrawal("55555", amount, expected);
	}

	private void runWithdrawal(String pin, int amount, String expected) throws Exception {
		// lenient: tests that stop at the PIN or amount check never use these stubs
		Mockito.lenient().when(dispatcher.checkCredentials(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(true);
		// models the README account: $5,000 balance, $1,000 daily limit
		TransactionResult bankResult = amount <= 1000
				? new TransactionResult(true, "", 0.0, new double[] {5000 - amount})
				: new TransactionResult(false, "Daily limit exceeded.", 0.0, new double[] {5000});
		Mockito.lenient().when(dispatcher.performTransaction(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.any()))
				.thenReturn(bankResult);
		atm.checkCardNumber("4111111111111111");

		if (expected.equals("PIN_ERROR")) {
			Assertions.assertThrows(InvalidPinFormatException.class, () -> withdraw(pin, amount));
		} else if (expected.equals("AMOUNT_ERROR")) {
			Assertions.assertThrows(InvalidAmountException.class, () -> withdraw(pin, amount));
		} else {
			Assertions.assertDoesNotThrow(() -> withdraw(pin, amount));
			Mockito.verify(dispatcher).performTransaction(ArgumentMatchers.any(), ArgumentMatchers.any(),
					ArgumentMatchers.any());
		}
	}

	private void withdraw(String pin, int amount) throws Exception {
		atm.checkPin(pin.toCharArray());
		atm.setTransaction(TransactionType.Withdrawal);
		atm.setFromAccount(AccountType.Chequing);
		atm.setAmount(amount);
	}

}
