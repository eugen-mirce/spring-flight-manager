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
            throw new UserServiceException("User With This Email Already Exists.");

        if(userDTO.getFirstName() == null || userDTO.getFirstName().isEmpty() )
            throw new UserServiceException("First name can not be empty.");
        if(userDTO.getLastName() == null || userDTO.getLastName().isEmpty() )
            throw new UserServiceException("Last name can not be empty.");
        if(userDTO.getEmail() == null || userDTO.getEmail().isEmpty() )
            throw new UserServiceException("Email can not be empty.");
        if(userDTO.getPassword() == null || userDTO.getPassword().isEmpty() )
            throw new UserServiceException("Password can not be empty.");

        UserEntity userEntity = modelMapper.map(userDTO,UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        RoleEntity userRole = roleRepository.findByName("ROLE_USER");
        userEntity.setRoles(Collections.singletonList(userRole));

        userEntity = userRepository.save(userEntity);

        return modelMapper.map(userEntity,UserDTO.class);
    }

    @Override
    public UserDTO updateUser(int id, UserDTO userDTO) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));

        if(userDTO.getFirstName() != null)
            userEntity.setFirstName(userDTO.getFirstName());

        if(userDTO.getLastName() != null)
            userEntity.setLastName(userDTO.getLastName());

        userRepository.save(userEntity);

        return modelMapper.map(userEntity,UserDTO.class);
    }

    @Override
    public void deleteUser(int id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));
        userRepository.delete(userEntity);
    }

    @Override
    public UserDTO getUser(int id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));

        UserDTO returnValue = modelMapper.map(userEntity,UserDTO.class);
        Collection<String> roles = new HashSet<>();
        for(RoleEntity roleEntity : userEntity.getRoles()) {
            roles.add(roleEntity.getName());
        }
        returnValue.setRoles(roles);
        return returnValue;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null)
            throw new UserServiceException("User Not Found.");

        UserDTO returnValue = modelMapper.map(userEntity,UserDTO.class);
        Collection<String> roles = new HashSet<>();
        for(RoleEntity roleEntity : userEntity.getRoles()) {
            roles.add(roleEntity.getName());
        }
        returnValue.setRoles(roles);
        return returnValue;
    }

    @Override
    public Page<UserDTO> getUsers(int page, int limit) {
        if(page > 0) page--;    //Page starts with index 0
        Pageable pageableRequest = PageRequest.of(page,limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableRequest);
        Type listType = new TypeToken<Page<UserDTO>>(){}.getType();
        return modelMapper.map(usersPage,listType);
    }

    @Override
    public TripDTO createTrip(int userId, TripDTO trip) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));

        if(trip.getReason() == null || trip.getReason().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getDescription() == null || trip.getDescription().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getFrom() == null || trip.getFrom().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getTo() == null || trip.getTo().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getDepartureDate() == null)
            throw new TripServiceException("Missing field.");
        if(trip.getArrivalDate() == null)
            throw new TripServiceException("Missing field.");
        if(trip.getDepartureDate().before(new Date()))
            throw new TripServiceException("Departure date should be a date in the future.");
        if(trip.getArrivalDate().before(new Date()))
            throw new TripServiceException("Arrival date should be a date in the future.");

        TripEntity tripEntity = modelMapper.map(trip,TripEntity.class);
        tripEntity.setUser(userEntity);
        tripEntity.setStatus("CREATED");     //Default Value
        tripEntity = tripRepository.save(tripEntity);

        userEntity.addTrip(tripEntity);
        userRepository.save(userEntity);

        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public List<TripDTO> getUserTrips(int userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));

        List<TripEntity> trips = tripRepository.findAllByUser(userEntity);
        Type listType = new TypeToken<List<TripDTO>>(){}.getType();
        return modelMapper.map(trips,listType);
    }

    @Override
    public TripDTO getTrip(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(()->new TripServiceException("Trip Not Found."));

        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public TripDTO updateTrip(int userId, int tripId, TripDTO trip) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(()->new TripServiceException("Trip Not Found."));

        if(trip.getReason() == null || trip.getReason().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getDescription() == null || trip.getDescription().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getFrom() == null || trip.getFrom().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getTo() == null || trip.getTo().isEmpty())
            throw new TripServiceException("Missing field.");
        if(trip.getDepartureDate() == null)
            throw new TripServiceException("Missing field.");
        if(trip.getArrivalDate() == null)
            throw new TripServiceException("Missing field.");
        if(trip.getDepartureDate().before(new Date()))
            throw new TripServiceException("Departure date should be a date in the future.");
        if(trip.getArrivalDate().before(new Date()))
            throw new TripServiceException("Arrival date should be a date in the future.");

        tripEntity.setReason(trip.getReason());
        tripEntity.setDescription(trip.getDescription());
        tripEntity.setFrom(trip.getFrom());
        tripEntity.setTo(trip.getTo());
        tripEntity.setDepartureDate(trip.getDepartureDate());
        tripEntity.setArrivalDate(trip.getArrivalDate());

        tripRepository.save(tripEntity);

        return modelMapper.map(tripEntity,TripDTO.class);
    }

    @Override
    public void requestTripApproval(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(()->new TripServiceException("Trip Not Found."));

        if(tripEntity.getStatus().equalsIgnoreCase("CREATED")) {
            tripEntity.setStatus("PENDING");
            tripRepository.save(tripEntity);
        } else if(tripEntity.getStatus().equalsIgnoreCase("APPROVED")) {
            throw new TripServiceException("Trip Is Already Approved");
        } else if(tripEntity.getStatus().equalsIgnoreCase("PENDING")) {
            throw new TripServiceException("You Have Already Requested To Approve");
        } else {
            throw new TripServiceException("Trip Is Denied");
        }
    }

    @Override
    public void deleteTrip(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Doesn't Exist."));
        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(()->new TripServiceException("Trip Not Found."));

        tripRepository.delete(tripEntity);
    }

    @Override
    public FlightDTO createFlight(int userId, int tripId, FlightDTO flight) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Not Found."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        //Check if trip is approved
        if(!tripEntity.getStatus().equalsIgnoreCase("APPROVED"))
            throw new TripServiceException("Trip is not approved");

        if(flight.getFrom() == null || flight.getFrom().isEmpty())
            throw new FlightServiceException("Missing field.");
        if(flight.getTo() == null || flight.getTo().isEmpty())
            throw new FlightServiceException("Missing field.");
        if(flight.getDepartureDate() == null)
            throw new FlightServiceException("Missing field.");
        if(flight.getArrivalDate() == null)
            throw new FlightServiceException("Missing field.");
        if(flight.getDepartureDate().before(new Date()))
            throw new FlightServiceException("Departure date should be a date in the future.");
        if(flight.getArrivalDate().before(new Date()))
            throw new FlightServiceException("Arrival date should be a date in the future.");


        FlightEntity flightEntity = modelMapper.map(flight,FlightEntity.class);
        flightEntity.setTrip(tripEntity);
        flightEntity = flightRepository.save(flightEntity);

        tripEntity.addFlight(flightEntity);
        tripRepository.save(tripEntity);

        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public List<FlightDTO> getFlights(int userId, int tripId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Not Found."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        List<FlightEntity> flights = flightRepository.findAllByTrip(tripEntity);
        Type listType = new TypeToken<List<FlightDTO>>(){}.getType();

        return modelMapper.map(flights,listType);
    }

    @Override
    public FlightDTO getFlight(int userId, int tripId, int flightId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserServiceException("User Not Found."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        FlightEntity flightEntity = flightRepository.findByIdAndTrip(flightId,tripEntity)
                .orElseThrow(() -> new FlightServiceException("Flight Not Found."));

        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public FlightDTO updateFlight(int userId, int tripId, int flightId, FlightDTO flight) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserServiceException("User Not Found."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        FlightEntity flightEntity = flightRepository.findByIdAndTrip(flightId,tripEntity)
                .orElseThrow(() -> new FlightServiceException("Flight Not Found."));

        if(flight.getFrom() == null || flight.getFrom().isEmpty())
            throw new FlightServiceException("Missing field.");
        if(flight.getTo() == null || flight.getTo().isEmpty())
            throw new FlightServiceException("Missing field.");
        if(flight.getDepartureDate() == null)
            throw new FlightServiceException("Missing field.");
        if(flight.getArrivalDate() == null)
            throw new FlightServiceException("Missing field.");
        if(flight.getDepartureDate().before(new Date()))
            throw new FlightServiceException("Departure date should be a date in the future.");
        if(flight.getArrivalDate().before(new Date()))
            throw new FlightServiceException("Arrival date should be a date in the future.");

        flightEntity.setFrom(flight.getFrom());
        flightEntity.setTo(flight.getTo());
        flightEntity.setDepartureDate(flight.getDepartureDate());
        flightEntity.setDepartureDate(flight.getDepartureDate());
        flightRepository.save(flightEntity);

        return modelMapper.map(flightEntity,FlightDTO.class);
    }

    @Override
    public void deleteFlight(int userId, int tripId, int flightId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserServiceException("User Not Found."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        FlightEntity flightEntity = flightRepository.findByIdAndTrip(flightId,tripEntity)
                .orElseThrow(() -> new FlightServiceException("Flight Not Found."));

        flightRepository.delete(flightEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) throw new UsernameNotFoundException(email);

        return new UserPrincipal(userEntity);
    }
}
