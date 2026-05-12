# Sudoku Mentor - Backlog Para Play Store

Este documento es la lista viva de pendientes para llevar Sudoku Mentor a una app robusta, completa y digna de Play Store. La idea es actualizarlo cada vez que aparezca una deuda nueva o una mejora importante.

## Objetivo Del Producto

Sudoku Mentor debe sentirse como una app tranquila, profesional y enfocada en aprender a resolver sudokus con lógica: pensado para resolver, no adivinar.

La prioridad no es agregar features por volumen, sino construir una experiencia estable, clara, pulida y presentable como portfolio.

## UI Y Pulido Visual

- [ ] Rediseñar el sistema visual general.
  - Mejorar botones, estados presionados, radios, sombras/bordes y jerarquía visual.
  - Sumar algún color de acento adicional para detalles puntuales sin romper la calma de la app.
  - Revisar si el azul actual necesita un segundo color de soporte.

- [ ] Crear ícono de app definitivo.
  - Launcher icon.
  - Adaptive icon.
  - Posible versión monocromática si aplica.

- [ ] Definir tokens de diseño reales.
  - Colores.
  - Espaciados.
  - Radios.
  - Tipografías.
  - Elevaciones/bordes.
  - Estados interactivos.

- [ ] Revisar botones principales y secundarios.
  - Home.
  - Game actions.
  - Premium.
  - Settings.
  - Dialogs.

- [ ] Mejorar el dialog de error en hints.
  - El dialog actual quedó viejo y visualmente pobre.
  - Debe explicar mejor qué pasó cuando hay errores en el tablero.
  - Debe mantener el tono calmo de la app.

- [ ] Mejorar la pantalla/dialog de victoria.
  - Agregar algún efecto visual de resolución.
  - Mostrar resumen claro: dificultad, tiempo, hints usadas, errores, quizá técnica final.
  - Hacer que se sienta como cierre de partida, no solo un popup.

- [ ] Mejorar el acceso desde hint a técnica.
  - Hoy tocar el nombre de la técnica es poco evidente.
  - Agregar un affordance más claro: icono, texto secundario o botón "Ver técnica".

- [ ] Revisar pantallas en varios tamaños.
  - Pixel 7 Pro.
  - Pantallas chicas.
  - Landscape si se decide soportar.
  - Tablets si se mantiene habilitado.

## Internacionalización Y Textos

- [ ] Mover textos hardcodeados a `strings.xml`.
  - Preparar estructura para múltiples idiomas.
  - Separar textos de UI, hints, técnicas, premium, errores y release.

- [ ] Preparar localización.
  - `values/strings.xml` base.
  - `values-es/strings.xml`.
  - Evaluar `values-en/strings.xml` para publicación internacional.

- [ ] Corrección completa de textos.
  - Ortografía.
  - Tildes.
  - Consistencia de tono.
  - Claridad de hints.
  - Claridad de técnicas.
  - Copy de Premium.

- [ ] Revisar si se entiende todo el flujo de aprendizaje.
  - Qué es una pista.
  - Qué es una técnica.
  - Por qué la app no invita a adivinar.
  - Qué beneficios da Premium.

## Gameplay, Hints Y Técnicas

- [ ] Auditar todas las pistas.
  - Confirmar que la lógica sea correcta.
  - Confirmar que el texto corresponda a las celdas resaltadas.
  - Confirmar que las eliminaciones y objetivos sean entendibles.

- [ ] Validar todos los ejemplos de técnicas.
  - Los tableros de tutorial deben representar realmente la técnica.
  - Especial atención a X-Wing y Y-Wing.
  - Mantener divisiones de cajas 3x3 bien visibles.

- [ ] Mejorar biblioteca de técnicas.
  - Textos más profundos para técnicas complejas.
  - Más de un ejemplo por técnica cuando ayude.
  - Posible uso de coordenadas tipo A1, B4 si mejora la explicación.

- [ ] Evaluar modo práctica por técnica.
  - Generar o cargar ejemplos para practicar una técnica concreta.
  - Ejemplo: "Practicar Naked Pair", "Practicar X-Wing".
  - Puede ser un beneficio Premium futuro.

- [ ] Revisar si hay que agregar más técnicas.
  - Naked Single.
  - Hidden Single.
  - Naked Pair.
  - Hidden Pair.
  - Intersection Removal.
  - Naked Triple.
  - X-Wing.
  - Y-Wing.
  - Futuras técnicas candidatas.

## Settings, Vibración Y Preferencias

- [ ] Resolver vibración.
  - Hoy sigue sin funcionar bien en prueba real.
  - Revisar permisos, APIs, configuración del dispositivo y fallback.
  - Si no queda confiable, quitarla antes de release.

- [ ] Expandir configuración.
  - Sonido opcional.
  - Mostrar/ocultar timer.
  - Tamaño de números.
  - Contraste del tablero.
  - Animaciones reducidas.

- [ ] Explorar estilos/skins.
  - Temas de color.
  - Variantes de tablero.
  - Posible feature Premium si suma valor real.

## Premium, Monetización Y Ads

- [ ] Definir beneficios Premium V1 y futuros.
  - V1: hints ilimitadas, sin anuncios para pedir pistas, apoyar desarrollo.
  - Futuro: skins, estadísticas avanzadas, práctica por técnica, revisión post-partida.

- [ ] Revisar pantalla Premium.
  - Hacerla más llamativa sin parecer agresiva.
  - Mejorar visual del tablero si hace falta.
  - Confirmar que el copy sea legal, claro y no engañoso.

