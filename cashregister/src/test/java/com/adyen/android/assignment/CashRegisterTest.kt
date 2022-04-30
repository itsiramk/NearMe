package com.adyen.android.assignment

import com.adyen.android.assignment.money.Bill
import com.adyen.android.assignment.money.Change
import com.adyen.android.assignment.money.Coin
import org.junit.Assert
import org.junit.Assert.fail
import org.junit.Test

class CashRegisterTest {
    @Test
    fun testTransaction() {
        fail("Add tests here.")
    }

    @Test

    fun testTransaction2() {

        val changeActual = Change()
            .add(Coin.FIVE_CENT, 3)
            .add(Coin.TWO_CENT, 1)
            .add(Bill.FIFTY_EURO, 2)
        val priceActual = Change().add(Bill.FIFTY_EURO,1)
        val paidActual = Change()
            .add(Coin.FIVE_CENT, 1)
            .add(Bill.FIFTY_EURO, 2)
       val cashRegisterActual = CashRegister(changeActual)
        System.out.println(cashRegisterActual.performTransaction(priceActual.total,paidActual))
      //  Assert.assertEquals(expected, cashRegisterActual)
    }
    @Test
    fun testTransaction3() {
        val changeActual = Change()
            .add(Bill.FIFTY_EURO, 2)
        val priceActual = Change().add(Coin.FIVE_CENT,2)
        val paidActual = Change()
            .add(Coin.TEN_CENT, 10)
       val cashRegisterActual = CashRegister(changeActual)
        cashRegisterActual.performTransaction(priceActual.total,paidActual)
        System.out.println()
      //  Assert.assertEquals(expected, cashRegisterActual)
    }
}

