package br.ufrn.calculataxa;

import android.content.DialogInterface;
import android.opengl.Visibility;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ArithmeticException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private Spinner cb_type, cb_tempo;
    private EditText et_taxa;
    private Button bt_transform;
    private String juros, tempo;
    private double taxa, dia, mes, bi, tri, sem, ano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mudarActionBar();

        cb_type = (Spinner) findViewById(R.id.cb_type);
        cb_tempo = (Spinner) findViewById(R.id.cb_tempo);
        et_taxa = (EditText) findViewById(R.id.et_taxa);
        bt_transform = (Button) findViewById(R.id.bt_transform);

        final ArrayAdapter adapterJuros = ArrayAdapter.createFromResource(this, R.array.cb_type, R.layout.custom_spinner);
        adapterJuros.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cb_type.setAdapter(adapterJuros);

        ArrayAdapter adapterTempo = ArrayAdapter.createFromResource(this, R.array.cb_tempo, R.layout.custom_spinner);
        adapterJuros.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cb_tempo.setAdapter(adapterTempo);

        bt_transform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taxa = pegarTaxa(); juros = pegarJuros(); tempo = pegarTempo();

                if (juros.equals("Juros Simples")){
                    taxa = transformaTaxaParaDiaJS(taxa, tempo);
                    dia = taxa; mes = formulaJS(taxa, "*", 30); bi = formulaJS(taxa, "*", 60); tri = formulaJS(taxa, "*", 90); sem = formulaJS(taxa, "*", 180); ano = formulaJS(taxa, "*", 360);

                    dialogJS();
                }else{
                    taxa = transformaTaxaParaDiaJC(taxa, tempo);
                    dia = taxa; mes =  formulaJC(taxa, "pot", 30); bi = formulaJC(taxa, "pot", 60); tri = formulaJC(taxa, "pot", 90); sem = formulaJC(taxa, "pot", 180); ano = formulaJC(taxa, "pot", 360);

                    dialogJC();
                }
            }
        });
    }

    public void mudarActionBar(){
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle(R.string.app_name);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.primary_dark));
    }

    public double pegarTaxa(){
        double t;
        try {
            t = Double.parseDouble(et_taxa.getText().toString());
            return t;
        }catch (Exception e) {
            mensagemErro();
        }
        return 0;
    }

    public String pegarJuros(){
        String j;
        try{
            j = cb_type.getSelectedItem().toString();
            return j;
        }catch(Exception e){
            mensagemErro();
        }
        return null;
    }

    public String pegarTempo(){
        String tempo;
        try{
            tempo = cb_tempo.getSelectedItem().toString();
            return tempo;
        }catch (Exception e){
            mensagemErro();
        }
        return null;
    }

    public void mensagemErro(){
        Toast.makeText(getApplicationContext(), "Preencha os campos corretamente.", Toast.LENGTH_SHORT).show();
    }

    // Metodos que contém as formulas para transformar Juros Simples
    public double transformaTaxaParaDiaJS(double taxa, String tempo){
        double t;
        if(tempo.equals("a. m.")){
            t = formulaJS(taxa, "/", 30);
            return t;
        }else if(tempo.equals("a. b.")){
            t = formulaJS(taxa, "/",60);
            return t;
        }else if(tempo.equals("a. t.")){
            t =  formulaJS(taxa, "/",90);
            return t;
        }else if(tempo.equals("a. s.")){
            t = formulaJS(taxa, "/",180);
            return t;
        }else if(tempo.equals("a. a.")){
            t = formulaJS(taxa, "/",360);
            return t;
        }
        return taxa;
    }

    public double formulaJS(double taxa, String op, int num){
        if(op.equals("/")){
            taxa = taxa/num;
            return taxa;
        }else{
            taxa = taxa*num;
            return taxa;
        }
    }

    //Metodos que contém as formulas para transformar as taxas em Juros Compostos
    public double transformaTaxaParaDiaJC(double taxa, String tempo){
        double t;
        if(tempo.equals("a. m.")){
            t = formulaJC(taxa, "raiz", 30);
            return t;
        }else if(tempo.equals("a. b.")){
            t = formulaJC(taxa, "raiz", 60);
            return t;
        }else if(tempo.equals("a. t.")){
            t = formulaJC(taxa, "raiz", 90);
            return t;
        }else if(tempo.equals("a. s.")){
            t = formulaJC(taxa, "raiz", 180);
            return t;
        }else if(tempo.equals("a. a.")){
            t = formulaJC(taxa, "raiz", 360);
            return t;
        }
        return taxa;
    }

    public double formulaJC(double taxa, String op, double num){
        if(op.equals("raiz")){
            taxa = (Math.pow(1+taxa/100, 1.0/num) - 1) * 100;
            return taxa;
        }else{
            taxa = (Math.pow(1+taxa/100, num) - 1) * 100;
            return taxa;
        }
    }

    //Alert
    public void dialogJS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DecimalFormat df = new DecimalFormat("###,##0.00");

        builder.setTitle(R.string.ad_taxas);
        builder.setMessage(df.format(dia) + " % a. d.\n" + df.format(mes) + " % a. m.\n" + df.format(bi) + " % a. b.\n" + df.format(tri) + " % a. t.\n" + df.format(sem) + " % a. s.\n" + df.format(ano) + " % a. a.\n");

        AlertDialog dialog = builder.create();

        builder.setNeutralButton("Voltar", null);

        builder.show();
    }

    public void dialogJC(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DecimalFormat df = new DecimalFormat("###,####0.0000");

        builder.setTitle(R.string.ad_taxas);
        builder.setMessage(df.format(dia) + " % a. d.\n" + df.format(mes) + " % a. m.\n" + df.format(bi) + " % a. b.\n" + df.format(tri) + " % a. t.\n" + df.format(sem) + " % a. s.\n" + df.format(ano) + " % a. a.\n");

        AlertDialog dialog = builder.create();

        builder.setNeutralButton("Voltar", null);

        builder.show();
    }
}
