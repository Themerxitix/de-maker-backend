package nl.demaker.demaker.service.impl;

import lombok.RequiredArgsConstructor;
import nl.demaker.demaker.dto.request.CarRequest;
import nl.demaker.demaker.dto.response.CarResponse;
import nl.demaker.demaker.exception.ResourceNotFoundException;
import nl.demaker.demaker.model.Car;
import nl.demaker.demaker.model.Customer;
import nl.demaker.demaker.repository.CarRepository;
import nl.demaker.demaker.repository.CustomerRepository;
import nl.demaker.demaker.service.CarService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    @Override
    public CarResponse createCar(CarRequest request) {
        // Check if customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        // Convert request to entity
        Car car = new Car();
        car.setLicensePlate(request.getLicensePlate());
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setCustomer(customer);

        // Save and return response
        Car savedCar = carRepository.save(car);
        return convertToResponse(savedCar);
    }

    @Override
    public CarResponse getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
        return convertToResponse(car);
    }

    @Override
    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CarResponse updateCar(Long id, CarRequest request) {
        // Find existing car
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));

        // Check if customer exists (if customer is being changed)
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        // Update fields
        car.setLicensePlate(request.getLicensePlate());
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setCustomer(customer);

        // Save and return response
        Car updatedCar = carRepository.save(car);
        return convertToResponse(updatedCar);
    }

    @Override
    public void deleteCar(Long id) {
        // Check if exists
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car", "id", id);
        }
        carRepository.deleteById(id);
    }

    // Helper method to convert entity to response
    private CarResponse convertToResponse(Car car) {
        CarResponse response = new CarResponse();
        response.setId(car.getId());
        response.setLicensePlate(car.getLicensePlate());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setYear(car.getYear());
        response.setCustomerId(car.getCustomer().getId());
        response.setCustomerName(car.getCustomer().getFirstName() + " " + car.getCustomer().getLastName());
        return response;
    }
}
