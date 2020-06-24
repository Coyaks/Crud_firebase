package com.example.crud_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crud_firebase.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //OJO: listPerson
    private List<Persona> listPerson=new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;

    EditText etNombre, etApellido, etCorreo, password;
    ListView lvPersonas;

    //OJO: CONEXION CON FIREBASE | ANDROID STUDIO
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Persona personaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNombre=findViewById(R.id.txtNombre);
        etApellido=findViewById(R.id.txtApellidos);
        etCorreo=findViewById(R.id.txtCorreo);
        password=findViewById(R.id.txtPass);
        lvPersonas=findViewById(R.id.lv_datosPersonas);

        //inicializar firebase
        inicializarFirebase();
        listarDatos();

        lvPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada=(Persona)parent.getItemAtPosition(position);
                etNombre.setText(personaSeleccionada.getNombre());
                etApellido.setText(personaSeleccionada.getApellidos());
                etCorreo.setText(personaSeleccionada.getCorreo());
                password.setText(personaSeleccionada.getPassword());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear(); //persistencia de datos
                for(DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Persona p=objSnaptshot.getValue(Persona.class);
                    listPerson.add(p);

                    arrayAdapterPersona=new ArrayAdapter<Persona>(MainActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    lvPersonas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference=firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String nombre=etNombre.getText().toString();
        String apellidos=etApellido.getText().toString();
        String correo=etCorreo.getText().toString();
        String pass=password.getText().toString();
        switch (item.getItemId()){
            case R.id.icon_add:
                //validacion de campos
                if(nombre.equals("") || apellidos.equals("") || correo.equals("") || pass.equals("")){
                    validacion();
                }else{
                    Persona p=new Persona();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellidos(apellidos);
                    p.setCorreo(correo);
                    p.setPassword(pass);

                    databaseReference.child("Persona").child(p.getId()).setValue(p);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                    break;
                }

            case R.id.icon_save:
                Persona p=new Persona();
                p.setId(personaSeleccionada.getId());
                p.setNombre(etNombre.getText().toString().trim());
                p.setApellidos(etApellido.getText().toString().trim());
                p.setCorreo(etCorreo.getText().toString().trim());
                p.setPassword(password.getText().toString().trim());
                databaseReference.child("Persona").child(p.getId()).setValue(p);

                Toast.makeText(this, "Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            case R.id.icon_delete:
                Persona p2=new Persona();
                p2.setId(personaSeleccionada.getId());
                databaseReference.child("Persona").child(p2.getId()).removeValue();
                Toast.makeText(this, "Eliminado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            default:
                break;
        }
        return true;
    }

    private void limpiarCajas() {
        etNombre.setText("");
        etApellido.setText("");
        etCorreo.setText("");
        password.setText("");
    }

    private void validacion(){
        String nombre=etNombre.getText().toString();
        String apellidos=etApellido.getText().toString();
        String correo=etCorreo.getText().toString();
        String pass=password.getText().toString();
        if(nombre.equals("")){
            etNombre.setError("Required");
        }else if(apellidos.equals("")){
            etApellido.setError("Required");
        }else if(correo.equals("")){
            etCorreo.setError("Required");
        }else if(pass.equals("")){
            password.setError("Required");
        }
    }
}
