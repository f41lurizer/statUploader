import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;


public class Uploader {
/* to do 
: 
 * 1. add/call a setHost method for team host
 * 2. add distinguishing of SnD to add FB's, otherwise do I default to 0, or exclude?
 * 3. increment bufferReads every time buffer reads input	
 */
	//command line arguments are: fileName, matchTypeID(bracket), eventID 
	static int bufferReads, maps;
	public static void main(String[] args) throws Exception {
		File input = null;// = new File("input.csv");
		int matchTypeID = 0, streamedMaps = 0, offStreamMaps = 0;
		int eventID = 0;
		int scoreTypeID = 0;
		if(args.length > 0)
		{
			input = new File(args[0]);
			matchTypeID = Integer.parseInt(args[1]); //bracket, need to set at start
			eventID = Integer.parseInt(args[2]);
		}
		//arraylists to store all the missing players and rosters (no duplicates) 
		ArrayList<String> missingPlayers = new ArrayList<String>();
		ArrayList<String> missingRosters = new ArrayList<String>();
		//initialize log file paths
		Path missingLogFile = Paths.get("logs/missing.log");
		Path callLogFile = Paths.get("logs/calls.log");
		Path mapsLogFile = Paths.get("logs/maps.log");
		Path statsLogFile = Paths.get("logs/stats.log");
		//initialize log variables
		String mapLog = "";
		String statsLog = "";
		Files.deleteIfExists(missingLogFile);
		Files.deleteIfExists(callLogFile);
		Files.deleteIfExists(mapsLogFile);
		Files.deleteIfExists(statsLogFile);
		Files.createFile(missingLogFile);
		Files.createFile(callLogFile);
		Files.createFile(mapsLogFile);
		Files.createFile(statsLogFile);
		

		int iter = 0;
		bufferReads = maps = 0;
		BufferedReader br = new BufferedReader(new FileReader(input));
		Networking net = new Networking();
		//while(bufferReads < 14781)
		while(br.ready())
		{
			//if(br.ready() == false)
				//System.out.println("bufferreader not ready");
			//System.out.println("Made it to outer loop" + iter);
			iter++;
			Map map;
			map = getMap(br);
			if(map != null)
			{
				maps++;
				
				mapLog += map.toString() + " Entry Number: " + maps + "\n";
				mapLog += "    " + map.teams[0].toString() + "\n";
				mapLog += "    " + map.teams[1].toString() + "\n";
				if(map.onStream)
				{
					streamedMaps++;
					scoreTypeID = 1;
					for(int i = 0; i < 4; i++)
						mapLog += "        " + map.teams[0].players[i] + "\n";
					for(int i = 0; i < 4; i++)
						mapLog += "        " + map.teams[1].players[i] + "\n";
				}
				if(!map.onStream)
				{
					offStreamMaps++;
					scoreTypeID = 3;
				}
				//now the networking part
				int rosterAID = net.getRosterID(map.teams[0].name, eventID);
				int rosterBID = net.getRosterID(map.teams[1].name, eventID);
				//if(rosterAID == 0)
					//System.out.println("Missing roster: ") + map.teams
				if(rosterAID == 0 && !missingRosters.contains(map.teams[0].name))
					missingRosters.add(map.teams[0].name);
				if(rosterBID == 0 && !missingRosters.contains(map.teams[1].name))
					missingRosters.add(map.teams[1].name);
				//String map, String mode
				int mapModeID = net.getMapModeID(map.map, map.mode);
				int matchID = net.getMatchID(map.day, rosterAID, rosterBID, matchTypeID, eventID, scoreTypeID);
				int gameID = net.getGameID(matchID, mapModeID, scoreTypeID, map.num);
				//int gameID, int aScore, int bScore, int aHost, int gameModeID
				int modeID = net.getModeID(gameID, map.teams[0].score, map.teams[1].score, map.teams[0].host?1:0, map.teams[1].host?1:0,
						getModeID(map.mode), matchID, map.time);
				if(scoreTypeID == 1)
				{
					for(int i = 0; i < 4; i++)
					{
						//int playerID, int modeID, int mode, int kills, int deaths, 
						//int obj1, int obj2, int obj3, int fb, int host
						Player a = map.teams[0].players[i];
						Player b = map.teams[1].players[i];
						int aID = net.getPlayerID(a.name);
						int bID = net.getPlayerID(b.name);
						//if the playerID is zero, log to ArrayList
						if(aID == 0 && !missingPlayers.contains(a.name))
							missingPlayers.add(a.name);
						if(bID == 0 && !missingPlayers.contains(b.name))
							missingPlayers.add(b.name);
						
						int playerRecordA = net.submitPlayer(aID, modeID, getModeID(map.mode), a.kills, a.deaths, a.obj1, 
								a.obj2, a.obj3, a.fb, a.host?1:0);
						int playerRecordB = net.submitPlayer(bID, modeID, getModeID(map.mode), b.kills, b.deaths, b.obj1, 
								b.obj2, b.obj3, b.fb, b.host?1:0);
						//System.out.println("submitted player records: " + playerRecordA + " and: " + playerRecordB);
					}
					//System.out.println("********** end of map*****************\n");
				}
			}
			else if (map == null)
			{
				//System.out.println("*********** NULL WAS RETURNED******** in record number: " + iter);
			}
			
		}
		//the final log string to be written to file: 
		String missingLog = "";
		//generate missing players string
		String missingPlayersLog = "\n\nMissing Players: \n";
		Iterator<String> itr = missingPlayers.iterator();
		while(itr.hasNext())
		{
			String str = itr.next() + "\n";
			missingPlayersLog += str;
		}
		String missingRostersLog = "\n\nMissing Rosters: \n"; 
		itr = missingRosters.iterator();
		while(itr.hasNext())
		{
			String str = itr.next() + "\n";
			missingRostersLog += str;
		}
		//concatenate all logs to the final log file and write to missing.log file
		missingLog += net.missingLog + missingPlayersLog + missingRostersLog;
		byte[] buf = missingLog.getBytes();
		Files.write(missingLogFile,  buf,  StandardOpenOption.TRUNCATE_EXISTING);
		
		//write call log from net to file
		buf = net.callLog.getBytes();
		Files.write(callLogFile, buf, StandardOpenOption.TRUNCATE_EXISTING);
		
		//write map log to file
		buf = mapLog.getBytes();
		Files.write(mapsLogFile, buf, StandardOpenOption.TRUNCATE_EXISTING);
		
		//set statistics log		
		statsLog += "\nUpload Statistics: \n";
		statsLog += "There were: " + net.nullResponses + " null responses\n";
		statsLog += "There were: " + missingPlayers.size() + " unidentified players\n";
		statsLog += "There were: " + missingRosters.size() + " unidentified rosters\n";
		statsLog += "There were: " + (streamedMaps + offStreamMaps) + " Total Maps\n";
		statsLog += "There were: " + streamedMaps + " Streamed maps\n";
		statsLog += "There were: " + offStreamMaps + " Off Stream maps\n";
		statsLog += "Read in:  " + bufferReads + " lines of input\n";
		
		//output statistics log to file and console
		System.out.print(statsLog);
		buf = statsLog.getBytes();
		Files.write(statsLogFile, buf, StandardOpenOption.TRUNCATE_EXISTING);
		
	}
	
	
	public static Map getMap(BufferedReader br) throws IOException
	{
		Map map = new Map();
		
		//process input line
		
		String testInput = br.readLine(); bufferReads++;
		if(testInput == null)
			return null;
		
		String[] split = testInput.split(",", -1);
			
		while(split[7].equals("") || split[7].equals("0"))
		{
			testInput = br.readLine(); bufferReads++;
			/*if(testInput == null)
				return null;
				*/
			split = testInput.split(",", -1);
		}
		map.startingLine = bufferReads;
		/*for(int z = 0; z < split.length; z++)
		{
			System.out.println("split " + z + ": *" + split[z] + "*");
		}*/
		//System.out.println("input line: " + testInput);
		//String [] split = br.readLine().split(",", -1);
	   	//currently, 7th element is what shows the On/Off stream stuff, so we check that
		if(split[7].equals("Off Stream"))
		{	
			int linesRead = 1; /*there are 9 lines in an entry. setting linesread = 1 means that
			 					we have read the first line of actual input for the map*/
			//System.out.println("made it to offstream");
			map.onStream = false;
			if(split[4].equals("0") || split[4].equals("")) //if stream information is above actual input, read next line
			{
				split = br.readLine().split(",", -1); bufferReads++;
				
			}
			map.day = Integer.parseInt(split[0]);
			map.num = Integer.parseInt(split[1]);
			map.mode = getModeString(split[2]);
			map.map = split[3];
			map.time = getTimeInSeconds(split[6]);
			
			map.teams[0].name = split[4];
			//set the score, if it's "" then set to 0
			map.teams[0].score = 0;
			if(!split[5].equals(""))
				map.teams[0].score = Double.parseDouble(split[5]);
			//no player information to read in, we just need the first line that contains a team name, we have rest
			split = br.readLine().split(",", -1); bufferReads++; linesRead++;
			
			while(split[4].equals(""))
			{
				split = br.readLine().split(",", -1); bufferReads++; linesRead++;
				
			}
			//once we get to a row with team 2 information
			map.teams[1].name = split[4];
			//set the score, if it's "" then set to 0
			map.teams[1].score = 0;
			if(!split[5].equals(""))
				map.teams[1].score = Double.parseDouble(split[5]);
			for(;linesRead <= 9; linesRead++)
			{
				br.readLine(); bufferReads++;
			}
		}
		else if(split[7].equals("On Stream"))
		{
			//System.out.println("made it to onstream");
			map.onStream = true;
			if(split[9].equals("0") || split[9].equals("")) //if stream information is above actual input, read next line
			{
				split = br.readLine().split(",", -1); bufferReads++;
			}
			//now we have our first line of "input" for the match
			if(split[4].equals("") || split[4].equals("0")) //it's an empty map, dont' bother with it
			{
				//System.out.println("returning null from onStream for match that starts on: " + bufferReads + " " + map.startingLine);
				for(int i = 0; i < 8; i++)
				{
					br.readLine(); bufferReads++;
				}
				return null;
			}
			//now we are on line 1 of the actual input
			map.day = Integer.parseInt(split[0]);
			map.num = Integer.parseInt(split[1]);
			map.mode = getModeString(split[2]);
			map.map = split[3];
			map.time = getTimeInSeconds(split[6]);//need to convert to integer time
			Team team = map.teams[0];
			map.teams[0].name = split[4];
			//set the score, if it's "" then set to 0
			map.teams[0].score = 0;
			if(!split[5].equals(""))
				map.teams[0].score = Double.parseDouble(split[5]);
			
			//read in player information
			setPlayer(split, map.teams[0].players[0]); //player 1A
			
			//now we read line 2 of the input
			split = br.readLine().split(",", -1); bufferReads++;
			
		
			//now we evaluate if it's the 2 2 2 or 1 - - 2 style of input
			
			if(split[4].equals("") || split[4].equals("0"))//if it's 1 - - 2 
			{
				String[] subSplit = new String[split.length];
				for(int i = 0; i < split.length; i++)//copy by value
				{//bracket, need to set at start
					subSplit[i] = split[i];
				}
				
				for(int i = 1; subSplit[4].equals("") && i < 3; i++)
				{
					setPlayer(subSplit, map.teams[0].players[i]);
					subSplit = br.readLine().split(",", -1); bufferReads++;
					
				}
				//we have a new split from loop
				if(!subSplit[4].equals(""))
				{
					//it's the 2 2 2 format so we have to get team 2's information
					map.teams[1].name = subSplit[4];
					//System.out.println("*** line: " + bufferReads + " of input file");
					//set the score, if it's "" then set to 0
					map.teams[1].score = 0;
					if(!subSplit[5].equals(""))
						map.teams[1].score = Double.parseDouble(subSplit[5]);
					setPlayer(subSplit, map.teams[0].players[3]); //player 1D							
				}
				br.readLine(); bufferReads++;
				setBPlayers(map, br);
			}
			else if(!split[4].equals("") && !split[4].equals("0"))//it's 2 2 2 
			{
				map.teams[1].name = split[4];
				//System.out.println("on line: " + bufferReads);
				map.teams[1].score = (int) Double.parseDouble(split[5]);
				
				setPlayer(split, map.teams[0].players[1]);
				split = br.readLine().split(",", -1); bufferReads++;
				setPlayer(split, map.teams[0].players[2]);
				split = br.readLine().split(",", -1); bufferReads++;
				setPlayer(split, map.teams[0].players[3]);
				br.readLine(); bufferReads++;
				setBPlayers(map, br);
			}
			
		}
		//System.out.println("returning map");
		map.teams[0].setHost();
		map.teams[1].setHost();
		map.setVictor();
		map.setTime();//remove this to return 0 for all unknown game times
		return map;
		
	}
	//player class
	private static class Player
	{
		public String name;
		public int RosterID, id, kills, deaths, obj1, obj2, obj3, fb;
		boolean host;
		public Player()
		{
			host = false;
			kills = deaths = obj1 = obj2 = obj3 = fb = -1;
		}
		public String toString()
		{
			String playerString = "Name: " + name;
			if(kills != -1)
				playerString += "\tKills: " + kills;
			else if(kills == -1)
			playerString += "\tKills: -";
			if(deaths != -1)
				playerString += "\tDeaths: " + deaths;
			else if(deaths == -1)
				playerString += "\tDeaths: -";
			if(obj1 != -1)
				playerString += "\tobj1: " + obj1;
			else if(obj1 == -1)
				playerString += "\tobj1: -";
			if(obj2 != -1)
				playerString += "\tobj2: " + obj2;
			else if(obj2 == -1)
				playerString += "\tobj2: -";
			if(obj3 != -1)
				playerString += "\tobj3: " + obj3;
			else if(obj3 == -1)
				playerString += "\tobj3: -";
			if(fb != -1)
				playerString += "\tfb: " + fb;
			else if(fb == -1)
				playerString += "\tfb: -";
			
			return playerString;
		}
	}
	private static class Team
	{
		public String name;
		public double score;
		public int rosterID; //, score
		public boolean host, victor;
		public Player[] players;
		public Team()
		{
			players = new Player[4];
			for(int i = 0; i < players.length; i++)
			{
				players[i] = new Player();
			}
			host = false;
			victor = false;
		}
		public String toString()
		{
			String teamString = "TeamName: " + name + "\tscore: " + Double.toString(score) + "\tvictor: " 
					+ victor + "\thost: " + host;
			return teamString;
			
		}
		public void setHost()//check all the players on team for host
		{
			for(int i = 0; i < 4; i++)
			{
				if(players[i].host == true)
					host = true;
			}
		}
	}
	//map class
	private static class Map
	{
	
