package latex.htn;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doStuff(View view) {
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(),
                    Uri.parse("resources/dracula_p361.jpg"));

            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().
                    getOnDeviceTextRecognizer();

            textRecognizer.processImage(image).addOnSuccessListener(
                    new OnSuccessListener<FirebaseVisionText>() {

                @Override
                public void onSuccess(FirebaseVisionText result) {
                    List<FirebaseVisionText.TextBlock> blocks = result.getTextBlocks();
                    if (blocks.size() == 0) {
                        Log.i("htn", "error");
                        return;
                    }
                    for (int i = 0; i < blocks.size(); i++) {
                        
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
