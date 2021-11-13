package com.example.testinggraph;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "Testando";

    //add PointsGraphSeries of DataPoint type
    PointsGraphSeries<DataPoint> xySeries;


    GraphView mScatterPlot;

    //make xyValueArray global
    private ArrayList<XYValue> xyValueArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScatterPlot = findViewById(R.id.graph);


        draw();
    }

    private void draw() {
        xyValueArray = new ArrayList<>();
        xySeries = new PointsGraphSeries<>();
        ArrayList<String[]> arrayList = LoadDataFromAssets("teste.txt");
        XYValue anterior = null;
        for(int x = 0; x<arrayList.size(); x++){

            Log.e(TAG, String.valueOf(arrayList.get(x)[2]));
            double Axis = Double.parseDouble(arrayList.get(x)[0]);
            double Ayis = Double.parseDouble(arrayList.get(x)[1]);
            XYValue value = new XYValue(Axis, Ayis);
            xyValueArray.add(value);

            if (anterior != null){
                LineGraphSeries<DataPoint> series;
                if(anterior.getX()<value.getX()){
                    series = new LineGraphSeries<>(new DataPoint[] {
                            new DataPoint(anterior.getX(), anterior.getY()),
                            new DataPoint(value.getX(), value.getY())
                    });
                }else{
                    series = new LineGraphSeries<>(new DataPoint[] {
                            new DataPoint(value.getX(), value.getY()),
                            new DataPoint(anterior.getX(), anterior.getY())
                    });
                }
                mScatterPlot.addSeries(series);


            }
            anterior = value;
        }

        createScatterPlot();
    }

    private void createScatterPlot() {
        Log.d(TAG, "createScatterPlot: Creating scatter plot.");
        //sort the array of xy values
        xyValueArray = sortArray(xyValueArray);

        //add the data to the series

        for(int i = 0;i <xyValueArray.size(); i++){
            double x = xyValueArray.get(i).getX();
            double y = xyValueArray.get(i).getY();
            try{
                xySeries.appendData(new DataPoint(x,y),true, 1000);
            }catch (IllegalArgumentException e){
                Log.e(TAG, "createScatterPlot: IllegalArgumentException: " + e.getMessage() );
            }
        }

        //set some properties
        xySeries.setShape(PointsGraphSeries.Shape.POINT);
        xySeries.setColor(Color.BLUE);
        xySeries.setSize(5f);
        xySeries.setCustomShape(new PointsGraphSeries.CustomShape() {

            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setStrokeWidth(5);
                // we are initialising the shape structure of dat points
//                canvas.drawLine(x - 20, y, x, y - 20, paint);
//                canvas.drawLine(x, y - 20, x + 20, y, paint);
//                canvas.drawLine(x + 20, y, x, y + 20, paint);
//                canvas.drawLine(x - 20, y, x, y + 20, paint);
            }
        });

        //set Scrollable and Scaleable
        mScatterPlot.getViewport().setScalable(true);
        mScatterPlot.getViewport().setScalableY(true);
        //mScatterPlot.getViewport().setScrollable(true);
        //mScatterPlot.getViewport().setScrollableY(true);

        //set manual x bounds
        mScatterPlot.getViewport().setYAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxY(10);
        mScatterPlot.getViewport().setMinY(-10);

        //set manual y bounds
        mScatterPlot.getViewport().setXAxisBoundsManual(true);
        mScatterPlot.getViewport().setMaxX(10);
        mScatterPlot.getViewport().setMinX(-10);




        mScatterPlot.addSeries(xySeries);
        //mScatterPlot.addSeries(lineGraphSeries);
    }


    /**
     * Ordena o ArrayList<XYValue> respeitando os valores da coordenada x.
     * @param array
     * @return
     */
    private ArrayList<XYValue> sortArray(ArrayList<XYValue> array){
        /*
        //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
         */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(),2))));
        int m = array.size() - 1;
        int count = 0;
        Log.d(TAG, "sortArray: Sorting the XYArray.");


        while (true) {
            m--;
            if (m <= 0) {
                m = array.size() - 1;
            }
            Log.d(TAG, "sortArray: m = " + m);
            try {

                double tempY = array.get(m - 1).getY();
                double tempX = array.get(m - 1).getX();
                if (tempX > array.get(m).getX()) {
                    array.get(m - 1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m - 1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                } else if (tempX == array.get(m).getX()) {
                    count++;
                    Log.d(TAG, "sortArray: count = " + count);
                } else if (array.get(m).getX() > array.get(m - 1).getX()) {
                    count++;
                    Log.d(TAG, "sortArray: count = " + count);
                }
                //break when factorial is done
                if (count == factor) {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "sortArray: ArrayIndexOutOfBoundsException. Need more than 1 data point to create Plot." +
                        e.getMessage());
                break;
            }
        }
        return array;
    }


    // função que retorna uma lista de string contendo os dados de um arquivo na pasta assets
    public ArrayList<String[]> LoadDataFromAssets(String inFile) {
        String tContents = "";

        try {
            InputStream stream = getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
        } catch (IOException e) {
            // Handle exceptions here
        }
        String[] cont = new String[0];
        if(!tContents.isEmpty()){
            cont = tContents.split("\n");
        }

        ArrayList<String[]> arrayList = new ArrayList<>();
        for(int x=0; x<cont.length;x++){
            arrayList.add(cont[x].split(" "));
        }

        return arrayList;

    }
}