package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/*
* Factory class to get sample list of flights.
*/

class FlightBuilder {
	static List<Flight> createFlights() {
		LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(3);
		return Arrays.asList(
				//A normal flight with two hour duration
				createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2)),
				//A normal multi segment flight
				createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
						threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(5)),
				//A flight departing in the past
				createFlight(threeDaysFromNow.minusDays(6), threeDaysFromNow),
				//A flight that departs before it arrives
				createFlight(threeDaysFromNow, threeDaysFromNow.minusHours(6)),
				//A flight with more than two hours ground time
				createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
						threeDaysFromNow.plusHours(5), threeDaysFromNow.plusHours(6)),
				//Another flight with more than two hours ground time
				createFlight(threeDaysFromNow, threeDaysFromNow.plusHours(2),
						threeDaysFromNow.plusHours(3), threeDaysFromNow.plusHours(4),
						threeDaysFromNow.plusHours(6), threeDaysFromNow.plusHours(7)));
	}

	private static Flight createFlight(final LocalDateTime... dates) {
		if ((dates.length % 2) != 0) {
			throw new IllegalArgumentException("you must pass an even number of dates");
		}
		List<Segment> segments = new ArrayList<>(dates.length / 2);
		for (int i = 0; i < (dates.length - 1); i += 2) {
			segments.add(new Segment(dates[i], dates[i + 1]));
		}
		return new Flight(segments);
	}
}

/*
* Bean that represents a flight.
*/

class Flight {
	private final List<Segment> segments;
	Flight(final List<Segment> segs) {
		segments = segs;
	}
	List<Segment> getSegments() {
		return segments;
	}
	@Override
	public String toString() {
		return segments.stream().map(Object::toString).collect(Collectors.joining(" "));
	}
}

/*
* Bean that represents a flight segment.
*/

class Segment {
	private final LocalDateTime departureDate;
	private final LocalDateTime arrivalDate;
	Segment(final LocalDateTime dep, final LocalDateTime arr) {
		departureDate = Objects.requireNonNull(dep);
		arrivalDate = Objects.requireNonNull(arr);
	}
	LocalDateTime getDepartureDate() {
		return departureDate;
	}
	LocalDateTime getArrivalDate() {
		return arrivalDate;
	}
	@Override
	public String toString() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		return '[' + departureDate.format(fmt) + '|' + arrivalDate.format(fmt) + ']';
	}
}

public class TestClasses {
	//FlightBuilder.createFlights();
	public static void main(String[] args)
	{
		//System.out.println(LocalDateTime.now() + "\n");
		int HoursCounter = 0;
		System.out.println("Изначальный набор данных о перелетах:");
		System.out.println(FlightBuilder.createFlights());
		System.out.println("\n" + "=======================================================");
		
		//First rule: Flight in a past
		System.out.println("Вылет до текущего момента времени:");
		for (int i=0; i<FlightBuilder.createFlights().size(); i++) {
			for (int j=0; j<FlightBuilder.createFlights().get(i).getSegments().size(); j++) {
				if (!(FlightBuilder.createFlights().get(i).getSegments().get(j).getDepartureDate().isBefore(LocalDateTime.now()))) {
					System.out.print(FlightBuilder.createFlights().get(i) + ", ");
					break;
				}
			}
		}
		
		//Second rule: Departs before it arrives
		System.out.println("\n Имеются сегменты с датой прилёта раньше даты вылета:");
		for (int i=0; i<FlightBuilder.createFlights().size(); i++) {
			for (int j=0; j<FlightBuilder.createFlights().get(i).getSegments().size(); j++) {
				if (!(FlightBuilder.createFlights().get(i).getSegments().get(j).getDepartureDate().isAfter(FlightBuilder.createFlights().get(i).getSegments().get(j).getArrivalDate()))) {
					System.out.print(FlightBuilder.createFlights().get(i) + ", ");
					break;
				}
			}
		}
	
		//Third rule: More that 2 hr on the ground
		System.out.println("\n Общее время, проведённое на земле превышает два часа:");
		for (int i=0; i<FlightBuilder.createFlights().size(); i++) {
			for (int j=0; j<FlightBuilder.createFlights().get(i).getSegments().size(); j++) {
				if (FlightBuilder.createFlights().get(i).getSegments().size() == 1) {
					System.out.print(FlightBuilder.createFlights().get(i) + ", ");
				}
				else if (FlightBuilder.createFlights().get(i).getSegments().size() > 1) {
					for (int k=1; k<FlightBuilder.createFlights().get(i).getSegments().size(); k++) {
						HoursCounter+= timeOnGround(FlightBuilder.createFlights().get(i).getSegments().get(k-1).getArrivalDate(), FlightBuilder.createFlights().get(i).getSegments().get(k).getDepartureDate());
					}
					if (HoursCounter <=2) {
						System.out.print(FlightBuilder.createFlights().get(i) + ", ");
						break;
					}
				}
			}
		}
	}
	public static int timeOnGround(LocalDateTime arrival, LocalDateTime departure) {
        return (int) ChronoUnit.HOURS.between(arrival, departure);
    }
}