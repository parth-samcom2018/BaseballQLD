package com.sportsclub.baseballqld;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.joanzapata.pdfview.PDFView;

import java.io.IOException;
import java.net.URL;

public class PDFVC extends AppCompatActivity {

    public static String url;
    public static String title;

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfvc);

        //BACK, rest defined in base class
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setTitle("PDF Viewer");

        pdfView = (PDFView) findViewById(R.id.pdfview);
        pdfView.enableSwipe(true);


        downloadFile();
    }

    private java.io.File file;

    private void downloadFile() {
        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();
        java.io.File folder = new java.io.File(extStorageDirectory, "pdf");
        folder.mkdir();
        file = new java.io.File(folder, "Read.pdf");
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        //  DM.downloadFile(url, file); //sync network call... bad..

        new DownloadFilesTask().execute(null, null, null);


        // File file = new File(Environment.getExternalStorageDirectory()+"/Mypdf/Read.pdf");
        /*GENERAL INTENT handling
        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        startActivity(intent);
        */
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {

            if (file != null) DM.downloadFile(url, file);

            long i = 1;
            return i;
        }

        protected void onProgressUpdate(Integer... progress) {
            //  setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            // showDialog("Downloaded " + result + " bytes");
            pdfView.fromFile(file).load();
        }
    }
}

