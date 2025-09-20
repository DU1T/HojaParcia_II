package app.model;
//Clase de modelo Libro
//Maqueta para manejar nuestros Libros, metodos getters y setters
public class Libro
{
    //Atributos
    private Integer id;    // null al insertar
    private String nombre;
    private int anio;
    private int idAutor;   // FK a autor.id
    private int idCategoria;
    private int estado;    // 1 = Activo, 0 = Inactivo

    //Constructor vacio
    public Libro() {}

    //Constructor con todos los atributos
    public Libro(Integer id, String nombre, int anio, int idAutor, int idCategoria, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.idAutor = idAutor;
        this.idCategoria = idCategoria;
        this.estado = estado;
    }
    //Constructor con atributos excepto ID, para carga a DB
    public Libro(String nombre, int anio, int idAutor,int idCategoria, int estado) {
        this(null, nombre, anio, idAutor, idCategoria, estado);
    }

    //Getters
    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public int getAnio() { return anio; }
    public int getIdAutor() { return idAutor; }
    public int getIdCategoria() { return idCategoria; }
    public int getEstado() { return estado; }
    //Setters
    public void setId(Integer id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    public void setEstado(int estado) { this.estado = estado; }
    //Sobrecarga del metodo toString
    @Override
    public String toString() {
        return "Libro{id=" + id + ", nombre='" + nombre + "'}";
    }
}
