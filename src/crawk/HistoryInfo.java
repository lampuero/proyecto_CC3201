/**
* Copyright (c) 2014 Ahmad Sakr (http://github.com/ahmadsakr)
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package crawk;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
* SummonerInfo {@link SummonerInfo} is the class that handles all data for a certain
* Summoner i.e Summoner minion = new Summoner("A Minion With IE", "EUNE");
* The Whole methods all use up 1 count together in your rate limit only.
*
* @author Ahmad Sakr
*/
public class HistoryInfo {
private long[] partidos;
private int Id;
private String region;
private String API_KEY;
private int responseCode;
/**
* Constructor for {@link SummonerInfo}.
*/
public HistoryInfo(int Id, String region) {
this.Id = Id;
this.region = region;
}
/**
* Checks if the API Key is valid, if it is it will return true, otherwise false
* NOTE: this request is not counted in your count limit as specified by riot in the API.
* @return API Key's validity.
* @throws IOException
*/
public boolean isAuthorized() throws IOException {
URL request;
request = new URL("https://eune.api.pvp.net/api/lol/static-data/eune/v1.2/realm?api_key=" + getAPIKey());
HttpURLConnection connect = (HttpURLConnection) request.openConnection();
return connect.getResponseCode() != 401;
}

public long[] getId() throws IOException {
JsonObject json;
URL request;
request = new URL("https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.2/matchlist/by-summoner/" +
Id + "?api_key=" + getAPIKey());
HttpURLConnection connect = (HttpURLConnection) request.openConnection();
setResponseCode(connect.getResponseCode());
if (connect.getResponseCode() == 401) {
	throw new IOException("401");
}
if (connect.getResponseCode() == 404) {
    System.out.println(request.toString());
	throw new IOException("404");
}
if (connect.getResponseCode() == 429) {
	try {
		Thread.sleep(10000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.out.println("exceded History");
	return getId();
}
BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
JsonParser parser = new JsonParser();
JsonElement element = parser.parse(in);
json = element.getAsJsonObject();
if (json.isJsonObject()) {
	JsonArray matches = json.getAsJsonArray("matches");
	partidos= new long[matches.size()];
	for(int i=0;i<matches.size();i++){
		json=matches.get(i).getAsJsonObject();
		partidos[i]=json.get("matchId").getAsLong();
		if (partidos[i]<0){
		  System.out.println("Partidos[i] = "+Long.toString(partidos[i]));
		  System.out.println("URL = "+request.toString());
		}
	}
}
in.close();
return partidos;
}
/**
* Sets The API Key that will be used to grab data from the Riot Games' Servers.
*
* @param key The API Key
*/
public void setAPIKey(String key) {
this.API_KEY = key;
}
/**
* @return The API Key
*/
private String getAPIKey() {
return API_KEY;
}
/**
* @return the response code of the HTTP request
*/
public int getResponseCode() {
return responseCode;
}
/**
* @param code the response code we are setting
*/
private void setResponseCode(int code) {
this.responseCode = code;
}

}