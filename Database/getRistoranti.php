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
    
    if(isset($_GET["nome"])){
    	$nome = $_GET["nome"];
        $q = "SELECT idRistorante, nome, indirizzo, idCitta, telefono, LAT, LNG, categoria, image, AVG(voto) as rating, MAX(if(valtAdv_preferiti.idUtente = $idUtente,1,0)) as isFavorite
            FROM `valtAdv_ristoranti`
            LEFT JOIN valtAdv_recensioni USING(idRistorante)
            LEFT JOIN valtAdv_preferiti USING(idRistorante)
            WHERE nome LIKE '%$nome%'
            GROUP BY valtAdv_ristoranti.idRistorante 
            ORDER BY rating DESC
            LIMIT 30;";
    }
    else if(isset($_GET["idCitta"])){
    	$idCitta = $_GET["idCitta"];
        $q = "SELECT idRistorante, nome, indirizzo, idCitta, telefono, LAT, LNG, categoria, image, AVG(voto) as rating, MAX(if(valtAdv_preferiti.idUtente = $idUtente,1,0)) as isFavorite
            FROM `valtAdv_ristoranti`
            LEFT JOIN valtAdv_recensioni USING(idRistorante)
            LEFT JOIN valtAdv_preferiti USING(idRistorante)
            WHERE idCitta = $idCitta
            GROUP BY valtAdv_ristoranti.idRistorante 
            ORDER BY rating DESC;";
    }
    else if(isset($_GET["idRistorante"])){
    	$idRistorante = $_GET["idRistorante"];
        $q = "SELECT idRistorante, nome, indirizzo, idCitta, telefono, LAT, LNG, categoria, image, AVG(voto) as rating, MAX(if(valtAdv_preferiti.idUtente = $idUtente,1,0)) as isFavorite
            FROM `valtAdv_ristoranti`
            LEFT JOIN valtAdv_recensioni USING(idRistorante)
            LEFT JOIN valtAdv_preferiti USING(idRistorante)
            WHERE idRistorante = $idRistorante
            GROUP BY valtAdv_ristoranti.idRistorante;";
    }
    $ris = mysqli_fetch_all(mysqli_query($con, $q), MYSQLI_ASSOC);
    //var_dump($ris);
    echo json_encode($ris);

?>