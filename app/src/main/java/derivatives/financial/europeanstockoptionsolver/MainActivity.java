package derivatives.financial.europeanstockoptionsolver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import mathematical.solver.*;
import java.util.HashMap;


import java.util.Calendar;


public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText spotLevel;
    EditText strike;
    EditText rate;
    EditText dividend;
    EditText maturity;
    EditText volatility;
    EditText price;
    AlertDialog Error;
    Spinner european_type_spinner;
    String selected_type;
    HashMap<String,Double> parameters;
    String independentParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get a Tracker (should auto-report)
        Tracker t = ((AnalyticsHelper) getApplication()).getTracker(AnalyticsHelper.TrackerName.APP_TRACKER);
        t.setScreenName("Pantalla Principal");

        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());


        // Buscar AdView como recurso y cargar una solicitud.
        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setFocusableInTouchMode(true);
        adView.requestFocus();

        spotLevel = (EditText) findViewById(R.id.spot_level);
        strike = (EditText) findViewById(R.id.strike);
        rate = (EditText) findViewById(R.id.rate);
        dividend = (EditText) findViewById(R.id.dividend);
        maturity = (EditText) findViewById(R.id.maturity);
        volatility = (EditText) findViewById(R.id.volatility);
        price = (EditText) findViewById(R.id.price);
        european_type_spinner = (Spinner) findViewById(R.id.type);


        ArrayAdapter<CharSequence> european_type_adapter = ArrayAdapter.createFromResource(this, R.array.european_types,
                android.R.layout.simple_spinner_item);
        european_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        european_type_spinner.setAdapter(european_type_adapter);

        View __solve = findViewById(R.id.solveButton);
        __solve.setOnClickListener(this);
        //__price.setFocusableInTouchMode(true);
        //__price.requestFocus();

        european_type_spinner.setOnItemSelectedListener(this);

        AlertDialog.Builder builderOk = new AlertDialog.Builder(this);
        builderOk.setMessage("Data Type Error")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        Error = builderOk.create();

        parameters = new HashMap<String,Double>();
        independentParameter = new String("VOLATILITY");
    }

    private Boolean validate(String _spotLevel, String _strike, String _rate, String _dividend, String _maturity,
                             String _volatility, String _type, String _price){

        int fieldNumber = 0;
        String textError;

        try {

            int blanks_number = 0;
            for (int i=1; i<=7; i++){
                switch (i){
                    case 1:
                        if (_spotLevel.equals("")) blanks_number++;
                        break;
                    case 2:
                        if (_strike.equals("")) blanks_number++;
                        break;
                    case 3:
                        if (_rate.equals("")) blanks_number++;
                        break;
                    case 4:
                        if (_dividend.equals("")) blanks_number++;
                        break;
                    case 5:
                        if (_maturity.equals("")) blanks_number++;
                        break;
                    case 6:
                        if (_volatility.equals("")) blanks_number++;
                        break;
                    case 7:
                        if (_price.equals("")) blanks_number++;
                        break;
                    default:
                        break;
                }
            }

            if (blanks_number > 1) {
                textError = "Only one parameter could be empty";
                Error.setMessage(textError);
                Error.show();
                return true;
            }

            fieldNumber = 1;
            if (!_spotLevel.equals("")){
                double spotLevel = Double.parseDouble(_spotLevel);}
            fieldNumber = 2;
            if (!_strike.equals("")){
                double strike = Double.parseDouble(_strike);}
            fieldNumber = 3;
            if (!_rate.equals("")){
                double rate = Double.parseDouble(_rate);}
            fieldNumber = 4;
            if (!_dividend.equals("")){
                double dividend = Double.parseDouble(_dividend);}
            fieldNumber = 5;
            if (!_maturity.equals("")){
                double maturity = Double.parseDouble(_maturity);}
            fieldNumber = 6;
            if (!_volatility.equals("")){
                double volatility = Double.parseDouble(_volatility);}
            fieldNumber = 7;
            if (!_price.equals("")){
                double price = Double.parseDouble(_price);}
        } catch(Exception e){
            switch (fieldNumber) {
                case 1:
                    textError = "Spot Level must be a real number";
                    break;
                case 2:
                    textError = "Strike be a real number";
                    break;
                case 3:
                    textError = "Rate must be a real number";
                    break;
                case 4:
                    textError = "Dividend must be an integer number";
                    break;
                case 5:
                    textError = "Maturity must be an integer number";
                    break;
                case 6:
                    textError = "Volatility must be an integer number";
                    break;
                default:
                    textError = "Fatal error";
                    break;
            }
            Error.setMessage(textError);
            Error.show();
            return true;
        }

        if (!_type.equals("Standard Put") && !_type.equals("Standard Call")
                && !_type.equals("Binary Put") && !_type.equals("Binary Call")) {
            textError = "The type is not supported";
            Error.setMessage(textError);
            Error.show();
            return true;
        }
        return false;
    }

    void parseData(){

        if (!this.spotLevel.getText().toString().equals("")){
            this.parameters.put("SPOT_LEVEL", Double.parseDouble(this.spotLevel.getText().toString()));
        }
        else {
            this.parameters.put("SPOT_LEVEL", 1.0);
            this.independentParameter = "SPOT_LEVEL";
        }

        if (!this.strike.getText().toString().equals("")){
            this.parameters.put("STRIKE", Double.parseDouble(this.strike.getText().toString()));
        }
        else {
            this.parameters.put("STRIKE", 1.0);
            this.independentParameter = "STRIKE";
        }

        if (!this.rate.getText().toString().equals("")){
            this.parameters.put("RATE", Double.parseDouble(this.rate.getText().toString()));
        }
        else {
            this.parameters.put("RATE", 0.05);
            this.independentParameter = "RATE";
        }

        if (!this.dividend.getText().toString().equals("")){
            this.parameters.put("DIVIDEND", Double.parseDouble(this.dividend.getText().toString()));
        }
        else {
            this.parameters.put("DIVIDEND", 0.05);
            this.independentParameter = "DIVIDEND";
        }

        if (!this.maturity.getText().toString().equals("")){
            this.parameters.put("MATURITY", Double.parseDouble(this.maturity.getText().toString()));
        }
        else {
            this.parameters.put("MATURITY", 1.0);
            this.independentParameter = "MATURITY";
        }

        if (!this.volatility.getText().toString().equals("")){
            this.parameters.put("VOLATILITY", Double.parseDouble(this.volatility.getText().toString()));
        }
        else {
            this.parameters.put("VOLATILITY", 0.30);
            this.independentParameter = "VOLATILITY";
        }

        if (!this.price.getText().toString().equals("")){
            this.parameters.put("PRICE", Double.parseDouble(this.price.getText().toString()));
        }
        else {
            this.parameters.put("PRICE", 0.015);
            this.independentParameter = "PRICE";
        }
    }

    public void parseOutput(Double result) {

        if (this.independentParameter.equals("SPOT_LEVEL")) {
            this.spotLevel.setText(result.toString());
            return;
        }
        if (this.independentParameter.equals("STRIKE")) {
            this.strike.setText(result.toString());
            return;
        }
        if (this.independentParameter.equals("VOLATILITY")) {
            this.volatility.setText(result.toString());
            return;
        }
        if (this.independentParameter.equals("RATE")) {
            this.rate.setText(result.toString());
            return;
        }
        if (this.independentParameter.equals("DIVIDEND")) {
            this.dividend.setText(result.toString());
            return;
        }
        if (this.independentParameter.equals("MATURITY")) {
            this.maturity.setText(result.toString());
            return;
        }
        if (this.independentParameter.equals("PRICE")) {
            this.price.setText(result.toString());
            return;
        }

    }

    @Override
    public void onClick(View view) {
        if (!this.validate(this.spotLevel.getText().toString(), this.strike.getText().toString(),
                this.rate.getText().toString(), this.dividend.getText().toString(),
                this.maturity.getText().toString(), this.volatility.getText().toString(),
                this.selected_type, this.price.getText().toString())) {

            this.parseData();

            if (view.getId()==R.id.solveButton) {

                Function myFunction;
                if (this.selected_type.equals("Standard Call"))
                    myFunction = new EuropeanCallSolverFunction();
                else
                    myFunction = new EuropeanPutSolverFunction();

                Solver mySolver = new Solver(myFunction, new NewtonAlgorithm(), this.independentParameter, 0.0,
                                             this.parameters);

                parseOutput(mySolver.solve());
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        this.selected_type = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView parent){

    }

    @Override
    public void onStart(){
        super.onStart();
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        super.onStop();
    }
}
