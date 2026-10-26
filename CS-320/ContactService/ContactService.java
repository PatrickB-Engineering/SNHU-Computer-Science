/*
 * ContactService.java
 * CS 320 Module Three Milestone
 * Patrick Byrne
 *
 * Stores contacts in memory keyed by contact ID. Supports add, delete, and
 * updating the four updatable fields. Field validation is left to Contact,
 * so an update with a bad value throws and leaves the contact unchanged.
 */
import java.util.HashMap;
import java.util.Map;

public class ContactService {

    private final Map<String, Contact> contacts = new HashMap<>();

    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact cannot be null");
        }
        if (contacts.containsKey(contact.getContactId())) {
            throw new IllegalArgumentException("Contact ID already exists");
        }
        contacts.put(contact.getContactId(), contact);
    }

    public void deleteContact(String contactId) {
        if (contacts.remove(contactId) == null) {
            throw new IllegalArgumentException("Contact ID not found");
        }
    }

    public void updateFirstName(String contactId, String firstName) {
        findContact(contactId).setFirstName(firstName);
    }

    public void updateLastName(String contactId, String lastName) {
        findContact(contactId).setLastName(lastName);
    }

    public void updatePhone(String contactId, String phone) {
        findContact(contactId).setPhone(phone);
    }

    public void updateAddress(String contactId, String address) {
        findContact(contactId).setAddress(address);
    }

    // Returns null if the ID isn't there, used by the tests to check state
    public Contact getContact(String contactId) {
        return contacts.get(contactId);
    }

    private Contact findContact(String contactId) {
        Contact contact = contacts.get(contactId);
        if (contact == null) {
            throw new IllegalArgumentException("Contact ID not found");
        }
        return contact;
    }
}
