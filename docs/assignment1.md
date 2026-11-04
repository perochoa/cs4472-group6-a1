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
2. A $0 or $1020 withdrawal should throw `InvalidAmountException` because it is an invalid amount. The spec doesn't say what happens when the daily limit is exceeded: the mocked bank returns a failed result for amounts over $1,000, but nothing turns that into an exception, so TC14 only expects `InvalidAmountException` because of this assumption
3. PIN and amount are tested separately (instructor clarification), each with the other variable held at its nominal value
4. The PIN boundaries are about its length (5 digits), not its numeric value, so min− and max+ are the 4-digit PIN 0000 and the 6-digit PIN 100000
5. A valid amount must be payable in $20 and $50 bills, so the amount goes up in $20 steps (the smallest bill) around the boundaries: min− = 0, min+ = 40, max− = 980, max+ = 1,020

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

Times are rough, in minutes.

| Failure ID | Exposing tests | Time testing before found | Time diagnosing |
|---|---|---|---|
| SF1 | TC02–TC06, TC08–TC14 (valid 5-digit PIN rejected with `InvalidPinFormatException`) | 90 | 15 |
| SF2 | TC01 (invalid 4-digit PIN 0000 accepted) | 90 | 5 |

Faults found by reading the code but hidden by SF1 (no test fails on them yet):

- `FormatChecker.checkPinFormat`, line 35: `pin[i] >= '9'` rejects the digit 9. Should show up in TC05 and TC06 once SF1 is fixed.
- `CashValidator.validateWithdrawal` accepts $0. Should show up in TC08.
- `CashValidator.validateWithdrawal` rejects the valid amount $980 (18 × $50 + 4 × $20), because it uses as many $50 bills as possible and then only tries $20 bills on the rest. Should show up in TC12.
- `Session.setAmount` has no $1,000 daily-limit check. Should show up in TC14.

## Deposit fees: Weak Robust Equivalence Class Analysis

**Target:** `bank.utils.FeesCalculator#calculateDepositInterest(double amount, double accountBalance, boolean student)`
**Technique:** Weak Robust Equivalence Class Analysis
**Spec:** Assignment 1, "Deposit"
**JUnit class:** `src/test/java/bank/utils/FeesCalculatorDepositTest.java`

### Section 1: Process and assumptions

**Process.** I split each input into equivalence classes using only the Deposit specification. The amount and balance boundaries depend on whether the client is a student, so I defined the classes separately for students and non-students. For the valid part, I picked one representative value from the middle of each class (not on a boundary, since boundaries are covered by BVA, not ECA). I made sure every valid class appears in at least one test and that each of the spec's outcomes (0.5%, 0.25%, 0.8%, 0.4%, 0.1%, no fee) is checked. For the robust part, I tested each invalid class (A5, A6, B7) once for a student and once for a non-student, since the valid classes for the other inputs differ between the two. In each of these tests exactly one input is invalid and the others are valid (single fault assumption).

**Equivalence classes.**

| Variable | Class | Range | Valid? |
|---|---|---|---|
| student | S1 | true | valid |
| student | S2 | false | valid |
| amount (student) | A1 | 0 < amount ≤ 50 | valid |
| amount (student) | A2 | amount > 50 | valid |
| amount (non-student) | A3 | 0 < amount ≤ 250 | valid |
| amount (non-student) | A4 | amount > 250 | valid |
| amount | A5 | amount < 0 | invalid |
| amount | A6 | amount = 0 | invalid |
| balance (student) | B1 | 0 ≤ balance ≤ 500 | valid |
| balance (student) | B2 | 500 < balance ≤ 5,000 | valid |
| balance (student) | B3 | balance > 5,000 | valid |
| balance (non-student) | B4 | 0 ≤ balance ≤ 2,500 | valid |
| balance (non-student) | B5 | 2,500 < balance ≤ 10,000 | valid |
| balance (non-student) | B6 | balance > 10,000 | valid |
| balance | B7 | balance < 0 | invalid |

**Assumptions.**

1. The method returns the fee in dollars, calculated as rate × amount (for example, 0.5% of $100 = $0.50).
2. The method is named `calculateDepositInterest`, but the spec calls this value a fee. I treat them as the same thing.
3. A deposit amount of $0 or less, or a negative balance, is invalid input (depositing nothing is not a real transaction). $0 does not belong to any valid amount class, so it is its own invalid class (A6). The spec doesn't say what should happen, so I assume the method should throw an `IllegalArgumentException`.
4. A balance of $0 is valid (an empty account can receive a deposit).
5. Expected and actual fees are compared with a tolerance of `1e-6` (0.000001), because `double` arithmetic can produce tiny rounding errors.

