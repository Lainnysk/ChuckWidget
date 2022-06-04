package com.example.widgetwork;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WidgetController extends android.appwidget.AppWidgetProvider {

    static String futureJokeString = "Click for joke";
    RemoteViews viewsJoke;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetController.update(this, context, appWidgetManager, appWidgetIds);
    }

    public static void update(WidgetController widgetController, Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
            widgetController.viewsJoke = views;
            views.setTextViewText(R.id.widgetButton, WidgetController.futureJokeString);
            widgetController.new JokeLoader().execute();
            Intent updateIntent = new Intent();
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_IMMUTABLE);//FLAG_UPDATE_CURRENT
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId,views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(new ComponentName(context, WidgetController.class));
        WidgetController.update(this, context, AppWidgetManager.getInstance(context), ids);
    }


    private class JokeLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            String jsonString = getJson("https://api.chucknorris.io/jokes/random");

            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                WidgetController.futureJokeString = jsonObject.getString("value");
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        private String getJson(String link){
            String data = "";
            try{
                URL url = new URL(link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    data = r.readLine();
                    urlConnection.disconnect();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return data;
        }
    }
}