/*
 * This file is part of Clothbit.
 * Copyright (C) 2021 shedaniel
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package me.shedaniel.clothbit.impl.utils;

import net.minecraft.Util;

public class Animator extends Number {
    private double amount;
    private double target;
    private long start;
    private long duration;
    
    public Animator() {
    }
    
    public Animator(double amount) {
        setAs(amount);
    }
    
    public void setAs(double value) {
        this.set(value, false, 0);
    }
    
    public void setTo(double value, long duration) {
        if (target != value) {
            this.set(value, true, duration);
        }
    }
    
    private void set(double value, boolean animated, long duration) {
        this.target = value;
        this.start = Util.getMillis();
        
        if (animated) {
            this.duration = duration;
        } else {
            this.duration = 0;
            this.amount = this.target;
        }
    }
    
    public void update(double delta) {
        if (duration != 0) {
            if (amount < target) {
                this.amount = Math.min(ease(amount, target + (target - amount), Math.min(((double) Util.getMillis() - start) / duration * delta * 3.0D, 1.0D)), target);
            } else if (amount > target) {
                this.amount = Math.max(ease(amount, target - (amount - target), Math.min(((double) Util.getMillis() - start) / duration * delta * 3.0D, 1.0D)), target);
            }
        }
    }
    
    private static double ease(double start, double end, double amount) {
        return start + (end - start) * amount;
    }
    
    @Override
    public int intValue() {
        return (int) amount;
    }
    
    @Override
    public long longValue() {
        return (long) amount;
    }
    
    @Override
    public float floatValue() {
        return (float) amount;
    }
    
    @Override
    public double doubleValue() {
        return amount;
    }
    
    public double target() {
        return target;
    }
}