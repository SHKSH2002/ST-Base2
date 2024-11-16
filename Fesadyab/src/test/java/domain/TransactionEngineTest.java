package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionEngineTest {

    private TransactionEngine transactionEngine;

    @BeforeEach
    void setUp() {
        transactionEngine = new TransactionEngine();
    }

    @Test
    void testGetAverageTransactionAmountByAccount_NoTransactions() {
        int average = transactionEngine.getAverageTransactionAmountByAccount(1);
        assertEquals(0, average);
    }

    @Test
    void testGetAverageTransactionAmountByAccount_WithTransactions() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1);
        txn1.setAmount(200);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1);
        txn2.setAmount(400);
        txn2.setDebit(false);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(2);
        txn3.setAmount(500);
        txn3.setDebit(false);

        transactionEngine.transactionHistory.add(txn1);
        transactionEngine.transactionHistory.add(txn2);
        transactionEngine.transactionHistory.add(txn3);

        int average = transactionEngine.getAverageTransactionAmountByAccount(1);
        assertEquals(300, average);
    }

    @Test
    void testGetTransactionPatternAboveThreshold_NoTransactions() {
        int result = transactionEngine.getTransactionPatternAboveThreshold(500);
        assertEquals(0, result);
    }

    @Test
    void testGetTransactionPatternAboveThreshold_WithTransactions() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1);
        txn1.setAmount(1200);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1);
        txn2.setAmount(1500);
        txn2.setDebit(false);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(1);
        txn3.setAmount(500);
        txn3.setDebit(false);

        transactionEngine.transactionHistory.add(txn1);
        transactionEngine.transactionHistory.add(txn2);
        transactionEngine.transactionHistory.add(txn3);

        int result = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(300, result);
    }

    @Test
    void testGetTransactionPatternAboveThreshold_DifferentPattern() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1);
        txn1.setAmount(1200);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1);
        txn2.setAmount(1500);
        txn2.setDebit(false);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(1);
        txn3.setAmount(1900);
        txn3.setDebit(true);

        Transaction txn4 = new Transaction();
        txn4.setTransactionId(4);
        txn4.setAccountId(1);
        txn4.setAmount(2200);
        txn4.setDebit(true);

        transactionEngine.transactionHistory.add(txn1);
        transactionEngine.transactionHistory.add(txn2);
        transactionEngine.transactionHistory.add(txn3);
        transactionEngine.transactionHistory.add(txn4);

        int result = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, result);
    }

    @Test
    void testGetTransactionPatternAboveThreshold_SamePattern() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1);
        txn1.setAmount(1200);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1);
        txn2.setAmount(1500);
        txn2.setDebit(false);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(1);
        txn3.setAmount(1800);
        txn3.setDebit(true);

        transactionEngine.transactionHistory.add(txn1);
        transactionEngine.transactionHistory.add(txn2);
        transactionEngine.transactionHistory.add(txn3);

        int result = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(300, result);
    }

    @Test
    void testDetectFraudulentTransaction_NoFraud() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(1);
        txn.setAmount(200);
        txn.setDebit(true);

        transactionEngine.transactionHistory.add(txn);
        int fraudScore = transactionEngine.detectFraudulentTransaction(txn);
        assertEquals(0, fraudScore);
    }

    @Test
    void testDetectFraudulentTransaction_FraudDetected() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(1);
        txn1.setAmount(100);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1);
        txn2.setAmount(500);
        txn2.setDebit(true);

        transactionEngine.transactionHistory.add(txn1);

        int fraudScore = transactionEngine.detectFraudulentTransaction(txn2);
        assertTrue(fraudScore > 0);
    }

    @Test
    void testAddTransactionAndDetectFraud_ExistingTransaction() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(1);
        txn.setAmount(300);
        txn.setDebit(true);

        transactionEngine.transactionHistory.add(txn);
        int fraudScore = transactionEngine.addTransactionAndDetectFraud(txn);
        assertEquals(0, fraudScore);
    }

    @Test
    void testAddTransactionAndDetectFraud_NewTransaction() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(100);
        txn.setAmount(1200);
        txn.setDebit(true);

        int result = transactionEngine.addTransactionAndDetectFraud(txn);
        assertEquals(1200, result);
    }

    @Test
    void testAddTransactionAndDetectFraud_DifferentDiff() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(1);
        txn.setAmount(200);
        txn.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(1);
        txn2.setAmount(400);
        txn2.setDebit(true);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAccountId(1);
        txn3.setAmount(1800);
        txn3.setDebit(false);

        transactionEngine.transactionHistory.add(txn);
        transactionEngine.transactionHistory.add(txn2);

        int result = transactionEngine.addTransactionAndDetectFraud(txn3);
        assertEquals(0, result);
    }

    @Test
    void testGetTransactionPatternAboveThreshold_SingleTransaction() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(100);
        txn.setAmount(1500);
        txn.setDebit(false);

        transactionEngine.transactionHistory.add(txn);
        int result = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, result);
    }

    @Test
    void testGetTransactionPatternAboveThreshold_PatternDetected() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(100);
        txn1.setAmount(500);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(100);
        txn2.setAmount(1500);
        txn2.setDebit(true);

        transactionEngine.transactionHistory.add(txn1);
        transactionEngine.transactionHistory.add(txn2);

        int result = transactionEngine.getTransactionPatternAboveThreshold(1000);
        assertEquals(1000, result);
    }
}
