package app.model;

public class Cliente
{
    private Integer id;
    private String nombre;
    private String nit;
    private String telefono;
    private Integer estado;

    // Constructor vacío (necesario para frameworks y utilidades)
    public Cliente() {}
    // Constructor con todos los campos
    public Cliente(Integer id, String nombre, String nit, String telefono, int estado) {
        this.id = id; this.nombre = nombre; this.nit = nit; this.telefono = telefono; this.estado = estado;
    }
    // Constructor sin ID (para cuando se va a insertar)
    public Cliente(String nombre, String nit, String telefono, int estado)
    { this( null , nombre, nit, telefono, estado); }

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}
