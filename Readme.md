Interfaz Grafica UR_OS 1.0 - Manual de Uso y Compilación

Autor: Johan Caro Valencia

Este proyecto se basa en el código original de UR_OS desarrollado por Pedro Wightman, disponible en https://github.com/pedrowightman/ur_os_pp_public. A partir de esa base, se han implementado mejoras, además de incorporar una interfaz gráfica intuitiva que facilita la interacción del usuario.

📌 1. Cómo Correr el Proyecto desde el Código

Si deseas ejecutar el proyecto directamente desde el código fuente, sigue estos pasos:

1️⃣ Requisitos Previos

Asegúrate de tener instalado:

JDK 22
JavaFX SDK 23.0.1
JavaFX SDK 23.0.1 jmods

Apache NetBeans / VS Code con soporte para Java

2️⃣ Descarga el Proyecto


3️⃣ Compilar el Proyecto

Ejecuta en la terminal:

javac --module-path "Dirreccion libs JavaFx ejemplo: C:/javafx-sdk-23.0.1/lib" --add-modules javafx.controls,javafx.fxml -d out src/ur_os/*.java

4️⃣ Ejecutar la Aplicación

java --module-path "Dirreccion libs JavaFX: C:/javafx-sdk-23.0.1/lib C:/javafx-sdk-23.0.1/lib" --add-modules javafx.controls,javafx.fxml -cp out ur_os.UR_OS

✅ ¡Listo! Ahora la aplicación debería ejecutarse correctamente.

📌 2. Cómo Compilar y Generar el Instalador

Si has modificado el código y deseas generar un nuevo instalador, sigue estos pasos.

Descarga los jmods de JavaFX

1️⃣ Compilar el Código Java

javac --module-path "Direccion jmods JavaFX ejemplo: C:/javafx-sdk-23.0.1/jmods" --add-modules javafx.controls,javafx.fxml -d out src/ur_os/*.java

2️⃣ Generar el .jar

jar --create --file=App.jar --main-class=ur_os.UR_OS -C out .

3️⃣ Generar el runtime con jlink

jlink --module-path "Direccion jmods Java ejemplo: C:/Java/jdk-22/jmods;Direccion jmods JavaFX ejemplo: C:/javafx-sdk-23.0.1/jmods" --add-modules java.base,java.desktop,java.logging,java.sql,java.xml,javafx.controls,javafx.fxml,javafx.graphics --output runtime

4️⃣ Generar el .msi con jpackage

jpackage --input . --dest "carpeta destino del instalador: C:/UR_OS_InterfazGrafica/build" --name UR_OS --main-jar App.jar --main-class ur_os.UR_OS --type exe --runtime-image runtime --verbose

Si deseas generarlo como un .exe ejecuta:

jpackage --input . --dest "carpeta destino del instalador: C:/UR_OS_InterfazGrafica/build" --name UR_OS --main-jar App.jar --main-class ur_os.UR_OS --type exe --runtime-image runtime --verbose

✅ Esto generará el instalador en la carpeta destino

📌 3. Guía de Instalación

Para instalar y ejecutar la aplicación en una PC sin Java instalado:

1️⃣ Ejecutar el Instalador

[Descargar UR_OS 1.0](https://github.com/Yucaloid/UR_OS_InterfazGrafica/releases/download/V1.0/UR_OS-1.0.msi)

Espera a que se complete el proceso.

La aplicación se instalara por defecto en C:\Program Files con nombre de carpeta UR_OS

2️⃣ Ejecutar la Aplicación

busca UR_OS y ejecútalo.

✅ ¡Listo! La aplicación está instalada y lista para usar. 🎉

🚀 ¡Gracias por usar UR_OS Interfaz! 

