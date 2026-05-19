package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.dto.QuantityInputDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.exception.Exception;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.service.Service;
import com.app.quantitymeasurement.entity.Entity;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quantities")
public class QuantityMeasurementController {

    private final Service service;

    private final QuantityMeasurementRepository repository;

    public QuantityMeasurementController(
            Service service,
            QuantityMeasurementRepository repository
    ) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/compare")
    public ResponseEntity<QuantityDTO> compare(
            @Valid @RequestBody QuantityInputDTO input
    ) {

        QuantityDTO result =
                service.compare(
                        input.getThisQuantityDTO(),
                        input.getThatQuantityDTO()
                );

        return response(result);
    }

    @PostMapping("/convert")
    public ResponseEntity<QuantityDTO> convert(
            @Valid @RequestBody QuantityInputDTO input
    ) {

        String targetUnit =
                input.getTargetUnit() != null
                        ? input.getTargetUnit()
                        : input.getThatQuantityDTO().getUnit();

        QuantityDTO result =
                service.convert(
                        input.getThisQuantityDTO(),
                        targetUnit
                );

        return response(result);
    }

    @PostMapping("/add")
    public ResponseEntity<QuantityDTO> add(
            @Valid @RequestBody QuantityInputDTO input
    ) {

        QuantityDTO result =
                service.add(
                        input.getThisQuantityDTO(),
                        input.getThatQuantityDTO(),
                        resolveTargetUnit(input)
                );

        return response(result);
    }

    @PostMapping("/subtract")
    public ResponseEntity<QuantityDTO> subtract(
            @Valid @RequestBody QuantityInputDTO input
    ) {

        QuantityDTO result =
                service.subtract(
                        input.getThisQuantityDTO(),
                        input.getThatQuantityDTO(),
                        resolveTargetUnit(input)
                );

        return response(result);
    }

    @PostMapping("/divide")
    public ResponseEntity<QuantityDTO> divide(
            @Valid @RequestBody QuantityInputDTO input
    ) {

        QuantityDTO result =
                service.divide(
                        input.getThisQuantityDTO(),
                        input.getThatQuantityDTO()
                );

        return response(result);
    }

    @GetMapping("/history")
    public List<Entity> history() {

        return repository.findAll();
    }

    @GetMapping("/history/operation/{operation}")
    public List<Entity> historyByOperation(
            @PathVariable String operation
    ) {

        return repository.findByOperationType(operation);
    }

    @GetMapping("/history/type/{measurementType}")
    public List<Entity> historyByType(
            @PathVariable String measurementType
    ) {

        return repository.findByMeasurementType(measurementType);
    }

    @GetMapping("/count/{operation}")
    public Map<String, Object> countByOperation(
            @PathVariable String operation
    ) {

        long count =
                repository
                        .countByOperationTypeAndErrorFalse(operation);

        return Map.of(
                "operation", operation,
                "count", count
        );
    }

    private ResponseEntity<QuantityDTO> response(
            QuantityDTO result
    ) {

        if (result.isError()) {

            throw new Exception(result.getErrorMessage());
        }

        return ResponseEntity.ok(result);
    }

    private String resolveTargetUnit(
            QuantityInputDTO input
    ) {

        if (input.getTargetUnit() != null
                && !input.getTargetUnit().isBlank()) {

            return input.getTargetUnit();
        }

        return input.getThisQuantityDTO().getUnit();
    }
}