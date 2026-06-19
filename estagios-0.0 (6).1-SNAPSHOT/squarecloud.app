{
  "version": "2.0",
  "type": "dynamic",
  "display_name": "Sistema de Estagios",
  "description": "Backend do sistema de gerenciamento de estágios",
  "memory": 512,
  "start": "java -jar estagios-0.0.1-SNAPSHOT.jar",
  "build": {
    "cmd": "mvn clean package -DskipTests"
  }
}