<?php
//gets gameID, teamAscore, teamBscore, teamAHost, mode(0 for hp, 1 for snd, 2 for ctf, 3 for uplink), matchID
include("dbConnect.php");
//include("debug.php");
//first, we need to update the map count for this match
$matchUpdateQuery = $mysqli->prepare("SELECT a_map_count, b_map_count from match_table where id = ?");
//echo $mysqli->error;
$matchUpdateQuery->bind_param('i', $_POST[MatchID]);
$matchUpdateQuery->execute();
$matchUpdateQuery->store_result();
$matchUpdateQuery->bind_result($aMapCount, $bMapCount);
$matchUpdateQuery->fetch();
//echo 'mapcounts were: ' . $aMapCount . " - " . $bMapCount;
//echo '<br>new mapcounts are: ' . $_POST[teamAScore] > $_POST[teamBScore] ? $aMapCount + 1: $aMapCount . " - " . $_POST[teamBScore] > $_POST[teamAScore] ? $bMapCount + 1 : $bMapCount;

$aMapCount = $_POST[TeamAScore] > $_POST[TeamBScore] ? $aMapCount + 1: $aMapCount;
$bMapCount = $_POST[TeamBScore] > $_POST[TeamAScore] ? $bMapCount + 1 : $bMapCount;


$matchUpdateQuery = $mysqli->prepare("UPDATE match_table SET a_map_count = ?, b_map_count = ? WHERE id = ?");
$matchUpdateQuery->bind_param('iii', $aMapCount, $bMapCount, $_POST[MatchID]);

$matchUpdateQuery->execute();
$matchUpdateQuery->store_result();
$modeID = 0;

$aVictory = $_POST[TeamAScore] > $_POST[TeamBScore] ? 1 : 0;
$aScore = (int) $_POST[TeamAScore];
$bScore = (int) $_POST[TeamBScore];

if($_POST[Mode] == 0)//hp
{
	
	$modeIDQuery = $mysqli->prepare("INSERT INTO hp (game_id, team_a_score, team_b_score, team_a_host, team_b_host, game_time, a_victory) VALUES (?, ?, ?, ?, ?, ?, ?)");
	echo $mysqli->error;
	$modeIDQuery->bind_param('iiiiiii', $_POST[GameID], $aScore , $bScore, $_POST[aHost], $_POST[bHost], $_POST[Time], $aVictory);
	$modeIDQuery->execute();
	$modeIDQuery->store_result();
	$modeID = $mysqli->insert_id;
}
else if($_POST[Mode] == 1)//snd
{
	$modeIDQuery = $mysqli->prepare("INSERT INTO snd (game_id, team_a_score, team_b_score, team_a_host, team_b_host, game_time, a_victory) VALUES (?, ?, ?, ?, ?, ?, ?)");
	$modeIDQuery->bind_param('iiiiiii', $_POST[GameID], $aScore , $bScore, $_POST[aHost], $_POST[bHost], $_POST[Time], $aVictory);
	$modeIDQuery->execute();
	$modeIDQuery->store_result();
	$modeID = $mysqli->insert_id;
}
else if($_POST[Mode] == 2)//ctf
{
	$modeIDQuery = $mysqli->prepare("INSERT INTO ctf (game_id, team_a_score, team_b_score, team_a_host, team_b_host, game_time, a_victory) VALUES (?, ?, ?, ?, ?, ?, ?)");
	$modeIDQuery->bind_param('iiiiiii', $_POST[GameID], $aScore , $bScore, $_POST[aHost], $_POST[bHost], $_POST[Time], $aVictory);
	$modeIDQuery->execute();
	$modeIDQuery->store_result();
	$modeID = $mysqli->insert_id;
}
else if($_POST[Mode] == 3)//uplink
{
	$modeIDQuery = $mysqli->prepare("INSERT INTO uplink (game_id, team_a_score, team_b_score, team_a_host, team_b_host, game_time, a_victory) VALUES (?, ?, ?, ?, ?, ?, ?)");
	$modeIDQuery->bind_param('iiiiiii', $_POST[GameID], $aScore , $bScore, $_POST[aHost], $_POST[bHost], $_POST[Time], $aVictory);
	$modeIDQuery->execute();
	$modeIDQuery->store_result();
	$modeID = $mysqli->insert_id;
}

echo $modeID;
?>