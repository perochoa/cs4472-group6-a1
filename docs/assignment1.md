## ATM Session: Robust Worst Case Boundary Value Analysis

**Target:** `atm.session.Session` (driven through `atm.ATM`)
**Technique:** Robust Worst Case Boundary Value Analysis
**Spec:** Assignment 1, "ATM Session" and "The Testing Targets"
**JUnit class:** `src/test/java/atm/SessionWithdrawalTest.java`

### Section 1: Process and assumptions

**Process.** TODO

**Input values.**

| Variable | min− | min | min+ | nominal | max− | max | max+ |
|---|---|---|---|---|---|---|---|
| PIN | 0000 | 00000 | 00001 | 55555 | 99998 | 99999 | 100000 |
| amount ($) | 0 | 20 | 40 | 500 | 980 | 1,000 | 1,020 |

**Assumptions.**

1. We perform tests on PIN checking through the ATM class because the Session class doesn't actually validate the PIN, the ATM class does
2. (Same as withdrawal fee tests assumption #5) Invalid input (amount ≤ $0 or > $1,000, a negative balance, or a day outside 1–7) should throw an `IllegalArgumentException`. The spec doesn't say what should happen, and the method returns a fee, so an exception is the only clean way to reject the input; returning 0 would hide the error. This matches the deposit tests.

### Section 2: Test cases

Account: daily limit $1,000, available balance $5,000.

| ID | PIN | Amount | Expected | Spec rule |
|---|---|---|---|---|
| TC01 | 0000 | 0 | InvalidPinFormatException | PIN must be 5 digits |
| TC02 | 0000 | 20 | InvalidPinFormatException | PIN must be 5 digits |
| TC03 | 0000 | 40 | InvalidPinFormatException | PIN must be 5 digits |
| TC04 | 0000 | 500 | InvalidPinFormatException | PIN must be 5 digits |
| TC05 | 0000 | 980 | InvalidPinFormatException | PIN must be 5 digits |
| TC06 | 0000 | 1,000 | InvalidPinFormatException | PIN must be 5 digits |
| TC07 | 0000 | 1,020 | InvalidPinFormatException | PIN must be 5 digits |
| TC08 | 00000 | 0 | InvalidAmountException | TODO: assumption for amount 0 |
| TC09 | 00000 | 20 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC10 | 00000 | 40 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC11 | 00000 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC12 | 00000 | 980 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC13 | 00000 | 1,000 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC14 | 00000 | 1,020 | InvalidAmountException | Amount over the $1,000 daily limit |
| TC15 | 00001 | 0 | InvalidAmountException | TODO: assumption for amount 0 |
| TC16 | 00001 | 20 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC17 | 00001 | 40 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC18 | 00001 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC19 | 00001 | 980 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC20 | 00001 | 1,000 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC21 | 00001 | 1,020 | InvalidAmountException | Amount over the $1,000 daily limit |
| TC22 | 55555 | 0 | InvalidAmountException | TODO: assumption for amount 0 |
| TC23 | 55555 | 20 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC24 | 55555 | 40 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC25 | 55555 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC26 | 55555 | 980 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC27 | 55555 | 1,000 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC28 | 55555 | 1,020 | InvalidAmountException | Amount over the $1,000 daily limit |
| TC29 | 99998 | 0 | InvalidAmountException | TODO: assumption for amount 0 |
| TC30 | 99998 | 20 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC31 | 99998 | 40 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC32 | 99998 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC33 | 99998 | 980 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC34 | 99998 | 1,000 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC35 | 99998 | 1,020 | InvalidAmountException | Amount over the $1,000 daily limit |
| TC36 | 99999 | 0 | InvalidAmountException | TODO: assumption for amount 0 |
| TC37 | 99999 | 20 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC38 | 99999 | 40 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC39 | 99999 | 500 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC40 | 99999 | 980 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC41 | 99999 | 1,000 | No exception (withdrawal sent) | Valid 5-digit PIN; amount payable in $20/$50 bills and within the daily limit |
| TC42 | 99999 | 1,020 | InvalidAmountException | Amount over the $1,000 daily limit |
| TC43 | 100000 | 0 | InvalidPinFormatException | PIN must be 5 digits |
| TC44 | 100000 | 20 | InvalidPinFormatException | PIN must be 5 digits |
| TC45 | 100000 | 40 | InvalidPinFormatException | PIN must be 5 digits |
| TC46 | 100000 | 500 | InvalidPinFormatException | PIN must be 5 digits |
| TC47 | 100000 | 980 | InvalidPinFormatException | PIN must be 5 digits |
| TC48 | 100000 | 1,000 | InvalidPinFormatException | PIN must be 5 digits |
| TC49 | 100000 | 1,020 | InvalidPinFormatException | PIN must be 5 digits |

### Test results

From the CI run on PR #6 (49 run, 42 failed, 7 passed).

| ID | Result |
|---|---|
| TC01 | Fail |
| TC02 | Fail |
| TC03 | Fail |
| TC04 | Fail |
| TC05 | Fail |
| TC06 | Fail |
| TC07 | Fail |
| TC08 | Fail |
| TC09 | Fail |
| TC10 | Fail |
| TC11 | Fail |
| TC12 | Fail |
| TC13 | Fail |
| TC14 | Fail |
| TC15 | Fail |
| TC16 | Fail |
| TC17 | Fail |
| TC18 | Fail |
| TC19 | Fail |
| TC20 | Fail |
| TC21 | Fail |
| TC22 | Fail |
| TC23 | Fail |
| TC24 | Fail |
| TC25 | Fail |
| TC26 | Fail |
| TC27 | Fail |
| TC28 | Fail |
| TC29 | Fail |
| TC30 | Fail |
| TC31 | Fail |
| TC32 | Fail |
| TC33 | Fail |
| TC34 | Fail |
| TC35 | Fail |
| TC36 | Fail |
| TC37 | Fail |
| TC38 | Fail |
| TC39 | Fail |
| TC40 | Fail |
| TC41 | Fail |
| TC42 | Fail |
| TC43 | Pass |
| TC44 | Pass |
| TC45 | Pass |
| TC46 | Pass |
| TC47 | Pass |
| TC48 | Pass |
| TC49 | Pass |

### Failure log

Times are rough, in minutes. All failures were found in the same test run, so "time testing before found" is the same for each: the total time spent testing up to that run (reading the spec, choosing the BVA values, writing the 49 tests).

| Failure ID | Exposing tests | Time testing before found | Time diagnosing |
|---|---|---|---|
| F1 | TC08–TC42 (valid 5-digit PIN rejected with `InvalidPinFormatException`) | 90 | 15 |
| F2 | TC01–TC04, TC06, TC07 (invalid 4-digit PIN 0000 accepted) | 90 | 5 |
| F3 | TC05 (valid amount $980 rejected with `InvalidAmountException`) | 90 | 15 |

Faults found by reading the code but hidden by F1 (no test fails on them yet):

- `FormatChecker.checkPinFormat`, line 35: `pin[i] >= '9'` rejects the digit 9. Should show up in TC29–TC42 once F1 is fixed.
- `CashValidator.validateWithdrawal` accepts $0. Should show up in TC08, TC15, TC22, TC29 and TC36.
- `Session.setAmount` has no $1,000 daily-limit check. Should show up in TC14, TC21, TC28, TC35 and TC42.
