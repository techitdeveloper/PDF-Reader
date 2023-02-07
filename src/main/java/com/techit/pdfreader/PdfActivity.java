package com.techit.pdfreader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener {
    String filePath = "";
    private TextToSpeech textToSpeech;
    PDFView pdfView;
    Button btnRead;
    EditText etPage;
    int pageNumber = 0;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        btnRead = findViewById(R.id.btnRead);
         pdfView = findViewById(R.id.pdfView);
         etPage = findViewById(R.id.etPage);
        filePath = getIntent().getStringExtra("path");
        File file = new File(filePath);
        Uri path = Uri.fromFile(file);
        pdfView.fromUri(path).defaultPage(0)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .load();
        try {
            PdfReader reader = new PdfReader(filePath);
            count = reader.getNumberOfPages();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(btnRead.getText().equals("Read")) {
                    if(TextUtils.isEmpty(etPage.getText().toString())) {
                        tts(filePath, 1);
                    }
                    else {
                        int currentPage = Integer.parseInt(etPage.getText().toString());
                        //tts(filePath, Math.min(currentPage, count));
                        if(currentPage > count) {
                            Toast.makeText(PdfActivity.this, "Enter Valid Page no." + count, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            tts(filePath, currentPage);
                        }
                    }

                    btnRead.setText("Stop");
                }
                else if(btnRead.getText().equals("Stop")) {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    btnRead.setText("Read");
                }
            }
        });
    }

    public void tts(final String filepath, final int pageNumber) {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
//                textToSpeech.setPitch(0.5f);
                String stringParser = "";
                try {

                    PdfReader pdfReader = new PdfReader(filepath);
//                    int n = pdfReader.getNumberOfPages();
//                    for(int counter=0; counter<n; counter++) {
//                        stringParser = stringParser + PdfTextExtractor.getTextFromPage(pdfReader, counter+1).trim();
//
//                    }
//                    //stringParser = PdfTextExtractor.getTextFromPage(pdfReader, 1).trim();
//                    Log.d("PDF", stringParser);
                    stringParser = PdfTextExtractor.getTextFromPage(pdfReader, pageNumber).trim();
                    pdfReader.close();
                    textToSpeech.speak(stringParser, TextToSpeech.QUEUE_FLUSH, null);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }




    @Override
    protected void onPause() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        //tvPageNumber.setText(""+pageNumber);
    }


}