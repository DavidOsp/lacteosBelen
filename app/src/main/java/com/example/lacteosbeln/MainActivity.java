package com.example.lacteosbeln;


import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lacteosbeln.util.FechaUtil;

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


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    //elementos de captura de la interfaz principal
    private EditText cantidadLeche;
    private TextView tipoDeEnvase;
    //private EditText numeroRuta;
    private EditText nombreProveedor; // item que funcionará como buscador
    private RecyclerView rvLista;  // item que mostrará lista obtenida del RV
    private AdaptadorBuscador adaptadorBuscador;
    private Spinner numeroDeRuta;

    private String nombreArchivo;
    private String idProveedorSeleccionado;

    FechaUtil fechaUtil = new FechaUtil();


    ArrayList<String> numeroDeRutas = new ArrayList<String>();
    ArrayList<Pedido> lista = new ArrayList<Pedido>();// arreglo que guarda a los proveedores de belen y su entrega.
    ArrayList<Proveedor> listaProveedores = new ArrayList<Proveedor>();// arreglo que guarda la instancia de la BD de los proveedores de belen.
    ArrayList<Proveedor> listaActualizada = new ArrayList<Proveedor>();// arreglo que guardará la lista actualizada del metodo filtrar.
    ArrayList<String> lectorDatos = new ArrayList<String>();// arreglo que lee los datos del excel de la base de datos
    ArrayList<String> leaNombres = new ArrayList<String>();// arreglo que separa los nombres del lectorDatos
    ArrayList<String> leaEnvases = new ArrayList<String>();// arreglo que Separa el tipo de envase del LectorDatos
    ArrayList<String> leaId = new ArrayList<String>();// arreglo que Separa el ID del LectorDatos


    ArrayList<Pedido> pedidosLunes = new ArrayList<Pedido>();
    ArrayList<Pedido> pedidosMartes = new ArrayList<Pedido>();
    ArrayList<Pedido> pedidosMiercoles = new ArrayList<Pedido>();
    ArrayList<Pedido> pedidosJueves = new ArrayList<Pedido>();
    ArrayList<Pedido> pedidosViernes = new ArrayList<Pedido>();
    ArrayList<Pedido> pedidosSabado = new ArrayList<Pedido>();
    ArrayList<Pedido> pedidosDomingo = new ArrayList<Pedido>();

    String inicioDiaInforme;
    String finDiaInforme;

    ArrayList<ResumenSemanal> resumenSemanalList = new ArrayList<ResumenSemanal>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getWindow().setBackgroundDrawableResource(R.drawable.interfaz_belen) ;

        this.leerArchivosSemanales();
        this.leerExcel();
        this.leerExcelProveedores();
        BasedeDatos dataBase = new BasedeDatos();
        dataBase.iniciarClase();
        this.componentesUI();
        this.instanciarInformeSemanal();
        this.crearExcelSemanal();

        //  this.formatoDeFechaExcel();
        //  this.leer();

    }

    // Metodo que carga los componentes gráficos de la interfaz.
    public void componentesUI() {
        cantidadLeche = (EditText) findViewById(R.id.ETCantidadLeche);
        tipoDeEnvase = (TextView) findViewById(R.id.tvTipoEnvase);
        numeroDeRuta = (Spinner) findViewById(R.id.spinnerRuta);
        nombreProveedor = findViewById(R.id.ETNombreProveedor);

        for (int i = 1; i < 7; i++) {
            this.numeroDeRutas.add(String.valueOf(i));
        }
        ArrayAdapter<String> UIRutas = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, numeroDeRutas);
        numeroDeRuta.setAdapter(UIRutas);


        nombreProveedor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });
        rvLista = findViewById(R.id.rvLista);
        rvLista.setLayoutManager(new GridLayoutManager(this, 1));

        adaptadorBuscador = new AdaptadorBuscador(MainActivity.this, listaActualizada);


        adaptadorBuscador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                for (int i = 0; i < 1; i++) {
                    nombreProveedor.setText(listaActualizada.get(rvLista.getChildAdapterPosition(view)).getNombre());
                    //nombreProveedor.setText(listaActualizada.get(i).getNombre());
                    if (listaActualizada.get(i).getEnvasado().equals("b")) {
                        tipoDeEnvase.setText("Botellas");
                    } else if (listaActualizada.get(i).getEnvasado().equals("l")) {
                        tipoDeEnvase.setText("Litros");
                    }

                }


                // forma de captura del nombre 2, descomentar si es necesario hacer pruebas.
                //nombreProveedor.setText(listaActualizada.get(rvLista.getChildAdapterPosition(view)).getId());

            }

        });
        rvLista.setAdapter(adaptadorBuscador);
    }


    // metodo que borra la busqueda anterior.
    public void borrarBusqueda(View vista) {
        nombreProveedor.setText("");
        tipoDeEnvase.setText("");
        listaActualizada.clear();
    }

    // Metodo que Muestra la busqueda del usuario por la UI.
    public void filtrar(String texto) {
        ArrayList<Proveedor> listaFiltrada = new ArrayList<Proveedor>();
        for (Proveedor p : listaProveedores) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(p);

            }
        }
        adaptadorBuscador.filtrar(listaFiltrada);
        this.listaActualizada = listaFiltrada;


    }

    // Metodo que guarda la lista de pedidos del dia.
    public void guardar(View view) {
        //Boton de guardar en a inferfaz.
        this.crearPedido();
        this.crearExcelDiario();
        this.borrarBusqueda(view);
        cantidadLeche.setText("");
    }

    // Metodo que instancia el pedido
    public void crearPedido() {

        try {
            Pedido pedido = new Pedido("R" + numeroDeRuta.getSelectedItem().toString(), String.valueOf(nombreProveedor.getText()), cantidadLeche.getText().toString(), String.valueOf(tipoDeEnvase.getText()));
            for (int i = 0; i < listaActualizada.size(); i++) {
                pedido.setIdProveedor(listaActualizada.get(i).getId());
            }
            lista.add(pedido);
            Toast.makeText(this, "Perido Guardado", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar el pedido", Toast.LENGTH_LONG).show();
        }


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

    // Sub clase que inicializa la base de datos de los proveedores via excel.
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

    public void leerArchivosSemanales() {
        this.leerLunes();
        this.leerMartes();
        this.leerMiercoles();
        this.leerJueves();
        this.leerViernes();
        this.leerSabado();
        this.leerDomingo();
    }

    public void leerLunes() {

        {
            File file = new File(this.getExternalFilesDir("Belen/InformesDiarios/"), "Informe_lunes.xls");
            FileInputStream inputStream = null;

            String datos = "";


            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido personaLunes = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains("lunes")) {

                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            personaLunes.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            personaLunes.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            personaLunes.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            personaLunes.setNumeroDeRuta(cell.toString());
                            Log.d("persona", personaLunes.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                personaLunes.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (personaLunes.getNumeroDeRuta() != null) {
                        pedidosLunes.add(personaLunes);
                    }
                }

                Log.d("pedidosLunes", pedidosLunes.toString());


            } catch (IOException e) {
                e.printStackTrace();
                String sinRegistros = "Sin registros";
                //  pedidosLunes.add(sinRegistros);
            }
        }
        Log.d("lunes", pedidosLunes.toString());


    }

    public void leerMartes() {

        {
            File file = new File(this.getExternalFilesDir("Belen/InformesDiarios/"), "Informe_martes.xls");
            FileInputStream inputStream = null;

            String datos = "";

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido personaMartes = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains("martes")) {

                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            personaMartes.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            personaMartes.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            personaMartes.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            personaMartes.setNumeroDeRuta(cell.toString());
                            Log.d("persona", personaMartes.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                personaMartes.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (personaMartes.getNumeroDeRuta() != null) {
                        pedidosMartes.add(personaMartes);
                    }
                }

                Log.d("pedidosMartes", pedidosMartes.toString());


            } catch (IOException e) {
                e.printStackTrace();
                String sinRegistros = "Sin registros";
                //   pedidosLunes.add(sinRegistros);
            }
        }
    }

    public void leerMiercoles() {

        {
            File file = new File(this.getExternalFilesDir("Belen/InformesDiarios/"), "Informe_miércoles.xls");
            FileInputStream inputStream = null;

            String datos = "";

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido personaMiercoles = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains("miércoles")) {
                            this.finDiaInforme = cell.toString();
                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            personaMiercoles.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            personaMiercoles.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            personaMiercoles.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            personaMiercoles.setNumeroDeRuta(cell.toString());
                            Log.d("persona", personaMiercoles.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                personaMiercoles.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (personaMiercoles.getNumeroDeRuta() != null) {
                        pedidosMiercoles.add(personaMiercoles);
                    }
                }

                Log.d("pedidosMiercoles", pedidosMiercoles.toString());


            } catch (IOException e) {
                e.printStackTrace();
                String sinRegistros = "Sin registros";

            }
        }
    }

    public void leerJueves() {

        {
            File file = new File(this.getExternalFilesDir("Belen/InformesDiarios/"), "Informe_jueves.xls");
            FileInputStream inputStream = null;

            String datos = "";

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido personaJueves = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains("jueves")) {
                            this.inicioDiaInforme = cell.toString();
                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            personaJueves.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            personaJueves.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            personaJueves.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            personaJueves.setNumeroDeRuta(cell.toString());
                            Log.d("persona", personaJueves.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                personaJueves.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (personaJueves.getNumeroDeRuta() != null) {
                        pedidosJueves.add(personaJueves);
                    }
                }

                Log.d("pedidosJueves", pedidosJueves.toString());


            } catch (IOException e) {
                e.printStackTrace();
                String sinRegistros = "Sin registros";
                //  pedidosLunes.add(null);
            }
        }
    }

    public void leerViernes() {

        {
            File file = new File(this.getExternalFilesDir("Belen/InformesDiarios/"), "Informe_viernes.xls");
            FileInputStream inputStream = null;

            String datos = "";

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido personaViernes = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains("viernes")) {

                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            personaViernes.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            personaViernes.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            personaViernes.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            personaViernes.setNumeroDeRuta(cell.toString());
                            Log.d("persona", personaViernes.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                personaViernes.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (personaViernes.getNumeroDeRuta() != null) {
                        pedidosViernes.add(personaViernes);
                    }
                }

                Log.d("pedidosviernes", pedidosViernes.toString());


            } catch (IOException e) {
                e.printStackTrace();
                String sinRegistros = "Sin registros";
                // pedidosLunes.add(sinRegistros);
            }
        }
    }

    public void leerSabado() {

        {
            File file = new File(this.getExternalFilesDir("Belen/InformesDiarios/"), "Informe_sábado.xls");
            FileInputStream inputStream = null;

            String datos = "";

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido personaSabado = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains("sabado")) {

                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            personaSabado.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            personaSabado.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            personaSabado.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            personaSabado.setNumeroDeRuta(cell.toString());
                            Log.d("persona", personaSabado.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                personaSabado.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (personaSabado.getNumeroDeRuta() != null) {
                        pedidosSabado.add(personaSabado);
                    }
                }

                Log.d("pedidossabado", pedidosSabado.toString());


            } catch (IOException e) {
                e.printStackTrace();
                String sinRegistros = "Sin registros";
                //   pedidosLunes.add(sinRegistros);
            }
        }
    }

    public void leerDomingo() {

        {
            File file = new File(this.getExternalFilesDir("Belen/InformesDiarios/"), "Informe_domingo.xls");
            FileInputStream inputStream = null;

            String datos = "";

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido personaDomingo = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains("domingo")) {

                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            personaDomingo.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            personaDomingo.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            personaDomingo.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            personaDomingo.setNumeroDeRuta(cell.toString());
                            Log.d("persona", personaDomingo.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                personaDomingo.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (personaDomingo.getNumeroDeRuta() != null) {
                        pedidosDomingo.add(personaDomingo);
                    }
                }

                Log.d("pedidosDomingo", pedidosDomingo.toString());


            } catch (IOException e) {
                e.printStackTrace();
                String sinRegistros = "Sin registros";
                // pedidosLunes.add(sinRegistros);
            }
        }
    }


    public void leerExcelSemanal() {
        ArrayList<String> ruta = new ArrayList<String>();
        ArrayList<String> envase = new ArrayList<String>();
        ArrayList<String> nombre = new ArrayList<String>();
        ArrayList<String> cantLeche = new ArrayList<String>();


        {
            File file = new File(this.getExternalFilesDir("Belen"), "Informe.xls");
            FileInputStream inputStream = null;

            String datos = "";

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


                        if (cell.toString().toLowerCase().equals("tipo de envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().contains(fechaUtil.getDiaDeLaSemana().toLowerCase() + '-' + fechaUtil.getDiaDelMes()) || cell.toString().toLowerCase().equals("numero de ruta")) {
                            String ignoreTittles = cell.toString();
                            ignoreTittles = null;
                        } else {
                            if (cell.toString().toLowerCase().equals("botellas") || cell.toString().toLowerCase().equals("litros")) {
                                envase.add(cell.toString());
                            } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                                nombre.add(cell.toString());
                            } else if (Pattern.matches("^[1-9]{1}$", cell.toString())) {
                                ruta.add(cell.toString());
                            } else if (Pattern.matches("^[0-9+]+$", cell.toString())) {
                                cantLeche.add(cell.toString());
                            }
                        }
                    }

                }
                for (int i = 0; i < ruta.size(); i++) {
                    Pedido pedido = new Pedido(ruta.get(i).toString(), nombre.get(i).toString(), cantLeche.get(i).toString(), envase.get(i).toString());
                    lista.add(pedido);
                }
                Log.d("Datos", datos);
                Log.d("Lectura", lista.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void instanciarInformeSemanal() {
        Log.d("ids", leaId.toString());

        for (int i = 0; i < leaId.size(); i++) {
            ResumenSemanal resumenSemanal = new ResumenSemanal();

            for (int j = 0; j < pedidosLunes.size(); j++) {
                if (leaId.get(i).toLowerCase().equals(pedidosLunes.get(j).getIdProveedor().toLowerCase())) {
                    if (resumenSemanal.getProveedor() == null){
                        resumenSemanal.setProveedor(new Proveedor(pedidosLunes.get(j).getIdProveedor(), pedidosLunes.get(j).getNombreProveedor(), pedidosLunes.get(j).getTipoDeEnvase()));
                    }
                    resumenSemanal.setLecheLunes(pedidosLunes.get(j).getCantidadLeche());
                }
            }
            for (int j = 0; j < pedidosMartes.size(); j++) {
                if (leaId.get(i).toLowerCase().equals(pedidosMartes.get(j).getIdProveedor().toLowerCase())) {
                    if (resumenSemanal.getProveedor() == null){
                        resumenSemanal.setProveedor(new Proveedor(pedidosMartes.get(j).getIdProveedor(),pedidosMartes.get(j).getNombreProveedor(),pedidosMartes.get(j).getTipoDeEnvase()));
                    }
                    resumenSemanal.setLecheMartes(pedidosMartes.get(j).getCantidadLeche());
                }
            }
            for (int j = 0; j < pedidosMiercoles.size(); j++) {
                if (leaId.get(i).toLowerCase().equals(pedidosMiercoles.get(j).getIdProveedor().toLowerCase())) {
                    if (resumenSemanal.getProveedor() == null){
                        resumenSemanal.setProveedor(new Proveedor(pedidosMiercoles.get(j).getIdProveedor(),pedidosMiercoles.get(j).getNombreProveedor(),pedidosMiercoles.get(j).getTipoDeEnvase()));
                    }
                    resumenSemanal.setLecheMiercoles(pedidosMiercoles.get(j).getCantidadLeche());

                }
            }
            for (int j = 0; j < pedidosJueves.size(); j++) {
                if (leaId.get(i).toLowerCase().equals(pedidosJueves.get(j).getIdProveedor().toLowerCase())) {
                    if (resumenSemanal.getProveedor() == null){
                        resumenSemanal.setProveedor(new Proveedor(pedidosJueves.get(j).getIdProveedor(),pedidosJueves.get(j).getNombreProveedor(),pedidosJueves.get(j).getTipoDeEnvase()));
                    }
                    resumenSemanal.setLecheJueves(pedidosJueves.get(j).getCantidadLeche());
                }
            }
            for (int j = 0; j < pedidosViernes.size(); j++) {
                if (leaId.get(i).toLowerCase().equals(pedidosViernes.get(j).getIdProveedor().toLowerCase())) {
                    if (resumenSemanal.getProveedor() == null){
                        resumenSemanal.setProveedor(new Proveedor(pedidosViernes.get(j).getIdProveedor(),pedidosViernes.get(j).getNombreProveedor(),pedidosViernes.get(j).getTipoDeEnvase()));
                    }
                    resumenSemanal.setLecheViernes(pedidosViernes.get(j).getCantidadLeche());
                }
            }
            for (int j = 0; j < pedidosSabado.size(); j++) {
                if (leaId.get(i).toLowerCase().equals(pedidosSabado.get(j).getIdProveedor().toLowerCase())) {
                   if (resumenSemanal.getProveedor() == null){
                       resumenSemanal.setProveedor(new Proveedor(pedidosSabado.get(j).getIdProveedor(),pedidosSabado.get(j).getNombreProveedor(),pedidosSabado.get(j).getTipoDeEnvase()));
                   }
                    resumenSemanal.setLecheSabado(pedidosSabado.get(j).getCantidadLeche());
                }
            }
            for (int j = 0; j < pedidosDomingo.size(); j++) {
                if (leaId.get(i).toLowerCase().equals(pedidosDomingo.get(j).getIdProveedor().toLowerCase())) {
                    if (resumenSemanal.getProveedor() == null){
                        resumenSemanal.setProveedor(new Proveedor(pedidosDomingo.get(j).getIdProveedor(),pedidosDomingo.get(j).getNombreProveedor(),pedidosDomingo.get(j).getTipoDeEnvase()));
                    }
                    resumenSemanal.setLecheDomingo(pedidosDomingo.get(j).getCantidadLeche());
                }
            }
            if (resumenSemanal.getLecheLunes() != null || resumenSemanal.getLecheMartes() != null || resumenSemanal.getLecheMiercoles() != null || resumenSemanal.getLecheJueves() != null || resumenSemanal.getLecheViernes() != null || resumenSemanal.getLecheSabado() != null || resumenSemanal.getLecheDomingo() != null) {
                resumenSemanalList.add(resumenSemanal);
            }
        }


        Log.d("tags", resumenSemanalList.toString());
    }


    public void leerExcel() {
        ArrayList<String> ruta = new ArrayList<String>();
        ArrayList<String> envase = new ArrayList<String>();
        ArrayList<String> nombre = new ArrayList<String>();
        ArrayList<String> cantLeche = new ArrayList<String>();

        {
            File file = new File(this.getExternalFilesDir("Belen"), "Informe_" + fechaUtil.getDiaDeLaSemana() + ".xls");
            FileInputStream inputStream = null;

            String datos = "";

            try {
                inputStream = new FileInputStream(file);
                POIFSFileSystem fileSystem = new POIFSFileSystem(inputStream);

                HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);

                HSSFSheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Pedido persona = new Pedido();

                    HSSFRow row = (HSSFRow) rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        HSSFCell cell = (HSSFCell) cellIterator.next();

                        if (cell.toString().toLowerCase().equals("codigo") || cell.toString().toLowerCase().equals("envase") || cell.toString().toLowerCase().equals("nombre del proveedor") || cell.toString().toLowerCase().equals("numero de ruta")) {

                        } else if (cell.toString().toLowerCase().contains(fechaUtil.getDiaDeLaSemana().toLowerCase())) {

                        } else if (Pattern.matches("^[c]{1}[0-9+]+$", cell.toString())) {
                            persona.setIdProveedor(cell.toString());
                        } else if (cell.toString().toLowerCase().contains("botellas") || cell.toString().toLowerCase().contains("litros")) {
                            persona.setTipoDeEnvase(cell.toString());
                        } else if (Pattern.matches("^[^0-9+]+$", cell.toString())) {
                            persona.setNombreProveedor(cell.toString());
                        } else if (Pattern.matches("^[R]{1}[0-9]{1}$", cell.toString())) {
                            persona.setNumeroDeRuta(cell.toString());
                            Log.d("persona", persona.toString());
                        } else {
                            try {
                                double leche = Double.parseDouble(cell.toString());
                                int cantidad = (int) leche;
                                String cantidadLeche = String.valueOf(cantidad);
                                persona.setCantidadLeche(cantidadLeche);

                            } catch (Exception e) {

                            }
                        }
                    }
                    if (persona.getNumeroDeRuta() != null) {
                        lista.add(persona);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void crearExcelDiario() {

        Workbook wb = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);

        Sheet sheet = null;
        sheet = wb.createSheet("Hoja 1");

        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Codigo");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Envase");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Nombre del Proveedor");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        fechaUtil.getDiaDeLaSemana();
        cell.setCellValue(fechaUtil.getDiaDeLaSemana() + '-' + fechaUtil.getDiaDelMes());
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Numero de ruta");
        cell.setCellStyle(cellStyle);


        for (int i = 2; i < 5; i++) {
            sheet.setColumnWidth(i, 4 * 1500
            );
        }

        for (int i = 0; i < lista.size(); i++) {
            Pedido persona = lista.get(i);
            Row fila = sheet.createRow(i + 1);

            cell = fila.createCell(0);
            cell.setCellValue(persona.getIdProveedor());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(1);
            cell.setCellValue(persona.getTipoDeEnvase());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(2);
            cell.setCellValue(persona.getNombreProveedor());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(3);
            cell.setCellValue(persona.getCantidadLeche());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(4);
            cell.setCellValue(persona.getNumeroDeRuta());
            cell.setCellStyle(cellStyle);


        }

        //this.formatoDeFechaExcel();
        // nombreArchivo = "Informe"  + fecha + ".xls";
        nombreArchivo = "Informe_" + fechaUtil.getDiaDeLaSemana() + ".xls";
        File file = new File(getExternalFilesDir("Belen"), nombreArchivo);
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


    public void crearExcelSemanal() {
        this.leerArchivosSemanales();
        Workbook wb = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);

        Sheet sheet = null;
        sheet = wb.createSheet("Hoja 1");

        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Tipo de envase");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Nombre del Proveedor");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Jueves");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Viernes");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(4);
        cell.setCellValue("Sabado ");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(5);
        cell.setCellValue("Domingo");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(6);
        cell.setCellValue("Lunes");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(7);
        cell.setCellValue("Martes");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(8);
        cell.setCellValue("Miércoles");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(9);
        cell.setCellValue("Total");
        cell.setCellStyle(cellStyle);

        cell = row.createCell(10);
        cell.setCellValue("fecha del informe");
        cell.setCellStyle(cellStyle);


        for (int i = 0; i < 9; i++) {
            sheet.setColumnWidth(i, 4 * 1000
            );
        }

        for (int i = 0; i <resumenSemanalList.size(); i++) {
            Proveedor p = (Proveedor)resumenSemanalList.get(i).getProveedor();
            Log.d("a ver ", p.getNombre());
            Row fila = sheet.createRow(i + 1);

            cell = fila.createCell(0);
            cell.setCellValue(p.getEnvasado());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(1);
            cell.setCellValue(p.getNombre());
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(2);
            if (resumenSemanalList.get(i).getLecheJueves() == null) {
                resumenSemanalList.get(i).setLecheJueves("0");
                cell.setCellValue("0");
            } else {
                cell.setCellValue(String.valueOf(resumenSemanalList.get(i).getLecheJueves()));
            }
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(3);
            if (resumenSemanalList.get(i).getLecheViernes() == null) {
                resumenSemanalList.get(i).setLecheViernes("0");
                cell.setCellValue("0");
            } else {
                cell.setCellValue(String.valueOf(resumenSemanalList.get(i).getLecheViernes()));
            }
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(4);
            if (resumenSemanalList.get(i).getLecheSabado() == null) {
                resumenSemanalList.get(i).setLecheSabado("0");
                cell.setCellValue("0");
            } else {
                cell.setCellValue(String.valueOf(resumenSemanalList.get(i).getLecheSabado()));
            }
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(5);
            if (resumenSemanalList.get(i).getLecheDomingo() == null) {
                resumenSemanalList.get(i).setLecheDomingo("0");
                cell.setCellValue("0");
            } else {
                cell.setCellValue(String.valueOf(resumenSemanalList.get(i).getLecheDomingo()));
            }
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(6);
            if (resumenSemanalList.get(i).getLecheLunes() == null) {
                resumenSemanalList.get(i).setLecheLunes("0");
                cell.setCellValue("0");
            } else {
                cell.setCellValue(String.valueOf(resumenSemanalList.get(i).getLecheLunes()));
            }
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(7);
            if (resumenSemanalList.get(i).getLecheMartes() == null) {
                resumenSemanalList.get(i).setLecheMartes("0");
                cell.setCellValue("0");
            } else {
                cell.setCellValue(String.valueOf(resumenSemanalList.get(i).getLecheMartes()));
            }
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(8);
            if (resumenSemanalList.get(i).getLecheMiercoles() == null) {
                resumenSemanalList.get(i).setLecheMiercoles("0");
                cell.setCellValue("0");
            } else {
                cell.setCellValue(String.valueOf(resumenSemanalList.get(i).getLecheMiercoles()));
            }
            cell.setCellStyle(cellStyle);

            cell = fila.createCell(9);
            cell.setCellValue(
                    Integer.parseInt(resumenSemanalList.get(i).getLecheLunes())+
                    Integer.parseInt(resumenSemanalList.get(i).getLecheMartes())+
                    Integer.parseInt(resumenSemanalList.get(i).getLecheMiercoles())+
                    Integer.parseInt(resumenSemanalList.get(i).getLecheJueves())+
                    Integer.parseInt(resumenSemanalList.get(i).getLecheViernes())+
                    Integer.parseInt(resumenSemanalList.get(i).getLecheSabado())+
                    Integer.parseInt(resumenSemanalList.get(i).getLecheDomingo()));
            cell.setCellStyle(cellStyle);


            cell = fila.createCell(10);
            cell.setCellValue(String.valueOf("" + this.inicioDiaInforme + " al " + this.finDiaInforme + "De" + fechaUtil.getMes() + "Del" + fechaUtil.getAño()));
            cell.setCellStyle(cellStyle);
        }

        //this.formatoDeFechaExcel();
        // nombreArchivo = "Informe"  + fecha + ".xls";
        nombreArchivo = "Informe.xls";
        File file = new File(getExternalFilesDir("Belen"), nombreArchivo);
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


