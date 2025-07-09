# Pastillero Inteligente – Proyecto SOA 2025 (Grupo L1)

Este repositorio contiene el desarrollo completo del proyecto **Pastillero Inteligente**, realizado por el **Grupo L1** para la materia **Sistemas Operativos Avanzados (SOA)** en la Universidad Nacional de La Matanza durante el primer cuatrimestre de 2025.

El proyecto se compone de:

- Un sistema embebido basado en ESP32 que controla un pastillero automático.
- Una aplicación Android llamada **Olvidín**, que permite gestionar los horarios de medicación.

## Proyecto embebido: Pastillero Inteligente

### Descripción

Sistema automatizado de dispensación de medicamentos que utiliza un mecanismo rotativo tipo carrusel. A la hora programada, el dispositivo libera una dosis de pastillas y emite una alerta sonora (buzzer). El usuario detiene la alarma presionando un botón físico.

### Componentes principales

- ESP32
- Motor (controlado por relé)
- Sensor de final de carrera
- Buzzer
- Potenciómetro (ajuste de volumen)
- Pulsadores
- Protoboard

### Estados del sistema

- `ST_INIT`: Inicialización
- `ST_IDLE`: Espera
- `ST_ROT`: Rotación del carrusel
- `ST_DSP`: Dispensación
- `ST_ERR`: Error

### Codigo Fuente

[ESP32](Embebido)

---

## Aplicación móvil: Olvidín

### Descripción

Aplicación Android que funciona como interfaz del usuario para gestionar la rutina de medicación, comunicándose con el sistema embebido mediante MQTT.

### Funcionalidades

- Agregar y eliminar horarios de medicación
- Consultar historial de tomas
- Ver el volumen actual del buzzer
- Enviar comandos al sistema embebido por protocolo MQTT (ESP32)

### Codigo Fuente

[Android](Android)

---

## Manuales y documentación

- [Actividad1_Lunes_L1.pdf](Informes/Actividad1_Lunes_L1.pdf): Diseño y funcionamiento del sistema embebido.
- [Actividad2_Lunes_L1.pdf](Informes/Actividad2_Lunes_L1.pdf): Desarrollo y uso de la app Android Olvidín.

---

## Integrantes

- **Cristian Céspedes**
- **Mara Guerrera**
- **Brian Menchaca** 
- **Pablo Vázquez Petracca**

## Universidad

**Universidad Nacional de La Matanza**

Departamento de Ingeniería e Investigaciones Tecnológicas

Florencio Varela 1903 – San Justo, Buenos Aires, Argentina
