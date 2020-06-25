package com.example.lacteosbeln.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class FechaUtil {
    private String hora;
    private String fecha;
    private String DiaDeLaSemana;

    SimpleDateFormat sdf= new SimpleDateFormat("E-dd");


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void formatoDeFechaExcel() {
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        hora = localDate.format(formatoHora);
        fecha = localDate.format(formatoFecha);
        Log.d("fecha", fecha + " Hora :" + hora);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDiaDeLaSemana(){
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("EEEE");
        return localDate.format(formatoFecha);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDiaDelMes(){
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd");
        return localDate.format(formatoFecha);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getMes(){
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("MMMM");
        return localDate.format(formatoFecha);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getAño(){
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("YYYY");
        return localDate.format(formatoFecha);
    }

}
