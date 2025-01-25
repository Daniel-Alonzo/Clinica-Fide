-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 09-11-2023 a las 04:17:26
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `consultorio`
--
CREATE DATABASE IF NOT EXISTS `consultorio` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `consultorio`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `citas`
--

CREATE TABLE `citas` (
  `id` int(11) NOT NULL,
  `id_paciente` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `area_atencion` varchar(200) NOT NULL,
  `sintomas` varchar(200) NOT NULL,
  `consultada` varchar(2) NOT NULL,
  `pagada` varchar(2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consultas`
--

CREATE TABLE `consultas` (
  `id` int(11) NOT NULL,
  `id_paciente` int(11) NOT NULL,
  `tratamientos` varchar(200) NOT NULL,
  `diagnostico` varchar(200) NOT NULL,
  `observaciones` varchar(200) NOT NULL,
  `medicamentos` varchar(200) NOT NULL,
  `archivos` varchar(300) DEFAULT NULL,
  `id_cita` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `equipos`
--

CREATE TABLE `equipos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `equipos`
--

INSERT INTO `equipos` (`id`, `nombre`, `cantidad`) VALUES
(1, 'Guantes', 20),
(2, 'Mascarillas', 40),
(3, 'Batas', 12),
(4, 'Termómetro', 30),
(5, 'Jeringas', 10),
(6, 'Agujas', 30);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidades`
--

CREATE TABLE `especialidades` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `especialidades`
--

INSERT INTO `especialidades` (`id`, `nombre`) VALUES
(1, 'Ginecología');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `historiales_clinicos`
--

CREATE TABLE `historiales_clinicos` (
  `id` int(11) NOT NULL,
  `id_paciente` int(11) NOT NULL,
  `fecha_alta` datetime NOT NULL,
  `fecha_baja` datetime DEFAULT NULL,
  `fecha_ultima_consulta` datetime DEFAULT NULL,
  `id_consultas` varchar(300) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamentos`
--

CREATE TABLE `medicamentos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `cantidad` bigint(20) NOT NULL DEFAULT 0,
  `precio` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `medicamentos`
--

INSERT INTO `medicamentos` (`id`, `nombre`, `cantidad`, `precio`) VALUES
(1, 'Paracetamol', 20, 40),
(2, 'Naproxeno', 300, 30),
(3, 'Next', 175, 20),
(5, 'Diclofenaco', 11, 59);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicos`
--

CREATE TABLE `medicos` (
  `id` int(11) NOT NULL,
  `usuario` varchar(100) NOT NULL,
  `nombre_completo` varchar(200) NOT NULL,
  `genero` varchar(30) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `id_especialidad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `medicos`
--

INSERT INTO `medicos` (`id`, `usuario`, `nombre_completo`, `genero`, `fecha_nacimiento`, `id_especialidad`) VALUES
(22, 'doctor', 'Doctor Chapatin', 'Hombre', '1950-01-01', 1),
(23, 'doc', 'Juana Torres', 'Mujer', '1975-01-01', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pacientes`
--

CREATE TABLE `pacientes` (
  `id` int(11) NOT NULL,
  `usuario` varchar(100) NOT NULL,
  `nombre_completo` varchar(300) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `genero` varchar(30) NOT NULL,
  `id_tipo_sangre` int(11) DEFAULT NULL,
  `alergias` varchar(300) DEFAULT NULL,
  `estatura` float DEFAULT NULL,
  `peso` float DEFAULT NULL,
  `foto` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pacientes`
--

INSERT INTO `pacientes` (`id`, `usuario`, `nombre_completo`, `fecha_nacimiento`, `genero`, `id_tipo_sangre`, `alergias`, `estatura`, `peso`, `foto`) VALUES
(22, 'coyoy', 'Mario Coyoy López', '2003-01-01', 'Hombre', 8, '', 2, 100, NULL),
(23, 'dani', 'Dani Torres', '2005-01-01', 'Mujer', NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `nombre`) VALUES
(1, 'Administración'),
(2, 'Farmacia'),
(3, 'Doctor'),
(4, 'Paciente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipos_sangre`
--

CREATE TABLE `tipos_sangre` (
  `id` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tipos_sangre`
--

INSERT INTO `tipos_sangre` (`id`, `nombre`) VALUES
(1, 'A+'),
(2, 'A-'),
(3, 'B+'),
(4, 'B-'),
(5, 'AB+'),
(6, 'AB-'),
(7, 'O+'),
(8, 'O-');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `contrasena` varchar(100) NOT NULL,
  `id_rol` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id`, `nombre`, `contrasena`, `id_rol`) VALUES
(1, 'admin', '123', 1),
(2, 'farmacia', '123', 2),
(22, 'doctor', '123', 3),
(46, 'coyoy', '12345', 4),
(47, 'doc', 'qwerty', 3),
(48, 'dani', '12345', 4);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `citas`
--
ALTER TABLE `citas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_cita_paciente` (`id_paciente`);

--
-- Indices de la tabla `consultas`
--
ALTER TABLE `consultas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_consulta_paciente` (`id_paciente`);

--
-- Indices de la tabla `equipos`
--
ALTER TABLE `equipos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `historiales_clinicos`
--
ALTER TABLE `historiales_clinicos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_historiales_clinicos_id_paciente` (`id_paciente`) USING BTREE;

--
-- Indices de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `medicos`
--
ALTER TABLE `medicos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `usuario` (`usuario`),
  ADD KEY `FK_medico_especialidad` (`id_especialidad`);

--
-- Indices de la tabla `pacientes`
--
ALTER TABLE `pacientes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK_paciente_tipo_sangre` (`id_tipo_sangre`) USING BTREE,
  ADD KEY `usuario` (`usuario`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tipos_sangre`
--
ALTER TABLE `tipos_sangre`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`),
  ADD KEY `FK_usuario_rol` (`id_rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `citas`
--
ALTER TABLE `citas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT de la tabla `consultas`
--
ALTER TABLE `consultas`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT de la tabla `equipos`
--
ALTER TABLE `equipos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `historiales_clinicos`
--
ALTER TABLE `historiales_clinicos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `medicamentos`
--
ALTER TABLE `medicamentos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `medicos`
--
ALTER TABLE `medicos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT de la tabla `pacientes`
--
ALTER TABLE `pacientes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT de la tabla `roles`
--
ALTER TABLE `roles`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `tipos_sangre`
--
ALTER TABLE `tipos_sangre`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `citas`
--
ALTER TABLE `citas`
  ADD CONSTRAINT `FK_cita_paciente` FOREIGN KEY (`id_paciente`) REFERENCES `pacientes` (`id`);

--
-- Filtros para la tabla `consultas`
--
ALTER TABLE `consultas`
  ADD CONSTRAINT `FK_consulta_paciente` FOREIGN KEY (`id_paciente`) REFERENCES `pacientes` (`id`);

--
-- Filtros para la tabla `historiales_clinicos`
--
ALTER TABLE `historiales_clinicos`
  ADD CONSTRAINT `FK_historial_clinico_paciente` FOREIGN KEY (`id_paciente`) REFERENCES `pacientes` (`id`);

--
-- Filtros para la tabla `medicos`
--
ALTER TABLE `medicos`
  ADD CONSTRAINT `FK_medico_especialidad` FOREIGN KEY (`id_especialidad`) REFERENCES `especialidades` (`id`),
  ADD CONSTRAINT `FK_medico_usuario` FOREIGN KEY (`usuario`) REFERENCES `usuarios` (`nombre`);

--
-- Filtros para la tabla `pacientes`
--
ALTER TABLE `pacientes`
  ADD CONSTRAINT `FK_paciente_tipo_sangre` FOREIGN KEY (`id_tipo_sangre`) REFERENCES `tipos_sangre` (`id`),
  ADD CONSTRAINT `FK_paciente_usuario` FOREIGN KEY (`usuario`) REFERENCES `usuarios` (`nombre`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `FK_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
