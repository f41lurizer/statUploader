<?php
include("dbConnect.php");
//echo 'searching for' . $_POST[TeamName];
$teamIDQuery = $mysqli->prepare("SELECT id FROM  team WHERE STRCMP(name, ?) = 0");
$teamIDQuery->bind_param('s', $_POST[TeamName]);
$teamIDQuery->execute();
$teamIDQuery->store_result();
$teamIDQuery->bind_result($teamID);
$teamIDQuery->fetch();

$rosterIDQuery = $mysqli->prepare("SELECT roster_id FROM roster_to_event WHERE team_id = ? AND event_id = ?");
$rosterIDQuery->bind_param('ii', $teamID, $_POST[EventID]);
$rosterIDQuery->execute();
$rosterIDQuery->store_result();
$rosterIDQuery->bind_result($rosterID);
$rosterIDQuery->fetch();
echo $rosterID;

?>