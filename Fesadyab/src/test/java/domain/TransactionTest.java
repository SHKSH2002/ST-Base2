package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testEquals_SameTransactionId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(100);
        txn1.setAmount(500);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(1);
        txn2.setAccountId(200);
        txn2.setAmount(300);
        txn2.setDebit(false);

        assertTrue(txn1.equals(txn2));
    }

    @Test
    void testEquals_DifferentTransactionId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(100);
        txn1.setAmount(500);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAccountId(100);
        txn2.setAmount(500);
        txn2.setDebit(true);

        assertFalse(txn1.equals(txn2));
    }

    @Test
    void testEquals_NullObject() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAccountId(100);
        txn1.setAmount(500);
        txn1.setDebit(true);

        assertFalse(txn1.equals(null));
    }

    @Test
    void testEquals_DifferentClassObject() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);

        String differentObject = "Some String";
        assertFalse(txn.equals(differentObject));
    }

    @Test
    void testGettersAndSetters() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(100);
        txn.setAmount(500);
        txn.setDebit(true);

        assertEquals(1, txn.getTransactionId());
        assertEquals(100, txn.getAccountId());
        assertEquals(500, txn.getAmount());
        assertTrue(txn.isDebit);
    }
}
