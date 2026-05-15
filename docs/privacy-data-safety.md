# Sudoku Mentor - Privacidad Y Data Safety

Documento de trabajo para preparar politica de privacidad, Play Data Safety y decisiones de datos. No reemplaza una revision legal; sirve para que el producto y el codigo esten alineados.

## Resumen Actual

La app no pide nombre, email ni telefono. Hoy usa identidad anonima de Firebase y servicios de Google/Firebase para operar, medir estabilidad, medir uso agregado, compras y anuncios rewarded.

Servicios integrados:

- Firebase Authentication anonimo.
- Firebase Analytics.
- Firebase Crashlytics.
- Firebase Firestore.
- Firebase Remote Config.
- Google Play Billing.
- Google AdMob rewarded ads.
- Google User Messaging Platform para consentimiento de anuncios.
- Room y SharedPreferences locales.

## Datos Locales En El Dispositivo

Room guarda una partida activa:

- Tablero actual.
- Solucion del tablero.
- Dificultad.
- Tiempo transcurrido.
- Pistas usadas.
- Pistas rewarded disponibles.

SharedPreferences guarda preferencias:

- Vibracion/haptics activada o desactivada.

Estos datos son funcionales para continuar la experiencia de juego. `allowBackup=false` esta configurado en el manifest, por lo que no se intenta backup automatico de app data.

## Datos En Firebase/Google

### Firebase Auth

- Crea un usuario anonimo.
- Usa el UID anonimo para asociar estadisticas y contexto tecnico.
- No solicita datos personales directos al usuario.

### Firestore

Ruta actual aproximada: `users/{uid}`.

Datos guardados:

- `isAnonymous`.
- `lastSeenAt`.
- Estadisticas agregadas:
  - Partidas iniciadas.
  - Partidas completadas.
  - Dificultad.
  - Tiempo total de resolucion.
  - Pistas usadas.
  - Errores revelados.
  - Fechas de ultimo uso/completado.
- Registros de partidas completadas:
  - Dificultad.
  - Tiempo.
  - Pistas usadas.
  - Errores revelados.
  - Fecha de completado.

No se guarda el tablero completo en Firestore actualmente.

### Analytics

Eventos actuales:

- Pantalla vista.
- Dificultad seleccionada.
- Continuar partida.
- Nueva partida iniciada.
- Partida completada.
- Pista solicitada.
- Pista mostrada.
- Limite de pistas alcanzado.
- Rewarded ad solicitado/ganado/fallido.
- Compra Premium iniciada/completada/restaurada.
- Tecnica abierta.

Parametros actuales:

- Dificultad.
- Tiempo transcurrido.
- Pistas usadas.
- Errores revelados.
- Si habia errores.
- Estrategia de pista.
- Cantidad de pistas.
- ID de tecnica.
- Fuente de apertura.
- Motivo tecnico de fallo cuando aplica.

### Crashlytics

Datos tecnicos de diagnostico:

- Excepciones fatales/no fatales.
- Logs tecnicos breves.
- UID anonimo configurado como user id.
- Contexto de juego:
  - Dificultad.
  - Tiempo.
  - Pistas usadas.
  - Pistas rewarded disponibles.
  - Estado Premium.
  - Errores revelados.
  - Si la partida estaba completa.

### Billing

Google Play Billing maneja compra/restauracion de `premium_supporter`.

La app no guarda el estado Premium en Firestore como fuente de verdad. Lo consulta desde Play Billing.

### Ads Y UMP

Los anuncios rewarded estan pensados para pedir pistas extra por eleccion explicita del usuario.

Estado actual recomendado:

- Mantener `ads_enabled = false` en Remote Config hasta terminar QA de AdMob y UMP.
- Activar anuncios reales solo despues de completar consentimiento, pruebas internas y politica de privacidad.

## Data Safety - Borrador De Respuestas

Este borrador debe revisarse contra la consola final de Play.

- Datos personales directos: no recolectados por la app de forma explicita.
- IDs de usuario: si, UID anonimo de Firebase y posibles identificadores manejados por servicios de Google.
- Actividad en la app: si, eventos de uso y progreso agregado.
- Informacion de rendimiento/crash: si, Crashlytics.
- Compras: si, Play Billing maneja compras dentro de la app.
- Publicidad: si la app activa AdMob rewarded ads, declarar uso de anuncios y datos asociados por SDKs de Google.
- Datos compartidos con terceros: si, con Google/Firebase/Play Services como proveedores integrados.
- Datos cifrados en transito: si, servicios de Google/Firebase usan transporte seguro.
- Eliminacion de datos: pendiente definir flujo o canal de solicitud.

## Riesgos Antes De Publicar

- Falta politica de privacidad publica y URL para Play Console.
- Falta definir canal de borrado de datos asociado a UID anonimo.
- Falta revisar reglas de Firestore para impedir acceso entre usuarios.
- Falta decidir si App Check se exige antes de abrir feedback/Firestore adicional.
- Falta QA real de UMP/AdMob por region.
- Falta confirmar Data Safety con la lista final de SDKs de Play Console.

## Politica De Privacidad - Estructura Recomendada

- Quien opera la app y contacto.
- Que datos se recopilan.
- Para que se usan.
- Servicios de terceros usados:
  - Firebase Authentication.
  - Firebase Analytics.
  - Firebase Crashlytics.
  - Firebase Firestore.
  - Firebase Remote Config.
  - Google Play Billing.
  - Google AdMob.
  - Google User Messaging Platform.
- Compras dentro de la app.
- Anuncios rewarded.
- Retencion de datos.
- Como pedir eliminacion de datos.
- Seguridad.
- Cambios a la politica.

## Decisiones Pendientes

- Definir email de contacto publico.
- Definir URL final de politica de privacidad.
- Definir proceso de borrado de datos.
- Definir si se conserva historial de partidas completadas o solo agregados.
- Definir si se permite optar fuera de Analytics en Settings.
- Definir si se activa App Check antes de produccion.
