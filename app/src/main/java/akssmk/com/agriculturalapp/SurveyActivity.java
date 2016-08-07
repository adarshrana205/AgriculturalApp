package akssmk.com.agriculturalapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyActivity extends AppCompatActivity{

    public static final String STATE ="state" ;
    public static final String DISTRICT = "district";
    public static final String CROP ="crop" ;
    private Spinner spinner1, spinner2, spinner3;
    private Button btnSubmit;
    private static final String URL_DISTRICT = "http://kharita.freevar.com/district.php";
    private static final String URL_CROP = "http://kharita.freevar.com/crop.php";
    ArrayAdapter<String> arrayAdapter,arrayAdapter1,arrayAdapter2;
    ArrayList<String> list11,list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_survey);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        spinner1= (Spinner) findViewById(R.id.spinner1);
        spinner2= (Spinner) findViewById(R.id.spinner2);
        spinner3= (Spinner) findViewById(R.id.spinner3);
        btnSubmit= (Button) findViewById(R.id.btnSubmit);
        list11=new ArrayList<>();
         list2=new ArrayList<>();

         arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.States));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(arrayAdapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String k = arrayAdapter.getItem(parent.getSelectedItemPosition());
                if (k != null)
                    sendRequest(URL_DISTRICT, "State", k);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         arrayAdapter1=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list11);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(arrayAdapter1);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String k = arrayAdapter1.getItem(parent.getSelectedItemPosition());
                if (k != null) {
                    Log.d("Key",k);
                    sendRequest(URL_CROP, "District", k);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         arrayAdapter2=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list2);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(arrayAdapter2);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String State=arrayAdapter.getItem(spinner1.getSelectedItemPosition());
                String District=arrayAdapter1.getItem(spinner2.getSelectedItemPosition());
                String Crop=arrayAdapter2.getItem(spinner3.getSelectedItemPosition());

                if(!State.isEmpty()&&!District.isEmpty()&&!Crop.isEmpty()){

                    Intent intent=new Intent(SurveyActivity.this,SurveyInformation.class);
                    intent.putExtra(STATE,State);
                    intent.putExtra(DISTRICT,District);
                    intent.putExtra(CROP,Crop);
                    startActivity(intent);
                }

            }
        });

    }

    private void sendRequest(final String url, final String key, final String value){

      StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
              if(url==URL_DISTRICT){
                  arrayAdapter1.clear();
                  list11=parseJson(response,"result","District");
                  arrayAdapter1.addAll(list11);
                  arrayAdapter1.notifyDataSetChanged();
              }
              else if (url==URL_CROP){
                  arrayAdapter2.clear();
                  list2=parseJson(response,"result","Crop");
                  arrayAdapter2.addAll(list2);
                  arrayAdapter2.notifyDataSetChanged();
              }
             Log.d("data",response);
          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

          }
      }){
          @Override
          protected Map<String, String> getParams() throws AuthFailureError {
              Map<String,String> map=new HashMap<>();
              map.put(key,value);
              return map;
          }
      };

        MySingleton.getInstance(MyApplication.getAppContext()).addToRequestQueue(stringRequest);
    }

    private ArrayList<String> parseJson(String key,String array_name,String object_name){
        ArrayList<String> list=new ArrayList<>();
        try {
            JSONObject j=new JSONObject(key);
            JSONArray a=j.getJSONArray(array_name);
            for(int i=0;i<a.length();i++){
                JSONObject w=a.getJSONObject(i);
                list.add(w.getString(object_name));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}