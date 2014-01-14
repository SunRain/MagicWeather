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
import android.content.Loader.ForceLoadContentObserver;
import android.location.Location;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.magicmod.mmweather.R;
import com.magicmod.mmweather.engine.WeatherInfo.DayForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 雅虎天气源
 * 
 * 修改自CyanogenMod的雅虎天气实现方式
 * 
 * 
 * @author SunRain
 * 
 * 2014年1月12日
 *
 */
public class YahooWeatherProvider implements WeatherProvider {
    private static final String TAG = "YahooWeatherProvider";

    private static final String URL_WEATHER =
            "http://weather.yahooapis.com/forecastrss?w=%s&u=%s";
    private static final String URL_LOCATION =
            "http://query.yahooapis.com/v1/public/yql?format=json&q=" +
            Uri.encode("select woeid, postal, admin1, admin2, admin3, " +
                    "locality1, locality2, country from geo.places where " +
                    "(placetype = 7 or placetype = 8 or placetype = 9 " +
                    "or placetype = 10 or placetype = 11 or placetype = 20) and text =");
    private static final String URL_PLACEFINDER =
            "http://query.yahooapis.com/v1/public/yql?format=json&q=" +
            Uri.encode("select woeid, city from geo.placefinder where gflags=\"R\" and text =");

    private static final String[] LOCALITY_NAMES = new String[] {
        "locality1", "locality2", "admin3", "admin2", "admin1"
    };

    private Context mContext;
    private WeatherInfo mWeatherInfo;

    private String mCityId, mLocalizedCityName;
    private Location mLocation;
    private boolean mMetricUnits = false;
    
    private WeatherDataChangedListener mWeatherDataChangedListener;
    
    private YahooWeatherResProvider mYahooWeatherResProvider;
    
    public YahooWeatherProvider(Context context) {
        mContext = context;
    }

    @Override
    public int getNameResourceId() {
        return R.string.weather_source_yahoo;
    }

    @Override
    public List<LocationResult> getLocations(String input) {
        String language = getLanguage();
        String params = "\"" + input + "\" and lang = \"" + language + "\"";
        String url = URL_LOCATION + Uri.encode(params);
        JSONObject jsonResults = fetchResults(url);
        if (jsonResults == null) {
            return null;
        }

        try {
            JSONArray places = jsonResults.optJSONArray("place");
            if (places == null) {
                // Yahoo returns an object instead of an array when there's only one result
                places = new JSONArray();
                places.put(jsonResults.getJSONObject("place"));
            }

            ArrayList<LocationResult> results = new ArrayList<LocationResult>();
            for (int i = 0; i < places.length(); i++) {
                LocationResult result = parsePlace(places.getJSONObject(i));
                if (result != null) {
                    results.add(result);
                }
            }
            return results;
        } catch (JSONException e) {
            Log.e(TAG, "Received malformed places data (input=" + input + ", lang=" + language + ")", e);
        }
        return null;
    }

    @Override
    public WeatherInfo getWeatherInfo(String id, String localizedCityName, boolean metric) {
        mCityId = id;
        mLocalizedCityName = localizedCityName;
        mMetricUnits = metric;
        
        refreshData(id, localizedCityName, metric);

        return mWeatherInfo;
    }

    private static class WeatherHandler extends DefaultHandler {
        private static final String TAG = "YahooWeather:WeatherHandler";
        String city;
        String temperatureUnit, speedUnit;
        String windDirection, conditionCode;
        String humidity, temperature, windSpeed;
        String condition, date, sunrise, sunset;
        String dateList[];
        ArrayList<DayForecast> forecasts = new ArrayList<DayForecast>();

