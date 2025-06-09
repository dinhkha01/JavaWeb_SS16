package dk.model.entity.bt1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    private String id;
    private String flightNumber;
    private String departure;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private int availableSeats;
    private int totalSeats;
    private String status;
    private String airline;
    private LocalDateTime createdAt;

    public Trip(String flightNumber, String departure, String destination,
                LocalDateTime departureTime, LocalDateTime arrivalTime,
                BigDecimal price, int availableSeats, int totalSeats,
                String airline) {
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.airline = airline;
        this.status = "ACTIVE";
    }
}