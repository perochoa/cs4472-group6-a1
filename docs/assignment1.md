## Transfer fees: Decision Table Analysis

**Target:** 'bank.utils.FeesCalculator#calculateTranferFee(double amount, double fromAccountBalance, double toAccountBalance, boolean student)'

**Technique:** Decision Table Analysis

**Spec:** Assignment 1, "Transfer"

**JUnit class:** 'src/test/java/bank/utils/FeesCalculatorTransferTest.java'

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

### Section 2: Test Cases

|ID|_student_|_amount_|*fromAccountBalance*|*toAccountBalance*|_expectedFee_|Spec rule|
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