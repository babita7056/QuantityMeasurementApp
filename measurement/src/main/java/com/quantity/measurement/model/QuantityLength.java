package com.quantity.measurement.model;
import com.quantity.measurement.enums.LengthUnit;

public class QuantityLength {

    private final double value;
    private final LengthUnit unit;

    public QuantityLength(double value, LengthUnit unit) {
        if(unit==null)throw new IllegalArgumentException("unit should not be null");
        this.value = value;
        this.unit = unit;
    }

    private double toFeet() {
        return unit.toFeet(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuantityLength other = (QuantityLength) obj;
        double thisInFeet=this.unit.toFeet(this.value);
        double otherInFeet=other.unit.toFeet(other.value);
        return Double.compare(thisInFeet, otherInFeet) == 0;
    }


public String toString() {
    return value+" "+unit.name();
    }
}

