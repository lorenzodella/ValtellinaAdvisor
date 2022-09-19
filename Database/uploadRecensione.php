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

	$idRistorante = $_POST["idRistorante"];
    $idUtente = $_POST["idUtente"];
    $data = $_POST["data"];
    $voto = $_POST["voto"];
	$commento = urldecode($_POST["commento"]);
    $commento = str_replace("'", "\'", $commento);
    $q = "INSERT INTO valtAdv_recensioni (idRistorante, idUtente, data, voto, commento) VALUES ($idRistorante, $idUtente, '$data', $voto, '$commento');";
    $ris = mysqli_query($con, $q);
    if(!$ris)
    	echo "error";
    else
    	var_dump($ris);
    //echo json_encode($ris, JSON_UNESCAPED_UNICODE);

?>