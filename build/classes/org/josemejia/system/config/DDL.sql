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
        rol varchar(50)
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
    in p_rol varchar(50)
)
begin
    insert into usuarios (id_usuario, nombre, apellido, correo, usuario, password, rol)
    values (p_id_usuario, p_nombre, p_apellido, p_correo, p_usuario, p_password, p_rol);
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

create procedure sp_consultar_libro_por_id (
    in p_id_libro int
)
begin
    select * from libros where id_libro = p_id_libro;
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

insert into usuarios (id_usuario, nombre, apellido, correo, usuario, password, rol)
values ('1', 'Jose', 'Mejia', 'jefe@exoduscodex.com', 'jefe', '1234', 'Bibliotecario Jefe');


