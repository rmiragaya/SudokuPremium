# Sudoku Mentor - Release Checklist

Checklist operativo para publicar builds internas, cerradas o productivas sin depender de memoria.

## Identidad De Publicacion

- App name: `Sudoku Mentor`
- Android namespace/code package: `ropa.miragaya.sudokupremium`
- Published applicationId: `ropa.miragaya.sudokumentor`
- Firebase Android package: `ropa.miragaya.sudokumentor`
- Current version: `versionCode = 2`, `versionName = 1.0.1`

El `applicationId` ya se uso para una version de testers internos. Tratarlo como estable para Play Console, Firebase, AdMob y Play Billing.

## Ambientes Firebase

- Flavor `dev`:
  - `applicationId`: `ropa.miragaya.sudokumentor.dev`
  - App name: `Sudoku Mentor Dev`
  - Firebase project: Dev
  - Config local esperada: `app/src/dev/google-services.json`
- Flavor `prod`:
  - `applicationId`: `ropa.miragaya.sudokumentor`
  - App name: `Sudoku Mentor`
  - Firebase project: Prod/Play Store
  - Config local esperada: `app/src/prod/google-services.json`

Usar `prodRelease` para builds de Play Console. No subir builds `dev` a la app productiva.

## Archivos Locales Requeridos

- `app/src/prod/google-services.json`
  - Debe existir localmente.
  - No debe commitearse.
  - El package interno debe coincidir con `ropa.miragaya.sudokumentor`.
- `app/src/dev/google-services.json`
  - Debe existir localmente para builds `dev`.
  - No debe commitearse.
  - El package interno debe coincidir con `ropa.miragaya.sudokumentor.dev`.
- `keystore.properties`
  - Debe existir solo para builds firmadas de release.
  - No debe commitearse.
  - Debe apuntar al keystore correcto para la app de Play Console.
- `local.properties`
  - Debe apuntar al SDK local.
  - No debe commitearse.

## Validacion Tecnica

Ejecutar desde la raiz con cache local:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat ktlintCheck
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat testProdDebugUnitTest
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat assembleProdDebug
```

Antes de subir a Play Console, agregar:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat bundleProdRelease
```

## Release Guardrails

- Confirmar que `BuildConfig.DEBUG` sea `false` en release.
- Confirmar que los menues debug no aparezcan en release.
- Confirmar que `USE_DEBUG_BOARD` siga en `false`.
- Confirmar que no se use `fallbackToDestructiveMigration` en release.
- Confirmar que Remote Config tenga valores seguros:
  - `ads_enabled = false` hasta terminar QA de AdMob/UMP.
  - `rewarded_hint_ad_unit_id` configurado solo cuando AdMob este listo.
  - `premium_enabled` activado solo si el producto `premium_supporter` esta listo.
- Confirmar que Crashlytics recibe eventos de prueba en una build interna, no usando el boton debug en release.
- Confirmar que Billing funciona en track interno:
  - Compra exitosa.
  - Compra cancelada.
  - Compra pendiente.
  - Restauracion.
  - Reinstalacion y recuperacion de Premium.
- Confirmar que UMP/consentimiento funciona antes de activar anuncios reales.
- Para release productivo, revisar advertencias de Play Console:
  - Mapping de desofuscacion: si se activa minify/R8, generar y subir el mapping file.
  - Simbolos nativos: si Play sigue detectando codigo nativo de dependencias, evaluar/subir simbolos de depuracion.

## Play Console

- Revisar pre-launch report:
  - Crashes.
  - ANRs.
  - Accesibilidad.
  - Rendimiento.
  - Capturas.
- Completar ficha:
  - Descripcion corta.
  - Descripcion completa.
  - Screenshots.
  - Feature graphic. Assets base en `play-console-assets/`.
  - Categoria y tags.
  - Politica de privacidad: `https://sites.google.com/view/sudoku-mentor-privacy`.
- Completar formularios:
  - Data Safety.
  - Clasificacion de contenido.
  - Ads.
  - Compras dentro de la app.
- Revisar factores de forma antes de produccion:
  - No habilitar Google Play Games en PC salvo que se haga QA especifico.
  - No prometer soporte Automotive/autos.
  - Mantener foco inicial en telefonos y tablets.

## Pendientes Antes De Produccion

- Revisar reglas de Firestore y App Check.
- Cerrar QA de Billing, rewarded ads y UMP.
- Revisar tutorial interactivo en dispositivo real y pantallas chicas.
- Capturas finales para Play Store.
- Probar en dispositivo real al menos:
  - Nueva partida.
  - Continuar partida.
  - Pedir pista.
  - Limite de pistas.
  - Premium.
  - Victoria.
  - Settings.
  - Biblioteca de tecnicas.
