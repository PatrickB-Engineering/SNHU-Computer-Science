/*
 * Contact.java
 * CS 320 Module Three Milestone
 * Patrick Byrne
 *
 * Holds a single contact. Every field is validated on the way in, both in
 * the constructor and in the setters, so a Contact can never exist in an
 * invalid state. The contact ID is final and has no setter, so it can't be
 * changed after the object is built.
 */
public class Contact {

    private static final int MAX_ID_LENGTH = 10;
    private static final int MAX_NAME_LENGTH = 10;
    private static final int PHONE_LENGTH = 10;
    private static final int MAX_ADDRESS_LENGTH = 30;

    private final String contactId;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    public Contact(String contactId, String firstName, String lastName,
                   String phone, String address) {
        if (contactId == null || contactId.length() > MAX_ID_LENGTH) {
            throw new IllegalArgumentException("Invalid contact ID");
        }
        this.contactId = contactId;

        // Reuse the setters so the rules only live in one place
        setFirstName(firstName);
        setLastName(lastName);
        setPhone(phone);
        setAddress(address);
    }

    public String getContactId() {
        return contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Invalid first name");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Invalid last name");
        }
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        // Exactly 10 characters, all digits
        if (phone == null || phone.length() != PHONE_LENGTH || !phone.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        this.phone = phone;
    }

    public void setAddress(String address) {
        if (address == null || address.length() > MAX_ADDRESS_LENGTH) {
            throw new IllegalArgumentException("Invalid address");
        }
        this.address = address;
    }
}