        private DayForecast getDayForecast(String date) {
            for(DayForecast forecast : forecasts) {
                if (forecast.getDate().equals(date))
                    return forecast;
            }
            return null;
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {

            if (qName.equals("yweather:location")) {
                city = attributes.getValue("city");
            } else if (qName.equals("yweather:units")) {
                temperatureUnit = attributes.getValue("temperature");
                speedUnit = attributes.getValue("speed");
            } else if (qName.equals("yweather:wind")) {
                windDirection = attributes.getValue("direction");
                windSpeed = attributes.getValue("speed");
            } else if (qName.equals("yweather:atmosphere")) {
                humidity = attributes.getValue("humidity");
            } else if (qName.equals("yweather:astronomy")) {
                sunrise = attributes.getValue("sunrise");
                sunset = attributes.getValue("sunset");
            } else if (qName.equals("yweather:condition")) {
                condition = attributes.getValue("text");
                conditionCode = attributes.getValue("code");
                temperature = attributes.getValue("temp");
                dateList = attributes.getValue("date").split(",")[1].trim().split(" ");
                date = String.format("%s %s %s", dateList[0], dateList[1], dateList[2]);
            } else if (qName.equals("yweather:forecast")) {
                DayForecast day =  new DayForecast();
                day.setCity(city);
                day.setCondition(attributes.getValue("text"));
                day.setConditionCode(attributes.getValue("code"));
                day.setDate(attributes.getValue("date"));
                day.setTempHigh(attributes.getValue("high"));
                day.setTempLow(attributes.getValue("low"));
                day.setTempUnit(temperatureUnit);
                forecasts.add(day);
            }
        }
        public boolean isComplete() {
            return temperatureUnit != null && speedUnit != null && conditionCode != null
                    && temperature != null && !forecasts.isEmpty();
        }
    }

    @Override
    public WeatherInfo getWeatherInfo(Location location, boolean metric) {
        mLocation = location;
        mMetricUnits = metric;
        
        refreshData(location, metric);
        return mWeatherInfo;
    }

    private LocationResult parsePlace(JSONObject place) throws JSONException {
        LocationResult result = new LocationResult();
        JSONObject country = place.getJSONObject("country");

        result.id = place.getString("woeid");
        result.country = country.getString("content");
        result.countryId = country.getString("code");
        if (!place.isNull("postal")) {
            result.postal = place.getJSONObject("postal").getString("content");
        }

        for (String name : LOCALITY_NAMES) {
            if (!place.isNull(name)) {
                result.city = place.getJSONObject(name).getString("content");
                break;
            }
        }

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "JSON data " + place.toString() + " -> id=" + result.id
                    + ", city=" + result.city + ", country=" + result.countryId);
        }

        if (result.id == null || result.city == null || result.countryId == null) {
            return null;
        }

