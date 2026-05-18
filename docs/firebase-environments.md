# Sudoku Mentor - Firebase Environments

Guia operativa para mantener separados desarrollo y produccion.

## Objetivo

Evitar que pruebas locales, reinstalaciones y usuarios anonimos de desarrollo contaminen los datos reales de Firebase/Play.

## Flavors Android

- `dev`
  - `applicationId`: `ropa.miragaya.sudokumentor.dev`
  - App name: `Sudoku Mentor Dev`
  - Firebase project esperado: Dev
  - Config local: `app/src/dev/google-services.json`
- `prod`
  - `applicationId`: `ropa.miragaya.sudokumentor`
  - App name: `Sudoku Mentor`
  - Firebase project esperado: Prod
  - Config local: `app/src/prod/google-services.json`

Los archivos `google-services.json` no se commitean.

## Setup Firebase Dev

1. Crear un proyecto Firebase nuevo: `Sudoku Mentor Dev`.
2. Agregar una app Android al proyecto Dev:
   - Package name: `ropa.miragaya.sudokumentor.dev`
   - App nickname: `Sudoku Mentor Dev`
3. Descargar el `google-services.json`.
4. Copiarlo a `app/src/dev/google-services.json`.
5. Habilitar Authentication:
   - Provider anonimo.
6. Crear Firestore:
   - Edicion Standard.
   - Region segura para el proyecto.
   - Reglas iniciales equivalentes a prod, ajustadas si hace falta para desarrollo.
7. Habilitar/validar:
   - Analytics.
   - Crashlytics.
   - Remote Config.
8. Configurar Remote Config Dev con valores seguros:
   - `ads_enabled = false` salvo pruebas explicitas.
   - `premium_enabled = true` si se quiere probar flujo visual.
   - `rewarded_hints_enabled = true`.
   - `rewarded_hint_ad_unit_id = ""` para no usar ads reales.
   - `free_hints_per_game = 3`.

## Setup Firebase Prod

1. Mantener el proyecto Firebase actual de Play/Prod.
2. Confirmar que la app Android registrada usa:
   - Package name: `ropa.miragaya.sudokumentor`.
3. Colocar el JSON productivo en `app/src/prod/google-services.json`.
4. No usar el JSON productivo para builds `dev`.

## Comandos

Validacion productiva antes de release interna/publica:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat ktlintCheck
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat testProdDebugUnitTest
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat assembleProdDebug
```

Build para Play Console:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat bundleProdRelease
```

Build local de desarrollo, una vez agregado el JSON Dev:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat assembleDevDebug
```

