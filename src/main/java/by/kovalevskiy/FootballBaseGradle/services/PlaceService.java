package by.kovalevskiy.FootballBaseGradle.services;

import by.kovalevskiy.FootballBaseGradle.exception.PlaceAlreadyExistsException;
import by.kovalevskiy.FootballBaseGradle.model.Place;
import by.kovalevskiy.FootballBaseGradle.repositories.PlaceRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final JdbcTemplate jdbcTemplate;

    public PlaceService(PlaceRepository placeRepository, JdbcTemplate jdbcTemplate) {
        this.placeRepository = placeRepository;
        this.jdbcTemplate = jdbcTemplate;
    }
    public Place showPlace (int id){
        Optional<Place> place = placeRepository.findById(id);
        return place.orElse(null);
    }
    public List<Place> showAllPlaces (){
        return placeRepository.findAll();
    }

    @Transactional
    public void createPlace(Place place) {
        if (!placeRepository.existsByNameAndAddress(place.getName(),place.getAddress())){
            placeRepository.save(place);
        }else {throw new PlaceAlreadyExistsException("Такое место проведения уже существует");
        }
    }
    @Transactional
    public void updatePlace(Place place) {
        boolean alreadyExists = placeRepository.existsByNameAndAddressAndIdNot(place.getName(), place.getAddress(),place.getId());
        if (!alreadyExists) {
            placeRepository.save(place);
        } else {
            throw new PlaceAlreadyExistsException("Такое место проведения уже существует");
        }
    }

    @Transactional
    public void deletePlace(int id) {
        placeRepository.deleteById(id);
    }

}
