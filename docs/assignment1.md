## ATM Session: Robust Worst Case Boundary Value Analysis

**Target:** `atm.session.Session` (driven through `atm.ATM`)
**Technique:** Robust Worst Case Boundary Value Analysis
**Spec:** Assignment 1, "ATM Session" and "The Testing Targets"
**JUnit class:** `src/test/java/atm/SessionWithdrawalTest.java`

### Section 1: Process and assumptions

**Process.** 
I used 7 values for the robust boundary value analysis of min-, min, min+, nominal value, max-, max, and max+ for both the PIN number and the amount. The range of valid PIN numbers can be defined as from 00000 to 99999, so we can define the values around max and min at those values, and choose the nominal value at the halfway point. The same is done for amount at the range of 20 (the minimum for a $20 or $50 withdrawal) to $1000 (daily limit).

As the instructor clarified, the PIN and the withdrawal amount are separate test targets, so I tested them separately instead of combining them. The PIN tests use the 7 PIN values with the amount held at its nominal value ($500), and the amount tests use the 7 amount values with the PIN held at its nominal value (55555). With one variable, robust worst case BVA gives the same 7 values as robust BVA under the single fault assumption (7^1 = 6 × 1 + 1), so these tests are both worst case and single fault. This gives 7 + 7 = 14 test cases.

Additionally, I mocked the bank class so that errors in the bank code don't affect what we're testing, the ATM Session code. We also avoid having to set up the bank database.

**Input values.**

| Variable | min− | min | min+ | nominal | max− | max | max+ |
|---|---|---|---|---|---|---|---|
| PIN | 0000 | 00000 | 00001 | 55555 | 99998 | 99999 | 100000 |
| amount ($) | 0 | 20 | 40 | 500 | 980 | 1,000 | 1,020 |

**Assumptions.**

1. We perform tests on PIN checking through the ATM class because the Session class doesn't actually validate the PIN, the ATM class does
2. A $0 or $1020 withdrawal should throw `InvalidAmountException` because it is an invalid amount
3. PIN and amount are tested separately (instructor clarification), each with the other variable held at its nominal value

### Section 2: Test cases

Account: daily limit $1,000, available balance $5,000.

**PIN format** (`sessionPinFormatTest`, amount held at $500)

| ID | PIN | Amount | Expected | Spec rule |
|---|---|---|---|---|
| TC01 | 0000 | 500 | InvalidPinFormatException | PIN must be 5 digits |
| TC02 | 00000 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC03 | 00001 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC04 | 55555 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC05 | 99998 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC06 | 99999 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC07 | 100000 | 500 | InvalidPinFormatException | PIN must be 5 digits |

**Withdrawal amount** (`sessionAmountTest`, PIN held at 55555)

| ID | PIN | Amount | Expected | Spec rule |
|---|---|---|---|---|
| TC08 | 55555 | 0 | InvalidAmountException | Assumption 2: $0 is an invalid amount |
| TC09 | 55555 | 20 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC10 | 55555 | 40 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC11 | 55555 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC12 | 55555 | 980 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC13 | 55555 | 1,000 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC14 | 55555 | 1,020 | InvalidAmountException | Amount over the $1,000 daily limit |

### Test results

From a local run of `mvn -B test` (14 run, 13 failed, 1 passed).

| ID | Result |
|---|---|
| TC01 | Fail |
| TC02 | Fail |
| TC03 | Fail |
| TC04 | Fail |
| TC05 | Fail |
| TC06 | Fail |
| TC07 | Pass |
| TC08 | Fail |
| TC09 | Fail |
| TC10 | Fail |
| TC11 | Fail |
| TC12 | Fail |
| TC13 | Fail |
| TC14 | Fail |

### Failure log

| Failure ID | Exposing tests | Time testing before found | Time diagnosing |
|---|---|---|---|
| SF1 | TC02–TC06, TC08–TC14 (valid 5-digit PIN rejected with `InvalidPinFormatException`) | 90 | 15 |
| SF2 | TC01 (invalid 4-digit PIN 0000 accepted) | 90 | 5 |

Faults found by reading the code but hidden by F1 (no test fails on them yet):

- `FormatChecker.checkPinFormat`, line 35: `pin[i] >= '9'` rejects the digit 9. Should show up in TC05 and TC06 once F1 is fixed.
- `CashValidator.validateWithdrawal` accepts $0. Should show up in TC08.
- `CashValidator.validateWithdrawal` rejects the valid amount $980 (14 × $50 + 9 × $20), because it uses as many $50 bills as possible and then only tries $20 bills on the rest. Should show up in TC12.
- `Session.setAmount` has no $1,000 daily-limit check. Should show up in TC14.
