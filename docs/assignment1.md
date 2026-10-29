## Deposit fees: Weak Robust Equivalence Class Analysis

**Target:** `bank.utils.FeesCalculator#calculateDepositInterest(double amount, double accountBalance, boolean student)`
**Technique:** Weak Robust Equivalence Class Analysis
**Spec:** Assignment 1, "Deposit"
**JUnit class:** `src/test/java/bank/utils/FeesCalculatorDepositTest.java`

### Section 1: Process and assumptions

**Process.** I split each input into equivalence classes using only the Deposit specification. The amount and balance boundaries depend on whether the client is a student, so I defined the classes separately for students and non-students. For the valid part, I picked one representative value from the middle of each class (not on a boundary, since boundaries are covered by BVA, not ECA). I made sure every valid class appears in at least one test and that each of the spec's outcomes (0.5%, 0.25%, 0.8%, 0.4%, 0.1%, no fee) is checked. For the robust part, I added one test per invalid class, where exactly one input is invalid and the others are valid (single fault assumption).

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
3. A negative amount or a negative balance is invalid input. The spec doesn't say what should happen, so I assume the method should throw an `IllegalArgumentException`.
4. A balance of $0 is valid (an empty account can receive a deposit).
5. Expected and actual fees are compared with a tolerance of 0.0001, because `double` arithmetic can produce tiny rounding errors.

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

### Test results

Fill this in after running the tests (locally or from the CI run on the PR).

| ID | Result |
|---|---|
| D1 | |
| D2 | |
| D3 | |
| D4 | |
| D5 | |
| D6 | |
| D7 | |
| D8 | |
| D9 | |
| D10 | |
| D11 | |
| D12 | |
| D13 | |
| D14 | |
