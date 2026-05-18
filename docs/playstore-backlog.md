# Sudoku Mentor - Backlog Para Play Store

Lista viva de pendientes para llevar Sudoku Mentor a una app publicable, estable y presentable como portfolio.

## Norte Del Producto

Sudoku Mentor debe sentirse tranquila, profesional y centrada en resolver con logica: pensado para resolver, no adivinar.

La prioridad sigue siendo calidad, claridad y pulido antes que volumen de features.

## P0 - Antes De Enviar A Produccion

- [ ] Probar en dispositivo real el flujo completo:
  - Nueva partida.
  - Continuar partida.
  - Tutorial automatico en primera partida facil.
  - Tutorial no automatico si la primera partida general no es facil.
  - Como jugar desde menu.
  - Pedir pista.
  - Cerrar y reabrir la misma pista sin consumir anuncio/pista extra.
  - Limite de pistas.
  - Ver anuncio para pista.
  - Premium.
  - Victoria.
  - Nueva partida despues de victoria.
  - Settings.
  - Biblioteca de tecnicas.

- [ ] Revisar pantallas chicas:
  - Tablero sin scroll innecesario.
  - Tutorial debajo del tablero sin tapar controles importantes.
  - Dialog de pistas agotadas.
  - Dialog de victoria.
  - Bottom sheet de dificultad.
  - Home con y sin partida guardada.

- [ ] Completar ficha visual de Play Store:
  - Screenshots finales.
  - Icono final.
  - Feature graphic final.
  - Confirmar que los assets en `play-console-assets/` sean los ultimos.

- [ ] QA de monetizacion:
  - Billing: compra exitosa.
  - Billing: compra cancelada.
  - Billing: compra pendiente.
  - Billing: restauracion.
  - Reinstalacion y recuperacion de Premium.
  - Rewarded ads con test ad.
  - Ads apagados por Remote Config hasta que UMP/AdMob este listo.

- [ ] Revisar privacidad/Play Console:
  - Data Safety.
  - Ads.
  - Compras dentro de la app.
  - Clasificacion de contenido.
  - Politica de privacidad publicada.
  - Reglas de Firestore.
  - App Check.
  - Factores de forma:
    - Confirmar que no quede habilitado Google Play Games en PC si no se va a soportar.
    - Confirmar que no se prometa soporte para autos/Automotive.
    - Mantener foco inicial en telefonos y tablets.

- [ ] Revisar pre-launch report:
  - Crashes.
  - ANRs.
  - Accesibilidad.
  - Rendimiento.
  - Capturas automaticas.

## UI Y Pulido Visual

- [x] Rediseñar Home con nuevo estilo visual oscuro.
- [x] Ajustar logo de Home con fondo transparente.
- [x] Ajustar launcher adaptive icon para evitar cortes.
- [x] Preparar assets basicos de Play Console.
- [x] Mejorar dialog de pistas agotadas.
- [x] Mejorar pantalla/dialog de victoria.
- [x] Mejorar acceso desde hint a tecnica.
- [x] Ocultar opciones no listas en release cuando aplica.

- [ ] Llevar el estilo visual nuevo al resto de la app:
  - Game.
  - Premium.
  - Settings.
  - Biblioteca de tecnicas.
  - Dialogs.
  - Bottom sheets.

- [ ] Consolidar tokens visuales:
  - Colores.
  - Espaciados.
  - Radios.
  - Bordes.
  - Estados pressed/disabled.
  - Tipografia.

- [ ] Revisar botones principales y secundarios:
  - Home.
  - Game actions.
  - Premium.
  - Settings.
  - Dialogs.

- [ ] Revisar animaciones:
  - Logo Home.
  - Victoria.
  - Confetti.
  - Reducir movimiento si se agrega setting de accesibilidad.

## Tutorial Y Aprendizaje

