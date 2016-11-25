package crawk;

public class Jugador {
  int id;
  long partido;
  int teamid;
  int champion;
  int summoner;
  
  public Jugador(int id, long partido, int teamid, int champion, int summoner){
    this.id=id;
    this.partido=partido;
    this.teamid=teamid;
    this.champion=champion;
    this.summoner=summoner;
  }
}
