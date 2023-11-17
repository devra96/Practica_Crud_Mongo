package com.example.practica_crud_mongo;

public class Dispositivo {
    private String mac, marca, modelo, tipo;

    public Dispositivo(String mac, String marca, String modelo, String tipo) {
        this.mac = mac;
        this.marca = marca;
        this.modelo = modelo;
        this.tipo = tipo;
    }

    public String getMac() {
        return mac;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return "Dispositivo{" +
                "mac='" + mac + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
