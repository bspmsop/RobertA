package com.app.raassoc;

/**
 * Created by New android on 14-11-2018.
 */

public class Contacto {

    private String numero,correo,direction;
    private int img;
    public Contacto(String numero,String correo, String direction,int img){
        this.numero=numero;
        this.correo=correo;
        this.direction=direction;
        this.img=img;

    }
    public String getCorreo(){
        return correo;
    }
    public String getNumero(){
        return numero;
    }
    public String getDirection(){
        return direction;
    }
    public int getImg(){
        return img;
    }
}
