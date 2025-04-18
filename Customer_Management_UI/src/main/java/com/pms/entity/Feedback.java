package com.pms.entity;

public class Feedback {
    private Long id;
    private Scheme scheme;
    private Customer customer;
    private int rating;
    private String comments;

    // Default constructor
    public Feedback() {}

    // Parameterized constructor
    public Feedback(Scheme scheme, Customer customer, int rating, String comments) {
        this.scheme = scheme;
        this.customer = customer;
        this.rating = rating;
        this.comments = comments;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Scheme getScheme() { return scheme; }
    public void setScheme(Scheme scheme) { this.scheme = scheme; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