### Section 2: Test cases

| ID | Student | Amount | Balance | Classes covered | Expected fee | Spec rule |
|---|---|---|---|---|---|---|
| D1 | true | 100 | 1,000 | S1, A2, B2 | 0.50 | student, amount > 50, balance > 500 → 0.5% |
| D2 | true | 100 | 300 | S1, A2, B1 | 0.25 | student, amount > 50, balance ≤ 500 → 0.25% |
| D3 | true | 20 | 6,000 | S1, A1, B3 | 0.10 | student, amount ≤ 50, balance > 5,000 → 0.5% |
| D4 | true | 20 | 300 | S1, A1, B1 | 0.00 | student, amount ≤ 50, balance ≤ 5,000 → no fee |
| D5 | true | 20 | 2,000 | S1, A1, B2 | 0.00 | student, amount ≤ 50, balance ≤ 5,000 → no fee |
| D6 | false | 500 | 3,000 | S2, A4, B5 | 4.00 | non-student, amount > 250, balance > 2,500 → 0.8% |
| D7 | false | 500 | 1,000 | S2, A4, B4 | 2.00 | non-student, amount > 250, balance ≤ 2,500 → 0.4% |
| D8 | false | 100 | 20,000 | S2, A3, B6 | 0.00 | non-student, amount ≤ 250, balance > 10,000 → no fee |
| D9 | false | 100 | 5,000 | S2, A3, B5 | 0.10 | non-student, amount ≤ 250, balance ≤ 10,000 → 0.1% |
| D10 | false | 100 | 1,000 | S2, A3, B4 | 0.10 | non-student, amount ≤ 250, balance ≤ 10,000 → 0.1% |
| D11 | true | −10 | 1,000 | S1, **A5**, B2 | IllegalArgumentException | Assumption 3 |
| D12 | true | 100 | −1 | S1, A2, **B7** | IllegalArgumentException | Assumption 3 |
| D13 | false | −10 | 1,000 | S2, **A5**, B4 | IllegalArgumentException | Assumption 3 |
| D14 | false | 100 | −1 | S2, A3, **B7** | IllegalArgumentException | Assumption 3 |
| D15 | true | 0 | 1,000 | S1, **A6**, B2 | IllegalArgumentException | Assumption 3 |
| D16 | false | 0 | 1,000 | S2, **A6**, B4 | IllegalArgumentException | Assumption 3 |

### Test results

From the CI run on PR #7 (16 run, 15 failed, 1 passed).

| ID | Result |
|---|---|
| D1 | Fail |
| D2 | Fail |
| D3 | Pass |
| D4 | Fail |
| D5 | Fail |
| D6 | Fail |
| D7 | Fail |
| D8 | Fail |
| D9 | Fail |
| D10 | Fail |
| D11 | Fail |
| D12 | Fail |
| D13 | Fail |
| D14 | Fail |
| D15 | Fail |
| D16 | Fail |

### Failure log

Times are rough, in minutes. All failures appeared in the same first run of the suite, so they share one testing time (deriving the cases and writing the tests). Diagnosis was done one failure at a time after that run.

| Failure ID | Exposing tests | Time testing before found | Time diagnosing |
|---|---|---|---|
| DF1 | D1, D2 (student, amount > $50 treated as ≤ $50: threshold is $100 in code) | 70 | 15 |
| DF2 | D4, D5 (student, amount ≤ $50, balance ≤ $5,000 charged 0.2% instead of no fee) | 70 | 10 |
| DF3 | D6, D7 (non-student, amount > $250 treated as ≤ $250: threshold is $500 in code) | 70 | 10 |
| DF4 | D8 (non-student, amount ≤ $250, balance > $10,000 charged 0.5% instead of no fee) | 70 | 5 |
| DF5 | D9, D10 (non-student, amount ≤ $250, balance ≤ $10,000 charged nothing instead of 0.1%) | 70 | 5 |
| DF6 | D11–D16 (invalid amount or balance accepted, no `IllegalArgumentException`) | 70 | 5 |
