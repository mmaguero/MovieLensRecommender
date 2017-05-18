# MovieLensRecommender
[GIW-MII-UGR-2016-2017] Desarrollo de un Sistema de Recomendación basado en Filtrado Colaborativo "User Based" desde cero y con Mahout Taste

## Desde cero
El Sistema de Recomendación basado en Filtrado Colaborativo consta de un recomendador que obtiene las valoraciones de películas de un usuario, además de su vecindario, a partir de la base de datos MovieLens, optando utilizar la correlación de Pearson. Luego en base a lo anterior, sugiere las películas recomendadas.
Las películas presentadas al azar al usuario se obtienen del fichero u.item y los vecindarios se crearán a partir de las valoraciones que ha hecho el usuario activo y de las contenidas en u.data. Se consideran vecindarios de tamaño 10.

## Mahout
Antes que nada debemos convertir nuestro proyecto Java a uno Maven para agregar las dependencias de Mahout al pom.xml:

```
<dependency>
  <groupId>org.apache.mahout</groupId>
  <artifactId>mahout-mr</artifactId>
  <version>0.13.0</version>
</dependency>
```

Acto seguido, debemos convertir el fichero u.data a uno separados por comas, para que Mahout pueda trabajar con él, una vez migremos el contenido de este fichero a un CSV, debemos agregar las valoraciones del usuario activo al mismo fichero con el ID disponible: 944 (puesto que existen 943 usuarios). Ya hecho todo esto, se procede a hacer el cálculo sobre el usuario activo 944, con los mismos parámetros utilizados en el sistema de recomendación previamente creado. Una estructura muy básica para implementar un sistema de recomendación “user based” en Mahout sería la siguiente:

```
DataModel model = new FileDataModel(new File("/path/to/dataset.csv"));
UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood,
similarity);
List recommendations = recommender.recommend(1, 3);
for (RecommendedItem recommendation : recommendations) {
System.out.println(recommendation);
}
```
