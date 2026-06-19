# Sudoku Mentor - Estado Actual

Ultima actualizacion: 2026-06-19.

Este documento existe para que un proximo Codex pueda entrar al proyecto sin reconstruir todo el contexto de memoria.

## Identidad Del Proyecto

- App: `Sudoku Mentor`.
- Root project: `SudokuPremium`.
- Modulo Android: `:app`.
- Namespace/codigo Kotlin: `ropa.miragaya.sudokupremium`.
- Application ID publicado: `ropa.miragaya.sudokumentor`.
- El `applicationId` ya se uso en Play Console para testers internos. No cambiarlo salvo decision explicita.
- Flavors de ambiente:
  - `dev`: `ropa.miragaya.sudokumentor.dev`, Firebase Dev, app name `Sudoku Mentor Dev`.
  - `prod`: `ropa.miragaya.sudokumentor`, Firebase Prod/Play Store, app name `Sudoku Mentor`.
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
- Rewarded ads ahora devuelven fallo en errores de UMP/carga/show para no dejar el loading colgado.
- Firebase Auth anonimo, Analytics, Crashlytics, Firestore y Remote Config.
- Room schema export activo y schemas versionados en `app/schemas`.
- Version actual preparada para la proxima build: `versionCode = 4`, `versionName = 1.0.3`.
- Observabilidad inicial:
  - Cada instalacion local genera un `supportCode` legible, por ejemplo `SM-7K4Q-92`.
  - Settings muestra el codigo en la seccion `Soporte`.
  - Crashlytics y Analytics reciben el mismo `support_code`.
  - Firestore guarda `supportCode` en `users/{uid}` cuando se actualizan stats.

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

- `docs/firebase-environments.md`
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

## Diagnostico De Ads

Si el usuario toca `Ver anuncio para 1 pista` y algo falla:

- Logcat:
  - Filtrar por tag `RewardedHintAds`.
- Crashlytics:
  - Buscar non-fatals con mensajes `Rewarded ad failed...` o `UMP...`.
  - Los logs previos incluyen inicio de request, carga y resultado.
  - Custom keys nuevas:
    - `rewarded_ad_stage`
    - `rewarded_ad_reason`
    - `rewarded_ads_enabled`
    - `rewarded_hints_enabled`
    - `rewarded_ad_unit_configured`
    - `rewarded_can_request_ads`
    - `rewarded_can_request_ads_known`
- Analytics:
  - Eventos `rewarded_hint_ad_requested`, `rewarded_hint_ad_failed`, `rewarded_hint_ad_earned`, `rewarded_hint_ad_dismissed`.
- Firestore:
  - En `users/{uid}` se actualizan contadores de rewarded ads y `lastRewardedHintAdFailureReason`.
  - En `users/{uid}.supportCode` queda el codigo legible para identificar instalaciones de testers.

En debug se usa el ad unit de prueba de Google. En release el ad unit viene de Remote Config: `rewarded_hint_ad_unit_id`.

La implementacion actual tiene timeout defensivo de 30 segundos: si UMP o AdMob no devuelve callback, se registra `timeout`, se manda `Failed` y la UI deja de mostrar loading.

## Validacion Reciente

Ultima validacion corrida en esta PC:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat ktlintCheck --no-daemon --console=plain
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat testProdDebugUnitTest --no-daemon --console=plain
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle"; .\gradlew.bat assembleProdDebug --no-daemon --console=plain
```

Resultado:

- `ktlintCheck`: pasa.
- `testProdDebugUnitTest`: pasa.
- `assembleProdDebug`: pasa.

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
- No commitear `app/src/dev/google-services.json`, `app/src/prod/google-services.json`, `keystore.properties` ni `local.properties`.
- Esta PC no tiene `keystore.properties`/upload keystore de Play; para subir AAB a Play Console hay que regenerar `prodRelease` en la PC que tenga la keystore o copiar localmente el `.jks`/`.keystore` y crear `keystore.properties`.
- Mantener `USE_DEBUG_BOARD = false`.
- No reactivar opciones debug en release.
- No agregar destructive Room migrations.
- No correr `ktlintCheck` en cada microcambio; usarlo para cierres diarios o antes de publicar.
