package com.example.lacteosbeln;


import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private EditText numeroRuta;
    private EditText nombreProveedor;
    private EditText cantidadLeche;
    private RadioButton seleccionaBotellas;
    private RadioButton seleccionaLitros;

    private String hora;
    private String fecha;
    private String nombreArchivo;
    private String file_path;


    ArrayList<Pedido> lista = new ArrayList<Pedido>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        this.componentesUI();
   //     this.formatoDeFechaExcel();
        //this.crearPedido();
        this.leer();


    }

    public void componentesUI() {
        numeroRuta = (EditText) findViewById(R.id.ETNumeroDeRuta);
        nombreProveedor = (EditText) findViewById(R.id.ETNombreProveedor);
        cantidadLeche = (EditText) findViewById(R.id.ETCantidadLeche);
        seleccionaBotellas = (RadioButton) findViewById(R.id.BtnBotellas);
        seleccionaLitros = (RadioButton) findViewById(R.id.BtnLitros);

    }


    public void formatoDeFechaExcel() {
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        hora = localDate.format(formatoHora);
        fecha = localDate.format(formatoFecha);
        Log.d("fecha", fecha + " Hora :" + hora);
    }

    public void guardar(View view) {
         this.crearPedido();
         this.crearExcel();
    }

    public void crearPedido() {
        if (seleccionaBotellas.isChecked()) {
            Pedido persona = new Pedido(String.valueOf(numeroRuta.getText()), String.valueOf(nombreProveedor.getText()), String.valueOf(cantidadLeche.getText()));
            persona.setCantidadBotellas(true);
            this.lista.add(persona);
            //  Log.d("prueba-Botellas", lista.toString());
        } else if (seleccionaLitros.isChecked()) {
            Pedido persona = new Pedido(String.valueOf(numeroRuta.getText()), String.valueOf(nombreProveedor.getText()), String.valueOf(cantidadLeche.getText()));
            persona.setCantidadLitros(true);
            this.lista.add(persona);
            // Log.d("prueba-Litros", lista.toString());
        } else {
            Log.d("err", "fuera del condicional linea 101");

        }

    }


    public void leer(){{
        File file = new File(this.getExternalFilesDir(null), "BDProveedores.xls");
        FileInputStream inputStream = null;

        String datos ="";

        try {
            inputStream= new FileInputStream(file);
          POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

          HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

          HSSFSheet sheet = workbook.getSheetAt(0);

          Iterator<Row> rowIterator = sheet.rowIterator();
          while (rowIterator.hasNext()){
              HSSFRow row = (HSSFRow) rowIterator.next();

              Iterator<Cell> cellIterator = row.cellIterator();
              while (cellIterator.hasNext()) {
                  HSSFCell cell = (HSSFCell) cellIterator.next();

                  datos = datos + " - " + cell.toString();
              }
            datos = datos +"\n";
          }
            Log.d("Excel", datos);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    }

    public void crearExcel() {

        Workbook wb = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);

        Sheet sheet = null;
        sheet = wb.createSheet("Hoja 1");

        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Numero de Ruta");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Nombre del Proveedor");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Cantidad de leche");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Tipo de envasado");
        cell.setCellStyle(cellStyle);

        for (int i = 0; i < 4; i++) {
            sheet.setColumnWidth(i, 4 * 2000);
        }

        for (int i = 0; i < lista.size(); i++) {
            Pedido persona = lista.get(i);
            Row fila = sheet.createRow(i + 1);

            cell = fila.createCell(0);
            cell.setCellValue(persona.getNumeroDeRuta());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(1);
            cell.setCellValue(persona.getNombreProveedor());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(2);
            cell.setCellValue(persona.getCantidadLeche());
            Log.d("cantLeche", persona.getCantidadLeche());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(3);
            if (persona.getCantidadBotellas()) {
                cell.setCellValue("Botellas");
            } else if (persona.getCantidadLitros()) {
                cell.setCellValue("Litros");
            }
            cell.setCellStyle(cellStyle);


        }

        this.formatoDeFechaExcel();
        nombreArchivo = "Informe"  + fecha + ".xls";
        File file = new File(getExternalFilesDir(null), nombreArchivo);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this, "Listo!", Toast.LENGTH_LONG);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "No listo!", Toast.LENGTH_LONG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "No listos!", Toast.LENGTH_LONG);

    }

}


