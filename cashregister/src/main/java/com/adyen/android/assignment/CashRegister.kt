package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.MonetaryElement

/**
 * The CashRegister class holds the logic for performing transactions.
 *
 * @param change The change that the CashRegister is holding.
 */
class CashRegister(private val change: Change) {
    /**
     * Performs a transaction for a product/products with a certain price and a given amount.
     *
     * @param price The price of the product(s).
     * @param amountPaid The amount paid by the shopper.
     *
     * @return The change for the transaction.
     *
     * @throws TransactionException If the transaction cannot be performed.
     */
    fun performTransaction(price: Long, amountPaid: Change): Change {
        // TODO: Implement logic.
        var total = 0
        amountPaid.getElements().forEach {
            val count = amountPaid.getCount(it)
            total += it.minorValue * count
        }
        System.out.println(total)
        System.out.println(price)
        var returnChange=Change()
        val balance = amountPaid.total - price
        System.out.println(balance)
        val abc = Change.max().getElements()
        System.out.println(abc)
        abc.forEach {
           /* if(balance.toInt()==it){
                returnChange = change.add(it,1)
            }*/
        }
        System.out.println(returnChange.total)
        return Change.max()
    }

    class TransactionException(message: String, cause: Throwable? = null) : Exception(message, cause)
}
