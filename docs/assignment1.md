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

## Withdrawal fees: Robust Worst Case Boundary Value Analysis

**Target:** `bank.utils.FeesCalculator#calculateWithdrawalFee(double amount, double accountBalance, boolean student, int dayOfWeek)`
**Technique:** Robust Worst Case Boundary Value Analysis
**Spec:** Assignment 1, "Withdrawal"
**JUnit class:** `src/test/java/bank/utils/FeesCalculatorWithdrawalTest.java`

### Section 1: Process and assumptions

**Process.** I worked only from the Withdrawal specification. For every numeric input I picked the seven robust BVA values: min−, min, min+, nominal, max−, max and max+. Worst case means every value of one input is combined with every value of the others (Cartesian product), instead of holding all but one input at nominal as normal BVA does. Robust means the min− and max+ values outside the valid range are included too, and those cases expect the input to be rejected.

`student` is a boolean, so it has no boundaries. Instead I split the input space into two partitions, one per value, because the spec gives each a different rule and a different input that matters:

- **Student** (`student = true`): the fee depends only on the day of the week. Worst case over amount × day of week = 7 × 7 = **49 cases** (W1–W49). Balance doesn't affect a student's fee, so it's held at its nominal value ($3,000).
- **Non-student** (`student = false`): the fee depends only on the balance. Worst case over amount × balance = 7 × 12 = **84 cases** (W50–W133). The day doesn't affect a non-student's fee, so it's held at its nominal value (Wednesday).

The balance has two internal boundaries ($1,000 and $5,000) where the fee changes. A single min/max range would skip them, so I treated balance as three sub-ranges and took the boundary values of each: just below, on and just above $1,000 and $5,000, plus a nominal value inside each range. That gives 12 balance values instead of 7.

Finally, **3 cross-check cases** (W134–W136) cover the invalid values of the input that's held constant in each partition: a negative balance for a student, and an invalid day for a non-student.

**Input values.**

| Variable | min− | min | min+ | nominal | max− | max | max+ |
|---|---|---|---|---|---|---|---|
| amount ($) | 0 | 1 | 2 | 500 | 999 | 1,000 | 1,001 |
| day of week (`Calendar`) | 0 | 1 (Sun) | 2 (Mon) | 4 (Wed) | 6 (Fri) | 7 (Sat) | 8 |

| Balance sub-range | Values ($) |
|---|---|
| invalid (min−) | −0.01 |
| 0 ≤ balance < 1,000 (0.3%) | 0, 0.01, 500, 999.99 |
| 1,000 ≤ balance < 5,000 (0.1%) | 1,000, 1,000.01, 3,000, 4,999.99 |
| balance ≥ 5,000 (no fee) | 5,000, 5,000.01, 1,000,000 |

**Assumptions.**

1. The method returns the fee in dollars, calculated as rate × amount (for example, 0.1% of $500 = $0.50).
2. Amounts are whole dollars, because an ATM dispenses bills, so the step for amount is $1. The smallest valid withdrawal is $1. The largest is $1,000, the daily limit the assignment sets for the test account ("The Testing Targets"); a withdrawal above it can't happen, so the fee calculator should reject it.
3. Balances can hold cents, so the step for balance is $0.01. A balance of $0 is valid. The spec gives no upper bound for the balance, so there's no max+; $1,000,000 stands in for a large balance.
4. The day of the week uses the `java.util.Calendar` constants, as the parameter is an `int`: 1 = Sunday, 2 = Monday, …, 7 = Saturday. Saturday and Sunday are the weekend; Monday to Friday are weekdays. The robust values 0 and 8 aren't days. The BVA values happen to cover both weekend days (1, 7) and both ends of the week (2, 6).
5. Invalid input (amount ≤ $0 or > $1,000, a negative balance, or a day outside 1–7) should throw an `IllegalArgumentException`. The spec doesn't say what should happen, and the method returns a fee, so an exception is the only clean way to reject the input; returning 0 would hide the error. This matches the deposit tests.
6. A case with several invalid inputs at once (for example amount 0 and day 0) still expects one `IllegalArgumentException`.
7. Expected and actual fees are compared with a tolerance of 0.0001, because `double` arithmetic can produce tiny rounding errors. The smallest fee difference that matters here is 0.1% of $1 = $0.001, ten times the tolerance.

### Section 2: Test cases

Expected fee in dollars. Student cases use a balance of $3,000; non-student cases use Wednesday (4).

