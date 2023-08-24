# Ordenar productos

## Author

Cesar Espinosa

## Overview

Implementación de un algoritmo de ordenación expuesto través de un servicio REST que recibe las
métricas y los pesos asociados a cada métrica y devuelve el listado de productos ordenados.

La implementación de este ejercicio sigue patrones de arquitectura Hexagonal.

El tiempo usado para desarrollar la solución fue de 8 horas. .

## Pre-requisitos para desarrollo

- [Git](https://git-scm.com/downloads)
- [JDK 17](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi)

## Tecnologías usadas

- `Kotlin`: Lenguaje conciso y flexibble que corre en la JVM.
- `Spring boot`: Facilita la creación de servicios REST, la inyección de dependencias y la empaquetación del proyecto en
  un fat jar. Además, proporciona una opción para usar caching de manera fácil.
- `Maven`: Facilita la creación de proyectos multi-módulo, con lo cual cada módulo esta totalmente aislado junto con sus
  propias dependencias. Además, la separación multi-módulo ayuda a la separación de capas al estilo arquirectura
  Hexagonal.
- `SpringDoc OpenPI`: Ayuda a la rápida documentación de servicios REST.
- `H2`: Base de datos SQL que guarda todo en memoria.

## Architectura de alto nivel

La arquitectura y distribución de componentes esta basada en el libro  "Get Your Hands Dirty on Clean Architecture"
escrito por Tom Hombergs:

![alt text](https://www.happycoders.eu/wp-content/uploads/2023/06/hexagonal-architecture-java.v4-944x709.png)

- Existe una módulo `application` que contiene todas las reglas y entidades de negocio. Es el corazón de la arquitectura
  y esta totalmente aislada de detalles de infrastructura.
  Este módulo define interfaces o `ports` que definen cómo es la comunicación con el mundo exterior para recibir eventos
  o para enviar información.

- El módulo `adapters` contiene componentes que se comunican con el mundo exterior y usan los puertos (`ports`) para
  enviar y recibir información del corazón de la arquitectura.
  Un ejemplo de adaptador es un servicio REST o un componente que escribe información en una base datos mediante JPA.

Para este ejercicio, se implementó un adaptador REST para recibir las métricas de ordenamiento via HTTP. También se
implementó un adaptador Repository para leer información de una base de datos mediante JPA.

## Suposiciones

- El peso de una métrica puede tener un valor entre 0.0 y 1.0.
- No es necesario que la sumatoria de pesos de todas las métricas sume 1.0. El peso simplemente es una medida de
  importancia de la métrica y el usuario es libre de definir el valor total.
- Los datos estan guardados en una base de datos SQL.
- Se usa paginación para mejorar el rendimiento de la aplicación tanto en el backend como en posibles clientes web.
- Se usa caching para mejorar el rendimeinto de la aplicación y evitar repetir comandos SQL que ya fueron usados.
- La forma de calcular el valor de la métrica "ratio de stock" de un producto
  es: `(cantidadSmall + cantidadMedium + cantidadLarge) / 3`.
- La forma de calcular el peso final de cada producto dados el valor de su métricas y respectivo peso
  es : `(valorMetrica1 * pesoMetrica1) + (valorMetricaN * pesoMetricaN)`.

Por ejemplo, dado el producto "V-NECH BASIC SHIRT" cuyas unidades de venta son 100 y stock es S=4/M=9/L=0, y dado unos
pesos sales_unit=0.7/stock_ratio=0.3, su peso final es:

`(100 * 0.7) + ( ((4 + 9 + 0) / 3) * 0.3) = (70 + 1.3) = 71.3`.

## Estructura interna

### Application

Contiene el modelo de dominio y servicios de aplicación que implementa el caso de uso. Esta capa se compone de las subcapas `model` y `service`. 

Esta capa corresponde a un módulo Maven.


#### - Model

Contiene entidades y value objects.

Las clases aquí implemetadas no referencian clases de otros módulos o de frameworks.

La principal entidad es:

- `Product`: Contiene detalles del producto: id, nombre, unidades de venta y stock.

Los principales value objects son:

- `Metric`: Iterfaz que todas las metricas deben implementar. 
- `SalesUnitMetric`: Contiene el peso asociado a la métrica unidades de ventas.
- `StockRatioMetric` Contiene el peso asociado a la métrica ratio de stock.


Esta capa corresponde a un submódulo Maven que no depende de otros módulos.

#### - Service

Contiene la definición e implementación de los puertos que serán usados por los adaptadores para enviar o recibir información.

Para este ejercicio dos puertos son usados:

- `SearchProductsUseCase`: Recibe información del adaptador REST para luego delegar la búsqueda de información al adaptador `SearchProductsRepository`. 
- `SearchProductsRepository`: Construye el comando final para leer información ordenada de la base de datos.

Esta capa corresponde a un submódulo Maven que depende del submódulo `model`.

### Adapter

Contiene los adaptadores que interactuan con el mundo exterior para recibir o leer información. 


Para este ejercicio se usaron dos adaptadores:

- `SearchProductsController`: Recibe los datos de ordenamiento via HTTP GET. Es un controlador REST de Spring.
- `SearchProductsJPARepository`: Construye y ejecuta la sentencia SQL para leer los productos ordenados de acuerdo a los pesos enviados. Es un Repository de Spring que usa caching.

Además, existe la clase auxiliar `SortBuilder`, la cual construye de manera dinámica el comando de ordenamiento SQL basado en las métricas y pesos recibidos.

Esta capa corresponde a un módulo Maven que depende del módulo `application`.


### Bootstrap

Este módulo ejecuta la inyección de depencias usando Spring y despliega el proyecto como una aplicación Spring Web.

Esta capa corresponde a un módulo Maven que depende del módulo `application` y `adapter`.

## Pruebas de integración y unidad

Todas las pruebas fueron creadas en el módulo `adapter`:

- `SortBuilderTest`: prueba de unidad para verificar la correcta creación del comando SQL  `ORDER BY`.
- `SearchProductsJPARepositoryIntegrationTest`: prueba de integración para verificar el correcto ordenamiento hecho por la base de datos.
- `SearchProductsControllerIntegrationTest`: prueba de integración para verificar la validación de datos de entrada y la estructura JSON de respuesta.

Para ejecutar las pruebas ejecute el comando:

```
./mvnw clean test
```

## Ejecutar la aplicación

Primero compile y empaquete la aplicación en un fat jar:

```
./mvnw clean package
```

Luego, ejecute el jar:

```
java -jar ./bootstrap/target/bootstrap-0.0.1-SNAPSHOT.jar
```
La aplicación debe mostrar un mensaje exitoso de despliegue generado por Spring boot:

```
INFO 20700 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'default'      
INFO 20700 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
INFO 20700 --- [           main] c.i.p.LauncherSpringBootApplicationKt    : Started LauncherSpringBootApplicationKt in 5.653 seconds (process running for 6.184)
```

Antes de ejecutar la aplicación verifique que el puerto 8080 no esté ocupado.

El servicio REST será desplegado en la URL http://localhost:8080/products.

## Probar el servicio REST

### Datos de entrada

Todos los datos de entradas (paginación, métricas) son enviados como query strings:

- `sales_unit`: Peso de la métrica unidades de ventas. Valor entre 0.0 y 1.0. 
- `stock_ratio`: Peso de la métrica ratio de stock. Valor entre 0.0 y 1.0.
- `page_number`: Paginación. Agrupación actual. Valor mínimo es 0.
- `page_size`: Paginación. Máximo de productos por página.
- `direction`: ASC o DESC. Sentido de ordenamiento ascendente o descendente.

Por ejemplo, dado un peso de 70% para las unidades de ventas y un peso de 30% para el ratio de stock, un request válido para solicitar los dos primeros productos ordenados descendentemente es:

```
curl -X 'GET' \
'http://localhost:8080/products?sales_unit=0.7&stock_ratio=0.3&page_number=0&page_size=2&direction=DESC' \
-H 'accept: application/json'
```

o para solicitar el tercer y cuarto producto:


```
curl -X 'GET' \
'http://localhost:8080/products?sales_unit=0.7&stock_ratio=0.3&page_number=0&page_size=2&direction=DESC' \
-H 'accept: application/json'
```

o para solicitar todos los productos (existen 6 en la base de datos):

```
curl -X 'GET' \
'http://localhost:8080/products?sales_unit=0.7&stock_ratio=0.3&page_number=0&page_size=6&direction=DESC' \
-H 'accept: application/json'
```

por defecto, la aplicación ordena por ID de manera descendente y usa una paginación de 20 elementos si ningún parámetro es enviado:
```
curl -X 'GET' \
'http://localhost:8080/products' \
-H 'accept: application/json'
```

#### Probar con Swagger OpenAPI
 
Ingrese a la URL http://localhost:8080/swagger-ui/index.html en donde tendrá la posibilidad de probar el servicio REST usando una interfaz web.

### Datos de salida

El servicio REST genera un JSON que contiene la lista de productos paginados e información sobre la paginación actual:

```

{
  "content": [
    {
      "id": 5,
      "name": "CONTRASTING LACE T-SHIRT",
      "salesUnit": 650,
      "stock": {
        "small": 0,
        "medium": 1,
        "large": 0
      }
    },
    {
      "id": 1,
      "name": "V-NECH BASIC SHIRT",
      "salesUnit": 100,
      "stock": {
        "small": 4,
        "medium": 9,
        "large": 0
      }
    },
    {
      "id": 3,
      "name": "RAISED PRINT T-SHIRT",
      "salesUnit": 80,
      "stock": {
        "small": 20,
        "medium": 2,
        "large": 20
      }
    },
    {
      "id": 2,
      "name": "CONTRASTING FABRIC T-SHIRT",
      "salesUnit": 50,
      "stock": {
        "small": 35,
        "medium": 9,
        "large": 9
      }
    },
    {
      "id": 6,
      "name": "SLOGAN T-SHIRT",
      "salesUnit": 20,
      "stock": {
        "small": 9,
        "medium": 2,
        "large": 5
      }
    },
    {
      "id": 4,
      "name": "PLEATED T-SHIRT",
      "salesUnit": 3,
      "stock": {
        "small": 25,
        "medium": 30,
        "large": 10
      }
    }
  ],
  "metrics": [
    {
      "weight": 0.2,
      "name": "salesUnit"
    },
    {
      "weight": 0,
      "name": "stockRatio"
    }
  ],
  "direction": "DESC",
  "currentPage": 0,
  "currentElements": 6,
  "maxElementsPerPage": 20,
  "totalElements": 6
}


```

## Agregar más productos a la Base datos en memoria

Los productos son agregados automaticamente a la base de datos cuando se despliega la aplicación. Para agregar (o quitar) más productos, por favor modifique el archivo `./bootstrap/src/main/resources/data.sql`.