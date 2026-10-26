/*
 * ContactServiceTest.java
 * CS 320 Module Three Milestone
 * Patrick Byrne
 *
 * Covers the three service requirements: add with a unique ID, delete by
 * ID, and update the four updatable fields by ID. A fresh service is built
 * before every test so nothing leaks between them.
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ContactServiceTest {

    private ContactService service;

    @BeforeEach
    void setUp() {
        service = new ContactService();
        service.addContact(new Contact("1001", "Patrick", "Byrne", "8135550123", "123 Main St"));
    }

    // Requirement 1: add contacts with a unique ID

    @Test
    void testAddContact() {
        service.addContact(new Contact("1002", "Jane", "Doe", "7275559876", "456 Oak Ave"));
        assertNotNull(service.getContact("1002"));
        assertEquals("Jane", service.getContact("1002").getFirstName());
    }

    @Test
    void testAddDuplicateIdFails() {
        Contact duplicate = new Contact("1001", "Jane", "Doe", "7275559876", "456 Oak Ave");
        assertThrows(IllegalArgumentException.class, () -> service.addContact(duplicate));
        // Original is still there, untouched
        assertEquals("Patrick", service.getContact("1001").getFirstName());
    }

    @Test
    void testAddNullContactFails() {
        assertThrows(IllegalArgumentException.class, () -> service.addContact(null));
    }

    // Requirement 2: delete contacts per contact ID

    @Test
    void testDeleteContact() {
        service.deleteContact("1001");
        assertNull(service.getContact("1001"));
    }

    @Test
    void testDeleteMissingIdFails() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteContact("9999"));
    }

    @Test
    void testDeletedIdCanBeReused() {
        service.deleteContact("1001");
        service.addContact(new Contact("1001", "Jane", "Doe", "7275559876", "456 Oak Ave"));
        assertEquals("Jane", service.getContact("1001").getFirstName());
    }

    // Requirement 3: update firstName, lastName, phone, address per contact ID

    @Test
    void testUpdateFirstName() {
        service.updateFirstName("1001", "Pat");
        assertEquals("Pat", service.getContact("1001").getFirstName());
    }

    @Test
    void testUpdateLastName() {
        service.updateLastName("1001", "Smith");
        assertEquals("Smith", service.getContact("1001").getLastName());
    }

    @Test
    void testUpdatePhone() {
        service.updatePhone("1001", "7275559876");
        assertEquals("7275559876", service.getContact("1001").getPhone());
    }

    @Test
    void testUpdateAddress() {
        service.updateAddress("1001", "456 Oak Ave");
        assertEquals("456 Oak Ave", service.getContact("1001").getAddress());
    }

    @Test
    void testUpdateMissingIdFails() {
        assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("9999", "Pat"));
        assertThrows(IllegalArgumentException.class, () -> service.updateLastName("9999", "Smith"));
        assertThrows(IllegalArgumentException.class, () -> service.updatePhone("9999", "7275559876"));
        assertThrows(IllegalArgumentException.class, () -> service.updateAddress("9999", "456 Oak Ave"));
    }

    @Test
    void testUpdateInvalidValueFailsAndKeepsOriginal() {
        assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("1001", null));
        assertThrows(IllegalArgumentException.class, () -> service.updateLastName("1001", "Byrneworths"));
        assertThrows(IllegalArgumentException.class, () -> service.updatePhone("1001", "123"));
        assertThrows(IllegalArgumentException.class,
            () -> service.updateAddress("1001", "1234567890123456789012345678901"));

        Contact contact = service.getContact("1001");
        assertEquals("Patrick", contact.getFirstName());
        assertEquals("Byrne", contact.getLastName());
        assertEquals("8135550123", contact.getPhone());
        assertEquals("123 Main St", contact.getAddress());
    }
}
