## Transfer fees: Decision Table Analysis

**Target:** `bank.utils.FeesCalculator#calculateTransferFee(double amount, double fromAccountBalance, double toAccountBalance, boolean student)`

**Technique:** Decision Table Analysis

**Spec:** Assignment 1, "Transfer"

**JUnit class:** `src/test/java/bank/utils/FeesCalculatorTransferTest.java`

### Section 1: Process and assumptions

**Process:** Working from the Transfer spec, I made a decision table with 4 binary conditions totalling 16 rules. The boundaries for _amount_, *fromAccountBalance*, and *toAccountBalance* depend on whether the user is a student or not.

**Decision Table**
| Condition | Rule 1 | Rule 2 | Rule 3 | Rule 4 | Rule 5 | Rule 6 | Rule 7 | Rule 8 | Rule 9 | Rule 10 | Rule 11 | Rule 12 | Rule 13 | Rule 14 | Rule 15 | Rule 16 |
| :------ | -------: | -------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: | ------: |
| _student_ | Y | Y | Y | Y | Y | Y | Y | Y | N | N | N | N | N | N | N | N |
| _amount_ | <$200 | <$200 | <$200 | <$200 | >=$200 | >=$200 | >=$200 | >=$200 | <$100 | <$100 | <$100 | <$100 | >=$100 | >=$100 | >=$100 | >=$100 |
| *fromAccountBalance* | <$2000 | <$2000 | >=$2000 | >=$2000 | <$2000 | <$2000 | >=$2000 | >=$2000 | <$4000 | <$4000 | >=$4000 | >=$4000 | <$2000 | <$2000 | >=$2000 | >=$2000 |
| *toAccountBalance* | <$1000 | >=$1000 | <$1000 | >=$1000 | <$1000 | >=$1000 | <$1000 | >=$1000 | <$2000 | >=$2000 | <$2000 | >=$2000 | <$1000 | >=$1000 | <$1000 | >=$1000 |
| _fee_ = 0.025% | | | | X | | X | | | | | | | | | | |
| _fee_ = 0.05% | | X | X | | X | | | | | | | | | | | |
| _fee_ = 0.1% | X | | | | | | | | | X | | | | X | | |
| _fee_ = 0.125% | | | | | | | | X | | | | | | | | |
| _fee_ = 0.2% | | | | | | | | | X | | | | X | | | |
| _fee_ = 0.25% | | | | | | | X | | | | | | | | | X |
| _fee_ = 0.5% | | | | | | | | | | | | X | | | X | |
| _fee_ = 1% | | | | | | | | | | | X | | | | | |

**Assumptions:** 

1. The method returns the fee in dollars, calculated as rate * amount, where rate is a percent.

2. Expected and actual fees are compared with tolerance '1e-6', as double arithmetic may cause rounding errors.

3. DTA is not concerned with validating input so no there are no tests for invalidity.

4. Values around thresholds assume each value is partitioned into two cases, < its threshold or >= to its threshold. As per the spec. 

### Section 2: Test Cases

|ID|_student_|_amount_|*fromAccountBalance*|*toAccountBalance*|Fee Rate (%)|Spec rule|
|------|------|------|------|------|------|------|
|DT1|true|199|1999|999|0.1|student, amount < 200, fromAccountBalance < 2000, toAccountBalance < 1000 → 0.1%|
|DT2|true|199|1999|1001|0.05|student, amount < 200, fromAccountBalance < 2000, toAccountBalance >= 1000 → 0.05%|
|DT3|true|199|2001|999|0.05|student, amount < 200, fromAccountBalance >= 2000, toAccountBalance < 1000 → 0.05%|
|DT4|true|199|2001|1001|0.025|student, amount < 200, fromAccountBalance >= 2000, toAccountBalance >= 1000 → 0.025%|
|DT5|true|201|1999|999|0.05|student, amount >= 200, fromAccountBalance < 2000, toAccountBalance < 1000 → 0.05%|
|DT6|true|201|1999|1001|0.025|student, amount >= 200, fromAccountBalance < 2000, toAccountBalance >= 1000 → 0.025%|
|DT7|true|201|2001|999|0.25|student, amount >= 200, fromAccountBalance >= 2000, toAccountBalance < 1000 → 0.25%|
|DT8|true|201|2001|1001|0.125|student, amount >= 200, fromAccountBalance >= 2000, toAccountBalance >= 1000 → 0.125%|
|DT9|false|99|3999|1999|0.2|non-student, amount < 100, fromAccountBalance < 4000, toAccountBalance < 2000 → 0.2%|
|DT10|false|99|3999|2001|0.1|non-student, amount < 100, fromAccountBalance < 4000, toAccountBalance >= 2000 → 0.1%|
|DT11|false|99|4001|1999|1.0|non-student, amount < 100, fromAccountBalance >= 4000, toAccountBalance < 2000 → 1.0%|
|DT12|false|99|4001|2001|0.5|non-student, amount < 100, fromAccountBalance >= 4000, toAccountBalance >= 2000 → 0.5%|
|DT13|false|101|1999|999|0.2|non-student, amount >= 100, fromAccountBalance < 2000, toAccountBalance < 1000 → 0.2%|
|DT14|false|101|1999|1001|0.1|non-student, amount >= 100, fromAccountBalance < 2000, toAccountBalance >= 1000 → 0.1%|
|DT15|false|101|2001|999|0.5|non-student, amount >= 100, fromAccountBalance >= 2000, toAccountBalance < 1000 → 0.5%|
|DT16|false|101|2001|1001|0.25|non-student, amount >= 100, fromAccountBalance >= 2000, toAccountBalance >= 1000 → 0.25%|

### Test results

From the CI run on PR #11 (16 ran, 16 failed, 0 passed)

|ID|Result|
|--|--|
|DT1|Fail|
|DT2|Fail|
|DT3|Fail|
|DT4|Fail|
|DT5|Fail|
|DT6|Fail|
|DT7|Fail|
|DT8|Fail|
|DT9|Fail|
|DT10|Fail|
|DT11|Fail|
|DT12|Fail|
|DT13|Fail|
|DT14|Fail|
|DT15|Fail|
|DT16|Fail|

### Failure Log

Times are rough, in minutes. All failures were found in the same test run, so "time testing before found" is the same for each: the total time spent testing up to that run (reading the spec, making the DTA table, writing the 16 tests).

|Failure ID|Exposing tests|Time testing before found|Time diagnosing|
|-|-|-|-|
|TF1| DT1-DT4 (student, amount<$200 treated as >=$200)|60|15|
|TF2| DT5-DT6 (student, amount>=$200, fromAccountBalance<$2000 treated as >=2000)|60|5|
|TF3| DT7-DT8, DT12, DT15 (`FeesCalculator.java`, `calculateTransferFee` lines 103, 105, 121, 133, and 135 provide `feePercentage=0.025/0.0125/0.05/0.05/0.055` should be 0.0025, 0.00125, 0.005, 0.005, and 0.0025) |60|5|
|TF4| DT9-DT10 (non-student, amount<$100,  fromAccountBalance<$4000 treated as >=$4000) |60|5|
|TF5| DT11 (non-student, amount<$100, fromAccountBalance>=$4000, toAccountBalance<$2000 treated as >=$2000) |60|5|
|TF6| DT13-DT14 (non-student, amount>=$100, fromAccountBalance<$2000 treated as >=$2000) |60|5|

- Branch conditions on lines 80,81,95,111,112,118,125 are incorrect according to the spec,
- % Values given at lines 83,85,89,91,99,103,105,113,115,119,121,127,129,133,135 are also incorrect according to the spec.