<?php
//get the map_mode ID and game ID (for the map/mode thingy)
//given mapName, mode POST
include("dbConnect.php");

$mapIDQuery = $mysqli->prepare("SELECT id FROM  map WHERE STRCMP(name, ?) = 0");
$mapIDQuery->bind_param('s', $_POST[MapName]);
$mapIDQuery->execute();
$mapIDQuery->store_result();
$mapIDQuery->bind_result($mapID);
$mapIDQuery->fetch();


$modeIDQuery = $mysqli->prepare("SELECT id FROM  mode WHERE STRCMP(name, ?) = 0");
$modeIDQuery->bind_param('s', $_POST[Mode]);
$modeIDQuery->execute();
$modeIDQuery->store_result();
$modeIDQuery->bind_result($modeID);
$modeIDQuery->fetch();


$mapModeIDQuery = $mysqli->prepare("SELECT id FROM  map_mode WHERE map_id = ? AND mode_id = ?");
$mapModeIDQuery->bind_param('ii', $mapID, $modeID);
$mapModeIDQuery->execute();
$mapModeIDQuery->store_result();
$mapModeIDQuery->bind_result($mapModeID);
$mapModeIDQuery->fetch();
echo $mapModeID;
?>