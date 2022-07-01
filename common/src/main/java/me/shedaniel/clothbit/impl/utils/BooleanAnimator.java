/*
 * This file is part of Clothbit.
 * Copyright (C) 2021, 2022 shedaniel
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

public class BooleanAnimator extends Animator {
    public BooleanAnimator() {
    }
    
    public BooleanAnimator(boolean bool) {
        super(bool ? 100 : 0);
    }
    
    @Override
    @Deprecated
    public void setAs(double value) {
        super.setAs(value);
    }
    
    public void setAs(boolean value) {
        super.setAs(value ? 100 : 0);
    }
    
    @Override
    @Deprecated
    public void setTo(double value, long duration) {
        super.setTo(value, duration);
    }
    
    public void setTo(boolean value, long duration) {
        super.setTo(value ? 100 : 0, duration);
    }
    
    @Override
    @Deprecated
    public double target() {
        return super.target();
    }
    
    public boolean targetBool() {
        return target() == 100;
    }
    
    public double progress() {
        return super.doubleValue() / 100;
    }
    
    @Override
    @Deprecated
    public int intValue() {
        return super.intValue();
    }
    
    @Override
    @Deprecated
    public long longValue() {
        return super.longValue();
    }
    
    @Override
    @Deprecated
    public float floatValue() {
        return super.floatValue();
    }
    
    @Override
    @Deprecated
    public double doubleValue() {
        return super.doubleValue();
    }
}
