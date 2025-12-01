# **💰 Gestión de Deudas de Clientes (API REST \+ Reporte Web)**

Este proyecto implementa una solución backend robusta para la gestión de deudas de clientes, exponiendo una API RESTfull segura y un reporte web simple para visualización de datos.

Desarrollado con Spring Boot y Spring Data JPA, utilizando la base de datos en memoria H2 para desarrollo.

## **🛠️ Stack Tecnológico**

| Componente | Tecnología | Versión Clave | Propósito |
| :---- | :---- | :---- | :---- |
| **Backend Framework** | Spring Boot | 4.0.0 | Servidor de la API REST. |
| **Persistencia** | Spring Data JPA | Hibernate 7 | Mapeo Objeto-Relacional. |
| **Base de Datos** | H2 Database | 2.4.240 | Base de datos en memoria para el desarrollo. |
| **Seguridad** | Spring Security | 7.0.1 | Autenticación Basic Auth para API y Reporte. |
| **Documentación** | SpringDoc | Última | Generación automática de Swagger UI. |

## **🚀 Inicio Rápido**

Para ejecutar el proyecto, asegúrate de tener instalado **Java 17+** y **Maven**.

### **1\. Compilación y Ejecución**

Ejecuta los siguientes comandos desde la raíz del proyecto:

\# Limpiar y compilar  
mvn clean install

\# Iniciar la aplicación  
mvn spring-boot:run

La aplicación se iniciará en http://localhost:8080.

## **🔑 Credenciales de Acceso y Pruebas**

El acceso a la API REST y al Reporte Web está protegido por **HTTP Basic Authentication**.

| Recurso | Tipo de Autenticación | Username | Password |
| :---- | :---- | :---- | :---- |
| **API REST** (/api/\*\*) | Basic Auth | admin\_evertec | prueba123 |
| **Reporte Web** (/reporte) | Basic Auth | admin\_evertec | prueba123 |
| **Swagger UI** (/swagger-ui.html) | Público | N/A | N/A |

### **1\. Acceso al Reporte Web (Vista Protegida)**

1. Abre un navegador (recomendado en **Modo Incógnito** para asegurar que pida credenciales).  
2. Navega a: http://localhost:8080/reporte  
3. Ingresa las credenciales de Basic Auth cuando se soliciten.

### **2\. Acceso a la Documentación (Swagger UI)**

La documentación interactiva de la API está abierta y no requiere autenticación.

1. Navega a: http://localhost:8080/swagger-ui.html  
2. Desde aquí puedes probar los endpoints, usando el botón **"Authorize"** en la parte superior para ingresar las credenciales de la API (admin\_evertec/prueba123).

## **💻 Uso de Postman para Probar la API (CRUD)**

**¡IMPORTANTE\!** Dado que se usa una base de datos en memoria (H2), para que los endpoints GET, PUT y DELETE funcionen después de iniciar la aplicación, **primero debe cargar datos** utilizando el endpoint /api/deudas/load-file.

### **Configuración de Autenticación**

Para cualquier solicitud a /api/deudas/\*\*:

1. En la pestaña **Authorization**, selecciona el tipo **Basic Auth**.  
2. Ingresa el **Username**: admin\_evertec  
3. Ingresa el **Password**: prueba123

### **1\. Carga Inicial de Datos (CRÍTICO)**

Este endpoint simula la carga masiva de un archivo de datos.

1. **Método:** POST  
2. **URL:** http://localhost:8080/api/deudas/load-file  
3. **Autorización:** Configurar **Basic Auth** con admin\_evertec / prueba123.  
4. **Cuerpo (Body):** Seleccionar la opción form-data.  
5. **Configurar Campo:**  
   * En la columna **KEY**, ingresar el nombre del parámetro esperado por el backend, que es **file**.  
   * En la columna **VALUE**, **cambiar el tipo de Text a File** y seleccionar el archivo .txt a subir.

### **2\. Ejemplo de Solicitud (Crear Deuda \- POST)**

Una vez cargados los datos iniciales, puede crear más deudas individualmente.

| Propiedad | Valor |
| :---- | :---- |
| **Método** | POST |
| **URL** | http://localhost:8080/api/deudas |
| **Body** | raw, tipo JSON |

**Cuerpo JSON de ejemplo:**

{  
  "acreedor": "Banco X",  
  "monto": 550.75,  
  "fechaVencimiento": "2026-03-01",  
  "estado": "PENDIENTE"  
}

## **🗺️ Endpoints de la API**

| Método | Ruta | Descripción | Seguridad |
| :---- | :---- | :---- | :---- |
| GET | /api/deudas | Obtiene todas las deudas registradas. | Protegida |
| GET | /api/deudas/{id} | Obtiene una deuda específica por ID. | Protegida |
| POST | /api/deudas | Crea una nueva deuda. | Protegida |
| PUT | /api/deudas/{id} | Actualiza una deuda existente. | Protegida |
| DELETE | /api/deudas/{id} | Elimina una deuda por ID. | Protegida |
| **POST** | **/api/deudas/load-file** | **Carga Inicial de Datos (Simulado)** | Protegida |

