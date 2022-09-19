<?php
	$host="localhost";
    $dbUser="root";
    $dbPwd="root";
    $dbName="my_dellamateralorenzo";

    $con=mysqli_connect($host, $dbUser, $dbPwd, $dbName);
    if(!$con)
    {
        die("Errore di connessione al database");
    }
    //echo "Connesso al database $dbName";
    //echo "<br><br>";
    
    mysqli_set_charset($con, "utf8");

	$idRecensione = $_GET["idRecensione"];
  	$q = "DELETE FROM valtAdv_recensioni WHERE idRecensione = $idRecensione;";
    $ris = mysqli_query($con, $q);
    if(!$ris)
    	echo "error";
    else
    	var_dump($ris);
    //echo json_encode($ris, JSON_UNESCAPED_UNICODE);

?>