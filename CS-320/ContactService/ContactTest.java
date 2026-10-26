/*
 * ContactTest.java
 * CS 320 Module Three Milestone
 * Patrick Byrne
 *
 * One section per Contact requirement. Each field gets a null check, a
 * too-long check, and a boundary check right at the limit so an off-by-one
 * in the validation would get caught.
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

public class ContactTest {

    private Contact validContact() {
        return new Contact("1234567890", "Patrick", "Byrne", "8135550123", "123 Main St");
    }

    @Test
    void testValidContactIsCreated() {
        Contact contact = validContact();
        assertEquals("1234567890", contact.getContactId());
        assertEquals("Patrick", contact.getFirstName());
        assertEquals("Byrne", contact.getLastName());
        assertEquals("8135550123", contact.getPhone());
        assertEquals("123 Main St", contact.getAddress());
    }

    // Requirement 1: contact ID, max 10, not null, not updatable

    @Test
    void testContactIdNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact(null, "Patrick", "Byrne", "8135550123", "123 Main St"));
    }

    @Test
    void testContactIdTooLong() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("12345678901", "Patrick", "Byrne", "8135550123", "123 Main St"));
    }

    @Test
    void testContactIdAtMaxLength() {
        Contact contact = new Contact("1234567890", "Patrick", "Byrne", "8135550123", "123 Main St");
        assertEquals(10, contact.getContactId().length());
    }

    @Test
    void testContactIdNotUpdatable() {
        // No setter should exist for the ID
        for (Method method : Contact.class.getMethods()) {
            if (method.getName().equalsIgnoreCase("setContactId")) {
                fail("Contact ID should not have a setter");
            }
        }
        // And the field itself should be final
        try {
            Field field = Contact.class.getDeclaredField("contactId");
            assertEquals(true, Modifier.isFinal(field.getModifiers()));
        } catch (NoSuchFieldException e) {
            fail("contactId field not found");
        }
    }

    // Requirement 2: firstName, max 10, not null

    @Test
    void testFirstNameNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", null, "Byrne", "8135550123", "123 Main St"));
    }

    @Test
    void testFirstNameTooLong() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patricksons", "Byrne", "8135550123", "123 Main St"));
    }

    @Test
    void testFirstNameAtMaxLength() {
        Contact contact = new Contact("1", "Patrickson", "Byrne", "8135550123", "123 Main St");
        assertEquals("Patrickson", contact.getFirstName());
    }

    @Test
    void testSetFirstNameValid() {
        Contact contact = validContact();
        contact.setFirstName("Pat");
        assertEquals("Pat", contact.getFirstName());
    }

    @Test
    void testSetFirstNameInvalid() {
        Contact contact = validContact();
        assertThrows(IllegalArgumentException.class, () -> contact.setFirstName(null));
        assertThrows(IllegalArgumentException.class, () -> contact.setFirstName("Patricksons"));
        assertEquals("Patrick", contact.getFirstName());
    }

    // Requirement 3: lastName, max 10, not null

    @Test
    void testLastNameNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", null, "8135550123", "123 Main St"));
    }

    @Test
    void testLastNameTooLong() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrneworths", "8135550123", "123 Main St"));
    }

    @Test
    void testLastNameAtMaxLength() {
        Contact contact = new Contact("1", "Patrick", "Byrneworth", "8135550123", "123 Main St");
        assertEquals("Byrneworth", contact.getLastName());
    }

    @Test
    void testSetLastNameValid() {
        Contact contact = validContact();
        contact.setLastName("Smith");
        assertEquals("Smith", contact.getLastName());
    }

    @Test
    void testSetLastNameInvalid() {
        Contact contact = validContact();
        assertThrows(IllegalArgumentException.class, () -> contact.setLastName(null));
        assertThrows(IllegalArgumentException.class, () -> contact.setLastName("Byrneworths"));
        assertEquals("Byrne", contact.getLastName());
    }

    // Requirement 4: phone, exactly 10 digits, not null

    @Test
    void testPhoneNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrne", null, "123 Main St"));
    }

    @Test
    void testPhoneTooShort() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrne", "813555012", "123 Main St"));
    }

    @Test
    void testPhoneTooLong() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrne", "81355501234", "123 Main St"));
    }

    @Test
    void testPhoneNotDigits() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrne", "813555012a", "123 Main St"));
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrne", "813-555-01", "123 Main St"));
    }

    @Test
    void testSetPhoneValid() {
        Contact contact = validContact();
        contact.setPhone("7275559876");
        assertEquals("7275559876", contact.getPhone());
    }

    @Test
    void testSetPhoneInvalid() {
        Contact contact = validContact();
        assertThrows(IllegalArgumentException.class, () -> contact.setPhone(null));
        assertThrows(IllegalArgumentException.class, () -> contact.setPhone("123"));
        assertThrows(IllegalArgumentException.class, () -> contact.setPhone("abcdefghij"));
        assertEquals("8135550123", contact.getPhone());
    }

    // Requirement 5: address, max 30, not null

    @Test
    void testAddressNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrne", "8135550123", null));
    }

    @Test
    void testAddressTooLong() {
        assertThrows(IllegalArgumentException.class,
            () -> new Contact("1", "Patrick", "Byrne", "8135550123", "1234567890123456789012345678901"));
    }

    @Test
    void testAddressAtMaxLength() {
        String address = "123456789012345678901234567890";
        Contact contact = new Contact("1", "Patrick", "Byrne", "8135550123", address);
        assertEquals(address, contact.getAddress());
    }

    @Test
    void testSetAddressValid() {
        Contact contact = validContact();
        contact.setAddress("456 Oak Ave");
        assertEquals("456 Oak Ave", contact.getAddress());
    }

    @Test
    void testSetAddressInvalid() {
        Contact contact = validContact();
        assertThrows(IllegalArgumentException.class, () -> contact.setAddress(null));
        assertThrows(IllegalArgumentException.class,
            () -> contact.setAddress("1234567890123456789012345678901"));
        assertEquals("123 Main St", contact.getAddress());
    }
}
