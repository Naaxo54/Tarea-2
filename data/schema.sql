-- ============================================================
-- Schema para base de datos SQLite de ejercicios
-- Sistema de Rutinas de Entrenamiento Personalizado
-- ============================================================

-- Crear tabla principal de ejercicios
CREATE TABLE IF NOT EXISTS ejercicios (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre           TEXT    NOT NULL,
    tipo             TEXT    NOT NULL CHECK(tipo IN ('cardiovascular', 'fuerza')),
    intensidad       TEXT    NOT NULL CHECK(intensidad IN ('baja', 'media', 'alta')),
    tiempo_estimado  INTEGER NOT NULL CHECK(tiempo_estimado > 0),
    descripcion      TEXT    NOT NULL
);

-- ============================================================
-- Datos de ejemplo (mismos del archivo CSV)
-- ============================================================

INSERT INTO ejercicios (nombre, tipo, intensidad, tiempo_estimado, descripcion) VALUES
('Salto de tijera', 'cardiovascular', 'baja', 5, 'Párate derecho con los pies juntos y los brazos a los costados. Salta abriendo las piernas al ancho de los hombros y levanta los brazos por encima de la cabeza al mismo tiempo. Regresa a la posición inicial saltando. Repite de forma continua manteniendo un ritmo moderado.'),
('Trote en el lugar', 'cardiovascular', 'baja', 8, 'Párate erguido y comienza a trotar en el mismo lugar levantando las rodillas hasta la altura de la cadera. Mantén los brazos en movimiento alternado al ritmo de las piernas. Respira de forma continua y mantén la espalda recta durante todo el ejercicio.'),
('Escalador de montaña', 'cardiovascular', 'media', 10, 'Comienza en posición de plancha alta con las manos directamente bajo los hombros. Lleva una rodilla hacia el pecho y luego extiende esa pierna mientras llevas la otra rodilla. Alterna de forma rápida y continua simulando el movimiento de escalar. Mantén las caderas bajas y el core apretado.'),
('Burpee básico', 'cardiovascular', 'alta', 12, 'Desde posición de pie, baja al suelo colocando las manos y lanza los pies hacia atrás en posición de plancha. Realiza una flexión y luego lleva los pies hacia las manos. Salta hacia arriba con los brazos por encima de la cabeza. Repite el movimiento de forma dinámica y continua.'),
('Salto con rodillas al pecho', 'cardiovascular', 'alta', 10, 'Párate con los pies al ancho de los hombros. Dobla ligeramente las rodillas y salta elevando ambas rodillas hasta la altura del pecho. Aterriza suavemente sobre la punta de los pies y dobla las rodillas para amortiguar. Repite explosivamente manteniendo el núcleo activado.'),
('Patadas de glúteo', 'cardiovascular', 'baja', 7, 'De pie con las manos en las caderas, lleva el talón derecho hacia el glúteo y regresa al suelo. Alterna con el talón izquierdo. Aumenta el ritmo gradualmente hasta hacer un trote con los talones tocando los glúteos. Mantén el pecho levantado y la postura erguida.'),
('Carrera estática de alta velocidad', 'cardiovascular', 'alta', 8, 'Párate con los pies al ancho de los hombros. Corre en el lugar lo más rápido posible elevando las rodillas hasta la cadera en cada paso. Mantén los brazos en movimiento activo. Concéntrate en la velocidad y la coordinación durante el intervalo completo. Respira de forma controlada.'),
('Skipping lateral', 'cardiovascular', 'media', 9, 'Párate de pie con los pies juntos. Da un paso lateral amplio a la derecha llevando el pie izquierdo hacia el derecho. Alterna la dirección de forma continua aumentando la velocidad gradualmente. Mantén las rodillas ligeramente dobladas y el cuerpo bajo para mayor estabilidad.'),
('Sentadilla básica', 'fuerza', 'baja', 10, 'Párate con los pies separados al ancho de los hombros y los dedos apuntando levemente hacia afuera. Empuja las caderas hacia atrás y dobla las rodillas bajando el cuerpo como si fuera a sentarse en una silla. Mantén la espalda recta y el pecho elevado. Regresa a la posición inicial empujando el suelo con los talones.'),
('Flexión de brazos (push-up)', 'fuerza', 'media', 10, 'Colócate en posición de plancha con las manos al ancho de los hombros y los brazos extendidos. Baja el pecho hacia el suelo doblando los codos a 45 grados respecto al torso. Desciende hasta que el pecho casi toque el suelo. Empuja de vuelta hasta la posición inicial. Mantén el cuerpo en línea recta durante todo el movimiento.'),
('Plancha abdominal', 'fuerza', 'media', 8, 'Apoya los antebrazos en el suelo con los codos directamente bajo los hombros y los pies juntos. Eleva el cuerpo formando una línea recta desde la cabeza hasta los talones. Aprieta el abdomen, los glúteos y los muslos. Mantén la posición el tiempo indicado sin dejar caer las caderas ni levantarlas.'),
('Sentadilla con salto', 'fuerza', 'alta', 12, 'Realiza una sentadilla regular bajando hasta que los muslos queden paralelos al suelo. Desde la posición baja, salta explosivamente hacia arriba extendiendo caderas, rodillas y tobillos. Aterriza suavemente con las rodillas dobladas y regresa inmediatamente a la posición baja de la sentadilla. Repite de forma continua.'),
('Zancada (lunge) frontal', 'fuerza', 'baja', 10, 'Párate con los pies juntos. Da un paso largo hacia adelante con el pie derecho y baja la rodilla izquierda hacia el suelo sin tocarlo. La rodilla delantera no debe sobrepasar los dedos del pie. Empuja con el pie delantero para regresar a la posición inicial. Alterna piernas en cada repetición.'),
('Remo en plancha (renegade row)', 'fuerza', 'alta', 14, 'Colócate en posición de plancha alta con un par de mancuernas (o en el suelo). Tira del codo derecho hacia atrás llevando la mancuerna hacia la cadera manteniendo la cadera estable. Baja la mano y repite con el lado izquierdo. Mantén el core activo para evitar rotar el tronco.'),
('Abdominales crunches', 'fuerza', 'baja', 8, 'Recuéstate boca arriba con las rodillas dobladas y los pies planos en el suelo. Coloca las manos detrás de la cabeza sin jalar el cuello. Contrae el abdomen y eleva los hombros del suelo unas pocas pulgadas. Exhala al subir e inhala al bajar. Mantén la zona lumbar pegada al suelo durante todo el movimiento.'),
('Fondos de tríceps (dips)', 'fuerza', 'media', 10, 'Siéntate en el borde de una silla resistente con las manos al lado de las caderas. Desplaza el cuerpo hacia adelante y baja el cuerpo doblando los codos a 90 grados. Empuja hacia arriba hasta extender casi completamente los brazos. Mantén la espalda cerca de la silla durante todo el movimiento.'),
('Elevación de talones (calf raise)', 'fuerza', 'baja', 6, 'Párate con los pies al ancho de los hombros y los dedos apuntando hacia adelante. Apoya las manos en una pared o superficie estable si es necesario. Elévate sobre la punta de los pies contrayendo los gemelos en el punto más alto. Baja controladamente hasta que los talones casi toquen el suelo y repite.'),
('Curl de bíceps con peso corporal', 'fuerza', 'media', 9, 'Apoya las manos en el borde de una mesa resistente (palmas hacia arriba) y coloca el cuerpo inclinado debajo. Tira del cuerpo hacia arriba flexionando los codos hasta que el pecho toque el borde. Baja lentamente extendiendo los brazos. Mantén el cuerpo en línea recta durante todo el movimiento.'),
('Superman en suelo', 'fuerza', 'baja', 7, 'Recuéstate boca abajo con los brazos extendidos por encima de la cabeza y las piernas juntas. Simultáneamente eleva los brazos, el pecho y las piernas del suelo contrayendo los glúteos y la espalda baja. Mantén la posición 2-3 segundos y baja controladamente. Repite el movimiento de forma fluida.'),
('Sentadilla sumo', 'fuerza', 'media', 11, 'Párate con los pies más separados que el ancho de los hombros y los dedos apuntando hacia afuera en ángulo de 45 grados. Mantén la espalda recta y el pecho elevado. Dobla las rodillas siguiendo la dirección de los dedos y baja hasta que los muslos queden paralelos al suelo. Regresa empujando desde los talones.');
