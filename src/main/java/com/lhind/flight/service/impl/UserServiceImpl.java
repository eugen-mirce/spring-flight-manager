package com.lhind.flight.service.impl;

import com.lhind.flight.exception.FlightServiceException;
import com.lhind.flight.exception.TripServiceException;
import com.lhind.flight.exception.UserServiceException;
import com.lhind.flight.model.entity.FlightEntity;
import com.lhind.flight.model.entity.RoleEntity;
import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.model.entity.UserEntity;
import com.lhind.flight.repository.FlightRepository;
import com.lhind.flight.repository.RoleRepository;
import com.lhind.flight.repository.TripRepository;
import com.lhind.flight.repository.UserRepository;
import com.lhind.flight.security.UserPrincipal;
import com.lhind.flight.service.UserService;
import com.lhind.flight.shared.dto.FlightDTO;
import com.lhind.flight.shared.dto.TripDTO;
import com.lhind.flight.shared.dto.UserDTO;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final FlightRepository flightRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    static final Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TripRepository tripRepository, FlightRepository flightRepository, RoleRepository roleRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.flightRepository = flightRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if(userRepository.findByEmail(userDTO.getEmail()) != null)
            logger.error("Service: User With This Email Already Exists.",new UserServiceException("User With This Email Already Exists."));

        if(userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty() )
            logger.error("Service: First name can not be empty.", new UserServiceException("First name can not be empty."));
        if(userDTO.getLastName() == null || userDTO.getLastName().isEmpty() )
            logger.error("Service: Last name can not be empty.", new UserServiceException("Last name can not be empty."));
        if(userDTO.getEmail() == null || userDTO.getEmail().isEmpty() )
            logger.error("Service: Email can not be empty.", new UserServiceException("Email can not be empty."));
        if(userDTO.getPassword() == null || userDTO.getPassword().isEmpty() )
            logger.error("Service: Password can not be empty.", new UserServiceException("Password can not be empty."));

        UserEntity userEntity = modelMapper.map(userDTO,UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        userEntity.setRoles(Collections.singletonList(userRole));

        userEntity = userRepository.save(userEntity);
        logger.info("Service: User Created With ID: "+userEntity.getId());
        return modelMapper.map(userEntity,UserDTO.class);
    }

    @Override
    public UserDTO updateUser(int id, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findById(id);
            logger.error("Service: User Not Found.",new UserServiceException("User Not Found."));

        if(userDTO.getFirstName() != null)
            userEntity.setFirstName(userDTO.getFirstName());

        if(userDTO.getLastName() != null)
            userEntity.setLastName(userDTO.getLastName());

        userRepository.save(userEntity);
        logger.info("Service: Updated User With ID: "+id);

        return modelMapper.map(userEntity,UserDTO.class);
    }

    @Override
    public void deleteUser(int id) {
        UserEntity userEntity = userRepository.findById(id);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));
        userRepository.delete(userEntity);
        logger.info("Service: Deleted User With ID: "+id);
    }

    @Override
    public UserDTO getUser(int id) {
        UserEntity userEntity = userRepository.findById(id);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        UserDTO returnValue = modelMapper.map(userEntity,UserDTO.class);
        Collection<String> roles = new HashSet<>();
        for(RoleEntity roleEntity : userEntity.getRoles()) {
            roles.add(roleEntity.getName());
        }
        returnValue.setRoles(roles);
        logger.info("Service: Fetched User With ID: "+id);
        return returnValue;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        UserDTO returnValue = modelMapper.map(userEntity,UserDTO.class);
        Collection<String> roles = new HashSet<>();
        for(RoleEntity roleEntity : userEntity.getRoles()) {
            roles.add(roleEntity.getName());
        }
        returnValue.setRoles(roles);
        logger.info("Service: Fetched User With Email: "+email);

        return returnValue;
    }

    @Override
    public Page<UserDTO> getUsers(int page, int limit) {
        if(page > 0) page--;    //Page starts with index 0
        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        Type listType = new TypeToken<Page<UserDTO>>(){}.getType();
        logger.info("Service: Fetched Users");
        return modelMapper.map(usersPage,listType);
    }

    @Override
    public TripDTO createTrip(int userId, TripDTO trip) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        if(trip.getReason() == null || trip.getReason().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getDescription() == null || trip.getDescription().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getFrom() == null || trip.getFrom().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getTo() == null || trip.getTo().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getDepartureDate() == null)
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getArrivalDate() == null)
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getDepartureDate().before(new Date()))
            logger.error("Service: Departure date should be a date in the future.", new TripServiceException("Departure date should be a date in the future."));
        if(trip.getArrivalDate().before(new Date()))
            logger.error("Service: Arrival date should be a date in the future.", new TripServiceException("Arrival date should be a date in the future."));

        TripEntity tripEntity = modelMapper.map(trip,TripEntity.class);
        tripEntity.setUser(userEntity);
        tripEntity.setStatus("CREATED");     //Default Value
        tripEntity = tripRepository.save(tripEntity);

        userEntity.addTrip(tripEntity);
        userRepository.save(userEntity);

        logger.info("Service: Created Trip With ID: "+tripEntity.getId());
        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public List<TripDTO> getUserTrips(int userId) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        List<TripEntity> trips = tripRepository.findAllByUser(userEntity);
        Type listType = new TypeToken<List<TripDTO>>(){}.getType();
        logger.info("Service: Fetching Trips For User With ID: "+userId);
        return modelMapper.map(trips,listType);
    }

    @Override
    public Page<TripDTO> getUserTrips(int userId, int page, int limit) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        if(page > 0) page--;
        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<TripEntity> trips = tripRepository.findAllByUser(userEntity,pageableRequest);
        Type listType = new TypeToken<Page<TripDTO>>(){}.getType();
        logger.info("Service: Fetching Trips For User With ID: "+userId);
        return modelMapper.map(trips,listType);
    }

    @Override
    public TripDTO getTrip(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public TripDTO updateTrip(int userId, int tripId, TripDTO trip) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        if(trip.getReason() == null || trip.getReason().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getDescription() == null || trip.getDescription().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getFrom() == null || trip.getFrom().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getTo() == null || trip.getTo().isEmpty())
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getDepartureDate() == null)
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getArrivalDate() == null)
            logger.error("Service: Missing field.", new TripServiceException("Missing field."));
        if(trip.getDepartureDate().before(new Date()))
            logger.error("Service: Departure date should be a date in the future.", new TripServiceException("Departure date should be a date in the future."));
        if(trip.getArrivalDate().before(new Date()))
            logger.error("Service: Arrival date should be a date in the future.", new TripServiceException("Arrival date should be a date in the future."));

        tripEntity.setReason(trip.getReason());
        tripEntity.setDescription(trip.getDescription());
        tripEntity.setFrom(trip.getFrom());
        tripEntity.setTo(trip.getTo());
        tripEntity.setDepartureDate(trip.getDepartureDate());
        tripEntity.setArrivalDate(trip.getArrivalDate());

        tripRepository.save(tripEntity);
        logger.info("Service: Update Trip With ID: "+tripId);

        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public void requestTripApproval(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        if(tripEntity.getStatus().equalsIgnoreCase("CREATED")) {
            tripEntity.setStatus("PENDING");
            tripRepository.save(tripEntity);
            logger.info("Service: Trip Status Was Changed To PENDING");
        }
        else if(tripEntity.getStatus().equalsIgnoreCase("APPROVED"))
            logger.error("Service: Trip Is Already Approved", new TripServiceException("Trip Is Already Approved"));
        else if(tripEntity.getStatus().equalsIgnoreCase("PENDING"))
            logger.error("Service:You Have Already Requested To Approve", new TripServiceException("You Have Already Requested To Approve"));
        else
            logger.error("Service: Trip Is Denied", new TripServiceException("Trip Is Denied"));
    }

    @Override
    public void deleteTrip(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        tripRepository.delete(tripEntity);
        logger.info("Service: Deleted Trip With ID: "+tripId);
    }

    @Override
    public FlightDTO createFlight(int userId, int tripId, FlightDTO flight) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        //Check if trip is approved
        if(!tripEntity.getStatus().equalsIgnoreCase("APPROVED"))
            logger.error("Service: Trip Is Not Approved.", new TripServiceException("Trip is not approved"));

        if(flight.getFrom() == null || flight.getFrom().isEmpty())
            logger.error("Service: Missing field.", new FlightServiceException("Missing field."));
        if(flight.getTo() == null || flight.getTo().isEmpty())
            logger.error("Service: Missing field.", new FlightServiceException("Missing field."));
        if(flight.getDepartureDate() == null)
            logger.error("Service: Missing field.", new FlightServiceException("Missing field."));
        if(flight.getArrivalDate() == null)
            logger.error("Service: Missing field.", new FlightServiceException("Missing field."));
        if(flight.getDepartureDate().before(new Date()))
            logger.error("Service: Departure date should be a date in the future.", new FlightServiceException("Departure date should be a date in the future."));
        if(flight.getArrivalDate().before(new Date()))
            logger.error("Service: Arrival date should be a date in the future.", new FlightServiceException("Arrival date should be a date in the future."));

        FlightEntity flightEntity = modelMapper.map(flight,FlightEntity.class);
        flightEntity.setTrip(tripEntity);
        flightEntity = flightRepository.save(flightEntity);

        tripEntity.addFlight(flightEntity);
        tripRepository.save(tripEntity);
        logger.info("Service: Flight Created For Trip With ID: "+tripId);
        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public List<FlightDTO> getFlights(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        List<FlightEntity> flights = flightRepository.findAllByTrip(tripEntity);
        Type listType = new TypeToken<List<FlightDTO>>(){}.getType();
        logger.info("Service: Fetching Flights For Trip With ID: "+tripId);
        return modelMapper.map(flights,listType);
    }

    @Override
    public Page<FlightDTO> getFlights(int userId, int tripId, int page, int limit) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        if(page > 0) page--;
        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<FlightEntity> flights = flightRepository.findAllByTrip(tripEntity,pageableRequest);
        Type listType = new TypeToken<Page<FlightDTO>>(){}.getType();
        logger.info("Service: Fetching Flights For Trip With ID: "+tripId);
        return modelMapper.map(flights,listType);
    }

    @Override
    public FlightDTO getFlight(int userId, int tripId, int flightId) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        FlightEntity flightEntity = flightRepository.findByIdAndTrip(flightId,tripEntity);
        if(flightEntity == null)
            logger.error("Service: Flight Not Found.",new FlightServiceException("Flight Not Found."));

        logger.info("Service: Fetching Flight With ID: "+flightId);

        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public FlightDTO updateFlight(int userId, int tripId, int flightId, FlightDTO flight) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        FlightEntity flightEntity = flightRepository.findByIdAndTrip(flightId,tripEntity);
        if(flightEntity == null)
            logger.error("Service: Flight Not Found.",new FlightServiceException("Flight Not Found."));

        if(flight.getFrom() == null || flight.getFrom().isEmpty())
            logger.error("Service: Missing field.", new FlightServiceException("Missing Field."));
        if(flight.getTo() == null || flight.getTo().isEmpty())
            logger.error("Service: Missing field.", new FlightServiceException("Missing Field."));
        if(flight.getDepartureDate() == null)
            logger.error("Service: Missing field.", new FlightServiceException("Missing Field."));
        if(flight.getArrivalDate() == null)
            logger.error("Service: Missing field.", new FlightServiceException("Missing Field."));
        if(flight.getDepartureDate().before(new Date()))
            logger.error("Service: Departure date should be a date in the future.", new FlightServiceException("Departure date should be a date in the future."));
        if(flight.getArrivalDate().before(new Date()))
            logger.error("Service: Arrival date should be a date in the future.", new FlightServiceException("Arrival date should be a date in the future."));

        flightEntity.setFrom(flight.getFrom());
        flightEntity.setTo(flight.getTo());
        flightEntity.setDepartureDate(flight.getDepartureDate());
        flightEntity.setDepartureDate(flight.getDepartureDate());
        flightRepository.save(flightEntity);

        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public void deleteFlight(int userId, int tripId, int flightId) {
        UserEntity userEntity = userRepository.findById(userId);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UserServiceException("User Doesn't Exist."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity);
        if(tripEntity == null)
            logger.error("Service: Trip Not Found.",new TripServiceException("Trip Not Found."));

        FlightEntity flightEntity = flightRepository.findByIdAndTrip(flightId,tripEntity);
        if(flightEntity == null)
            logger.error("Service: Flight Not Found.",new FlightServiceException("Flight Not Found."));

        flightRepository.delete(flightEntity);
        logger.info("Service: Deleted Flight With ID: "+flightId);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null)
            logger.error("Service: User Not Found.",new UsernameNotFoundException(email));

        logger.info("Service: Fetching User With Email: "+email);
        return new UserPrincipal(userEntity);
    }
}
