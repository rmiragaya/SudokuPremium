# Sudoku Mentor - Estado Actual

Ultima actualizacion: 2026-05-17.

Este documento existe para que un proximo Codex pueda entrar al proyecto sin reconstruir todo el contexto de memoria.

## Identidad Del Proyecto

- App: `Sudoku Mentor`.
- Root project: `SudokuPremium`.
- Modulo Android: `:app`.
- Namespace/codigo Kotlin: `ropa.miragaya.sudokupremium`.
- Application ID publicado: `ropa.miragaya.sudokumentor`.
- El `applicationId` ya se uso en Play Console para testers internos. No cambiarlo salvo decision explicita.
- Stack: Kotlin, Jetpack Compose, Material 3, Navigation Compose tipado, Hilt, Room, Firebase, Play Billing, AdMob rewarded ads.

## Direccion De Producto

La app apunta a una experiencia tranquila, profesional y enfocada en aprender a resolver con logica: "pensado para resolver, no adivinar".

Prioridad actual:

- Calidad y pulido antes que sumar features grandes.
- Hints que expliquen razonamiento y usen resaltados del tablero.
- UX clara para usuarios nuevos.
- Preparacion seria para Play Store y portfolio.

## Estado Funcional

Ya existe:

- Generacion y resolucion de sudokus por dificultad.
- Persistencia local de partida activa con Room.
- Pantalla Home redisenada con fondo oscuro, logo y CTA principal.
- Pantalla de juego con tablero, timer, notas, deshacer, pistas y menu.
- Dialogo permanente `Como jugar`.
- Tutorial interactivo automatico:
  - Solo aparece si la primera partida general de la instalacion es `Facil`.
  - Explica reglas basicas con resaltado de fila, columna y caja 3x3.
  - Luego guia 4 movimientos reales.
  - Se puede saltar.
- Biblioteca de tecnicas con ejemplos visuales.
- Flujo de victoria con resumen y accion para nueva partida.
- Premium/ads/rewarded hints integrados a nivel app, pendientes de QA completo de release.
- Firebase Auth anonimo, Analytics, Crashlytics, Firestore y Remote Config.
- Room schema export activo y schemas versionados en `app/schemas`.

## Estado Visual

Reciente:

- Home actual usa `app/src/main/res/drawable-nodpi/home_bkg.png`.
- Logo principal usado en Home y victoria: `sudoku_mentor_icon_dark_transparent_png`.
- Launcher adaptativo usa `sudoku_mentor_launcher_foreground` y `ic_launcher_background`.
- Assets para Play Console viven en `play-console-assets/`.
- Paleta visual nueva vive principalmente en `ui/theme/SudokuPalette.kt`, `colors.xml` y `themes.xml`.

Pendiente visual importante:

- Terminar de llevar el estilo nuevo a toda la app con coherencia.
- Revisar visualmente juego, Premium, Settings, tecnicas y dialogs en dispositivo real.
- Capturas finales para Play Store.

## Textos E Internacionalizacion

Estado actual:

- Textos cortos de UI principal estan en `app/src/main/res/values/strings.xml`.
- Los mensajes del tutorial guiado que salen desde `GameViewModel` usan `StringProvider`.
- `values-en/strings.xml` existe como scaffold vacio para evitar mezclar idiomas antes de tener traduccion completa.

Decision actual:

- El contenido pedagogico largo de tecnicas sigue en Kotlin:
  - `domain/techniques/TechniqueTutorialFixtures.kt`
  - `ui/techniques/TechniquesScreen.kt`
- Los textos dinamicos de estrategias siguen cerca del dominio:
  - `domain/model/StrategyContext.kt`
  - `domain/model/SudokuHint.kt`

Eso es intencional por ahora: moverlo a recursos requiere un refactor de contenido/dominio mas grande.

## Privacidad Y Play Console

Documentos relevantes:

- `docs/privacy-data-safety.md`
- `docs/release-checklist.md`
- `docs/playstore-backlog.md`

Politica de privacidad publicada:

- https://sites.google.com/view/sudoku-mentor-privacy

Notas:

- La app no pide nombre, email ni telefono.
- Usa usuario anonimo de Firebase.
- Usa Analytics, Crashlytics, Firestore, Remote Config, Billing, AdMob rewarded ads y UMP.
- `allowBackup=false` esta configurado.
- `data_extraction_rules.xml` excluye backup/transfer.

## Validacion Reciente

Ultima validacion corrida en esta PC:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat ktlintCheck --no-daemon --console=plain
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat testDebugUnitTest --no-daemon --console=plain
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat assembleDebug --no-daemon --console=plain
```

Resultado:

- `ktlintCheck`: pasa.
- `testDebugUnitTest`: pasa.
- `assembleDebug`: pasa.

## Pendientes Principales Para Continuar

El backlog vivo esta en `docs/playstore-backlog.md`. Lo mas importante ahora:

- QA manual en dispositivo real despues del rediseño.
- Revisar tutorial interactivo en pantallas chicas.
- Completar ficha visual de Play Store: screenshots y feature graphic final.
- QA de Billing, rewarded ads y UMP antes de activar monetizacion real.
- Revisar reglas de Firestore y App Check.
- Revisar accesibilidad basica.
- Separar `GameScreen.kt` y `TechniquesScreen.kt` cuando se retome arquitectura/pulido.

## Cuidado Al Continuar

- No cambiar `applicationId`.
- No commitear `app/google-services.json`, `keystore.properties` ni `local.properties`.
- Mantener `USE_DEBUG_BOARD = false`.
- No reactivar opciones debug en release.
- No agregar destructive Room migrations.
- No correr `ktlintCheck` en cada microcambio; usarlo para cierres diarios o antes de publicar.
