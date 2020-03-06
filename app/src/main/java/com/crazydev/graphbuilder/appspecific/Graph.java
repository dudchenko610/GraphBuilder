package com.crazydev.graphbuilder.appspecific;

import android.opengl.GLES20;
import android.os.Handler;
import android.os.Message;


import com.crazydev.graphbuilder.db.MemoryManager;
import com.crazydev.graphbuilder.math.Vector3D;
import com.crazydev.graphbuilder.rendering.OpenGLRenderer;
import com.crazydev.graphbuilder.rendering.VertexBatcher;




import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import bobenus.collections.BobenusList;
import bobenus.exceptions.ComputationException;
import bobenus.exceptions.ParserException;
import bobenus.expressionparser.Analizer;
import bobenus.expressionparser.Builder;
import bobenus.expressionparser.Node;
import bobenus.feedback.OnAnalizerStatus;
import bobenus.math.Asymptote;
import bobenus.math.Segment;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINE_STRIP;

public class Graph extends GraphThread implements Serializable, OnAnalizerStatus {

    public static final String STATUS_ERROR       = "ERROR";
    public static final String STATUS_ODZ         = "odz";
    public static final String STATUS_ASYMPTOTES  = "asymptotes";
    public static final String STATUS_DERIVATIVES = "derivatives";
    public static final String STATUS_ZEROS       = "zeros";
    public static final String STATUS_DONE        = "done";

    public transient Vector3D color;

    public static int ID_HASH = 0;
    public int id_hash;

    private transient Node root;
    private transient Analizer analizer;

    private float vertices[][];
    private float verticesAsymptotes[][];
    private float verticesPuncturedPoints[][];

    private static float DX_2 = 0.02f;
    private static float DX_5 = 0.000_1f;
    private static float DX_9 = 0.000_000_001f;

    public String                     function;
    public ArrayList<Segment>   allowableRange;
    public ArrayList<Double>   puncturedPoints;
    public ArrayList<Double>             zeros;
    public ArrayList<Asymptote>     asymptotes;

    private transient OpenGLRenderer openGLRenderer;
    private transient OnGraphStatus   onGraphStatus;
    private transient MemoryManager   memoryManager;

    private double left;
    private double right;

    private boolean isReady = false;

