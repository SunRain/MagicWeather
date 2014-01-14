/*
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

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.magicmod.mmweather.R;
import com.magicmod.mmweather.engine.WeatherInfo.DayForecast;
import com.magicmod.mmweather.utils.Constants;

public class YahooWeatherResProvider implements WeatherResProvider{
    private static final String TAG = "YahooWeatherResProvider";
    private static final boolean DEBUG = Constants.DEBUG;

    @Override
    public int getWeatherIconResId(Context context, String conditionCode, String iconSet) {
        if (iconSet == null) {
            iconSet = "_";
        }
        final String iconName = "weather" + iconSet + conditionCode; 
        final Resources res = context.getResources();
        final int resId = res.getIdentifier(iconName, "drawable", context.getPackageName());
        
        if (resId != 0) {
            return resId;
        }
        return R.drawable.weather_na;
    }

    @Override
    public Bitmap getWeatherIconBitmap(Context context, String conditionCode, String iconSet) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DayForecast getPreFixedWeatherInfo(Context context, DayForecast forecast) {
        if (forecast == null) {
            return null;
        }
        forecast.setHumidity(forecast.getHumidity() + "%");
        forecast.setTemperature(forecast.getTemperature() + "\u00b0" + forecast.getTempUnit());
        forecast.setTempHigh(forecast.getTempHigh() + "\u00b0" + forecast.getTempUnit());
        forecast.setTempLow(forecast.getTempLow() + "\u00b0" + forecast.getTempUnit());
        forecast.setWindSpeed(forecast.getWindSpeed() + forecast.getWindSpeedUnit());
        
        String windDirec = forecast.getWindDirection();
        if (!windDirec.equals(WeatherInfo.DATA_NULL)) {
            int resId;
            
            int windDirection = Integer.parseInt(forecast.getWindDirection());
            if (windDirection < 23) resId = R.string.weather_N;
            else if (windDirection < 68) resId = R.string.weather_NE;
            else if (windDirection < 113) resId = R.string.weather_E;
            else if (windDirection < 158) resId = R.string.weather_SE;
            else if (windDirection < 203) resId = R.string.weather_S;
            else if (windDirection < 248) resId = R.string.weather_SW;
            else if (windDirection < 293) resId = R.string.weather_W;
            else if (windDirection < 338) resId = R.string.weather_NW;
            else resId = R.string.weather_N;
            windDirec = context.getString(resId);
            forecast.setWindDirection(windDirec);
            
        }
        return forecast;
    }

    @Override
    public DayForecast getPreFixedWeatherInfo(DayForecast forecast) {
        // TODO Auto-generated method stub
        return forecast;
    }
    
    

}
