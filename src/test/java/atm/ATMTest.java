package atm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import atm.dispatcher.MessageDispatcher;
import atm.exceptions.InvalidAmountException;
import atm.exceptions.InvalidCredentialsException;
import atm.exceptions.InvalidPinFormatException;
import atm.ui.panels.MainPanel;
import atm.utils.CredentialsCheck;
import atm.utils.FormatChecker;
import bank.transactions.utils.AccountType;
import bank.transactions.utils.TransactionResult;
import bank.transactions.utils.TransactionType;

@ExtendWith(MockitoExtension.class)
public class ATMTest {
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

	@Test
	public void checkCorrectPINTest() {
		Mockito.when(dispatcher.checkCredentials(null, new char[] {'5','5','5','5'})).thenReturn(true);
		Assertions.assertDoesNotThrow(() -> atm.checkPin(new char[] {'5','5','5','5'}));
	}
	
	@Test
	public void checkIncorrectPINTest() {
		Mockito.when(dispatcher.checkCredentials(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(false);
		Assertions.assertThrows(InvalidCredentialsException.class, () -> atm.checkPin(new char[] {'5','5','5','4'}));
	}
	
	@ParameterizedTest(name = "{0}: pin={1}, amount={2} -> {3}")
	@CsvSource({
		"TC01, 0000, 0, PIN_ERROR",
		"TC02, 0000, 20, PIN_ERROR",
		"TC03, 0000, 40, PIN_ERROR",
		"TC04, 0000, 500, PIN_ERROR",
		"TC05, 0000, 980, PIN_ERROR",
		"TC06, 0000, 1000, PIN_ERROR",
		"TC07, 0000, 1020, PIN_ERROR",
		"TC08, 00000, 0, AMOUNT_ERROR",
		"TC09, 00000, 20, OK",
		"TC10, 00000, 40, OK",
		"TC11, 00000, 500, OK",
		"TC12, 00000, 980, OK",
		"TC13, 00000, 1000, OK",
		"TC14, 00000, 1020, AMOUNT_ERROR",
		"TC15, 00001, 0, AMOUNT_ERROR",
		"TC16, 00001, 20, OK",
		"TC17, 00001, 40, OK",
		"TC18, 00001, 500, OK",
		"TC19, 00001, 980, OK",
		"TC20, 00001, 1000, OK",
		"TC21, 00001, 1020, AMOUNT_ERROR",
		"TC22, 55555, 0, AMOUNT_ERROR",
		"TC23, 55555, 20, OK",
		"TC24, 55555, 40, OK",
		"TC25, 55555, 500, OK",
		"TC26, 55555, 980, OK",
		"TC27, 55555, 1000, OK",
		"TC28, 55555, 1020, AMOUNT_ERROR",
		"TC29, 99998, 0, AMOUNT_ERROR",
		"TC30, 99998, 20, OK",
		"TC31, 99998, 40, OK",
		"TC32, 99998, 500, OK",
		"TC33, 99998, 980, OK",
		"TC34, 99998, 1000, OK",
		"TC35, 99998, 1020, AMOUNT_ERROR",
		"TC36, 99999, 0, AMOUNT_ERROR",
		"TC37, 99999, 20, OK",
		"TC38, 99999, 40, OK",
		"TC39, 99999, 500, OK",
		"TC40, 99999, 980, OK",
		"TC41, 99999, 1000, OK",
		"TC42, 99999, 1020, AMOUNT_ERROR",
		"TC43, 100000, 0, PIN_ERROR",
		"TC44, 100000, 20, PIN_ERROR",
		"TC45, 100000, 40, PIN_ERROR",
		"TC46, 100000, 500, PIN_ERROR",
		"TC47, 100000, 980, PIN_ERROR",
		"TC48, 100000, 1000, PIN_ERROR",
		"TC49, 100000, 1020, PIN_ERROR",
	})
	public void sessionWithdrawalTest(String id, String pin, int amount, String expected) throws Exception {
		// lenient: tests that stop at the PIN or amount check never use these stubs
		Mockito.lenient().when(dispatcher.checkCredentials(ArgumentMatchers.any(), ArgumentMatchers.any()))
				.thenReturn(true);
		Mockito.lenient().when(dispatcher.performTransaction(ArgumentMatchers.any(), ArgumentMatchers.any(),
				ArgumentMatchers.any()))
				.thenReturn(new TransactionResult(true, "", 0.0, new double[] {5000 - amount}));
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

// Withdrawal
// If the client is a student and
	// If the transaction is performed during the weekday, then there is no fee.
	// Otherwise, the fee is 0.1% of the amount withdrawn.
// If the client is not a student and
	// If the balance of the account is less than $1,000, then the fee is 0.3% of the amount withdrawn.
	// If, however, the balance is $1,000 or more, but less than $5,000, the fee is 0.1% of the amount withdrawn.
	// If the balance is $5,000 or more, then there is no fee.