| ID | Student | Amount | Balance | Day | Expected fee | Spec rule |
|---|---|---|---|---|---|---|
| W1 | true | 0 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W2 | true | 0 | 3,000 | 1 Sun | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W3 | true | 0 | 3,000 | 2 Mon | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W4 | true | 0 | 3,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W5 | true | 0 | 3,000 | 6 Fri | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W6 | true | 0 | 3,000 | 7 Sat | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W7 | true | 0 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W8 | true | 1 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W9 | true | 1 | 3,000 | 1 Sun | 0.001 | student, weekend → 0.1% |
| W10 | true | 1 | 3,000 | 2 Mon | 0.000 | student, weekday → no fee |
| W11 | true | 1 | 3,000 | 4 Wed | 0.000 | student, weekday → no fee |
| W12 | true | 1 | 3,000 | 6 Fri | 0.000 | student, weekday → no fee |
| W13 | true | 1 | 3,000 | 7 Sat | 0.001 | student, weekend → 0.1% |
| W14 | true | 1 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W15 | true | 2 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W16 | true | 2 | 3,000 | 1 Sun | 0.002 | student, weekend → 0.1% |
| W17 | true | 2 | 3,000 | 2 Mon | 0.000 | student, weekday → no fee |
| W18 | true | 2 | 3,000 | 4 Wed | 0.000 | student, weekday → no fee |
| W19 | true | 2 | 3,000 | 6 Fri | 0.000 | student, weekday → no fee |
| W20 | true | 2 | 3,000 | 7 Sat | 0.002 | student, weekend → 0.1% |
| W21 | true | 2 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W22 | true | 500 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W23 | true | 500 | 3,000 | 1 Sun | 0.500 | student, weekend → 0.1% |
| W24 | true | 500 | 3,000 | 2 Mon | 0.000 | student, weekday → no fee |
| W25 | true | 500 | 3,000 | 4 Wed | 0.000 | student, weekday → no fee |
| W26 | true | 500 | 3,000 | 6 Fri | 0.000 | student, weekday → no fee |
| W27 | true | 500 | 3,000 | 7 Sat | 0.500 | student, weekend → 0.1% |
| W28 | true | 500 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W29 | true | 999 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W30 | true | 999 | 3,000 | 1 Sun | 0.999 | student, weekend → 0.1% |
| W31 | true | 999 | 3,000 | 2 Mon | 0.000 | student, weekday → no fee |
| W32 | true | 999 | 3,000 | 4 Wed | 0.000 | student, weekday → no fee |
| W33 | true | 999 | 3,000 | 6 Fri | 0.000 | student, weekday → no fee |
| W34 | true | 999 | 3,000 | 7 Sat | 0.999 | student, weekend → 0.1% |
| W35 | true | 999 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W36 | true | 1,000 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W37 | true | 1,000 | 3,000 | 1 Sun | 1.000 | student, weekend → 0.1% |
| W38 | true | 1,000 | 3,000 | 2 Mon | 0.000 | student, weekday → no fee |
| W39 | true | 1,000 | 3,000 | 4 Wed | 0.000 | student, weekday → no fee |
| W40 | true | 1,000 | 3,000 | 6 Fri | 0.000 | student, weekday → no fee |
| W41 | true | 1,000 | 3,000 | 7 Sat | 1.000 | student, weekend → 0.1% |
| W42 | true | 1,000 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W43 | true | 1,001 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W44 | true | 1,001 | 3,000 | 1 Sun | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W45 | true | 1,001 | 3,000 | 2 Mon | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W46 | true | 1,001 | 3,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W47 | true | 1,001 | 3,000 | 6 Fri | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W48 | true | 1,001 | 3,000 | 7 Sat | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W49 | true | 1,001 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W50 | false | 0 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W51 | false | 0 | 0 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W52 | false | 0 | 0.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W53 | false | 0 | 500 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W54 | false | 0 | 999.99 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W55 | false | 0 | 1,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W56 | false | 0 | 1,000.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W57 | false | 0 | 3,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W58 | false | 0 | 4,999.99 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W59 | false | 0 | 5,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W60 | false | 0 | 5,000.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W61 | false | 0 | 1,000,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W62 | false | 1 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: negative balance is invalid |
| W63 | false | 1 | 0 | 4 Wed | 0.003 | not student, balance < 1000 → 0.3% |
| W64 | false | 1 | 0.01 | 4 Wed | 0.003 | not student, balance < 1000 → 0.3% |
| W65 | false | 1 | 500 | 4 Wed | 0.003 | not student, balance < 1000 → 0.3% |
| W66 | false | 1 | 999.99 | 4 Wed | 0.003 | not student, balance < 1000 → 0.3% |
| W67 | false | 1 | 1,000 | 4 Wed | 0.001 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W68 | false | 1 | 1,000.01 | 4 Wed | 0.001 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W69 | false | 1 | 3,000 | 4 Wed | 0.001 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W70 | false | 1 | 4,999.99 | 4 Wed | 0.001 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W71 | false | 1 | 5,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W72 | false | 1 | 5,000.01 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W73 | false | 1 | 1,000,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W74 | false | 2 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: negative balance is invalid |
| W75 | false | 2 | 0 | 4 Wed | 0.006 | not student, balance < 1000 → 0.3% |
| W76 | false | 2 | 0.01 | 4 Wed | 0.006 | not student, balance < 1000 → 0.3% |
| W77 | false | 2 | 500 | 4 Wed | 0.006 | not student, balance < 1000 → 0.3% |
| W78 | false | 2 | 999.99 | 4 Wed | 0.006 | not student, balance < 1000 → 0.3% |
| W79 | false | 2 | 1,000 | 4 Wed | 0.002 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W80 | false | 2 | 1,000.01 | 4 Wed | 0.002 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W81 | false | 2 | 3,000 | 4 Wed | 0.002 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W82 | false | 2 | 4,999.99 | 4 Wed | 0.002 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W83 | false | 2 | 5,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W84 | false | 2 | 5,000.01 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W85 | false | 2 | 1,000,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W86 | false | 500 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: negative balance is invalid |
| W87 | false | 500 | 0 | 4 Wed | 1.500 | not student, balance < 1000 → 0.3% |
| W88 | false | 500 | 0.01 | 4 Wed | 1.500 | not student, balance < 1000 → 0.3% |
| W89 | false | 500 | 500 | 4 Wed | 1.500 | not student, balance < 1000 → 0.3% |
| W90 | false | 500 | 999.99 | 4 Wed | 1.500 | not student, balance < 1000 → 0.3% |
| W91 | false | 500 | 1,000 | 4 Wed | 0.500 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W92 | false | 500 | 1,000.01 | 4 Wed | 0.500 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W93 | false | 500 | 3,000 | 4 Wed | 0.500 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W94 | false | 500 | 4,999.99 | 4 Wed | 0.500 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W95 | false | 500 | 5,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W96 | false | 500 | 5,000.01 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W97 | false | 500 | 1,000,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W98 | false | 999 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: negative balance is invalid |
| W99 | false | 999 | 0 | 4 Wed | 2.997 | not student, balance < 1000 → 0.3% |
| W100 | false | 999 | 0.01 | 4 Wed | 2.997 | not student, balance < 1000 → 0.3% |
| W101 | false | 999 | 500 | 4 Wed | 2.997 | not student, balance < 1000 → 0.3% |
| W102 | false | 999 | 999.99 | 4 Wed | 2.997 | not student, balance < 1000 → 0.3% |
| W103 | false | 999 | 1,000 | 4 Wed | 0.999 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W104 | false | 999 | 1,000.01 | 4 Wed | 0.999 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W105 | false | 999 | 3,000 | 4 Wed | 0.999 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W106 | false | 999 | 4,999.99 | 4 Wed | 0.999 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W107 | false | 999 | 5,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W108 | false | 999 | 5,000.01 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W109 | false | 999 | 1,000,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W110 | false | 1,000 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: negative balance is invalid |
| W111 | false | 1,000 | 0 | 4 Wed | 3.000 | not student, balance < 1000 → 0.3% |
| W112 | false | 1,000 | 0.01 | 4 Wed | 3.000 | not student, balance < 1000 → 0.3% |
| W113 | false | 1,000 | 500 | 4 Wed | 3.000 | not student, balance < 1000 → 0.3% |
| W114 | false | 1,000 | 999.99 | 4 Wed | 3.000 | not student, balance < 1000 → 0.3% |
| W115 | false | 1,000 | 1,000 | 4 Wed | 1.000 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W116 | false | 1,000 | 1,000.01 | 4 Wed | 1.000 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W117 | false | 1,000 | 3,000 | 4 Wed | 1.000 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W118 | false | 1,000 | 4,999.99 | 4 Wed | 1.000 | not student, 1000 ≤ balance < 5000 → 0.1% |
| W119 | false | 1,000 | 5,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W120 | false | 1,000 | 5,000.01 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W121 | false | 1,000 | 1,000,000 | 4 Wed | 0.000 | not student, balance ≥ 5000 → no fee |
| W122 | false | 1,001 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W123 | false | 1,001 | 0 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W124 | false | 1,001 | 0.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W125 | false | 1,001 | 500 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W126 | false | 1,001 | 999.99 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W127 | false | 1,001 | 1,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W128 | false | 1,001 | 1,000.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W129 | false | 1,001 | 3,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W130 | false | 1,001 | 4,999.99 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W131 | false | 1,001 | 5,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W132 | false | 1,001 | 5,000.01 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W133 | false | 1,001 | 1,000,000 | 4 Wed | IllegalArgumentException | Assumption: amount outside $1-$1,000 is invalid |
| W134 | true | 500 | -0.01 | 4 Wed | IllegalArgumentException | Assumption: negative balance is invalid |
| W135 | false | 500 | 3,000 | 0 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |
| W136 | false | 500 | 3,000 | 8 (invalid) | IllegalArgumentException | Assumption: day outside 1-7 is invalid |

