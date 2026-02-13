package com.expensesplitter.expense.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExpenseSplitHelper {

    public static BigDecimal calculateEqualSplit(BigDecimal total, int count) {
        return total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}
