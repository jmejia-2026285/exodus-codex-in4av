drop database if exists exoduscodex_in4av;
create database exoduscodex_in4av;
use exoduscodex_in4av;


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

call sp_agregar_usuario("2573", "Alanbrito", "larios", "Alan", "AlanL", "257", "BIBLIOTECARIO");