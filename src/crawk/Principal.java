package crawk;


public class Principal {
	static Base BD;
	static MatchInfo MT;
	static HistoryInfo HT;
	static int Id;
	static String SERVER;
	static String key;
	public static void main(String[] arg){
	  SERVER = arg[0].toUpperCase();
	  key = arg[1];
		int iteracion=0;
		while(iteracion<10000){
			System.out.println(iteracion);
			iteracion++;
			try{
			  BD= new Base(SERVER);
			}catch (Exception e)
			{e.printStackTrace(System.out);}
			try{
			  Id=BD.lastSummoner();
			}catch (Exception e)
			{e.printStackTrace(System.out);}
			System.out.println("Historial de:"+Integer.toString(Id));
			HT=new HistoryInfo(Id,SERVER);
			HT.setAPIKey(key);
			try{
				long[] idpart = HT.getId();
				for(int i=0;i<idpart.length;i++){
					if ((idpart[i])!=0 && !BD.existePartido(idpart[i])){
					  System.out.println("Partido: "+Long.toString(idpart[i]));
						MT=new MatchInfo(idpart[i],SERVER);
						MT.setAPIKey(key);
						MT.prepare();
						MT.process();
						BD.agregarDatos(MT.partido,MT.jugadores,MT.summoners,MT.equipos,MT.kills);
					}
				}
			}catch(Exception e){e.printStackTrace(System.out);}
		}
	}
}
