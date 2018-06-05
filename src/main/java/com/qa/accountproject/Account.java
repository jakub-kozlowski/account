package com.qa.accountproject;

public class Account {

    private final int id;
    private final String accountNumber;
    private final String firstName;
    private final String lastName;

    public Account(int id, String accountNumber, String firstName, String lastName) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
