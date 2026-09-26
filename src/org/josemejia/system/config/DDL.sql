drop database if exists exoduscodex_in4av;
create database exoduscodex_in4av;
use exoduscodex_in4av;

        create table usuarios (
		id_usuario varchar(50) primary key,
		nombre varchar(50),
		apellido varchar(50),
		correo varchar(50) unique,
		usuario varchar(50) unique,
		password varchar(50),
        rol varchar(50),
        foto varchar(255)
    );

    create table libros (
        id_libro int primary key auto_increment, 
        isbn varchar(50) unique,
		titulo varchar(50),
		autor_principal varchar(50),
		editorial varchar(50),
        anio_publicacion int, 
        copias_disponibles int,
		portada varchar(255)
    );

delimiter $$

-- ==========================================
-- PROCEDIMIENTOS PARA AGREGAR
-- ==========================================

-- Agregar un nuevo usuario
create procedure sp_agregar_usuario (
    in p_id_usuario varchar(50),
    in p_nombre varchar(50),
    in p_apellido varchar(50),
    in p_correo varchar(50),
    in p_usuario varchar(50),
    in p_password varchar(50),
    in p_rol varchar(50),
    in p_foto varchar(255)
)
begin
    insert into usuarios (id_usuario, nombre, apellido, correo, usuario, password, rol, foto)
    values (p_id_usuario, p_nombre, p_apellido, p_correo, p_usuario, p_password, p_rol, p_foto);
end$$

-- Agregar un nuevo libro
create procedure sp_agregar_libro (
    in p_isbn varchar(50),
    in p_titulo varchar(50),
    in p_autor_principal varchar(50),
    in p_editorial varchar(50),
    in p_anio_publicacion int,
    in p_copias_disponibles int,
    in p_portada varchar(255)
)
begin
    insert into libros (isbn, titulo, autor_principal, editorial, anio_publicacion, copias_disponibles, portada)
    values (p_isbn, p_titulo, p_autor_principal, p_editorial, p_anio_publicacion, p_copias_disponibles, p_portada);
end$$

-- ==========================================
-- PROCEDIMIENTOS PARA CONSULTAR
-- ==========================================

-- Consultar todos los elementos
create procedure sp_consultar_usuarios ()
begin
    select * from usuarios;
end$$

create procedure sp_consultar_libros ()
begin
    select * from libros;
end$$


-- Consultar por ID
create procedure sp_consultar_usuario_por_id (
    in p_id_usuario varchar(50)
)
begin
    select * from usuarios where id_usuario = p_id_usuario;
end$$

-- ==========================================
-- PROCEDIMIENTOS NUEVOS (faltantes)
-- ==========================================

-- Login: validar usuario y password
create procedure sp_login_usuario (
    in p_usuario varchar(50),
    in p_password varchar(50)
)
begin
    select id_usuario, nombre, apellido, correo, usuario, rol
    from usuarios
    where usuario = p_usuario and password = p_password;
end$$

-- Buscar libros por texto (titulo, autor o isbn)
create procedure sp_buscar_libro (
    in p_termino varchar(100)
)
begin
    select * from libros
    where titulo like concat('%', p_termino, '%')
       or autor_principal like concat('%', p_termino, '%')
       or isbn like concat('%', p_termino, '%');
end$$

-- Actualizar libro existente
create procedure sp_actualizar_libro (
    in p_id_libro int,
    in p_isbn varchar(50),
    in p_titulo varchar(50),
    in p_autor_principal varchar(50),
    in p_editorial varchar(50),
    in p_anio_publicacion int,
    in p_copias_disponibles int,
    in p_portada varchar(255)
)
begin
    update libros
    set isbn = p_isbn,
        titulo = p_titulo,
        autor_principal = p_autor_principal,
        editorial = p_editorial,
        anio_publicacion = p_anio_publicacion,
        copias_disponibles = p_copias_disponibles,
        portada = p_portada
    where id_libro = p_id_libro;
end$$

-- Eliminar libro
create procedure sp_eliminar_libro (
    in p_id_libro int
)
begin
    delete from libros where id_libro = p_id_libro;
end$$
delimiter ;
use exoduscodex_in4av;

-- Eliminar usuario
drop procedure if exists sp_eliminar_usuario;

delimiter $$

create procedure sp_eliminar_usuario (
    in p_id_usuario varchar(50)
)
begin
    -- Protección en BD: un Bibliotecario Jefe no puede eliminarse
    if exists (select 1 from usuarios
               where id_usuario = p_id_usuario
                 and rol = 'Bibliotecario Jefe') then
        signal sqlstate '45000'
            set message_text = 'No se puede eliminar a un Bibliotecario Jefe.';
    end if;

    delete from usuarios where id_usuario = p_id_usuario;
end$$

-- Actualizar usuario existente
create procedure sp_actualizar_usuario (
    in p_id_usuario varchar(50),
    in p_nombre varchar(50),
    in p_apellido varchar(50),
    in p_correo varchar(50),
    in p_usuario varchar(50),
    in p_foto varchar(255)
)
begin
    update usuarios
    set nombre = p_nombre,
        apellido = p_apellido,
        correo = p_correo,
        usuario = p_usuario,
        foto = p_foto
    where id_usuario = p_id_usuario;
end$$

delimiter ;

CALL sp_agregar_usuario('1', 'Mariana',  'Vasquez',  'mariana.vasquez@exoduscodex.com',  'mvasquez',  'mariana123',  'Bibliotecario Jefe', NULL);
CALL sp_agregar_usuario('2', 'Andres',   'Paz',      'andres.paz@exoduscodex.com',       'apaz',      'andres123',   'Bibliotecario',      NULL);
CALL sp_agregar_usuario('3', 'Valeria',  'Cruz',     'valeria.cruz@exoduscodex.com',     'vcruz',     'valeria123',  'Bibliotecario',      NULL);
CALL sp_agregar_usuario('4', 'Fernando', 'Ordonez',  'fernando.ordonez@exoduscodex.com', 'fordonez',  'fernando123', 'Bibliotecario',      NULL);
CALL sp_agregar_usuario('5', 'Camila',   'Reyes',    'camila.reyes@exoduscodex.com',     'creyes',    'camila123',   'Bibliotecario',      NULL);

CALL sp_agregar_libro('978-0-307-47472-8', 'Cien años de soledad',    'Gabriel García Márquez', 'Sudamericana',           1967, 4, NULL);
CALL sp_agregar_libro('978-84-376-0494-7', 'Rayuela',                 'Julio Cortázar',          'Sudamericana',           1963, 3, NULL);
CALL sp_agregar_libro('978-0-14-118353-1', '1984',                    'George Orwell',           'Secker & Warburg',       1949, 5, NULL);
CALL sp_agregar_libro('978-0-06-112008-4', 'To Kill a Mockingbird',   'Harper Lee',              'J. B. Lippincott & Co.', 1960, 2, NULL);
CALL sp_agregar_libro('978-0-345-33970-1', 'El señor de los anillos', 'J. R. R. Tolkien',        'George Allen & Unwin',   1954, 6, NULL);