- [x] Agregar `Como jugar` al menu de tres puntitos.
- [x] Agregar tutorial interactivo automatico solo si la primera partida general es facil.
- [x] Explicar reglas basicas con tablero: fila, columna y caja 3x3.
- [x] Guiar los primeros 4 movimientos reales.
- [x] Permitir saltar tutorial.
- [x] Guardar flags locales de tutorial/primera partida.

- [ ] QA manual del tutorial:
  - Instalar limpio e iniciar facil.
  - Instalar limpio e iniciar media/dificil/experto.
  - Luego de primera no facil, iniciar facil y confirmar que no aparece automatico.
  - Tocar numero incorrecto.
  - Saltar tutorial.
  - Completar los 4 pasos.

- [ ] Revisar copy del tutorial:
  - Que explique claramente del 1 al 9.
  - Que no repita texto innecesario.
  - Que el resaltado coincida con la explicacion.

## Gameplay, Hints Y Tecnicas

- [x] Evitar que una pista cerrada por error consuma anuncio/pista extra si el tablero no cambio.
- [x] La guia tutorial no consume pistas ni anuncios.
- [x] Nueva partida desde victoria abre selector de dificultad.
- [x] Back desde selector de dificultad cierra el modal.
- [x] Back desde victoria vuelve al tablero.

- [ ] Auditar todas las pistas:
  - Logica correcta.
  - Texto alineado con celdas resaltadas.
  - Eliminaciones entendibles.
  - No esconder fallos del solver con fallback invalido.

- [ ] Validar ejemplos de tecnicas:
  - Naked Single.
  - Hidden Single.
  - Naked Pair.
  - Hidden Pair.
  - Intersection Removal.
  - Naked Triple.
  - X-Wing.
  - Y-Wing.

- [ ] Mejorar biblioteca de tecnicas:
  - Revisar profundidad de textos.
  - Revisar ejemplos multiples por tecnica.
  - Evaluar coordenadas tipo A1/B4 solo si mejora claridad.

- [ ] Evaluar modo practica por tecnica:
  - Puede ser feature futura o Premium.

## Settings, Preferencias Y Accesibilidad

- [ ] Resolver o quitar vibracion:
  - Actualmente no es confiable en prueba real.
  - Si no queda bien, no mostrarla en release.

- [ ] Diseñar pantalla de estilos antes de mostrarla en release:
  - Temas de color.
  - Variantes de tablero.
  - Alto contraste.
  - Posible Premium futuro si aporta valor real.

- [ ] Expandir settings solo cuando sea util:
  - Sonido opcional.
  - Mostrar/ocultar timer.
  - Tamaño de numeros.
  - Contraste del tablero.
  - Reducir animaciones.

- [ ] Revisar accesibilidad:
  - Content descriptions.
  - Tamaños tactiles.
  - Contraste.
  - TalkBack basico.
  - Textos que no se corten.

## Textos E Internacionalizacion

- [x] Primera pasada de textos cortos de UI a `strings.xml`.
- [x] Mensajes del tutorial en `GameViewModel` usando `StringProvider`.
- [x] `values-en/strings.xml` queda como scaffold vacio para no mezclar idiomas.

- [ ] Definir estrategia para contenido pedagogico largo:
  - `TechniqueTutorialFixtures.kt`.
  - `TechniquesScreen.kt`.
  - `StrategyContext.kt`.
  - `SudokuHint.kt`.

- [ ] Correccion completa de copy:
  - Tildes.
  - Consistencia de tono.
  - Claridad de hints.
  - Claridad de tecnicas.
  - Copy de Premium.
  - Copy de Play Store.

- [ ] Preparar localizacion real si se decide publicar en ingles:
  - Espanol base.
  - Ingles completo.
  - Evitar mezcla parcial de idiomas.

## Premium, Ads Y Monetizacion

- [ ] Definir beneficios Premium V1 definitivos:
  - Hints ilimitadas.
  - Sin anuncios para pedir pistas.
  - Apoyar desarrollo.

