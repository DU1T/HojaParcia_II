package app.model;

public class Categoria
{
    private Integer id;
    private String nombre;
    private Integer estado;

    // Constructor vacío (necesario para frameworks y utilidades)
    public Categoria(){}

    // Constructor con todos los campos
    public Categoria(Integer id, String nombre, int estado)
    {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }
    // Constructor sin ID (para cuando se va a insertar)
    public Categoria(String nombre, Integer estado)
    {
        this(null, nombre, estado);
    }

    //Setters
    public void setId(Integer id)
    {
        this.id = id;
    }
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
    public void setEstado(Integer estado)
    {
        this.estado = estado;
    }
    //Getters
    public Integer getEstado() {
        return estado;
    }
    public String getNombre() {
        return nombre;
    }
    public Integer getId()
    {
        return id;
    }
    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estado=" + (estado == 1 ? "Activo" : "Inactivo") +
                '}';
    }
}
