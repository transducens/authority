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

/**
 * A period is the time interval between two dates (low and high).
 */
public class Period {

    Date low;
    Date high;

    /**
     * The basic constructor.
     *
     * @param low The lowest date in the period.
     * @param high The highest date in the period.
     */
    public Period(Date low, Date high) {
        this.low = new Date(low);
        this.high = new Date(high);
    }

    /**
     * Default constructor
     */
    public Period() {
        low = new Date(0, 0, DateType.UNKNOWN);
        high = new Date(0, 0, DateType.UNKNOWN);
    }

    /**
     * The general constructor.
     *
     * @param value1 the lowest year/century number in the period.
     * @param value2 the highest year/century number in the period.
     * @param uncertainty1 the precision for the lowest number.
     * @param uncertainty2 , the precision for the highest number.
     * @param type year or century.
     */
    public Period(int value1, int uncertainty1, int value2, int uncertainty2,
            DateType type) {
        low = new Date(value1, uncertainty1, type);
        high = new Date(value2, uncertainty2, type);
    }

    /**
     * Copy constructor
     *
     * @param other a period.
     */
    public Period(Period other) {
        this(other.low, other.high);
    }

    /**
     * Set lowest date negative.
     */
    public void setLowBC() {
        low.setBC();
    }

    /**
     * Set highest date negative.
     */
    public void setHighBC() {
        high.setBC();
    }

    /**
     * @return a string representing the period.
     */
    @Override
    public String toString() {
        if (high.equals(low)) {
            return low.toString();
        } else {
            return low.toString() + " - " + high.toString();
        }
    }

    /**
     * @param other another period
     * @return true if this period's lowest and highest date are identical to
     * the other period's lowest and highest date.
     */
    public boolean equals(Period other) {
        return this.low.equals(other.low) && this.high.equals(other.high);
    }

    /**
     * @return a hash code consistent with equals(Period).
     */
    @Override
    public int hashCode() {
        return low.hashCode() ^ 31 * high.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {

            final Period other = (Period) obj;
            if (this.low.equals(other.low)
                    && this.high.equals(other.high)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * @param other another period
     * @return true if both this period's lowest and highest date are compatible
     * with the other period's lowest and highest date.
     */
    public boolean compatible(Period other) {
        return this.low.compatible(other.low)
                && this.high.compatible(other.high);
    }

    /**
     * 
     * @return true if the period is undefined (for example, not initialized).
     */
    public boolean isUndefined() {
        return low.getType() == DateType.UNKNOWN
                && high.getType() == DateType.UNKNOWN;
    }
}