- [ ] Probar Billing de punta a punta.
  - Compra exitosa.
  - Compra cancelada.
  - Compra pendiente.
  - Restore.
  - Desinstalar/reinstalar y recuperar Premium.

- [ ] Probar rewarded ads de punta a punta.
  - Debug con test ad.
  - Release con Remote Config apagado.
  - Release con Remote Config prendido cuando AdMob esté listo.
  - Error amable si no carga anuncio.

- [ ] Revisar UMP/consentimiento para ads.
  - Confirmar que está correcto antes de activar anuncios reales.
  - Revisar países/regiones y privacidad.

## Login, Usuario Y Datos

- [ ] Evaluar login con Google.
  - Hoy existe anonymous login.
  - Google login podría servir para sincronizar progreso, rankings o restaurar identidad.
  - Evitar hacerlo invasivo si no aporta valor inmediato.

- [ ] Definir qué datos vale la pena asociar al usuario.
  - Partidas terminadas.
  - Dificultad.
  - Tiempo.
  - Hints usadas.
  - Técnicas vistas/practicadas.
  - Estado Premium viene de Play Billing, no de Firestore como fuente principal.

- [ ] Revisar política de privacidad.
  - Auth.
  - Analytics.
  - Crashlytics.
  - Firestore.
  - AdMob.
  - Billing.

## Play Store Y Release

- [ ] Resolver advertencias de Play Console.
  - Archivo de desofuscación si se activa R8/ProGuard.
  - Símbolos nativos si Play vuelve a advertir.
  - Cualquier warning del pre-launch report.

- [ ] Completar ficha de Play Store.
  - Descripción corta.
  - Descripción completa.
  - Screenshots.
  - Feature graphic.
  - Categoría.
  - Tags.
  - Política de privacidad.

- [ ] Completar Data Safety.
  - Datos recolectados.
  - Datos compartidos.
  - Propósito de uso.
  - Borrado de datos si aplica.

- [ ] Revisar clasificación de contenido.
  - Confirmar que sea apta para todo público.
  - Confirmar ads y compras dentro de la app.

- [ ] Preparar checklist de release.
  - Version code/name.
  - AAB firmado.
  - Release notes.
  - Remote Config seguro.
  - Ads reales apagados hasta validar.
  - Crashlytics funcionando.
  - Billing activo.

- [ ] Revisar pre-launch report.
  - Crashes.
  - ANRs.
  - Accesibilidad.
  - Rendimiento.
  - Capturas automáticas.

## Arquitectura, Calidad Y Tests

- [ ] Completar internacionalización sin ensuciar ViewModels.
  - Evitar meter strings Android en dominio puro.
  - Definir dónde se formatean textos dinámicos de hints.

- [ ] Separar archivos grandes.
  - `GameScreen.kt`.
  - `TechniquesScreen.kt`.
  - Componentes de settings/premium si siguen creciendo.

- [ ] Consolidar theme/tokens.
  - `Dimens` está poco usado.
  - Material theme conserva rastros de template.
  - Alinear todo al diseño final.

- [ ] Mejorar cobertura de tests.
  - GameViewModel ya tiene base.
  - Agregar tests para SettingsViewModel.
  - Agregar tests para premium/billing con fakes.
  - Agregar tests de fixtures de técnicas.

- [ ] Revisar Room antes de release.
  - `exportSchema = true`.
  - Migraciones reales.
  - No usar destructive migrations en release.

- [ ] Revisar generación de sudokus.
  - Mantener seeds random en producción.
  - Mantener testabilidad determinística donde haga falta.
  - Evitar dependencias Android innecesarias en dominio.

- [ ] Revisar performance.
  - Generación de tablero.
  - Animaciones.
  - Pantalla de técnicas.
  - Uso de recompositions en tablero.

- [ ] Revisar accesibilidad.
  - Content descriptions.
  - Tamaños táctiles.
  - Contraste.
  - TalkBack básico.
  - Textos que no se corten.

## Analytics, Crashlytics Y Observabilidad

- [ ] Revisar eventos Analytics.
  - Inicio de partida.
  - Partida terminada.
  - Dificultad.
  - Hints usadas.
  - Límite de hints alcanzado.
  - Ads rewarded.
  - Premium.
  - Técnicas abiertas.

- [ ] Revisar agregados en Firestore.
  - Mantenerlos compactos para controlar costos.
  - No guardar eventos individuales innecesarios.

- [ ] Mejorar contexto de Crashlytics.
  - Dificultad.
  - Estado premium.
  - Hints usadas.
  - Versión de app.
  - Última acción relevante si sirve.

## Ideas Futuras

- [ ] Estadísticas avanzadas.
  - Tiempo promedio por dificultad.
  - Técnicas más usadas.
  - Rachas.
  - Progreso de aprendizaje.

- [ ] Revisión post-partida.
  - Qué técnicas aparecieron.
  - Cuántas pistas se usaron.
  - Dónde se cometieron errores.

- [ ] Modo entrenamiento.
  - Elegir técnica.
  - Resolver micro-ejercicios.
  - Feedback guiado.

- [ ] Temas visuales premium.
  - Clásico.
  - Alto contraste.
  - Nocturno suave.
  - Azul actual pulido.

- [ ] Sincronización futura.
  - Requiere decidir si Google login aporta valor suficiente.

## Notas De Decisión

- No agregar features invasivas antes de consolidar calidad.
- No esconder ayudas importantes detrás de Premium de forma agresiva.
- Premium debe sentirse como apoyo y entrenamiento extendido, no como bloqueo artificial.
- Ads solo rewarded y por elección explícita del usuario.
- La UI final debe verse seria y profesional, no como demo.
