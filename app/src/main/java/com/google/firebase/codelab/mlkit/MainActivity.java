// Hack the North 2018: Text to LaTeX
// An Android app to convert pictures of math to LaTeX code.

package com.google.firebase.codelab.mlkit;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String[] OPERATIONS = {"+", "-", "x", "*", "÷"};
    public static final String[] RELATIONS = {"=", "≤", "≥"};
    public static final String[] SETS =	{"∩", "∪", "∈", "∉"};

    private String math;
    private String newLineWithSeparation;
    private ArrayList<String> list;

    // ============================================================================================

    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private Button mButton;
    private Button mCloudButton;
    private Bitmap mSelectedImage;
    private GraphicOverlay mGraphicOverlay;
    private Integer mImageMaxWidth; // Max width (portrait mode)
    private Integer mImageMaxHeight; // Max height (portrait mode)

    /**
     * Create the main window and associated widgets.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //=========================================================================================

        Button btnCamera = (Button)findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

        //=========================================================================================

//        mImageView = findViewById(R.id.image_view);
//        mButton = findViewById(R.id.button_text);
//        mCloudButton = findViewById(R.id.button_cloud_text);

//        mGraphicOverlay = findViewById(R.id.graphic_overlay);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                runTextRecognition();
//            }
//        });
//        mCloudButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                runCloudTextRecognition();
//            }
//        });
//        Spinner dropdown = findViewById(R.id.spinner);
//        String[] items = new String[]{"Image 1", "Image 2", "Image 3"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout
//                .simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter);
//        dropdown.setOnItemSelectedListener(this);
    }

    //=============================================================================================

    // getFilesDir()
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            Bitmap imageData = (Bitmap)data.getExtras().get("data");
            runCloudTextRecognition(imageData);

//            MediaStore.Images.Media.insertImage(getContentResolver(), imageData, "scanned",
//                    "scanned copy"); // This doesn't work (check this)
        }
    }

    //=============================================================================================

    /**
     * Runs the on-device text recognition model. Deprecated since cloud-based services are
     * available.
     */
    @Deprecated
    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        mButton.setEnabled(false);
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                mButton.setEnabled(true);
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                mButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    /**
     * Process the captured text from the on-device recognition model.
     * @param texts
     */
    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        mGraphicOverlay.clear();

        ArrayList<String> lines2 = new ArrayList<String>();

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();

            for (int j = 0; j < lines.size(); j++) {
                lines2.add(lines.get(j).getText());
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "output.txt"));
            PrintWriter printWriter = new PrintWriter(fos);

            for (String s : lines2) {
                printWriter.println(s);
            }
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the cloud-based text recognition model.
     */
    private void runCloudTextRecognition(Bitmap b) {
//        mCloudButton.setEnabled(false);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(b);
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionDocumentText>() {
                            @Override
                            public void onSuccess(FirebaseVisionDocumentText texts) {
//                                mCloudButton.setEnabled(true);
                                processCloudTextRecognitionResult(texts);
                                showToast("Image converted!");
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
//                                mCloudButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    /**
     * Process the text gathered by the recognition model.
     * @param text
     */
    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully
        if (text == null) {
            showToast("No text found");
            return;
        }
//        mGraphicOverlay.clear();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();

        ArrayList<String> lines2 = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();

            for (int j = 0; j < paragraphs.size(); j++) {
                lines2.add(paragraphs.get(j).getText());
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "output.txt"));
            PrintWriter printWriter = new PrintWriter(fos);

            for (String s : lines2) {
                printWriter.println(s);
            }
            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        initiateConversion(getFilesDir() + "output.txt");
    }

    /**
     * Displays a pop-up message.
     * @param message
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Functions for loading images from app assets.

    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        mGraphicOverlay.clear();
        switch (position) {
            case 0:
                mSelectedImage = getBitmapFromAsset(this, "Please_walk_on_the_grass.jpg");

                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                mSelectedImage = getBitmapFromAsset(this, "non-latin.jpg");
                break;
            case 2:
                // Whatever you want to happen when the thrid item gets selected
                mSelectedImage = getBitmapFromAsset(this, "nl2.jpg");
                break;
        }
        if (mSelectedImage != null) {
            // Get the dimensions of the View
            Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

            int targetWidth = targetedSize.first;
            int maxHeight = targetedSize.second;

            // Determine how much to scale down the image
            float scaleFactor =
                    Math.max(
                            (float) mSelectedImage.getWidth() / (float) targetWidth,
                            (float) mSelectedImage.getHeight() / (float) maxHeight);

            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            mSelectedImage,
                            (int) (mSelectedImage.getWidth() / scaleFactor),
                            (int) (mSelectedImage.getHeight() / scaleFactor),
                            true);

            mImageView.setImageBitmap(resizedBitmap);
            mSelectedImage = resizedBitmap;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    /**
     * Creates a bitmap from a file (probably).
     * @param context
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream is;
        Bitmap bitmap = null;
        try {
            is = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    // ============================================================================================

    /**
     * Once a normal text file has been created, pass it here to complete the operation and
     * generate a LaTeX file.
     * @param fileName
     */
    public void initiateConversion(String fileName) {
        Context context = this;
        String dir = context.getFilesDir().getAbsolutePath();
        String tex_file_name = "new_tex";
        list = new ArrayList<>();

        list.addAll(Arrays.asList(OPERATIONS));
        list.addAll(Arrays.asList(RELATIONS));
        list.addAll(Arrays.asList(SETS));

        newLineWithSeparation = System.getProperty("line.separator") +
                System.getProperty("line.separator");

        math = "\\documentclass{article}" + newLineWithSeparation;
        math += "\\usepackage{amsfonts}" + newLineWithSeparation;
        math += "\\usepackage{amssymb}" + newLineWithSeparation;
        math += "\\usepackage{amsmath}" + newLineWithSeparation;
        math += "\\usepackage{mathtools}" + newLineWithSeparation;
        math += "\\begin{document}" + newLineWithSeparation;

        // Do stuff here
        readFile(fileName);

        math += "\\end{document}";
        writeFile(dir, tex_file_name); // Change dir to a more appropriate directory
    }

    /**
     * Reads a text file to be converted into LaTeX. fileName should be getFilesDir().
     *
     * @param fileName
     */
    public void readFile(String fileName) {
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                convertToLatex(line);
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '" + fileName + "'");
            // Or we could just do this: ex.printStackTrace();
        }
    }

    /**
     * Writes to a TeX file from a converted plaintext file. dir should be getFilesDir().
     * @param dir
     * @param tex_file_name
     */
    public void writeFile(String dir, String tex_file_name) {
        dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        FileWriter writer = null;
        try {
            writer = new FileWriter(dir + "\\" + tex_file_name + ".tex", false);
            writer.write(math, 0, math.length());
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Converts a line of plaintext into LaTeX code.
     * @param line
     */
    public void convertToLatex(String line) {
        String[] splited = line.trim().split("\\s+");
        for (String str: splited) {
            if (list.contains(str)) {
                switch (str) {
                    case "+":  math += "\\plus" + " ";
                        break;
                    case "-": math += "\\minus" + " ";
                        break;
                    case "x": math += "\\times" + " ";
                        break;
                    case "*": math += "\\ast" + " ";
                        break;
                    case "÷": math += "\\div" + " ";
                        break;
                    case "=": math += "\\equals" + " ";
                        break;
                    case "≤": math += "\\leq" + " ";
                        break;
                    case "≥": math += "\\geq" + " ";
                        break;
                    case "∩": math += "\\cap" + " ";
                        break;
                    case "∪": math += "\\cup" + " ";
                        break;
                    case "∈": math += "\\in" + " ";
                        break;
                    case "∉": math += "\\notin" + " ";
                        break;
                }
            } else { // Character is not a symbol (e.g. a letter)
                math += str + " ";
            }
        }
        math = math.trim() + newLineWithSeparation;
    }
}