- [ ] Revisar pantalla Premium:
  - Mas atractiva sin ser agresiva.
  - Copy legal y claro.
  - Confirmar que no prometa mas de lo implementado.

- [ ] Probar Play Billing de punta a punta.
- [ ] Probar rewarded ads de punta a punta.
- [ ] Revisar UMP/consentimiento antes de activar anuncios reales.
- [ ] Confirmar Remote Config seguro para release:
  - Ads apagados si no esta todo listo.
  - Premium activado solo si el producto esta listo.

## Datos, Firebase Y Privacidad

- [x] Documento de privacidad/data safety en `docs/privacy-data-safety.md`.
- [x] Politica de privacidad publicada en Google Sites.
- [x] `allowBackup=false`.
- [x] `data_extraction_rules.xml` excluye backup/transfer.

- [ ] Revisar reglas de Firestore.
- [ ] Revisar App Check.
- [ ] Confirmar que no se guarden tableros completos en Firestore.
- [ ] Revisar costos potenciales de Analytics/Firestore.
- [ ] Agregar forma de solicitar borrado de datos si Play Console lo exige para la configuracion final.

## Play Store Y Release

- [x] Checklist de release creado en `docs/release-checklist.md`.
- [x] Room schema export activo y versionado.
- [x] `applicationId` publicado documentado en `AGENTS.md`.
- [x] Separar builds por flavor `dev`/`prod` para Firebase Dev y Firebase Prod.

- [ ] Completar ficha de Play Store:
  - Descripcion corta.
  - Descripcion completa.
  - Screenshots.
  - Feature graphic.
  - Categoria.
  - Tags.
  - Politica de privacidad.

- [ ] Completar formularios:
  - Data Safety.
  - Ads.
  - Compras dentro de la app.
  - Clasificacion de contenido.
  - Acceso a la app.

- [ ] Antes de release productivo:
  - Ejecutar `bundleRelease`.
  - Revisar versionCode/versionName.
  - Revisar signing config.
  - Revisar mapping si se activa minify/R8.
  - Revisar advertencia de Play Console sobre archivo de desofuscacion:
    - Si se activa `minifyEnabled`/R8, generar y subir el mapping file.
    - Si R8 sigue apagado, documentar que la advertencia no aplica.
  - Revisar advertencia de Play Console sobre simbolos nativos:
    - Identificar que dependencias aportan `.so`.
    - Subir simbolos nativos si Play lo sigue recomendando para produccion.

## Arquitectura, Calidad Y Tests

- [x] Tests base de `GameViewModel`.
- [x] Tests para tutorial interactivo.
- [x] Tests de generacion/hints/solver existentes.
- [x] Limpieza de archivos muertos recientes.

- [ ] Separar archivos grandes:
  - `GameScreen.kt`.
  - `TechniquesScreen.kt`.

- [ ] Mejorar cobertura:
  - SettingsViewModel.
  - Premium/Billing con fakes.
  - Fixtures de tecnicas.
  - Reglas de tutorial en casos limite.

- [ ] Revisar performance:
  - Generacion de tablero.
  - Recomposition del tablero.
  - Animaciones.
  - Biblioteca de tecnicas.

- [ ] Revisar dependencias:
  - Mantener solo librerias necesarias.
  - Confirmar que Lottie/Konfetti aportan valor real.
  - Evitar assets pesados sin uso.

## Ideas Futuras

- [ ] Feedback del usuario desde Settings.
- [ ] Estadisticas avanzadas.
- [ ] Revision post-partida.
- [ ] Modo entrenamiento por tecnica.
- [ ] Temas visuales premium.
- [ ] Sincronizacion futura con login Google solo si aporta valor.

## Decisiones Vigentes

- No cambiar `applicationId`.
- No usar destructive migrations en release.
- No dejar debug boards activos.
- No agregar features invasivas antes de consolidar calidad.
- Premium debe sentirse como apoyo/entrenamiento extendido, no como bloqueo agresivo.
- Ads solo rewarded y por eleccion explicita del usuario.
