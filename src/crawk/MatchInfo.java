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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
public class MatchInfo {
private long Id;
private String region;
private String API_KEY;
int responseCode;
int[][][] jugadores;
String[][] nombres;
String[] valores = new String[3];
int creation;
HttpURLConnection connect;

/**
* Constructor for {@link SummonerInfo}.
*/
public MatchInfo(long idpart, String region) {
this.Id = idpart;
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
request = new URL("https://eune.api.pvp.net/api/lol/static-data/eune/v1.2/realm?include_timeline=true&api_key=" + getAPIKey());
HttpURLConnection connect = (HttpURLConnection) request.openConnection();
return connect.getResponseCode() != 401;
}
/**
* Opens connection to the Riot API, gets data in JSON, decodes it and assigns it to a variable.
* @throws IOException
*/
void prepare() throws IOException {
URL request;
request = new URL("https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.2/match/" +
Id + "?api_key=" + getAPIKey());
connect = (HttpURLConnection) request.openConnection();
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
	System.out.println("exceded Match");
	prepare();
}
}
public void process() throws IOException {
JsonObject json;
BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
JsonParser parser = new JsonParser();
JsonElement element = parser.parse(in);
json = element.getAsJsonObject();
if (json.isJsonObject()) {
  valores[2]= json.get("matchVersion").getAsString();
  valores[0]=json.get("queueType").getAsString();
  valores[1]=json.get("season").getAsString();
  creation=json.get("matchCreation").getAsInt();
	JsonArray participants = json.getAsJsonArray("participants");
	JsonArray idd=json.getAsJsonArray("participantIdentities");
	int ganadores=0;
	int perdedores=0;
	jugadores = new int[2][3][(int)idd.size()/2];
	nombres = new String[2][(int)idd.size()/2];
	for(int i=0;i<idd.size();i++){
		JsonObject json2=idd.get(i).getAsJsonObject();
		json=participants.get(i).getAsJsonObject();
		if(json.getAsJsonObject("stats").get("winner").getAsBoolean()){
		  if (ganadores<5){
			jugadores[0][0][ganadores]=json.get("championId").getAsInt();
			jugadores[0][1][ganadores]=json2.getAsJsonObject("player").get("summonerId").getAsInt();
			nombres[0][ganadores]=json2.getAsJsonObject("player").get("summonerName").getAsString();
			ganadores++;
		  }
		}
		else{
		  if (perdedores<5){
			jugadores[1][0][perdedores]=json.get("championId").getAsInt();
			jugadores[1][1][perdedores]=json2.getAsJsonObject("player").get("summonerId").getAsInt();
			nombres[1][perdedores]=json2.getAsJsonObject("player").get("summonerName").getAsString();
			perdedores++;
		  }
		}
	}
}
in.close();
}
/**
* Gets The Region of the summoner.
*
* @return The Region
*/
public String getRegion() {
return region.toUpperCase();
}
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
