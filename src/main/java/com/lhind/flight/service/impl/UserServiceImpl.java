package com.lhind.flight.service.impl;

import com.lhind.flight.exception.TripServiceException;
import com.lhind.flight.exception.UserServiceException;
import com.lhind.flight.model.entity.FlightEntity;
import com.lhind.flight.model.entity.TripEntity;
import com.lhind.flight.model.entity.UserEntity;
import com.lhind.flight.repository.FlightRepository;
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
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final FlightRepository flightRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TripRepository tripRepository, FlightRepository flightRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.flightRepository = flightRepository;
        this.modelMapper = modelMapper;
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
        userEntity.setEncryptedPassword(userDTO.getPassword());             //TODO EncryptPassword
//        userEntity.setRoles()                                               //TODO SetRole User

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
        return modelMapper.map(userEntity,UserDTO.class);
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

        //TODO Exceptions for missing fields

        TripEntity tripEntity = modelMapper.map(trip,TripEntity.class);
        tripEntity.setUser(userEntity);
        tripEntity.setStatus("WAITING FOR APPROVAL");     //Default Value
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
    public FlightDTO createFlight(int userId, int tripId, FlightDTO flight) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new UserServiceException("User Not Found."));

        TripEntity tripEntity = tripRepository.findByIdAndUser(tripId,userEntity)
                .orElseThrow(() -> new TripServiceException("Trip Not Found."));

        //Check if trip is approved
        if(!tripEntity.getStatus().equalsIgnoreCase("APPROVED"))
            throw new TripServiceException("Trip is not approved");

        //TODO Exceptions for missing fields
        FlightEntity flightEntity = modelMapper.map(flight,FlightEntity.class);
        flightEntity.setTrip(tripEntity);
        flightEntity = flightRepository.save(flightEntity);

        tripEntity.addFlight(flightEntity);
        tripRepository.save(tripEntity);

        return modelMapper.map(flightEntity,FlightDTO.class);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity == null) throw new UsernameNotFoundException(email);

        return new UserPrincipal(userEntity);
    }
}