        return result;
    }

    private JSONObject fetchResults(String url) {
        String response = HttpRetriever.retrieve(url);
        if (response == null) {
            return null;
        }

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Request URL is " + url + ", response is " + response);
        }

        try {
            JSONObject rootObject = new JSONObject(response);
            return rootObject.getJSONObject("query").getJSONObject("results");
        } catch (JSONException e) {
            Log.w(TAG, "Received malformed places data (url=" + url + ")", e);
        }

        return null;
    }

    private String getLanguage() {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String country = locale.getCountry();
        String language = locale.getLanguage();

        if (TextUtils.isEmpty(country)) {
            return language;
        }
        return language + "-" + country;
    }

    @Override
    public WeatherInfo getWeatherInfo() {
        // TODO Auto-generated method stub
        return mWeatherInfo;
    }

    @Override
    public void refreshData() {
        // TODO Auto-generated method stub
        if (mCityId != null) {
            refreshData(mCityId, mLocalizedCityName, mMetricUnits);
        } else if (mLocation != null) {
            refreshData(mLocation, mMetricUnits);
        }
        
        if (mWeatherDataChangedListener != null)
            mWeatherDataChangedListener.onDataChanged();
    }

    @Override
    public void refreshData(String id, String localizedCityName, boolean metricUnits) {
        mCityId = id;
        mLocalizedCityName = localizedCityName;
        mMetricUnits = metricUnits;
        
        String url = String.format(URL_WEATHER, id, metricUnits ? "c" : "f");
        String response = HttpRetriever.retrieve(url);

        if (response == null) {
            mWeatherInfo = null;
            return;
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            StringReader reader = new StringReader(response);
            WeatherHandler handler = new WeatherHandler();
            parser.parse(new InputSource(reader), handler);

            if (handler.isComplete()) {
                // There are cases where the current condition is unknown, but the forecast
                // is not - using the (inaccurate) forecast is probably better than showing
                // the question mark
                if (handler.conditionCode.equals(3200)) {
                    handler.condition = handler.forecasts.get(0).getCondition();
                    handler.conditionCode = handler.forecasts.get(0).getConditionCode();
                }
                ArrayList<DayForecast> forecasts = new ArrayList<WeatherInfo.DayForecast>();

                long time = System.currentTimeMillis();
                for (DayForecast forecast : handler.forecasts) {
                    if (forecast.getDate().equals(handler.date)) {
                        forecast.setCondition(handler.condition);
                        forecast.setConditionCode(handler.conditionCode);
                        forecast.setHumidity(handler.humidity);
                        forecast.setSunRaise(handler.sunrise);
                        forecast.setSunSet(handler.sunset);
                        forecast.setTemperature(handler.temperature);
                        forecast.setTempUnit(handler.temperatureUnit);
                        forecast.setWindSpeed(handler.windSpeed);
                        forecast.setWindDirection(handler.windDirection);
                        forecast.setWindSpeedUnit(handler.speedUnit);
                    }
                    if (localizedCityName != null) {
                        forecast.setCity(localizedCityName);
                    }
                    forecast.setSynctimestamp(String.valueOf(time));
                    forecasts.add(forecast);
                }
                mWeatherInfo = new WeatherInfo(forecasts);
                Log.d(TAG, "Weather updated: " + mWeatherInfo);
            } else {
                Log.w(TAG, "Received incomplete weather XML (id=" + id + ")");
                mWeatherInfo = null;
            }
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Could not create XML parser", e);
            mWeatherInfo = null;
        } catch (SAXException e) {
            Log.e(TAG, "Could not parse weather XML (id=" + id + ")", e);
            mWeatherInfo = null;
        } catch (IOException e) {
            Log.e(TAG, "Could not parse weather XML (id=" + id + ")", e);
            mWeatherInfo = null;
        }
        if (mWeatherDataChangedListener != null)
            mWeatherDataChangedListener.onDataChanged();
    }

    @Override
    public void refreshData(Location location, boolean metricUnits) {
        mLocation = location;
        mMetricUnits = metricUnits;
        
        String language = getLanguage();
        String params = String.format(Locale.US, "\"%f %f\" and locale=\"%s\"",
                location.getLatitude(), location.getLongitude(), language);
        String url = URL_PLACEFINDER + Uri.encode(params);
        JSONObject results = fetchResults(url);
        if (results == null) {
            mWeatherInfo = null;
            return;
        }

        try {
            JSONObject result = results.getJSONObject("Result");
            String woeid = result.getString("woeid");
            String city = result.getString("city");

            if (city == null) {
                city = result.getString("neighborhood");
            }

            // The city name in the placefinder result is HTML encoded :-(
            if (city != null) {
                city = Html.fromHtml(city).toString();
            }

            Log.d(TAG, "Resolved location " + location + " to " + city + " (" + woeid + ")");

            mWeatherInfo = getWeatherInfo(woeid, city, metricUnits);
        } catch (JSONException e) {
            Log.e(TAG, "Received malformed placefinder data (location="
                    + location + ", lang=" + language + ")", e);
            mWeatherInfo = null;
        }
        if (mWeatherDataChangedListener != null)
            mWeatherDataChangedListener.onDataChanged();
    }

    @Override
    public void setDataChangedListener(WeatherDataChangedListener listener) {
        mWeatherDataChangedListener = listener; 
    }

    @Override
    public WeatherResProvider getWeatherResProvider() {
        // TODO Auto-generated method stub
        if (mYahooWeatherResProvider == null) {
            mYahooWeatherResProvider = new YahooWeatherResProvider();
        }
        return mYahooWeatherResProvider;
    }
}
