import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.net.ssl.HttpsURLConnection;


public class Networking {
	String missingLog = "Null Responses: \n";
	String callLog = "Call Log: \n";
	int nullResponses = 0;
	String baseURL = "http://www.codstreams.net/statUpload/";
	public int submitPlayer(int playerID, int modeID, int mode, int kills, int deaths, 
			int obj1, int obj2, int obj3, int fb, int host) throws Exception
	{
		String post = "PlayerID=" + Integer.toString(playerID) + "&Mode=" + Integer.toString(mode)
				+ "&ModeID=" + Integer.toString(modeID) + "&Kills=" + Integer.toString(kills)
				+ "&Deaths=" + Integer.toString(deaths) + "&Obj1=" + Integer.toString(obj1)
				+ "&Obj2=" + Integer.toString(obj2) + "&Obj3=" + Integer.toString(obj3)
				+ "&Fb=" + Integer.toString(fb) + "&Host=" + Integer.toString(host);
		String url = baseURL + "submitPlayerRecord.php";
		return sendRequest(url, post);
	}
	public int getMatchID(int day, int rosterAID, int rosterBID, int matchTypeID, int eventID, int scoreTypeID) throws Exception
	{
		//$_POST[EventID], $_POST[RosterAID], $_POST[RosterBID], $_POST[MatchTypeID]);
		String post = "EventID=" + Integer.toString(eventID) + "&RosterAID=" + Integer.toString(rosterAID)
				+ "&RosterBID=" + Integer.toString(rosterBID) + "&MatchTypeID=" + Integer.toString(matchTypeID)
				+ "&Day=" + Integer.toString(day) + "&ScoreTypeID=" + Integer.toString(scoreTypeID);
		String url = baseURL + "getMatchID.php";
		return sendRequest(url, post);
	}
	public int getGameID(int matchID, int mapModeID, int scoreTypeID, int gameNum) throws Exception
	{
		//[MatchID], $_POST[GameNum] , $_POST[MapModeID], $_POST[ScoreTypeID]);
		String post = "MatchID=" + Integer.toString(matchID) + "&MapModeID=" + Integer.toString(mapModeID)
				+ "&ScoreTypeID=" + Integer.toString(scoreTypeID) + "&GameNum=" + Integer.toString(gameNum);
		String url = baseURL + "getGameID.php";
		return sendRequest(url, post);
	}
	public int getModeID(int gameID, double aScore, double bScore, int aHost, int bHost, int gameModeID, int matchID, int time) throws Exception
	{
		int aScoreInt = (int) aScore, bScoreInt = (int) bScore;
		String post = "GameID=" + Integer.toString(gameID) + "&TeamAScore=" + Integer.toString(aScoreInt)
				+ "&TeamBScore=" + Integer.toString(bScoreInt) + "&aHost=" + Integer.toString(aHost)
				+ "&bHost=" + Integer.toString(bHost)
				+ "&Mode=" + Integer.toString(gameModeID) + "&MatchID=" + Integer.toString(matchID)
				+ "&Time=" + Integer.toString(time);
		String url = baseURL + "getModeID.php";
		return sendRequest(url, post);
	}
	public int getRosterID(String teamName, int eventID) throws Exception
	{
		String url = baseURL + "getRosterID.php";
		String post = "TeamName=" + teamName + "&EventID=" + Integer.toString(eventID);
		return sendRequest(url, post);
	}
	public int getPlayerID(String playerName) throws Exception
	{
		String url = baseURL + "getPlayerID.php";
		String post = "PlayerName=" + playerName;
		return sendRequest(url, post);
	}
	public int getMapModeID(String map, String mode) throws Exception
	{
		String url = baseURL + "getMapModeID.php";
		String post = "MapName=" + map + "&Mode=" + mode;
		return sendRequest(url, post);
	}
	
	//HTTP POST request
	private int sendRequest(String url, String urlParameters) throws Exception {
		final String USER_AGENT = "Mozilla/5.0";
		//String url = "https://selfsolve.apple.com/wcResults.do";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		//String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		callLog += "\nSending 'POST' request to URL : " + url + "\n"
				+ "Post parameters : " + urlParameters + "\n"
				+ "Response Code : " + responseCode + "\n";
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		//System.out.println("response was: " + response.toString());
		if(Integer.parseInt(response.toString()) == 0)//if it returned zero, add record to the log to be written 
		{
			String logStr = "\nRequest: " + urlParameters + "\nURL: " + url + "\nResponse: " + response.toString() + "\n";  
			missingLog += logStr;
			nullResponses++;
		}
		return Integer.parseInt(response.toString());

		}
}
