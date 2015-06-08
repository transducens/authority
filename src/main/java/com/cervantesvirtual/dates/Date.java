/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.cervantesvirtual.dates;

/*
 * A date is either a year or century number or an unknown date.
 * It has an associated uncertainty. A negative value is interpreted as a BC date.
 */
public class Date {

    DateType type;
    int value;
    int uncertainty;

    /**
     * The basic constructor
     *
     * @param value a year or century number.
     * @param uncertainty explicit precision in the dating.
     * @param type year number, century number or unknown.
     */
    public Date(int value, int uncertainty, DateType type) {
        this.value = value;
        this.uncertainty = uncertainty;
        this.type = type;
    }

    /**
     * The copy constructor
     *
     * @param other another date.
     */
    public Date(Date other) {
        this.type = other.type;
        this.value = other.value;
        this.uncertainty = other.uncertainty;
    }

    /**
     * @return the year or century number (or 0 if date is unknown).
     */
    public int getValue() {
        return value;
    }

    /**
     * @return the precision in the date.
     */
    public int getUncertainty() {
        return uncertainty;
    }

    /**
     * @return the date type.
     */
    public DateType getType() {
        return type;
    }

    /**
     * The century the date belongs to, i.e., 1 for years from 1 to 100
     * (inclusive), 2 for year from 101 to 200, and -1 for year between -100 and
     * -1 (beware: year 0 does not exist!).
     *
     * @return the century number the date belongs to.
     */
    public int getCentury() {
        if (type == DateType.CENTURY) {
            return value;
        } else {
            return (value >= 0) ? (value - 1) / 100 + 1 : (value + 1) / 100 - 1;
        }
    }

    /**
     * The default uncertainty for a date, depending on historical period. The
     * values must be read from a user properties file
     *
     * @param year the year of the date
     * @return the default uncertainty for a date, depending on historical
     * period (non-negative). The exact values should be part of a properties
     * file.
     */
    public static int defaultUncertainty(int year) {
        return (year < 500) ? 50 : (year < 1500) ? 20 : (year < 1700) ? 10
                : (year < 1900) ? 5 : 2;
    }

    /**
     * @return The estimated precision in the dating of the event (always
     * greater than the default uncertainty).
     */
    private int precission() {
        switch (type) {
            case YEAR:
                return Math.max(uncertainty, defaultUncertainty(value));
            case CENTURY:
                return 0; // default for centuries is 0.
            default: // unknown date
                return 10000;
        }
    }

    /**
     * @param other
     * @return true if the dates are identical.
     */
    public boolean equals(Date other) {
        return this.type == other.type && this.value == other.value
                && this.uncertainty == other.uncertainty;
    }

    /**
     * @return A hash code consistent with equals(Date).
     */
    @Override
    public int hashCode() {
        return value ^ 31 * uncertainty;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            final Date other = (Date) obj;
            if (this.type == other.type
                    && this.value == other.value
                    && this.uncertainty == other.uncertainty) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Check if dates are compatible. Function compareTO has not been
     * implemented because we are not aware of any total ordering of dates
     * with uncertainties such that it allows to locate compatible dates with a
     * simple binary search.
     *
     * @param other another date
     *
     * @return true if the difference between dates is compatible with the
     * estimated error margin.
     */
    public boolean compatible(Date other) {
        if (this.type == DateType.UNKNOWN || other.type == DateType.UNKNOWN) {
            return true;
        } else if (this.type == other.type) {
            return Math.abs(this.value - other.value) <= (this.precission() + other
                    .precission());
        } else { // year vs century
            return this.getCentury() == other.getCentury();
        }
    }

    /**
     * @return true if date is less than 0.
     */
    public boolean isBC() {
        return value < 0;
    }

    /**
     * Set date negative.
     */
    public void setBC() {
        if (value > 0) {
            value *= -1;
        }
    }

    /**
     * Set the uncertainty to its default value, depending on antiquity.
     */
    public void useDefaultUncertainty() {
        uncertainty = defaultUncertainty(value);
    }

    /**
     * Increment value and uncertainty.
     *
     * @param value the increment added to the value.
     * @param uncertainty the amount added to the uncertainty.
     */
    public void add(int value, int uncertainty) {
        this.value += value;
        this.uncertainty += uncertainty;
    }

    /**
     * @return The string representation of the date.
     * u0081 is plusminus sign
     */
    @Override
    public String toString() {
        switch (type) {
            case YEAR:
                return String.valueOf(value) + '\u00B1' + uncertainty;  
            case CENTURY:
                return "s. " + value + '\u00B1' + uncertainty;
            case UNKNOWN:
                return "?";
            default:
                return null;
        }
    }
}
