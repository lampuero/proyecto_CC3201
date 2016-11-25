package crawk;

public class Partido {
  long id;
  String queuetype;
  String season;
  String version;
  int creation;
  
  public Partido(long id, String queuetype, String season, String version, int creation){
    this.id=id;
    this.queuetype=queuetype;
    this.season=season;
    this.version=version;
    this.creation=creation;
  }
  
}
