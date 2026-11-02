package atm;

import org.junit.jupiter.api.AfterEach;
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

	@AfterEach
	public void tearDown() throws Exception {
	}

	
	@ParameterizedTest(name = "{0}: pin={1}, amount={2} -> {3} ({4})")
	@DisplayName("Session withdrawal, PIN format and amount")
	@CsvSource({
		"TC01, 0000, 0, PIN_ERROR, 'PIN must be 5 digits'",
		"TC02, 0000, 20, PIN_ERROR, 'PIN must be 5 digits'",
		"TC03, 0000, 40, PIN_ERROR, 'PIN must be 5 digits'",
		"TC04, 0000, 500, PIN_ERROR, 'PIN must be 5 digits'",
		"TC05, 0000, 980, PIN_ERROR, 'PIN must be 5 digits'",
		"TC06, 0000, 1000, PIN_ERROR, 'PIN must be 5 digits'",
		"TC07, 0000, 1020, PIN_ERROR, 'PIN must be 5 digits'",
		"TC08, 00000, 0, AMOUNT_ERROR, 'Assumption 2: $0 is an invalid amount'",
		"TC09, 00000, 20, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC10, 00000, 40, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC11, 00000, 500, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC12, 00000, 980, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC13, 00000, 1000, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC14, 00000, 1020, AMOUNT_ERROR, 'Amount over the $1,000 daily limit'",
		"TC15, 00001, 0, AMOUNT_ERROR, 'Assumption 2: $0 is an invalid amount'",
		"TC16, 00001, 20, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC17, 00001, 40, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC18, 00001, 500, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC19, 00001, 980, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC20, 00001, 1000, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC21, 00001, 1020, AMOUNT_ERROR, 'Amount over the $1,000 daily limit'",
		"TC22, 55555, 0, AMOUNT_ERROR, 'Assumption 2: $0 is an invalid amount'",
		"TC23, 55555, 20, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC24, 55555, 40, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC25, 55555, 500, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC26, 55555, 980, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC27, 55555, 1000, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC28, 55555, 1020, AMOUNT_ERROR, 'Amount over the $1,000 daily limit'",
		"TC29, 99998, 0, AMOUNT_ERROR, 'Assumption 2: $0 is an invalid amount'",
		"TC30, 99998, 20, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC31, 99998, 40, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC32, 99998, 500, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC33, 99998, 980, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC34, 99998, 1000, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC35, 99998, 1020, AMOUNT_ERROR, 'Amount over the $1,000 daily limit'",
		"TC36, 99999, 0, AMOUNT_ERROR, 'Assumption 2: $0 is an invalid amount'",
		"TC37, 99999, 20, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC38, 99999, 40, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC39, 99999, 500, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC40, 99999, 980, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC41, 99999, 1000, OK, 'Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit'",
		"TC42, 99999, 1020, AMOUNT_ERROR, 'Amount over the $1,000 daily limit'",
		"TC43, 100000, 0, PIN_ERROR, 'PIN must be 5 digits'",
		"TC44, 100000, 20, PIN_ERROR, 'PIN must be 5 digits'",
		"TC45, 100000, 40, PIN_ERROR, 'PIN must be 5 digits'",
		"TC46, 100000, 500, PIN_ERROR, 'PIN must be 5 digits'",
		"TC47, 100000, 980, PIN_ERROR, 'PIN must be 5 digits'",
		"TC48, 100000, 1000, PIN_ERROR, 'PIN must be 5 digits'",
		"TC49, 100000, 1020, PIN_ERROR, 'PIN must be 5 digits'",
	})
	public void sessionWithdrawalTest(String id, String pin, int amount, String expected,
			String rule) throws Exception {
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
