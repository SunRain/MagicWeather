
package com.magicmod.mmweather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import com.magicmod.mmweather.engine.WeatherDataChangedListener;
import com.magicmod.mmweather.engine.WeatherEngine;
import com.magicmod.mmweather.engine.WeatherInfo;
import com.magicmod.mmweather.engine.WeatherProvider;
import com.magicmod.mmweather.engine.YahooWeatherProvider;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    
    YahooWeatherProvider yahooWeatherProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //YahooWeatherProvider yahooWeatherProvider = new YahooWeatherProvider(getApplicationContext());
       // yahooWeatherProvider.getWeatherInfo("56579778", null, true).toString();
        
        WeatherUpdateTask mTask = new WeatherUpdateTask();
        mTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private class WeatherUpdateTask extends AsyncTask<Void, Void, WeatherInfo> {

        @Override
        protected WeatherInfo doInBackground(Void... params) {
            // TODO Auto-generated method stub

            /*YahooWeatherProvider yahooWeatherProvider = new YahooWeatherProvider(getApplicationContext());
            yahooWeatherProvider.setDataChangedListener(new WeatherDataChangedListener() {

                @Override
                public void onDataChanged() {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "====onDataChanged");
                    
                }
            });
            
            yahooWeatherProvider.getWeatherInfo("56579778", null, true);//.toString();*/
            
            WeatherEngine mWeatherEngine = WeatherEngine.getinstance(getApplicationContext());
            WeatherProvider mWeatherProvider = mWeatherEngine.getWeatherProvider();
            
            mWeatherEngine.setToCache(mWeatherProvider.getWeatherInfo("56579778", null, true));
            
            
           // String s = mWeatherEngine.encodeWeatherInfo(mWeatherProvider.getWeatherInfo("56579778", null, true));
            //mWeatherEngine.decodeWeatherInfo(mWeatherEngine.encodeWeatherInfo(mWeatherProvider.getWeatherInfo("56579778", null, true))).toString();

            //WeatherInfo weatherInfo = mWeatherEngine.decodeWeatherInfo(s);
            
            //Log.d(TAG, weatherInfo.toString());
            
            return null;
        }
    }

}
