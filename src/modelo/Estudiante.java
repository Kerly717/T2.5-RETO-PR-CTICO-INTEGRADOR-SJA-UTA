package modelo;

public class Estudiante {

    private final int matricula;
    private String cedula;
    private String nombres;
    private double promedio;

    public Estudiante(int matricula, String cedula, String nombres, double promedio) {
        if (matricula <= 0) {
            throw new IllegalArgumentException("La matrícula debe ser positiva");
        }
        this.matricula = matricula;
        setCedula(cedula);
        setNombres(nombres);
        setPromedio(promedio);
    }

    public int getMatricula() {
        return matricula;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            throw new IllegalArgumentException("La cédula debe tener 10 dígitos");
        }
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("Los nombres no pueden estar vacíos");
        }
        this.nombres = nombres.trim();
    }

    public double getPromedio() {
        return promedio;
    }

    public void setPromedio(double promedio) {
        if (!Double.isFinite(promedio) || promedio < 0 || promedio > 10) {
            throw new IllegalArgumentException("El promedio debe estar entre 0 y 10");
        }
        this.promedio = promedio;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | CI %s | promedio %.2f", matricula, nombres, cedula, promedio);
    }
}
