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
import android.danyk.R;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.danyk.DAO.TicketDAO;
import android.danyk.DAO.UserDAO;


public class actividad_ticket extends AppCompatActivity {

    private TextView estado;
    private TextInputEditText titulo, descripcion;
    private RadioGroup prioridad;
    private static final int PICK_IMAGES_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private final List<Uri> imageUris = new ArrayList<>();
    private LinearLayout layoutVistaPrevia;
    private StorageReference storageReference;
    private static final int SOLICITUD_CAMARA = 100;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_ticket);

        layoutVistaPrevia = findViewById(R.id.layout_vistaPrevia);
        TextView mostrarNombre = findViewById(R.id.creacion_ticket_usuario);
        estado = findViewById(R.id.creacion_ticket_estado);
        Button botonEnvio = findViewById(R.id.creacion_ticket_enviar);
        titulo = findViewById(R.id.creacion_ticket_titulo);
        descripcion = findViewById(R.id.creacion_ticket_descripcion);
        prioridad = findViewById(R.id.creacion_ticket_prioridad);
        storageReference = FirebaseStorage.getInstance().getReference();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})

        Button botonAdjuntarImagen = findViewById(R.id.boton_adjuntarImagen);
        botonAdjuntarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSeleccion();
            }
        });

        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                    Bitmap imageBitmap = obtenerBitmapDesdeUri(imageUri);
                    mostrarVistaPrevia(imageBitmap);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                Bitmap imageBitmap = obtenerBitmapDesdeUri(imageUri);
                mostrarVistaPrevia(imageBitmap);
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoUri != null) {
                imageUris.add(photoUri);
                Bitmap imageBitmap = obtenerBitmapDesdeUri(photoUri);
                mostrarVistaPrevia(imageBitmap);
            }
        }
    }

    private Bitmap obtenerBitmapDesdeUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void mostrarVistaPrevia(Bitmap imageBitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 500, 350, false);
        Bitmap rotatedBitmap = rotarSiEsNecesario(imageUris.get(imageUris.size() - 1), resizedBitmap);
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(20, 10, 20, 10);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(rotatedBitmap);
        layoutVistaPrevia.addView(imageView);
        layoutVistaPrevia.setVisibility(View.VISIBLE);
    }

    private Bitmap rotarSiEsNecesario(Uri imageUri, Bitmap imageBitmap) {
        int orientacion = cogerOrientacionImagen(imageUri);

        switch (orientacion) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotarBitmap(imageBitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotarBitmap(imageBitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotarBitmap(imageBitmap, 270);
            default:
                return imageBitmap;
        }
    }

    private int cogerOrientacionImagen(Uri imageUri) {
        try {
            InputStream input = getContentResolver().openInputStream(imageUri);
            ExifInterface exifInterface = new ExifInterface(input);
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
            return ExifInterface.ORIENTATION_NORMAL;
        }
    }

    private Bitmap rotarBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void subirImagenes() {
        List<String> imageUrls = new ArrayList<>();
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
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "img", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
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
        String Titulo, Estado, Prioridad, Descripcion;
        Titulo = String.valueOf(titulo.getText());
        Estado = estado.getText().toString();
        Descripcion = String.valueOf(descripcion.getText());

        int radioButtonId = prioridad.getCheckedRadioButtonId();

        if (radioButtonId != -1) {
            RadioButton radioButton = findViewById(radioButtonId);
            Prioridad = radioButton.getText().toString();

            TicketDAO ticketDAO = new TicketDAO();

            ticketDAO.insertarTicket(Titulo, Estado, Prioridad, Descripcion, imageUrls, new OnCompleteListener<Void>() {
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