### Test results

From the CI run on PR #10 (136 run, 111 failed, 25 passed).

| ID | Result |
|---|---|
| W1 | Fail |
| W2 | Fail |
| W3 | Fail |
| W4 | Fail |
| W5 | Fail |
| W6 | Fail |
| W7 | Fail |
| W8 | Fail |
| W9 | Fail |
| W10 | Fail |
| W11 | Fail |
| W12 | Fail |
| W13 | Fail |
| W14 | Fail |
| W15 | Fail |
| W16 | Fail |
| W17 | Fail |
| W18 | Fail |
| W19 | Fail |
| W20 | Fail |
| W21 | Fail |
| W22 | Fail |
| W23 | Fail |
| W24 | Fail |
| W25 | Fail |
| W26 | Fail |
| W27 | Fail |
| W28 | Fail |
| W29 | Fail |
| W30 | Fail |
| W31 | Fail |
| W32 | Fail |
| W33 | Fail |
| W34 | Fail |
| W35 | Fail |
| W36 | Fail |
| W37 | Fail |
| W38 | Fail |
| W39 | Fail |
| W40 | Fail |
| W41 | Fail |
| W42 | Fail |
| W43 | Fail |
| W44 | Fail |
| W45 | Fail |
| W46 | Fail |
| W47 | Fail |
| W48 | Fail |
| W49 | Fail |
| W50 | Fail |
| W51 | Fail |
| W52 | Fail |
| W53 | Fail |
| W54 | Fail |
| W55 | Fail |
| W56 | Fail |
| W57 | Fail |
| W58 | Fail |
| W59 | Fail |
| W60 | Fail |
| W61 | Fail |
| W62 | Fail |
| W63 | Fail |
| W64 | Fail |
| W65 | Fail |
| W66 | Fail |
| W67 | Pass |
| W68 | Pass |
| W69 | Pass |
| W70 | Pass |
| W71 | Fail |
| W72 | Fail |
| W73 | Pass |
| W74 | Fail |
| W75 | Fail |
| W76 | Fail |
| W77 | Fail |
| W78 | Fail |
| W79 | Pass |
| W80 | Pass |
| W81 | Pass |
| W82 | Pass |
| W83 | Fail |
| W84 | Fail |
| W85 | Pass |
| W86 | Fail |
| W87 | Fail |
| W88 | Fail |
| W89 | Fail |
| W90 | Fail |
| W91 | Pass |
| W92 | Pass |
| W93 | Pass |
| W94 | Pass |
| W95 | Fail |
| W96 | Fail |
| W97 | Pass |
| W98 | Fail |
| W99 | Fail |
| W100 | Fail |
| W101 | Fail |
| W102 | Fail |
| W103 | Pass |
| W104 | Pass |
| W105 | Pass |
| W106 | Pass |
| W107 | Fail |
| W108 | Fail |
| W109 | Pass |
| W110 | Fail |
| W111 | Fail |
| W112 | Fail |
| W113 | Fail |
| W114 | Fail |
| W115 | Pass |
| W116 | Pass |
| W117 | Pass |
| W118 | Pass |
| W119 | Fail |
| W120 | Fail |
| W121 | Pass |
| W122 | Fail |
| W123 | Fail |
| W124 | Fail |
| W125 | Fail |
| W126 | Fail |
| W127 | Fail |
| W128 | Fail |
| W129 | Fail |
| W130 | Fail |
| W131 | Fail |
| W132 | Fail |
| W133 | Fail |
| W134 | Fail |
| W135 | Fail |
| W136 | Fail |

### Failure log

Times are rough, in minutes.

| Failure ID | Exposing tests | Time testing before found | Time diagnosing |
|---|---|---|---|
| WF1 | W9–W13, W16–W20, W23–W27, W30–W34, W37–W41 (student, valid) | | |
| WF2 | W63–W66, W75–W78, W87–W90, W99–W102, W111–W114 (non-student, balance < $1,000) | | |
| WF3 | W71, W72, W83, W84, W95, W96, W107, W108, W119, W120 (non-student, balance $5,000–$5,000.01) | | |
| WF4 | all 56 invalid-input cases (W1–W8, W14, W15, W21, W22, W28, W29, W35, W36, W42–W62, W74, W86, W98, W110, W122–W136) | | |
