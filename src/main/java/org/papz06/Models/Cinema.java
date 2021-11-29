package org.papz06.Models;

public class Cinema {
    int id, managerId, available;
    String name, website, phoneNumber, address;


    public Cinema(int id, int managerId, String name, String website, String phoneNumber, String address, int available) {
        this.id = id;
        this.managerId = managerId;
        this.name = name;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.available = available;
    }

    public Cinema() {
    }

    public String toString() {
        return id + " " + managerId + " " + name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
