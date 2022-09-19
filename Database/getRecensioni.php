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

	if(isset($_GET["idRistorante"])){
	$idRistorante = $_GET["idRistorante"];
    $q = "SELECT idRecensione, idRistorante, valtAdv_ristoranti.nome AS nomeRistorante, data, voto, commento, username, colore
    		FROM valtAdv_recensioni
            INNER JOIN valtAdv_utenti USING (idUtente)
            INNER JOIN valtAdv_ristoranti USING (idRistorante)
            WHERE idRistorante = $idRistorante
            ORDER BY data";
    }
    else if(isset($_GET["idUtente"])){
	$idUtente = $_GET["idUtente"];
    $q = "SELECT idRecensione, idRistorante, valtAdv_ristoranti.nome AS nomeRistorante, data, voto, commento, username, colore
    		FROM valtAdv_recensioni
            INNER JOIN valtAdv_utenti USING (idUtente)
            INNER JOIN valtAdv_ristoranti USING (idRistorante)
            WHERE idUtente = $idUtente
            ORDER BY data";
    }
    $ris = mysqli_fetch_all(mysqli_query($con, $q), MYSQLI_ASSOC);
    //var_dump($ris);
    echo json_encode($ris, JSON_UNESCAPED_UNICODE);

?>