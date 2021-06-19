package com.lhind.flight.model.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "trips")
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "reason", nullable = false, length = 25)
    private String reason;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "from", nullable = false, length = 25)
    private String from;

    @Column(name = "to", nullable = false, length = 25)
    private String to;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "departure_date", nullable = false)
    private Date departureDate;

    @Temporal(value = TemporalType.DATE)
    @Column(name = "arrival_date", nullable = false)
    private Date arrivalDate;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity user;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<FlightEntity> flights;

    @Column(name = "status", nullable = false)
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<FlightEntity> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightEntity> flights) {
        this.flights = flights;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addFlight(FlightEntity flightEntity) {
        if(flights == null) flights = new ArrayList<>();
        flights.add(flightEntity);
    }
}
