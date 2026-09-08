drop database if exists ExodusCodex_in4av;
create database ExodusCodex_in4av;
use ExodusCodex_in4av;

    create table USUARIOS (
		id_usuario Varchar(50) primary key,
		nombre varchar(50),
		apellido varchar(50),
		correo varchar(50) unique,
		usuario varchar(50) unique,
		password varchar(50),
        rol varchar(50)
    );

    create table LIBROS (
        id_libro int primary key auto_increment, 
        isbn varchar(50) unique,
		titulo varchar(50),
		autor_principal varchar(50),
		editorial varchar(50),
        anio_publicacion int, 
        copias_disponibles int,
		portada varchar(50)
    );

DELIMITER $$

-- ==========================================
-- PROCEDIMIENTOS PARA AGREGAR
-- ==========================================

-- Agregar un nuevo usuario
CREATE PROCEDURE sp_agregar_usuario (
    IN p_id_usuario Varchar(50),
    IN p_nombre VARCHAR(50),
    IN p_apellido VARCHAR(50),
    IN p_correo VARCHAR(50),
    IN p_usuario VARCHAR(50),
    IN p_password VARCHAR(50),
    IN p_rol VARCHAR(50)
)
BEGIN
    INSERT INTO USUARIOS (id_usuario, nombre, apellido, correo, usuario, password, rol)
    VALUES (p_id_usuario, p_nombre, p_apellido, p_correo, p_usuario, p_password, p_rol);
END$$

-- Agregar un nuevo libro
CREATE PROCEDURE sp_agregar_libro (
    IN p_isbn VARCHAR(50),
    IN p_titulo VARCHAR(50),
    IN p_autor_principal VARCHAR(50),
    IN p_editorial VARCHAR(50),
    IN p_anio_publicacion INT,
    IN p_copias_disponibles INT,
    IN p_portada VARCHAR(50)
)
BEGIN
    INSERT INTO LIBROS (isbn, titulo, autor_principal, editorial, anio_publicacion, copias_disponibles, portada)
    VALUES (p_isbn, p_titulo, p_autor_principal, p_editorial, p_anio_publicacion, p_copias_disponibles, p_portada);
END$$

-- ==========================================
-- PROCEDIMIENTOS PARA CONSULTAR
-- ==========================================

-- Consultar todos los elementos
CREATE PROCEDURE sp_consultar_usuarios ()
BEGIN
    SELECT * FROM USUARIOS;
END$$

CREATE PROCEDURE sp_consultar_libros ()
BEGIN
    SELECT * FROM LIBROS;
END$$


-- Consultar por ID
CREATE PROCEDURE sp_consultar_usuario_por_id (
    IN p_id_usuario varchar(50)
)
BEGIN
    SELECT * FROM USUARIOS WHERE id_usuario = p_id_usuario;
END$$

CREATE PROCEDURE sp_consultar_libro_por_id (
    IN p_id_libro INT
)
BEGIN
    SELECT * FROM LIBROS WHERE id_libro = p_id_libro;
END$$
-- ==========================================
-- PROCEDIMIENTOS NUEVOS (faltantes)
-- ==========================================

-- Login: validar usuario y password
CREATE PROCEDURE sp_login_usuario (
    IN p_usuario VARCHAR(20),
    IN p_password VARCHAR(20)
)
BEGIN
    SELECT id_usuario, nombre, apellido, correo, usuario, rol
    FROM USUARIOS
    WHERE usuario = p_usuario AND password = p_password;
END$$

-- Buscar libros por texto (titulo, autor o isbn)
CREATE PROCEDURE sp_buscar_libro (
    IN p_termino VARCHAR(20)
)
BEGIN
    SELECT * FROM LIBROS
    WHERE titulo LIKE CONCAT('%', p_termino, '%')
       OR autor_principal LIKE CONCAT('%', p_termino, '%')
       OR isbn LIKE CONCAT('%', p_termino, '%');
END$$

-- Actualizar libro existente
CREATE PROCEDURE sp_actualizar_libro (
    IN p_id_libro INT,
    IN p_isbn VARCHAR(20),
    IN p_titulo VARCHAR(20),
    IN p_autor_principal VARCHAR(20),
    IN p_editorial VARCHAR(20),
    IN p_anio_publicacion INT,
    IN p_copias_disponibles INT,
    IN p_portada VARCHAR(20)
)
BEGIN
    UPDATE LIBROS
    SET isbn = p_isbn,
        titulo = p_titulo,
        autor_principal = p_autor_principal,
        editorial = p_editorial,
        anio_publicacion = p_anio_publicacion,
        copias_disponibles = p_copias_disponibles,
        portada = p_portada
    WHERE id_libro = p_id_libro;
END$$

-- Eliminar libro
CREATE PROCEDURE sp_eliminar_libro (
    IN p_id_libro INT
)
BEGIN
    DELETE FROM LIBROS WHERE id_libro = p_id_libro;
END$$
DELIMITER ;


