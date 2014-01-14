/*
 * Copyright (C) 2013 The CyanogenMod Project
 * Copyright (C) 2014 The MagicMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.magicmod.mmweather.engine;

import android.content.Context;
import android.location.Location;

import java.util.List;

/**
 * 抽象类,继承此类以实现某一个具体的天气源,从而在WeatherEngine.java里面进行操作
 * 
 * @author SunRain
 * 
 * 2014年1月12日
 *
 */
public interface WeatherProvider {
    public class LocationResult {
        public String id;
        public String city;
        public String postal;
        public String countryId;
        public String country;
    }

    /**
     * 2014年1月13日
     * @param input city,State/Country
     * @return
     */
    List<LocationResult> getLocations(String input);
    
    WeatherResProvider getWeatherResProvider();

    /**
     * 2014年1月12日
     * @param id
     * @param localizedCityName
     * @param metricUnits
     * @return
     */
    WeatherInfo getWeatherInfo(String id, String localizedCityName, boolean metricUnits);

    /**
     * 2014年1月12日
     * @param location
     * @param metricUnits
     * @return
     */
    WeatherInfo getWeatherInfo(Location location, boolean metricUnits);
    /**
     * 2014年1月12日
     * @return
     */
    WeatherInfo getWeatherInfo();
    
    void refreshData();
    void refreshData(String id, String localizedCityName, boolean metricUnits);
    void refreshData(Location location, boolean metricUnits);

    int getNameResourceId();
    
    void setDataChangedListener(WeatherDataChangedListener listener);
}
