package nl.demaker.demaker.service;

import nl.demaker.demaker.dto.request.CarRequest;
import nl.demaker.demaker.dto.response.CarResponse;

import java.util.List;

public interface CarService {

    CarResponse createCar(CarRequest request);

    CarResponse getCarById(Long id);

    List<CarResponse> getAllCars();

    CarResponse updateCar(Long id, CarRequest request);

    void deleteCar(Long id);
}
