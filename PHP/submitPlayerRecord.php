<?php
//mode(0 for hp, 1 for snd, 2 for ctf, 3 for uplink)
include("dbConnect.php");
//include ("debug.php");
//will have: hpID, playerID, kills, deaths, defends, captures, host
$playerInsertID = 0;
if($_POST[Mode] == 0)//hp
{
	$playerIDQuery = $mysqli->prepare("INSERT INTO hp_player (hp_id, player_id, kills, deaths, defends, captures, host) VALUES (?, ?, ?, ?, ?, ?, ?)");
	$playerIDQuery->bind_param('iiiiiii', $_POST[ModeID], $_POST[PlayerID] , $_POST[Kills] == -1?NULL:$_POST[Kills], $_POST[Deaths] == -1?NULL:$_POST[Deaths], $_POST[Obj2] == -1?NULL:$_POST[Obj2], $_POST[Obj1] == -1?NULL:$_POST[Obj1], $_POST[Host] == -1?NULL:$_POST[Host]);
	$playerIDQuery->execute();
	$playerInsertID = $mysqli->insert_id;
}
//will have: sndId, playerID, plants, kills, deaths, host, defuses
else if($_POST[Mode] == 1)//snd
{
	$playerIDQuery = $mysqli->prepare("INSERT INTO snd_player (snd_id, player_id, kills, deaths, plants, defuse, host, last_man_standing, first_bloods) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
	//echo $mysqli->error;
	$playerIDQuery->bind_param('iiiiiiiii', $_POST[ModeID], $_POST[PlayerID] , $_POST[Kills] == -1?NULL:$_POST[Kills], $_POST[Deaths] == -1?NULL:$_POST[Deaths], $_POST[Obj1] == -1?NULL:$_POST[Obj1], $_POST[Obj2] == -1?NULL:$_POST[Obj2], $_POST[Host] == -1?NULL:$_POST[Host], $_POST[Obj3] == -1?NULL:$_POST[Obj3], $_POST[Fb] == -1?NULL:$_POST[Fb]);
	$playerIDQuery->execute();
	$playerInsertID = $mysqli->insert_id;
}
//will have: ctfID, playerID, defends, captures, returns, host, kills, deaths
else if($_POST[Mode] == 2)//ctf
{
	$playerIDQuery = $mysqli->prepare("INSERT INTO ctf_player (ctf_id, player_id, kills, deaths, captures, defends, returns, host) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
	$playerIDQuery->bind_param('iiiiiiii', $_POST[ModeID], $_POST[PlayerID] , $_POST[Kills] == -1?NULL:$_POST[Kills], $_POST[Deaths] == -1?NULL:$_POST[Deaths], $_POST[Obj1] == -1?NULL:$_POST[Obj1], $_POST[Obj2] == -1?NULL:$_POST[Obj2], $_POST[Obj3] == -1?NULL:$_POST[Obj3], $_POST[Host] == -1?NULL:$_POST[Host]);
	$playerIDQuery->execute();
	$playerInsertID = $mysqli->insert_id;
}
//will have: uplinkID, playerID, kills, deaths, uplinks, host
else if($_POST[Mode] == 3)//uplink
{
	$playerIDQuery = $mysqli->prepare("INSERT INTO uplink_player (uplink_id, player_id, kills, deaths, uplinks, host) VALUES (?, ?, ?, ?, ?, ?)");
	$playerIDQuery->bind_param('iiiiii', $_POST[ModeID], $_POST[PlayerID] , $_POST[Kills] == -1?NULL:$_POST[Kills], $_POST[Deaths] == -1?NULL:$_POST[Deaths], $_POST[Obj1] == -1?NULL:$_POST[Obj1], $_POST[Host] == -1?NULL:$_POST[Host]);
	$playerIDQuery->execute();
	$playerInsertID = $mysqli->insert_id;
}
echo $playerInsertID;

?>