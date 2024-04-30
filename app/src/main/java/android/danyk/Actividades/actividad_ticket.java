package android.danyk.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.danyk.R;
import android.danyk.Utilidades.ImagePreviewItem;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.danyk.DAO.TicketDAO;
import android.danyk.DAO.UserDAO;


public class actividad_ticket extends AppCompatActivity {

    private TextView estado;
    private TextInputEditText titulo, descripcion;
    private RadioGroup prioridad;
    private static final int PICK_IMAGES_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private final List<Uri> imageUris = new ArrayList<>();
    private LinearLayout linearLayoutVistaPrevia;
    private StorageReference storageReference;
    private static final int SOLICITUD_CAMARA = 100;
    File photoFile;
    private boolean finalizado = false;
    private ScrollView scrollView;
    private String creadoPor;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_ticket);


        linearLayoutVistaPrevia = findViewById(R.id.linear_layout_vista_previa);
        TextView mostrarNombre = findViewById(R.id.creacion_ticket_usuario);
        estado = findViewById(R.id.creacion_ticket_estado);
        Button botonEnvio = findViewById(R.id.creacion_ticket_enviar);
        Button botonCancelar = findViewById(R.id.creacion_ticket_cancelar);
        titulo = findViewById(R.id.creacion_ticket_titulo);
        descripcion = findViewById(R.id.creacion_ticket_descripcion);
        prioridad = findViewById(R.id.creacion_ticket_prioridad);
        storageReference = FirebaseStorage.getInstance().getReference();
        scrollView = findViewById(R.id.scrollView);

        Button botonAdjuntarImagen = findViewById(R.id.boton_adjuntarImagen);
        botonAdjuntarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSeleccion();
            }
        });
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(actividad_ticket.this, actividad_menu.class);
                startActivity(intent);
            }
        });
        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizado = true;
                subirImagenes();
            }
        });

        UserDAO userDAO = new UserDAO();
        mostrarNombre.setText(userDAO.getUserNombre());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                Uri selectedImageUri = data.getData();
                imageUris.add(selectedImageUri);
                mostrarVistaPrevia();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFile != null) {
                Uri photoUri = Uri.fromFile(photoFile);
                imageUris.add(photoUri);
                mostrarVistaPrevia();
            }
        }
    }

    private void mostrarVistaPrevia() {
        linearLayoutVistaPrevia.removeAllViews();
        if (imageUris.isEmpty()) {
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));        } else {
            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px));

            for (int i = 0; i < imageUris.size(); i++) {
                View previewView = LayoutInflater.from(this).inflate(R.layout.image_preview_item, null);
                String imageName = getFileNameFromUri(imageUris.get(i));
                TextView imageNameTextView = previewView.findViewById(R.id.image_name);
                imageNameTextView.setText(imageName);
                ImageView eliminarFoto = previewView.findViewById(R.id.eliminarFoto);
                int finalI = i;
                eliminarFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageUris.remove(finalI);
                        mostrarVistaPrevia();
                    }
                });

                linearLayoutVistaPrevia.addView(previewView);
            }
        }
    }


    @SuppressLint("Range")
    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void subirImagenes() {
        List<String> imageUrls = new ArrayList<>();
        if (imageUris.isEmpty()) {
            insertarDatos(imageUrls);
        } else {
            for (int i = 0; i < imageUris.size(); i++) {
                Uri imagenUri = imageUris.get(i);
                String imagenNombre = "imagen_" + System.currentTimeMillis() + ".jpg";
                StorageReference imageRef = storageReference.child("Imagenes_Tickets").child(imagenNombre);

                try {
                    InputStream inputStream = getContentResolver().openInputStream(imagenUri);
                    assert inputStream != null;
                    byte[] bytes = getBytes(inputStream);
                    imageRef.putBytes(bytes)
                            .addOnSuccessListener(taskSnapshot -> {
                                imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                    imageUrls.add(downloadUri.toString());
                                    if (imageUrls.size() == imageUris.size()) {
                                        insertarDatos(imageUrls);
                                    }
                                });
                            })
                            .addOnFailureListener(Throwable::printStackTrace);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void mostrarSeleccion() {
        final CharSequence[] options = {"Tomar una imagen", "Seleccionar desde la galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Tomar una imagen")) {
                    solicitarPermisoCamera();
                } else if (options[item].equals("Seleccionar desde la galería")) {
                    abrirGaleria();
                } else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void solicitarPermisoCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, SOLICITUD_CAMARA);
        } else {
            abrirCamera();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void abrirCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri photoUri = null;
            try {
                photoUri = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoUri != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private Uri createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        photoFile = imageFile; // Guardar la referencia al archivo de la foto
        return FileProvider.getUriForFile(this, "img", imageFile);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
        startActivityForResult(intent, PICK_IMAGES_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SOLICITUD_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                Toast.makeText(this, "Permiso de la cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void insertarDatos(List<String> imageUrls) {
        String Titulo, Prioridad, Descripcion;
        Titulo = String.valueOf(titulo.getText());
        Descripcion = String.valueOf(descripcion.getText());
        UserDAO user = new UserDAO();
        creadoPor = String.valueOf(user.getCurrentUser().getCorreo());

        int radioButtonId = prioridad.getCheckedRadioButtonId();

        if (radioButtonId != -1) {
            RadioButton radioButton = findViewById(radioButtonId);
            Prioridad = radioButton.getText().toString();
            finalizado = false;
            String estadoFinalizado = "Pendiente";
            estado.setText(estadoFinalizado);
            TicketDAO ticketDAO = new TicketDAO();
            ticketDAO.insertarTicket(Titulo, estadoFinalizado, Prioridad, Descripcion, imageUrls, finalizado, creadoPor, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(actividad_ticket.this, actividad_menu.class);
                        overridePendingTransition(R.anim.aparecer_transicion, R.anim.desaparecer_transicion);
                        startActivity(intent);
                        Toast.makeText(actividad_ticket.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}