    private transient Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch(msg.what) {
                case -1:
                    Graph.this.isReady = true;
                    Graph.this.onGraphStatus.onGraphStatusChanged(Graph.this, Graph.STATUS_ERROR);
                    break;
                case 0:

                    try {
                        Graph.this.computationsAreReady();
                        Graph.this.onGraphStatus.onGraphStatusChanged(Graph.this, Graph.STATUS_DONE);
                    } catch (ComputationException e) {
                        Graph.this.onGraphStatus.onGraphStatusChanged(Graph.this, Graph.STATUS_ERROR);
                    }
                    break;
                default:

                    if (msg.obj instanceof String) {
                        Graph.this.onGraphStatus.onGraphStatusChanged(Graph.this, (String) msg.obj);
                    }


                    break;
            }

        }
    };

    public Graph(Node root, double left, double right, Vector3D color, OnGraphStatus onGraphStatus, MemoryManager memoryManager, OpenGLRenderer openGLRenderer)
            throws ComputationException {

        this.left           = left;
        this.right          = right;

        this.onGraphStatus  = onGraphStatus;
        this.openGLRenderer = openGLRenderer;
        this.memoryManager  = memoryManager;
        this.root           = root;
        this.color          = color;
        this.function       = root.getFunction();

        ID_HASH ++;
        id_hash = ID_HASH;

        this.start();

    }

    public Graph(String function, Vector3D color, BobenusList allowableRange, ArrayList<Double> zeros, ArrayList<Double> puncturedPoints,
                 ArrayList<Asymptote> asymptotes, int id_hash, OnGraphStatus onGraphStatus, OpenGLRenderer openGLRenderer) throws ParserException, ComputationException, IOException, ClassNotFoundException {

        this.function = function;

        this.color = color;
        Builder builder = new Builder();
        this.root = builder.build(function);

        this.allowableRange  = allowableRange;
        this.zeros           = zeros;
        this.puncturedPoints = puncturedPoints;
        this.asymptotes      = asymptotes;

        this.onGraphStatus  = onGraphStatus;
        this.openGLRenderer = openGLRenderer;

        this.buidVertices();
        this.buildAsymptotes();
        this.buildPuncturedPoints();

        this.id_hash = id_hash;
        this.isReady = true;
        this.openGLRenderer.addGraph(this);
    }

    private void buidVertices () throws ComputationException {
        this.vertices           = new float[allowableRange.size()][];

        for (int i = 0; i < allowableRange.size(); i ++) {
            Segment segment = allowableRange.get(i);

            int n = (int) ((segment.right.value - segment.left.value) / DX_2);

            float x = (float) (segment.left.value);
            this.vertices[i] = new float[2 * (n) + (segment.right.isIncluded ? 2 : 0)];

            int j = 0;

            if (!segment.left.isIncluded) {
                //    n --;
                j = 2;

                float y = (float) root.getValue(x + DX_5);

                this.vertices[i][0] = x + DX_5;
                this.vertices[i][1] = y;

                x += DX_2;
            }

            int d = 0;

            if (!segment.right.isIncluded) {
                //    n --;
                d = 2;
                float y = (float) root.getValue(segment.right.value - DX_5);

                this.vertices[i][vertices[i].length - 2] = (float) (segment.right.value - DX_5);
                this.vertices[i][vertices[i].length - 1] = y;
            }

            float y = 0;
            for (; j < 2 * n - d; j += 2) {
                y = (float) root.getValue(x);

           /*     if (Double.isNaN(y)) {
                    Log.d("zbs", "segment.right = " + segment.right + " x = " + x + " y = " + root.getValue(x));
                }*/

                this.vertices[i][j + 0] = x;
                this.vertices[i][j + 1] = y;

                x += DX_2;
            }

            if (segment.right.isIncluded) {
                this.vertices[i][j + 0] = (float) segment.right.value;
                this.vertices[i][j + 1] = (float) this.root.getValue(segment.right.value);

            }

        }
    }

    private void buildAsymptotes() {
        this.verticesAsymptotes = new float[asymptotes.size()][];

        for (int i = 0; i < asymptotes.size(); i ++) {
            this.verticesAsymptotes[i] = new float[4];

            Asymptote asymptote = asymptotes.get(i);

            switch(asymptote.type) {
                case VERTICAL:
                    this.verticesAsymptotes[i][0] = (float) asymptote.x;
                    this.verticesAsymptotes[i][1] = (float) -20000;
                    this.verticesAsymptotes[i][2] = (float) asymptote.x;
                    this.verticesAsymptotes[i][3] = (float)  20000;
                    break;
                case HORIZONTAL:
                    this.verticesAsymptotes[i][0] = (float) -20000;
                    this.verticesAsymptotes[i][1] = (float) asymptote.y;
                    this.verticesAsymptotes[i][2] = (float)  20000;
                    this.verticesAsymptotes[i][3] = (float) asymptote.y;

                    break;
                case TANGENTIAL:

                    this.verticesAsymptotes[i][0] = (float) -100000;
                    this.verticesAsymptotes[i][1] = (float) (asymptote.k * -100000 + asymptote.b);
                    this.verticesAsymptotes[i][2] = (float)  100000;
                    this.verticesAsymptotes[i][3] = (float) (asymptote.k *  100000 + asymptote.b);

                    break;
            }

        }
    }

    private void buildPuncturedPoints() throws ComputationException {
        this.verticesPuncturedPoints = new float[this.puncturedPoints.size()][];

        float s = 0.25f;

        for(int i = 0; i < this.puncturedPoints.size(); i++) {
            this.verticesPuncturedPoints[i] = new float[8];

            double x = this.puncturedPoints.get(i);
            double y = this.root.getValue(x - DX_9);
            if (Double.isNaN(y)) {
                y = this.root.getValue(x + DX_9);
            }

            this.verticesPuncturedPoints[i][0] = (float) (x - s);
            this.verticesPuncturedPoints[i][1] = (float) (y - s);
            this.verticesPuncturedPoints[i][2] = (float) (x + s);
            this.verticesPuncturedPoints[i][3] = (float) (y + s);

            this.verticesPuncturedPoints[i][4] = (float) (x - s);
            this.verticesPuncturedPoints[i][5] = (float) (y + s);
            this.verticesPuncturedPoints[i][6] = (float) (x + s);
            this.verticesPuncturedPoints[i][7] = (float) (y - s);

        }
    }

    public void draw(VertexBatcher vertexBatcher, float left, float right, float bottom, float top) {

        GLES20.glLineWidth(2.0f);
        // draw graphics
        for (int i = 0; i < this.vertices.length; i ++) {
            vertexBatcher.depictCurve(color, 1f, vertices[i], vertices[i].length, GL_LINE_STRIP);
        }

        GLES20.glLineWidth(1.0f);
        // draw asymptotes
        for (int i = 0; i < this.verticesAsymptotes.length; i ++) {
            vertexBatcher.depictCurve(color, 0.5f, verticesAsymptotes[i], verticesAsymptotes[i].length, GL_LINES);
        }

        // draw punctured points
        for (int i = 0; i < this.puncturedPoints.size(); i ++) {
            vertexBatcher.depictCurve(color, 1f, verticesPuncturedPoints[i], verticesPuncturedPoints[i].length, GL_LINES);
        }


        this.id_hash = id_hash;

    }

    public void delete() {
        this.toFinish = false;
        this.isReady  = true;
        this.openGLRenderer.getGraphs().remove(this);
        this.interrupt();
    }

    private void computationsAreReady() throws ComputationException {

        this.allowableRange    = analizer.getAllowableRange();
        this.puncturedPoints   = analizer.getPuncturedPoints();
        this.zeros             = analizer.getZeros();
        this.asymptotes        = analizer.getAsyptotes();
        this.function          = root.getFunction();

        // NonExistent are not non existent

     /* Log.d("myparser","ОДЗ " + allowableRange.toString());
        Log.d("myparser","Точки в которых ф. не сущ. " + puncturedPoints.toString());
        Log.d("myparser","Нули функции " + zeros.toString());*/

        this.buidVertices();
        this.buildAsymptotes();
        this.buildPuncturedPoints();

        if (this.toFinish) {
            this.memoryManager.saveGraph(this);
            this.openGLRenderer.addGraph(this);
            this.isReady = true;
        }

    }

    @Override
    public void run() {
        try {
            this.analizer = new Analizer(root, left, right, this);

        } catch (ComputationException | ClassNotFoundException | IOException e) {
            Message msg = new Message();
            msg.what = -1;
            this.handler.sendMessage(msg);
        }

    }

    @Override
    public void onTaskComplete(Node node, String s) {
        switch (s) {
            case "done":
                Message msg = new Message();
                msg.what = 0;
                this.handler.sendMessage(msg);

                break;

            default:
                Message msg2 = new Message();
                msg2.what = 1;
                msg2.obj  = s;
                this.handler.sendMessage(msg2);

                break;
        }

    }

    public boolean isReady() {
        return this.isReady;
    }

    public Vector3D getColor() {
        return this.color;
    }
}
