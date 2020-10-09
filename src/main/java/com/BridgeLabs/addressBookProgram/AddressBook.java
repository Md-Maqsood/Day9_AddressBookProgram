package com.BridgeLabs.addressBookProgram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddressBook implements ManageAddressBook {

	public enum SearchBy {
		CITY, STATE
	}

	private static final Logger logger = LogManager.getLogger(AddressBook.class);
	static Scanner sc = new Scanner(System.in);
	static Map<String, AddressBook> nameToAddressBookMap = new HashMap<String, AddressBook>();
	public String name;
	public List<Contact> contacts;
	public Map<String, Contact> nameToContactMap;
	public Map<String, ArrayList<Contact>> cityToContactsMap;
	public Map<String, ArrayList<Contact>> stateToContactsMap;

	public AddressBook(String name) {
		super();
		this.name = name;
		this.contacts = new LinkedList<Contact>();
		this.nameToContactMap = new LinkedHashMap<String, Contact>();
		this.cityToContactsMap = new HashMap<>();
		this.stateToContactsMap = new HashMap<>();
	}

	public void addContacts() {
		while (true) {
			logger.debug("1.Add next Contact\n2.Exit\nEnter your choice: ");
			int choice1 = Integer.parseInt(sc.nextLine());
			if (choice1 == 1) {
				logger.debug(
						"Enter the fields in order: \nfirst_name\nlastname\naddress\ncity\nstate\nzip\nphone no.\nemail");
				Contact contact = new Contact(sc.nextLine(), sc.nextLine(), sc.nextLine(), sc.nextLine(), sc.nextLine(),
						Integer.parseInt(sc.nextLine()), Long.parseLong(sc.nextLine()), sc.nextLine());
				if (checkDuplicacy(contact)) {
					logger.debug("Same entry already present. Cannot allow duplicate entries in an address book.");
				} else {
					this.contacts.add(contact);
					this.nameToContactMap.put(contact.getFirstName() + " " + contact.getLastName(), contact);
				}
			} else {
				break;
			}
		}

	}

	/**
	 * uc7 Method to check if a duplicate of given contact is already present in
	 * this address book
	 * 
	 * @param contact
	 * @return
	 */
	public boolean checkDuplicacy(Contact contact) {
		Contact possibleDuplicate = nameToContactMap.get(contact.getFirstName() + " " + contact.getLastName());
		if (possibleDuplicate != null) {
			if (possibleDuplicate.equals(contact)) {
				return true;
			}
		}
		return false;
	}

	public void editContact() {
		logger.debug("Enter name of person whose contact details are to be edited: ");
		String name = sc.nextLine();
		logger.debug("Enter the new fields in order: \naddress\ncity\nstate\nzip\nphone no.\nemail");
		try {
			nameToContactMap.get(name).setAddress(sc.nextLine());
			nameToContactMap.get(name).setCity(sc.nextLine());
			nameToContactMap.get(name).setState(sc.nextLine());
			nameToContactMap.get(name).setZip(Integer.parseInt(sc.nextLine()));
			nameToContactMap.get(name).setPhoneNumber(Long.parseLong(sc.nextLine()));
			nameToContactMap.get(name).setEmail(sc.nextLine());
		} catch (NullPointerException e) {
			logger.debug("No contact found with that name.");
		}
	}

	public void deleteContact() {
		logger.debug("Enter the name of Contact person to be deleted: ");
		String name = sc.nextLine();
		try {
			contacts.remove(nameToContactMap.get(name));
			nameToContactMap.remove(name);
		} catch (NullPointerException e) {
			logger.debug("No contact found with that name.");
		}
	}

	public static void addAddressBooks() {
		while (true) {
			logger.debug("1.Add an address book\n2.Exit\nEnter your choice: ");
			int choice = Integer.parseInt(sc.nextLine());
			if (choice == 1) {
				logger.debug("Enter name of the address book");
				String name = sc.nextLine();
				nameToAddressBookMap.put(name, new AddressBook(name));
			} else if (choice == 2) {
				break;
			} else {
				logger.debug("Invalid choice. Try again.");
			}
		}
	}

	/**
	 * uc8 Method to search all contacts in a given city/state in all address books
	 */
	public static void getPersonsByCityOrState() {
		logger.debug("Choose \n1 To search by city\n2 To search by state\nEnter your choice: ");
		SearchBy searchByParameter = (Integer.parseInt(sc.nextLine()) == 1) ? SearchBy.CITY : SearchBy.STATE;
		logger.debug("Enter the name of " + searchByParameter.name() + ": ");
		String cityOrStateName = sc.nextLine();
		for (String addressBookName : nameToAddressBookMap.keySet()) {
			AddressBook addressBook = nameToAddressBookMap.get(addressBookName);
			logger.debug("Persons in the " + searchByParameter.name() + " " + cityOrStateName + " in the address book "
					+ addressBookName + " are: ");
			for (Contact contact : addressBook.contacts) {
				if ((searchByParameter == SearchBy.CITY ? contact.getCity() : contact.getState())
						.equals(cityOrStateName)) {
					logger.debug(contact);
				}
			}
			logger.debug("");
		}
	}

	/**
	 * uc9 Method to map list of contacts to cities and states in this address book
	 */
	public void generateContactsListByCityAndState() {
		this.cityToContactsMap = new HashMap<>();
		this.stateToContactsMap = new HashMap<>();
		for (Contact contact : contacts) {
			String cityName = contact.getCity();
			if (cityToContactsMap.containsKey(cityName)) {
				cityToContactsMap.get(cityName).add(contact);
			} else {
				ArrayList<Contact> cityContactsList = new ArrayList<Contact>();
				cityContactsList.add(contact);
				cityToContactsMap.put(cityName, cityContactsList);
			}
			String stateName = contact.getState();
			if (stateToContactsMap.containsKey(stateName)) {
				stateToContactsMap.get(stateName).add(contact);
			} else {
				ArrayList<Contact> stateContactsList = new ArrayList<Contact>();
				stateContactsList.add(contact);
				stateToContactsMap.put(stateName, stateContactsList);
			}
		}
	}

	/**
	 * uc9 Method to display all contacts in all cities/states in all address books
	 */
	public static void viewPersonsByCityOrState() {
		logger.debug("Choose \n1 To view by city\n2 To view by state\nEnter your choice: ");
		SearchBy viewByParameter = (Integer.parseInt(sc.nextLine()) == 1) ? SearchBy.CITY : SearchBy.STATE;
		for (String addressBookName : nameToAddressBookMap.keySet()) {
			AddressBook addressBook = nameToAddressBookMap.get(addressBookName);
			addressBook.generateContactsListByCityAndState();
			logger.debug("In the address book " + addressBookName);
			logger.debug("");
			for (String cityOrStateName : (viewByParameter == SearchBy.CITY ? addressBook.cityToContactsMap.keySet()
					: addressBook.stateToContactsMap.keySet())) {
				logger.debug(viewByParameter.name() + ": " + cityOrStateName);
				for (Contact contact : (viewByParameter == SearchBy.CITY
						? addressBook.cityToContactsMap.get(cityOrStateName)
						: addressBook.stateToContactsMap.get(cityOrStateName))) {
					logger.debug(contact);
				}
				logger.debug("");
			}
			logger.debug("");
		}
	}

	/**
	 * uc10 Method to get total no. of contacts by city in this address book
	 * 
	 * @return
	 */
	public Map<String, Integer> getCountByCity() {
		Map<String, Integer> contactsCount = new HashMap<String, Integer>();
		for (String cityName : cityToContactsMap.keySet()) {
			contactsCount.put(cityName, cityToContactsMap.get(cityName).size());
		}
		return contactsCount;
	}

	/**
	 * uc10 Method to get total no. of contacts by state in this address book
	 * 
	 * @return
	 */
	public Map<String, Integer> getCountByState() {
		Map<String, Integer> contactsCount = new HashMap<String, Integer>();
		for (String stateName : stateToContactsMap.keySet()) {
			contactsCount.put(stateName, stateToContactsMap.get(stateName).size());
		}
		return contactsCount;
	}

	/**
	 * uc10 Method to display no. of contacts by city and state in all address books
	 */
	public static void displayCountByCityAndState() {
		for (String addressBookName : nameToAddressBookMap.keySet()) {
			AddressBook addressBook = nameToAddressBookMap.get(addressBookName);
			logger.debug("In the address book " + addressBookName);
			logger.debug("");
			Map<String, Integer> countByCity = addressBook.getCountByCity();
			logger.debug("Contact counts by city");
			for (String cityName : countByCity.keySet()) {
				logger.debug(cityName + ": " + countByCity.get(cityName));
			}
			Map<String, Integer> countByState = addressBook.getCountByState();
			logger.debug("\nContact counts by state");
			for (String stateName : countByState.keySet()) {
				logger.debug(stateName + ": " + countByState.get(stateName));
			}
			logger.debug("");
		}
	}

	@Override
	public String toString() {
		return "Address Book " + name + " with " + contacts.size() + (contacts.size() == 1 ? " contact" : " contacts");
	}

	public static void main(String[] args) {
		addAddressBooks();
		do {
			logger.debug("Enter the name of the address book to continue: ");
			AddressBook addressBook = nameToAddressBookMap.get(sc.nextLine());
			if (addressBook == null) {
				logger.debug("No address book found with that name.");
				;
			} else {
				addressBook.addContacts();
				logger.debug(addressBook);
				logger.debug("Before edit:");
				for (Contact contact : addressBook.contacts) {
					logger.debug(contact);
				}
				addressBook.editContact();
				logger.debug("After edit");
				for (Contact contact : addressBook.contacts) {
					logger.debug(contact);
				}
				addressBook.deleteContact();
				logger.debug("After deletion of contact: \n" + addressBook);
				addressBook.generateContactsListByCityAndState();
			}
			logger.debug("Enter 1 to continue with another address book");
		} while (Integer.parseInt(sc.nextLine()) == 1);
		getPersonsByCityOrState();
		viewPersonsByCityOrState();
		displayCountByCityAndState();
		sc.close();
	}

}

interface ManageAddressBook {
	public void addContacts();

	public void editContact();

	public void deleteContact();
}

class Contact {
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String state;
	private int zip;
	private long phoneNumber;
	private String email;

	public Contact(String firstName, String lastName, String address, String city, String state, int zip,
			long phoneNumber, String email) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		this.zip = zip;
	}

	public long getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Contact: " + firstName + " " + lastName + ", " + address + ", " + city + ", " + state + ", " + zip
				+ ", " + phoneNumber + "\n" + email + ".";
	}

	@Override
	public boolean equals(Object obj) {
		Contact checkContact = (Contact) obj;
		return (checkContact.getFirstName().equals(this.firstName))
				&& (checkContact.getLastName().contentEquals(this.lastName));
	}

}
