package com.app.quantitymeasurement.serviceImpl;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.entity.Entity;
import com.app.quantitymeasurement.enums.IMeasurable;
import com.app.quantitymeasurement.enumsImpl.LengthUnit;
import com.app.quantitymeasurement.enumsImpl.TemperatureUnit;
import com.app.quantitymeasurement.enumsImpl.VolumeUnit;
import com.app.quantitymeasurement.enumsImpl.WeightUnit;
import com.app.quantitymeasurement.model.Quantity;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.service.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    private static final Logger logger =
            LoggerFactory.getLogger(ServiceImpl.class);

    private final QuantityMeasurementRepository repository;

    // Constructor Injection
    public ServiceImpl(QuantityMeasurementRepository repository) {
        this.repository = repository;
        logger.info("QuantityMeasurementService initialized");
    }

    private IMeasurable getUnit(String unit, String type) {

        logger.debug("Resolving unit {} for type {}", unit, type);

        return switch (type.toUpperCase()) {

            case "LENGTH" -> LengthUnit.valueOf(unit.toUpperCase());

            case "WEIGHT" -> WeightUnit.valueOf(unit.toUpperCase());

            case "VOLUME" -> VolumeUnit.valueOf(unit.toUpperCase());

            case "TEMPERATURE" -> TemperatureUnit.valueOf(unit.toUpperCase());

            default -> throw new IllegalArgumentException("Invalid type");
        };
    }

    @Override
    public QuantityDTO add(QuantityDTO q1,
                           QuantityDTO q2,
                           String targetUnit) {

        try {

            IMeasurable u1 =
                    getUnit(q1.getUnit(), q1.getMeasurementType());

            IMeasurable u2 =
                    getUnit(q2.getUnit(), q2.getMeasurementType());

            Quantity<?> result =
                    new Quantity<>(q1.getValue(), u1)
                            .add(
                                    new Quantity<>(q2.getValue(), u2),
                                    getUnit(targetUnit,
                                            q1.getMeasurementType())
                            );

            Entity entity = new Entity();

            entity.setOperand1Value(q1.getValue());
            entity.setOperand1Unit(q1.getUnit());

            entity.setOperand2Value(q2.getValue());
            entity.setOperand2Unit(q2.getUnit());

            entity.setMeasurementType(q1.getMeasurementType());

            entity.setOperationType("ADD");

            entity.setResultValue(result.getValue());
            entity.setResultUnit(targetUnit);

            repository.save(entity);

            return new QuantityDTO(
                    result.getValue(),
                    targetUnit,
                    q1.getMeasurementType()
            );

        } catch (Exception e) {

            logger.error("ADD operation failed", e);

            return new QuantityDTO(true, e.getMessage());
        }
    }

    @Override
    public QuantityDTO subtract(QuantityDTO q1,
                                QuantityDTO q2,
                                String targetUnit) {

        try {

            IMeasurable u1 =
                    getUnit(q1.getUnit(), q1.getMeasurementType());

            IMeasurable u2 =
                    getUnit(q2.getUnit(), q2.getMeasurementType());

            Quantity<?> result =
                    new Quantity<>(q1.getValue(), u1)
                            .subtract(
                                    new Quantity<>(q2.getValue(), u2),
                                    getUnit(targetUnit,
                                            q1.getMeasurementType())
                            );

            Entity entity = new Entity();

            entity.setOperand1Value(q1.getValue());
            entity.setOperand1Unit(q1.getUnit());

            entity.setOperand2Value(q2.getValue());
            entity.setOperand2Unit(q2.getUnit());

            entity.setMeasurementType(q1.getMeasurementType());

            entity.setOperationType("SUBTRACT");

            entity.setResultValue(result.getValue());
            entity.setResultUnit(targetUnit);

            repository.save(entity);

            return new QuantityDTO(
                    result.getValue(),
                    targetUnit,
                    q1.getMeasurementType()
            );

        } catch (Exception e) {

            logger.error("SUBTRACT operation failed", e);

            return new QuantityDTO(true, e.getMessage());
        }
    }

    @Override
    public QuantityDTO divide(QuantityDTO q1, QuantityDTO q2) {

        try {

            IMeasurable u1 =
                    getUnit(q1.getUnit(), q1.getMeasurementType());

            IMeasurable u2 =
                    getUnit(q2.getUnit(), q2.getMeasurementType());

            double result =
                    new Quantity<>(q1.getValue(), u1)
                            .divide(
                                    new Quantity<>(q2.getValue(), u2)
                            );

            Entity entity = new Entity();

            entity.setOperand1Value(q1.getValue());
            entity.setOperand1Unit(q1.getUnit());

            entity.setOperand2Value(q2.getValue());
            entity.setOperand2Unit(q2.getUnit());

            entity.setMeasurementType(q1.getMeasurementType());

            entity.setOperationType("DIVIDE");

            entity.setResultValue(result);
            entity.setResultUnit("SCALAR");

            repository.save(entity);

            return new QuantityDTO(
                    result,
                    "SCALAR",
                    q1.getMeasurementType()
            );

        } catch (Exception e) {

            logger.error("DIVIDE operation failed", e);

            return new QuantityDTO(true, e.getMessage());
        }
    }

    @Override
    public QuantityDTO convert(QuantityDTO q,
                               String targetUnit) {

        try {

            IMeasurable u =
                    getUnit(q.getUnit(), q.getMeasurementType());

            Quantity<?> result =
                    new Quantity<>(q.getValue(), u)
                            .toConvert(
                                    getUnit(targetUnit,
                                            q.getMeasurementType())
                            );

            return new QuantityDTO(
                    result.getValue(),
                    targetUnit,
                    q.getMeasurementType()
            );

        } catch (Exception e) {

            logger.error("CONVERT operation failed", e);

            return new QuantityDTO(true, e.getMessage());
        }
    }

    @Override
    public QuantityDTO compare(QuantityDTO q1,
                               QuantityDTO q2) {

        try {

            IMeasurable u1 =
                    getUnit(q1.getUnit(), q1.getMeasurementType());

            IMeasurable u2 =
                    getUnit(q2.getUnit(), q2.getMeasurementType());

            boolean result =
                    new Quantity<>(q1.getValue(), u1)
                            .equals(
                                    new Quantity<>(q2.getValue(), u2)
                            );

            return new QuantityDTO(
                    result ? 1 : 0,
                    "BOOLEAN",
                    q1.getMeasurementType()
            );

        } catch (Exception e) {

            logger.error("COMPARE operation failed", e);

            return new QuantityDTO(true, e.getMessage());
        }
    }
}