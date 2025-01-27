package model;

import java.util.ArrayList;
import java.util.List;

// TODO: The WorldMap class manages the representation of the game world, including countries, their connections,
//       and their infection statuses. It initializes the map, handles connections between countries, and provides
//       methods for resetting infection statuses, locking airplane routes, and retrieving country details.

public class WorldMap {
    private List<Country> countries;
    private boolean airplaneRoutesLocked = false;

    public WorldMap() {
        countries = new ArrayList<>();
        initializeMap();
    }

    private void initializeMap() {
        Country norway = new Country("Norway", 1000);
        Country england = new Country("England", 3000);
        Country germany = new Country("Germany", 4000);
        Country poland = new Country("Poland", 3500);
        Country italy = new Country("Italy", 2000);
        Country greece = new Country("Greece", 1500);
        Country spain = new Country("Spain", 2500);
        Country romania = new Country("Romania", 1700);
        Country turkey = new Country("Turkey", 4000);
        Country ukraine = new Country("Ukraine", 2200);
        Country russia = new Country("Russia", 3800);


        addBidirectionalConnection(england, TransportType.TRAIN, norway);
        addBidirectionalConnection(england, TransportType.PLANE, germany);
        addBidirectionalConnection(england, TransportType.BOAT, spain);
        addBidirectionalConnection(spain, TransportType.TRAIN, germany);
        addBidirectionalConnection(spain, TransportType.PLANE, italy);
        addBidirectionalConnection(germany, TransportType.BOAT, norway);
        addBidirectionalConnection(germany, TransportType.BOAT, poland);
        addBidirectionalConnection(germany, TransportType.TRAIN, italy);
        addBidirectionalConnection(poland, TransportType.BOAT, russia);
        addBidirectionalConnection(poland, TransportType.PLANE, ukraine);
        addBidirectionalConnection(poland, TransportType.TRAIN, romania);
        addBidirectionalConnection(russia, TransportType.PLANE, norway);
        addBidirectionalConnection(russia, TransportType.TRAIN, ukraine);
        addBidirectionalConnection(ukraine, TransportType.BOAT, turkey);
        addBidirectionalConnection(turkey, TransportType.PLANE, romania);
        addBidirectionalConnection(turkey, TransportType.BOAT, greece);
        addBidirectionalConnection(greece, TransportType.PLANE, romania);
        addBidirectionalConnection(greece, TransportType.BOAT, italy);


        countries.add(norway);
        countries.add(england);
        countries.add(germany);
        countries.add(poland);
        countries.add(italy);
        countries.add(greece);
        countries.add(spain);
        countries.add(romania);
        countries.add(turkey);
        countries.add(ukraine);
        countries.add(russia);
    }

    private void addBidirectionalConnection(Country country1, TransportType transportType, Country country2) {
        country1.addConnection(transportType, country2.getName());
        country2.addConnection(transportType, country1.getName());
        System.out.println("Connected " + country1.getName() + " <-> " + country2.getName() + " via " + transportType);
    }
    public void resetCountries() {
        for (Country country : countries) {
            country.resetInfectionStatus();
        }
    }

    public boolean areAirplaneRoutesLocked() {
        return airplaneRoutesLocked;
    }

    public void setAirplaneRoutesLocked(boolean locked) {
        this.airplaneRoutesLocked = locked;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public Country getCountryByName(String name) {
        for (Country country : countries) {
            if (country.getName().equalsIgnoreCase(name)) {
                return country;
            }
        }
        return null;
    }
}
