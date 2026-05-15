# Sudoku Mentor - Release Checklist

Checklist operativo para publicar builds internas, cerradas o productivas sin depender de memoria.

## Identidad De Publicacion

- App name: `Sudoku Mentor`
- Android namespace/code package: `ropa.miragaya.sudokupremium`
- Published applicationId: `ropa.miragaya.sudokumentor`
- Firebase Android package: `ropa.miragaya.sudokumentor`
- Current version: `versionCode = 1`, `versionName = 1.0`

El `applicationId` ya se uso para una version de testers internos. Tratarlo como estable para Play Console, Firebase, AdMob y Play Billing.

## Archivos Locales Requeridos

- `app/google-services.json`
  - Debe existir localmente.
  - No debe commitearse.
  - El package interno debe coincidir con `ropa.miragaya.sudokumentor`.
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
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat testDebugUnitTest
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat assembleDebug
```

Antes de subir a Play Console, agregar:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat bundleRelease
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
  - Feature graphic.
  - Categoria y tags.
  - Politica de privacidad.
- Completar formularios:
  - Data Safety.
  - Clasificacion de contenido.
  - Ads.
  - Compras dentro de la app.

## Pendientes Antes De Produccion

- Room schema export configurado y schemas versionados en `app/schemas`.
- Cerrar politica de privacidad publica.
- Revisar reglas de Firestore y App Check.
- Probar en dispositivo real al menos:
  - Nueva partida.
  - Continuar partida.
  - Pedir pista.
  - Limite de pistas.
  - Premium.
  - Victoria.
  - Settings.
  - Biblioteca de tecnicas.
