package com.example.group9_inclass05;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView tv1;
    int index=0;
    ImageButton ib2,ib1;
    ImageView iv;
    ProgressDialog p;

    ArrayList<String> items=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b1= (Button) findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Internet()){
                    Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                new GetKeywords().execute("http://dev.theappsdr.com/apis/photos/keywords.php");

            }
        });

    }
//Internet Connectivity Check
    public boolean Internet(){

        ConnectivityManager cm=(ConnectivityManager)getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cm.getActiveNetworkInfo();
        if(nf==null || !nf.isConnected() || (nf.getType()!=cm.TYPE_WIFI && nf.getType()!=cm.TYPE_MOBILE))
        {
            return false;
        }
        return true;
    }

//Getkeywords from URL
public class GetKeywords extends AsyncTask<String, Void, ArrayList<String>> {
        URL u;
    @Override
    protected ArrayList<String> doInBackground(String... strings) {

items.clear();
        BufferedReader b=null;
        HttpURLConnection htp=null;
        try {
            u=new URL(strings[0]);
            htp=(HttpURLConnection) u.openConnection();
            htp.connect();
            if(htp.getResponseCode()== HttpURLConnection.HTTP_OK) {
                Log.d("Demo","Connection started");
              b=new BufferedReader(new InputStreamReader(htp.getInputStream()));
                String line="";
                while((line=b.readLine())!=null){
                    String[] parts=line.split(";");

                    for(int i=0;i<parts.length;i++)
                    {
                        items.add(parts[i]);
                    }
                    Log.d("Demo","Array is"+items);
                    Log.d("Demo","array"+line.split(";").toString());
                    //str.append(line);
                }
              } return items;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(htp!=null)
                {
                    htp.disconnect();
                }
                if(b!=null)
                {
                    try {
                        b.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> items) {
if(items!=null) {
    Log.d("Demo",items.toString());
    AlertDialog.Builder b=new AlertDialog.Builder(MainActivity.this);
    final CharSequence[] cs=items.toArray(new CharSequence[items.size()]);
    b.setTitle("Choose Keywords");

    b.setItems(cs, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("Demo","Clicked"+cs[which]);
            tv1=(TextView)findViewById(R.id.tv1);
            tv1.setText(cs[which]);
            new geturls().execute(encoded("http://dev.theappsdr.com/apis/photos/index.php"));
            dialog.dismiss();
        }
    });
    AlertDialog alert=b.create();
    alert.show();

} else
{
    Log.d("Demo","Null result");
}
        }
    }

    public String encoded(String url) {
    String result = url + "?keyword=" + tv1.getText().toString();
    Log.d("Demo", "url is" + result);
    return result;
}

//get imageURL's from selected keyword
public class geturls extends AsyncTask<String, Void, ArrayList<String>>{

ArrayList<String> url=new ArrayList<String>();
StringBuilder str=new StringBuilder();
    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        try {
            //ab.clear();
            URL u1=new URL(strings[0]);
            HttpURLConnection htp1=(HttpURLConnection)u1.openConnection();
            htp1.connect();
            if(htp1.getResponseCode()==HttpURLConnection.HTTP_OK){
                BufferedReader b=new BufferedReader(new InputStreamReader(htp1.getInputStream()));
                String line=b.readLine();
                //Log.d("Demo","is" +b.readLine());
                while(line!=null)
                {
                    url.add(line);
                 Log.d("Demo","is"+line);
                 line=b.readLine();
                }
                    return url;

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

return null;
    }

    @Override
    protected void onPostExecute(final ArrayList<String> strings) {
       // Log.d("Demo","urls"+strings.toString());
        Log.d("Demo","image started");
        if(strings.size()==0||strings.size()==1)
        {
            findViewById(R.id.ib1).setEnabled(false);
            findViewById(R.id.ib2).setEnabled(false);

        }else {
            findViewById(R.id.ib1).setEnabled(true);
            findViewById(R.id.ib2).setEnabled(true);
        }
if(strings.size()!=0) {
    index = 0;

        p = new ProgressDialog(MainActivity.this);
        new image(MainActivity.this).execute(strings.get(index));



        findViewById(R.id.ib2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = index + 1;
                if (index < strings.size()) {

                    new image(MainActivity.this).execute(strings.get(index));
                } else if (index >= strings.size()) {
                    index = strings.size() - index;
                    new image(MainActivity.this).execute(strings.get(index));
                }
            }
        });
        findViewById(R.id.ib1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = index - 1;
                if (index >= 0) {
                    new image(MainActivity.this).execute(strings.get(index));
                } else if (index < 0) {
                    index = strings.size() + index;
                    new image(MainActivity.this).execute(strings.get(index));
                }

            }
        });

    }
else if(strings.size()==0){
    Toast.makeText(MainActivity.this, "No Images Available", Toast.LENGTH_SHORT).show();
    iv.setImageResource(0);
}
    }
}

//returning image to main Thread

    public void image(Bitmap b){
       iv=(ImageView)findViewById(R.id.imageView);
        iv.setImageBitmap(b);
        p.dismiss();
    }
}