		public String map, mode;
		public int num, day, time, startingLine;
		public boolean onStream;
		public Team[] teams;
		public Map()
		{
			//System.out.println("map constructor");
			teams = new Team[2];
			teams[0] = new Team();
			teams[1] = new Team();
			onStream = true;//make onstream true by default			
		}
		public void setTime()
		{
			if(time == 0)
			{
				//System.out.println("in settime; " + bufferReads);
				if(!mode.equals("SnD"))
					time = 600;
					
			}
				
		}
		public void setVictor()
		{
			if(teams[0].score > teams[1].score)
			{
				teams[0].victor = true;
			}
			else if(teams[1].score > teams[0].score)
			{
				teams[1].victor = true;
			}
		}
		public String toString()
		{
			return "Map: " + map + "\tmode: " + mode + "\tnum: " + Integer.toString(num) + "\ttime: "
					+ Integer.toString(time) + "\tstreamed: " + onStream + "\tday: " + Integer.toString(day) + "\tstarting Line: "
					+ startingLine;
		}
	}
	private static int getTimeInSeconds(String input)
	{
		int time = 0;
		String[] split = input.split("\\.");
		for(int i = 0; i < split.length; i++)
		{
			//System.out.println("split for time was: " + split[i]);
		}
		//System.out.println("split for time at line : " + bufferReads);
		if(input.equals("") || input.toUpperCase().equals("N/A"))//if there is no entry for time
			return 0;
		if(split.length == 0)//if there is no seconds
			return Integer.parseInt(input) * 60;
		time += Integer.parseInt(split[0]) * 60; //number of seconds instead of minutes
		if(split.length == 2)//if there are no seconds entered  don't try to use them
			time += Integer.parseInt(split[1]);
		return time;		
	}
	private static boolean getHost(String str)
	{
		if(str.toUpperCase().equals("HOST"))
			return true;
		else if(str.toUpperCase().equals("NOTHOST"))
			return false;
		return false;
	}
	private static void setPlayer(String[] split, Player player)
	{
		
		player.host = getHost(split[9]);
		player.name = split[10];
		player.kills = !split[11].equals("")?(int) Double.parseDouble(split[11]):0;
		player.deaths = !split[12].equals("")?(int) Double.parseDouble(split[12]):0;
		player.obj1 = !split[13].equals("")?(int) Double.parseDouble(split[13]):0;
		player.obj2 = !split[14].equals("")?(int) Double.parseDouble(split[14]):0;
		player.obj3 = !split[15].equals("")?(int) Double.parseDouble(split[15]):0;
		player.fb = 0;
		if(split.length > 16)
			player.fb = !split[16].equals("")?(int) Double.parseDouble(split[16]):0;
		
		/*if(!split[11].equals(""))
			player.kills = !split[11].equals("")?Integer.parseInt(split[11]):0;
		if(!split[12].equals(""))
			player.deaths = Integer.parseInt(split[12]);
		if(!split[13].equals(""))
			player.obj1 = Integer.parseInt(split[13]);
		if(!split[14].equals(""))
			player.obj2 = Integer.parseInt(split[14]);
		if(!split[15].equals(""))
			player.obj3 = Integer.parseInt(split[15th t);
		if(!split[16].equals(""))
			player.fb = Integer.parseInt(split[16]);
		*/
		return;
	}
	private static void setBPlayers(Map map, BufferedReader br) throws IOException
	{
		for(int i = 0; i < 4; i++)
		{
			String[] split = br.readLine().split(",", -1); bufferReads++;
			Player player = map.teams[1].players[i];
			setPlayer(split, player);
		}
	}
	private static String getModeString(String inMode)
	{
		if(inMode.equals("HP"))
			return "Hardpoint";
		else if(inMode.equals("SnD"))
			return "Search and Destroy";
		else if(inMode.equals("CTF"))
			return "Capture the Flag";
		else if(inMode.equals("Uplink"))
		return inMode;
		return "";
	}
	private static int getModeID(String mode)
	{
		if(mode.equals("Hardpoint"))
			return 0;
		else if(mode.equals("Search and Destroy"))
			return 1;
		else if(mode.equals("Capture the Flag"))
			return 2;
		else if(mode.equals("Uplink"))
			return 3;
		return 4;
	}
}
