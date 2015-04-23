<?php
//gets rosterAID, rosterBID, matchTypeID (bracketID), eventID, day
include("dbConnect.php");
//include("debug.php");

//first, bracket ID should be hardwired into java, sent POST
$matchIDQuery = $mysqli->prepare("SELECT id, a_map_count, b_map_count FROM  match_table WHERE match_type_id = ? AND day = ? AND (roster_a_id = ? OR roster_b_id = ?) AND (roster_a_id = ? OR roster_b_id = ?)");
$matchIDQuery->bind_param('iiiiii', $_POST[MatchTypeID], $_POST[Day], $_POST[RosterAID] , $_POST[RosterAID], $_POST[RosterBID], $_POST[RosterBID]);
$matchIDQuery->execute();
$matchIDQuery->store_result();
$matchIDQuery->bind_result($matchID, $aMap, $bMap);

$makeNew = 1;
if($matchIDQuery->num_rows > 0)
{
	$matchIDQuery->fetch();
	$makeNew = ($aMap >= 3 || $bMap >= 3) ? 1 : 0;

}
else if($makeNew == 1)
{
	$matchIDQuery = $mysqli->prepare("INSERT INTO match_table (event_id, roster_a_id, roster_b_id, match_type_id, day, a_map_count, b_map_count, score_type_id) VALUES (?, ?, ?, ?, ?, 0, 0, ?)");
	$matchIDQuery->bind_param('iiiiii', $_POST[EventID], $_POST[RosterAID], $_POST[RosterBID], $_POST[MatchTypeID], $_POST[Day], $_POST[ScoreTypeID]);
	$matchIDQuery->execute();
	$matchID = $mysqli->insert_id;

}
echo $matchID;
?>