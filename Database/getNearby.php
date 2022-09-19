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
    $maxDist = $_GET["maxDist"];
    $myLAT = $_GET["myLAT"];
    $myLNG = $_GET["myLNG"];
    
    $q = "SELECT idRistorante, nome, indirizzo, idCitta, telefono, LAT, LNG, categoria, image, rating, isFavorite, 6378137 * 2 * ATAN2(SQRT(a), SQRT(1 - a)) as distanza
          FROM (
                  SELECT idRistorante, nome, indirizzo, idCitta, telefono, LAT, LNG, categoria, image, rating, isFavorite,
                          SIN(dlat / 2) * sin(dlat / 2) +
                          COS(RADIANS(LAT)) * COS(RADIANS($myLAT)) *
                          SIN(dlng / 2) * SIN(dlng / 2) as a
                  FROM (
                          SELECT idRistorante, nome, indirizzo, idCitta, telefono, LAT, LNG, categoria, image, AVG(voto) as rating, MAX(if(valtAdv_preferiti.idUtente = $idUtente,1,0)) as isFavorite,
                                  RADIANS($myLAT - LAT) as dlat, RADIANS($myLNG - LNG) as dlng
                          FROM `valtAdv_ristoranti`
                          LEFT JOIN valtAdv_recensioni USING(idRistorante)
                          LEFT JOIN valtAdv_preferiti USING(idRistorante)
                          GROUP BY valtAdv_ristoranti.idRistorante 
                          ORDER BY valtAdv_ristoranti.idRistorante
                       ) AS T1
               ) AS T2
          HAVING distanza < $maxDist;";
    $ris = mysqli_fetch_all(mysqli_query($con, $q), MYSQLI_ASSOC);
    //var_dump($ris);
    echo json_encode($ris);

?>