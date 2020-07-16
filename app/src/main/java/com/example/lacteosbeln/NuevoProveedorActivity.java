package com.example.lacteosbeln;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class NuevoProveedorActivity extends AppCompatActivity {

    EditText tvNombre;
    EditText tvApellido;
    RadioButton litros;
    RadioButton botellas;

    ArrayList<Proveedor> listaProveedores = new ArrayList<Proveedor>();// arreglo que guarda la instancia de la BD de los proveedores de belen.

    ArrayList<String> lectorDatos = new ArrayList<String>();// arreglo que lee los datos del excel de la base de datos
    ArrayList<String> leaNombres = new ArrayList<String>();// arreglo que separa los nombres del lectorDatos
    ArrayList<String> leaEnvases = new ArrayList<String>();// arreglo que Separa el tipo de envase del LectorDatos
    ArrayList<String> leaId = new ArrayList<String>();// arreglo que Separa el ID del LectorDatos

    Proveedor nuevoProveedor = new Proveedor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_proveedor);
        //getSupportActionBar().hide();

        tvNombre = findViewById(R.id.etNombre);
        tvApellido = findViewById(R.id.etApellido);
        litros = findViewById(R.id.rbLitros);
        botellas =findViewById(R.id.rbBotellas);

        this.leerExcelProveedores();
        BasedeDatos basedeDatos = new BasedeDatos();
        basedeDatos.iniciarClase();


    }

    // Metodo que lee el excel con los proveedores y el tipo de envasado para luego usarse en la subclase BaseDeDatos
    public void leerExcelProveedores() {
        {
            File file = new File(this.getExternalFilesDir("DB"), "BDProveedores.xls");
            FileInputStream inputStream = null;

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    HSSFRow row = (HSSFRow) rowIterator.next();

                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        lectorDatos.add(cell.toString().toLowerCase());

                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BD", e.toString());
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void BotonGuardar (View view){
        nuevoProveedor.setNombre(tvNombre.getText().toString() + " " + tvApellido.getText().toString());
        if (litros.isChecked()){
            nuevoProveedor.setEnvasado("l");
        }else if (botellas.isChecked()){
            nuevoProveedor.setEnvasado("b");
        }
        String sid = String.valueOf(listaProveedores.size());
        int id = Integer.parseInt(sid);
        id = id + 1;
        nuevoProveedor.setId("C"+id);

        listaProveedores.add(nuevoProveedor);
        Toast.makeText(this, "Proveedor creado con exito!", Toast.LENGTH_SHORT).show();


        Log.d("npro", nuevoProveedor.toString());
        Log.d("npro", String.valueOf(listaProveedores.size()));

        reescribirBD();
        onBackPressed();

    }

    public void reescribirBD(){
        Workbook wb = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);

        Sheet sheet = null;
        sheet = wb.createSheet("Hoja 1");


        for (int i = 0; i < listaProveedores.size(); i++) {
            Row fila = sheet.createRow(i);

            cell = fila.createCell(0);
            cell.setCellValue(listaProveedores.get(i).getId());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(1);
            cell.setCellValue(listaProveedores.get(i).getEnvasado());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(2);
            cell.setCellValue(listaProveedores.get(i).getNombre());
            cell.setCellStyle(cellStyle);

        }

        File file = new File(getExternalFilesDir("DB"), "BDProveedores.xls");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class BasedeDatos {

        public void iniciarClase() {
            this.ingresarProveedores();
            this.instanciarProveedores();
        }

        // Metodo que lee de forma vectorial los tipo de dato Nombre y Tipo de envasado.
        public void ingresarProveedores() {

            for (int i = 0; i < lectorDatos.size(); i++) {
                String dato = lectorDatos.get(i).toString();
                lectorDatos.get(i);
                boolean esNumero = false;

                try {
                    if (Pattern.matches("^[c]{1}[0-9+]+$", lectorDatos.get(i).toString())) {
                        leaId.add(lectorDatos.get(i).toString());
                    }

                } catch (Exception e) {

                }


                if (Pattern.matches("[^0-9+]+", lectorDatos.get(i).toString())) {
                    if (lectorDatos.get(i).toString().equals("l") || lectorDatos.get(i).toString().equals("b")) {

                    } else {
                        leaNombres.add(lectorDatos.get(i));
                    }
                }
            }

            for (int i = 0; i < lectorDatos.size(); i++) {
                if (lectorDatos.get(i).equals("l") || lectorDatos.get(i).equals("b")) {
                    leaEnvases.add(lectorDatos.get(i));
                }
            }

        }

        // Metodo que instancia los objetos de tipo proveedor,que luego serán usados en pedido.
        public void instanciarProveedores() {

            for (int i = 0; i < leaId.size(); i++) {
                String id = leaId.get(i).toString();
                listaProveedores.add(new Proveedor(id));

            }

            for (int i = 0; i < leaNombres.size(); i++) {
                String nombre = leaNombres.get(i).toString();
                listaProveedores.get(i).setNombre(nombre);
            }

            listaProveedores.size();

            for (int j = 0; j < leaEnvases.size(); j++) {
                listaProveedores.get(j).setEnvasado(leaEnvases.get(j).toString());

            }


            //  Log.d("proveedor", listaProveedores.toString());

        }

    }


}
