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
import java.util.ArrayList;
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
HttpURLConnection connect;
Partido partido = new Partido();
ArrayList<Jugador> jugadores;
ArrayList<Summoner> summoners;
ArrayList<Kill> kills;
Equipo[] equipos;

/**
* Constructor for {@link SummonerInfo}.
*/
public MatchInfo(long idpart, String region) {
  this.Id = idpart;
  this.region = region;
  equipos = new Equipo[2];
  jugadores = new ArrayList<Jugador>();
  summoners = new ArrayList<Summoner>();
  kills = new ArrayList<Kill>();
  
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
/**
* Opens connection to the Riot API, gets data in JSON, decodes it and assigns it to a variable.
* @throws IOException
*/
void prepare() throws IOException {
URL request;
request = new URL("https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.2/match/" +
Id + "?includeTimeline=true&api_key=" + getAPIKey());
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
		e.printStackTrace();
	}
	System.out.println("exceded Match");
	prepare();
}
}
private void agregarEquipos(JsonObject json){
  JsonArray teams = json.getAsJsonArray("teams");
  JsonObject team = teams.get(1).getAsJsonObject();
  equipos[0]= new Equipo();
  equipos[0].partido = Id;
  equipos[0].ganador = team.get("winner").getAsBoolean();
  equipos[0].teamid = team.get("teamId").getAsInt();
  equipos[1] = new Equipo();
  equipos[1].partido = Id;
  equipos[1].ganador = !equipos[0].ganador;
  equipos[1].teamid = 300-team.get("teamId").getAsInt();
}

private void agregarJugadores(JsonObject json){
  JsonArray participants = json.getAsJsonArray("participants");
  JsonArray id=json.getAsJsonArray("participantIdentities");
  for(int i=0;i<id.size();i++){
      JsonObject currentId=id.get(i).getAsJsonObject();
      JsonObject currentP=participants.get(i).getAsJsonObject();
      Jugador jAct = new Jugador();
      Summoner sAct = new Summoner();
      jAct.champion=currentP.get("championId").getAsInt();
      jAct.teamid=currentP.get("teamId").getAsInt();
      jAct.id=currentP.get("participantId").getAsInt();
      jAct.partido=Id;
      jAct.summoner=currentId.getAsJsonObject("player").get("summonerId").getAsInt();
      sAct.id=jAct.summoner;
      sAct.nombre=currentId.getAsJsonObject("player").get("summonerName").getAsString();
      jugadores.add(jAct);
      summoners.add(sAct);
  }
}

private void agregarKills(JsonObject json){
  JsonObject timeline = json.getAsJsonObject("timeline");
  JsonArray frames = timeline.getAsJsonArray("frames");
  for(int i=1;i<frames.size();i++){
    JsonObject frame = frames.get(i).getAsJsonObject();
    JsonArray events = frame.getAsJsonArray("events");
    for(int j=1;j<events.size();j++){
      JsonObject event= events.get(j).getAsJsonObject();
      String eventType=event.get("eventType").getAsString();
      if(eventType.equals("CHAMPION_KILL")){
        Kill actual = new Kill();
        actual.partido=Id;
        actual.time=event.get("timestamp").getAsInt();
        actual.dead=event.get("victimId").getAsInt();
        actual.killer=event.get("killerId").getAsInt();
        kills.add(actual);
      }
    }
  }
}

public void process() throws IOException {
JsonObject json;
BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
JsonParser parser = new JsonParser();
JsonElement element = parser.parse(in);
json = element.getAsJsonObject();
if (json.isJsonObject()) {
  partido.id=Id;
  partido.queuetype=json.get("queueType").getAsString();
  partido.season=json.get("season").getAsString();
  partido.version= json.get("matchVersion").getAsString();
  partido.creation=json.get("matchCreation").getAsInt();
  agregarEquipos(json);
  agregarJugadores(json);
  agregarKills(json);
  
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
