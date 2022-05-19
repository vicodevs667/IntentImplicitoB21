package com.example.intentimplicitob2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Atributos visuales

    private EditText etTelefono;
    private ImageButton btnLlamar, btnCamara;
    private ImageView ivImagen;

    //Atributos de clase - primitivos
    private String numeroTelefonico;

    //Atributos constantes para permisos
    private final int CALL_CODE = 100;
    private final int CAMERA_CODE = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializarVistas();
        btnLlamar.setOnClickListener(view -> {
            activarLlamada();
        });
        btnCamara.setOnClickListener(view -> {
            activarCamara();
        });
    }

    private void activarCamara() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
        }

    }

    private void activarLlamada() {
        numeroTelefonico = etTelefono.getText().toString();
        //Validar que el usuario un número
        if(!numeroTelefonico.isEmpty()){
            //SDK_INT = 24 ...... VERSION_CODES.M = 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                //versiones nuevas
                //Para versiones nuevas tienen que acceder al
                //tratamiento de permisos configurado en las librerias
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_CODE);
            }
            else {
                //versiones antiguas
                configuracionVersionesAntiguas();
            }
        }
    }

    private void configuracionVersionesAntiguas() {
        //Intent implicito para que un componente
        //Realize una acción, el sistema busca el mejor para hacerlo
        Intent intentCall = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:"+ numeroTelefonico));
        if (revisarPermisos(Manifest.permission.CALL_PHONE)){
            startActivity(intentCall);
        }
        else{
            Toast.makeText(this,"Permission Denied", Toast.LENGTH_LONG).show();
        }
    }

    private void inicializarVistas() {
        etTelefono = findViewById(R.id.etTelefono);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnCamara = findViewById(R.id.btnCamara);
        ivImagen = findViewById(R.id.ivImagen);
    }

    private boolean revisarPermisos(String permiso){
        int valorPermiso = this.checkCallingOrSelfPermission(permiso);
        return valorPermiso == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_CODE:
                String permiso = permissions[0];
                int valorPermisoOtorgado = grantResults[0];
                if (permiso.equals(Manifest.permission.CALL_PHONE)) {
                    //Comprobar si el permiso ha sido otorgado o denegado
                    if (valorPermisoOtorgado == PackageManager.PERMISSION_GRANTED) {
                            Intent intentLlamada = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numeroTelefonico));
                            startActivity(intentLlamada);

                    } else {
                        Toast.makeText(this, "permiso denegado", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case CAMERA_CODE:
                int valor = grantResults[0];
                if (valor == PackageManager.PERMISSION_GRANTED) {
                    Intent intentCamara = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intentCamara, CAMERA_CODE);
                }

                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CAMERA_CODE:
                Bitmap foto = (Bitmap) data.getExtras().get("data");
                ivImagen.setImageBitmap(foto);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}