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
    
    $idUtente = $_GET["idUtente"];
    
    $q = "SELECT idRistorante, nome, indirizzo, idCitta, telefono, LAT, LNG, categoria, image, AVG(voto) as rating, MAX(if(valtAdv_preferiti.idUtente = $idUtente,1,0)) as isFavorite
            FROM `valtAdv_ristoranti`
            LEFT JOIN valtAdv_recensioni USING(idRistorante)
            LEFT JOIN valtAdv_preferiti USING(idRistorante)
            GROUP BY valtAdv_ristoranti.idRistorante 
            HAVING isFavorite = 1
            ORDER BY valtAdv_ristoranti.idRistorante;";
    $ris = mysqli_fetch_all(mysqli_query($con, $q), MYSQLI_ASSOC);
    //var_dump($ris);
    echo json_encode($ris);

?>