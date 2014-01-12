
package com.magicmod.mmweather.engine;

import android.R.integer;
import android.text.StaticLayout;

import java.util.ArrayList;

/**
 * @author SunRain 2014年1月12日
 */
public class WeatherInfo {
    public static final String DATA_NULL = "N/A";

    private String yujing;// 是否有预警，如果无，则为“暂无预警”
    private String alarmtext;

    private ArrayList<DayForecast> forecasts;

    public WeatherInfo(ArrayList<DayForecast> forecasts) {
        this.forecasts = forecasts;
    }

    public ArrayList<DayForecast> getDayForecast() {
        return forecasts;
    }

    public static class DayForecast {
        private String city;
        private String date; // 天气数据的日期
        private String temperature; // 当前温度
        private String temphight; // 最高温度
        private String templow; // 最低温度
        private String tempUnit; // 温度格式==>摄氏或者华氏
        private String condition; // 天气情况=>晴/阴之类
        private String conditionCode; //天气对应的图标
        private String windSpeed; // 风力
        private String windDirection; // 风向
        private String windSpeedUnit; // 风速单位,如 km/h
        private String humidity; // 湿度
        private String synctimestamp; // 同步时间

        private String PM2Dot5Data; // PM2.5
        private String AQIData; // AQI(空气质量指数)
        private String sunrise;
        private String sunset;

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return city == null ? null : city;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date == null ? null : date;
        }

        public void setTemperature(String temp) {
            this.temperature = temp;
        }

        public String getTemperature() {
            return temperature == null ? DATA_NULL : temperature;
        }

        public void setTempHigh(String s) {
            this.temphight = s;
        }

        public String getTempHigh() {
            return temphight == null ? DATA_NULL : temphight;
        }

        public void setTempLow(String s) {
            this.templow = s;
        }

        public String getTempLow() {
            return templow == null ? DATA_NULL : templow;
        }

        public void setTempUnit(String s) {
            this.tempUnit = s;
        }

        public String getTempUnit() {
            return tempUnit == null ? DATA_NULL : tempUnit;
        }

        public void setCondition(String s) {
            this.condition = s;
        }

        public String getCondition() {
            return condition == null ? DATA_NULL : condition;
        }

        public void setConditionCode(String s) {
            this.conditionCode = s;
        }

        public String getConditionCode() {
            return conditionCode == null ? DATA_NULL : conditionCode;
        }

        public void setWindSpeed(String s) {
            this.windSpeed = s;
        }

        public String getWindSpeed() {
            return windSpeed == null ? DATA_NULL : windSpeed;
        }

        public void setWindDirection(String s) {
            this.windDirection = s;
        }

        public String getWindDirection() {
            return windDirection == null ? DATA_NULL : windDirection;
        }

        public void setWindSpeedUnit(String s) {
            this.windSpeedUnit = s;
        }

        public String getWindSpeedUnit() {
            return windSpeedUnit == null ? DATA_NULL : windSpeedUnit;
        }

        public void setHumidity(String s) {
            this.humidity = s;
        }

        public String getHumidity() {
            return humidity == null ? DATA_NULL : humidity;
        }

        public void setSynctimestamp(String s) {
            this.synctimestamp = s;
        }

        public String getSynctimestamp() {
            return synctimestamp == null ? DATA_NULL : synctimestamp;
        }

        public void setPM2Dot5Data(String s) {
            this.PM2Dot5Data = s;
        }

        public String getPM2Dot5Data() {
            return PM2Dot5Data == null ? DATA_NULL : PM2Dot5Data;
        }

        public void setAQIData(String s) {
            this.AQIData = s;
        }

        public String getAQIData() {
            return AQIData == null ? DATA_NULL : AQIData;
        }

        public void setSunRaise(String s) {
            this.sunrise = s;
        }

        public String getSunRaise() {
            return sunrise == null ? DATA_NULL : sunrise;
        }

        public void setSunSet(String s) {
            this.sunset = s;
        }

        public String getSunSet() {
            return sunset == null ? DATA_NULL : sunset;
        }

        @Override
        public String toString() {
            return String
                    .format("AllWeather [city = %s, day = %s, temperature = %s, tempUnit = %s, condition = %s, conditionCode = %s, wind = %s, windDirection = %s, windSpeedUnit = %s, humidity = %s, PM2Dot5Data = %s, AQIData = %s, sunraise = %s, sunset = %s, synctimestamp = %s ]",
                            city, date, temperature, tempUnit, condition, conditionCode, windSpeed,
                            windDirection, windSpeedUnit, humidity, PM2Dot5Data, AQIData, sunrise,
                            sunset, synctimestamp);
        }
    }

    @Override
    public String toString() {
        int i = 0;
        StringBuilder builder = new StringBuilder();
        for (DayForecast forecast : forecasts) {
            builder.append(String.format("DayForecast[%d] => {%s}\n", i, forecast.toString()));
            i++;
        }
        return builder.toString();
    }

}
