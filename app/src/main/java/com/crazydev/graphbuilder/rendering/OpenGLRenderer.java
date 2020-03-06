package com.crazydev.graphbuilder.rendering;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.crazydev.graphbuilder.R;
import com.crazydev.graphbuilder.appspecific.Graph;
import com.crazydev.graphbuilder.framework.Input.TouchEvent;
import com.crazydev.graphbuilder.io.Assets;
import com.crazydev.graphbuilder.io.ViewShot;
import com.crazydev.graphbuilder.math.Vector2D;
import com.crazydev.graphbuilder.math.Vector3D;
import com.crazydev.graphbuilder.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;

public class OpenGLRenderer extends GLSurfaceView  implements  GLSurfaceView.Renderer {

    private ShaderProgram shaderProgram;
    private VertexBatcher vertexBatcher;
    private Context       context;
    private OpenGLRendererTouchEventListener openGLRendererTouchEventListener;

    private Object stateChanged = new Object();
    private Vector3D color = new Vector3D(1, 1, 1);
    enum    GLGameState {
        Running,
        Paused,
        Finished,
        Idle
    };

    private GLGameState state = GLGameState.Paused;

    private HashMap<String, Sprite> numbersSprites = new HashMap<String, Sprite>();

    private ArrayList<String> expressions = new ArrayList<String>();
    private ArrayList<Graph> graphs      = new ArrayList<Graph>();
  //  private HashMap<Integer, Graph> gr = new HashMap<Integer, Graph>();
    private GL10 gl10;
    private boolean p_grid = false;

    public OpenGLRenderer (Context context, AttributeSet attribs) {
        super(context, attribs);

        this.context = context;
        this.setEGLContextClientVersion(2);
        this.setRenderer(this);

        this.openGLRendererTouchEventListener = new OpenGLRendererTouchEventListener();
        this.setOnTouchListener(openGLRendererTouchEventListener);

        int color = context.getResources().getColor(R.color.default_color_surface);

        int A = (color >> 24) & 0xff; // or color >>> 24
        int R = (color >> 16) & 0xff;
        int G = (color >>  8) & 0xff;
        int B = (color      ) & 0xff;

        this.color.x = R;
        this.color.y = G;
        this.color.z = B;

    }

    private boolean makeScreenShot = false;

    @Override
    public void onDrawFrame(GL10 gl) {


        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(color.x, color.y, color.z, 1f);

        GLGameState state = null;
        synchronized(stateChanged) {
            state = this.state;
        }

        if (state == GLGameState.Running) {


            GLES20.glLineWidth(1.0f);

            this.handleTouchEvents();
            this.depictDecartGrid();
            this.depictGraphs();


            if (makeScreenShot) {
                this.makeScreenShot = false;
                try {
                    ViewShot.makeGLSurfaceViewShot(this, gl, this.context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }

        if (state == GLGameState.Paused) {

            synchronized(stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }

        if (state == GLGameState.Finished) {

            synchronized(stateChanged) {
                this.state = GLGameState.Idle;
                stateChanged.notifyAll();
            }
        }

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.shaderProgram = new ShaderProgram(context, R.raw.vertex_shader, R.raw.fragment_shader, getWidth(), getHeight());
        this.shaderProgram.setSides(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        this.shaderProgram.setViewPort(new Vector2D(0, 0));
        this.vertexBatcher = new VertexBatcher(shaderProgram, 200000);

        this.state = GLGameState.Running;

        Assets.load(context);

        Sprite sprite;

        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_m_b, new Vector2D(1, 1), 2, 4);
        numbersSprites.put("-", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_0_b, new Vector2D(1, 1), 2, 4);
        numbersSprites.put("0", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_1_b, new Vector2D(3, 1), 2, 4);
        numbersSprites.put("1", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_2_b, new Vector2D(5, 1), 2, 4);
        numbersSprites.put("2", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_3_b, new Vector2D(7, 1), 2, 4);
        numbersSprites.put("3", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_4_b, new Vector2D(9, 1), 2, 4);
        numbersSprites.put("4", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_5_b, new Vector2D(11, 1), 2, 4);
        numbersSprites.put("5", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_6_b, new Vector2D(13, 1), 2, 4);
        numbersSprites.put("6", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_7_b, new Vector2D(15, 1), 2, 4);
        numbersSprites.put("7", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_8_b, new Vector2D(17, 1), 2, 4);
        numbersSprites.put("8", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_9_b, new Vector2D(19, 1), 2, 4);
        numbersSprites.put("9", sprite);
        sprite = new Sprite(vertexBatcher, Assets.digitsRegion_p_b, new Vector2D(21, 1), 2, 4);
        numbersSprites.put("p", sprite);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        float ratio = 1f;

        float verticalRatio, horizontalRatio;

        if (width > height) {
            ratio = (float) width / height;

            horizontalRatio = ratio;
            verticalRatio = 1;

        } else {
            ratio = (float) height / width;

            horizontalRatio = 1;
            verticalRatio = ratio;

        }

        shaderProgram.setRatio(horizontalRatio, verticalRatio);

    }

    private float [] axesVerts = {-20000, 0, 20000, 0, 0, -20000, 0, 20000};
    private Vector3D axesColor = new Vector3D(128 / 255.0f, 128 / 255.0f, 128 / 255.0f);
    private Vector3D gridColor = new Vector3D(230 / 255.0f, 230 / 255.0f, 230 / 255.0f);
    private Vector2D touchedDown = new Vector2D();
    private Vector2D diff = new Vector2D();
    private Vector2D delta = new Vector2D();
    private boolean moving  = false;
    private boolean zooming = false;
    private float pLength = 0;
    private float [] gridVerts = new float[1000];

    private void depictDecartGrid() {

        float w = shaderProgram.ACTUAL_WIDTH;
        float h = shaderProgram.ACTUAL_HEIGHT;

        float x_c = shaderProgram.left   + (shaderProgram.right - shaderProgram.left  ) / 2;
        float y_c = shaderProgram.bottom + (shaderProgram.top   - shaderProgram.bottom) / 2;

        float x_left  = x_c - w / 2.0f;
        float x_right = x_c + w / 2.0f;

        float y_bottom = y_c - h / 2.0f;
        float y_top    = y_c + h / 2.0f;

        int x_l = ((int) x_left)  + (x_left < 0  ? 0 : 1);
        int x_r = ((int) x_right) + (x_right < 0 ? 1 : 0);

        int y_b = ((int) y_bottom) + (y_bottom < 0  ? 0 : 1);
        int y_t = ((int) y_top)    + (y_top < 0 ? 1 : 0);


        int gridLen = 0;
        for (int i = x_l; i <= x_r; i ++) {
            gridVerts[gridLen ++] = i;
            gridVerts[gridLen ++] = y_bottom;
            gridVerts[gridLen ++] = i;
            gridVerts[gridLen ++] = y_top;
        }

        for (int i = y_b; i <= y_t; i ++) {
            gridVerts[gridLen ++] = x_left;
            gridVerts[gridLen ++] = i;
            gridVerts[gridLen ++] = x_right;
            gridVerts[gridLen ++] = i;

        }

       vertexBatcher.depictCurve(gridColor, 1.0f, gridVerts, gridLen, GL_LINES);
       vertexBatcher.depictCurve(axesColor, 1.0f, axesVerts, axesVerts.length, GL_LINES);

       float r_x = shaderProgram.ACTUAL_WIDTH  / 70;
       float r_y = shaderProgram.ACTUAL_HEIGHT / 70;

       if (r_x > r_y) {
           r_y *= 1.8f;
           r_x = 0.7f * r_y;
       } else {
           r_y = r_x / 0.7f;
       }

       vertexBatcher.startDepictShapes();

       int prec = -100000;

       for (int i = y_b - 1; i <= y_t + 1; i ++) {

           if (i == 0) {
               continue;
           }

           int j = Math.abs(i);
           double piParrams = (j / Math.PI) - ((int) (j / Math.PI)) ;
           int p_num = (int) (i / Math.PI);
           String p_label = p_num + "p";

           String num = String.valueOf(i);
           float x_offset = 0;

           if (x_right - num.length() * r_x - r_x < 0) {
               x_offset = x_right - num.length() * r_x ;
           }

           if (x_left > 0) {
               x_offset = x_left + r_x;
           }

           if (x_left < 0 && x_right - num.length() * r_x - r_x > 0) {
               x_offset = r_x;
           }

           float n_p_offset = 0;
           float p_offset   = -1.0f * p_label.length() * r_x - r_x;

           if (x_left + p_label.length() * r_x + r_x > 0) {
               n_p_offset = x_left + 1.1f *r_x + r_x + num.length() * r_x;

               p_offset = n_p_offset;

           } else {
               p_offset += x_offset;

           }


           if (shaderProgram.getZoom() > 3) {
               if (i % 10 == 0) {
                   depictNumber(num, x_offset, i + r_y / 1.2f, r_x, r_y);
               }

              if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

                   if (p_num % 9 == 0) {
                       depictNumber(p_label, p_offset, (float) (p_num * Math.PI), r_x, r_y);
                   }

                   prec = p_num;
               }

               continue;
           }

           if (shaderProgram.getZoom() > 2) {
               if (i % 5 == 0) {
                   depictNumber(num, x_offset, i + r_y / 1.2f, r_x, r_y);

               }

               if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

                   if (p_num % 3 == 0) {
                       depictNumber(p_label,p_offset, (float) (p_num * Math.PI), r_x, r_y);
                   }
                   prec = p_num;
               }

               continue;
           }

           if (shaderProgram.getZoom() > 1) {
               if (i % 2 == 0) {
                   depictNumber(num, x_offset, i + r_y / 1.2f, r_x, r_y);

               }

               if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

                   if (p_num % 2 == 0) {
                       depictNumber(p_label, p_offset, (float) (p_num * Math.PI), r_x, r_y);
                   }
                   prec = p_num;
               }

               continue;
           } else {
               if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

                   depictNumber(p_label, p_offset, (float) (p_num * Math.PI), r_x, r_y);
                   prec = p_num;
               }

               depictNumber(num, x_offset, i + r_y / 1.2f, r_x, r_y);
           }



       }

       prec = -10000;

       for (int i = x_l - 1; i <= x_r + 1; i ++) {

           if (i == 0) {
               continue;
           }

           String num = String.valueOf(i);

           int j = Math.abs(i);
           double piParrams = (j / Math.PI) -  ((int) (j / Math.PI)) ;
           int p_num = (int) (i / Math.PI);
           String p_label = p_num + "p";

           float y_offset = 0;
           float r_y_2 = r_y / 1.2f;

           if (y_top - num.length() * r_y_2 - r_y_2 < 0) {
               y_offset = y_top -  r_y_2 ;
           }

           if (y_bottom > 0) {
               y_offset = y_bottom + r_y_2;
           }

           if (y_bottom < 0 && y_top - r_y_2 - r_y_2 > 0) {
               y_offset = r_y_2;
           }


           float n_p_offset = 0;
           float p_offset   = y_offset;

           if (y_bottom + r_y > 0) {
               n_p_offset = y_bottom +  4*r_y;

               p_offset = n_p_offset;

           } else {
               p_offset = y_offset;
           }

           if (shaderProgram.getZoom() > 3) {
               if (i % 10 == 0) {
                   depictNumber(num, i + r_x / 1f, y_offset, r_x, r_y);
               }

               if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

                   if (p_num % 9 == 0) {
                       depictNumber(p_label, (float) (p_num * Math.PI), p_offset - 1.6f * r_y, r_x, r_y);
                   }

                   prec = p_num;
               }
               continue;
           }

           if (shaderProgram.getZoom() > 2) {
               if (i % 5 == 0) {
                   depictNumber(num, i + r_x / 1f, y_offset, r_x, r_y);
               }

               if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

                   if (p_num % 3 == 0) {
                       depictNumber(p_label, (float) (p_num * Math.PI), p_offset - 1.6f * r_y, r_x, r_y);
                   }

                   prec = p_num;
               }
               continue;
           }

           if (shaderProgram.getZoom() > 1) {
               if (i % 2 == 0) {
                   depictNumber(num, i + r_x / 1f, y_offset, r_x, r_y);
               }

               if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

                   if (p_num % 2 == 0) {
                       depictNumber(p_label, (float) (p_num * Math.PI), p_offset - 1.6f * r_y, r_x, r_y);
                   }

                   prec = p_num;

               }
               continue;
           }

           if (this.p_grid && piParrams > 0 && piParrams < 1 && j > 3 && prec != p_num) {

               depictNumber(p_label, (float) (p_num * Math.PI), p_offset - 1.6f * r_y, r_x, r_y);

               prec = p_num;
           }

           depictNumber(num, i + r_x / 1f, y_offset, r_x, r_y);

       }

       depictNumber("0", r_x, r_y, r_x, r_y);

       vertexBatcher.depictShapes(Assets.digits);

    }

    private void handleTouchEvents() {
        List<TouchEvent> touchEvents = openGLRendererTouchEventListener.getTouchEvents();

     //   Log.i("length", "len = " + touchEvents.size());

        int max = 0;

        for (int i = 0; i < touchEvents.size(); i ++) {
            if (touchEvents.get(i).pointer > max) {
                max = touchEvents.get(i).pointer;
            }
        }

        if (max > 1) {
            moving  = false;
            zooming = false;
            return;
        }

        if (max == 1) {
            moving = false;

            if (touchEvents.size() > 1) {
                for (int i = 0; i < touchEvents.size(); i += 2) {

                    if (i + 1 == touchEvents.size()) {
                        break;
                    }

                    TouchEvent event_0 = touchEvents.get(i + 0);
                    TouchEvent event_1 = touchEvents.get(i + 1);

                    shaderProgram.touchToWorld_no_zoom(event_0);
                    shaderProgram.touchToWorld_no_zoom(event_1);

                    diff.set(event_0.touchPosition).subtract(event_1.touchPosition);

                    if (!zooming) {
                        pLength = diff.length();
                        zooming = true;
                        continue;
                    }

                    float t = diff.length();

                    Log.d("update", "" + t + " k " + event_0.pointer + " " + event_1.pointer);
                    shaderProgram.zoom(pLength - t);

                    pLength = t;

                }
            }

            return;
        }

        zooming = false;

        if (max == 0) {

            for (int i = 0; i < touchEvents.size(); i ++) {
                TouchEvent event = touchEvents.get(i);
                shaderProgram.touchToWorld(event);

                if (event.type == TouchEvent.TOUCH_DOWN) {
                    touchedDown.set(event.touchPosition);
                    moving = true;
                }

                if (event.type == TouchEvent.TOUCH_UP) {
                    touchedDown.set(event.touchPosition);
                    moving = false;
                }

                if (event.type == TouchEvent.TOUCH_DRAGGED && moving) {
                    delta.set(touchedDown).subtract(event.touchPosition);
                    touchedDown.set(event.touchPosition);
                    shaderProgram.translateViewport(delta);
                }

            }
        }
    /*    try {
            Thread.sleep(   100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


    }

    private void depictNumber(String num, float x, float y, float w, float h) {

        float offset = 0;
        Sprite sprite;
        for (int i = 0; i < num.length(); i ++) {

            char c = num.charAt(i);

            if (c == 'p') {
                sprite = numbersSprites.get("p");
                sprite.setPosition(x + w / 2+ offset - w / 2.2f, y, w, h);
                sprite.draw();
                continue;
            }

            sprite = numbersSprites.get(String.valueOf(c));

            if (num.charAt(i) == '-') {
                sprite.setPosition(x + offset - w / 2.2f, y, w / 1.8f, h / 12);
                offset += w / 2.5f;
            } else {
                sprite.setPosition(x + offset, y, w, h);
                offset += w;
            }

            sprite.draw();
        }
    }


    private void depictGraphs() {

        float w = shaderProgram.ACTUAL_WIDTH;
        float h = shaderProgram.ACTUAL_HEIGHT;

        float x_c = shaderProgram.left   + (shaderProgram.right - shaderProgram.left  ) / 2;
        float y_c = shaderProgram.bottom + (shaderProgram.top   - shaderProgram.bottom) / 2;

        float x_left  = x_c - w / 2.0f;
        float x_right = x_c + w / 2.0f;

        float y_bottom = y_c - h / 2.0f;
        float y_top    = y_c + h / 2.0f;


        for (int i = 0; i < graphs.size(); i ++) {
            Graph graph = graphs.get(i);
            graph.draw(vertexBatcher, x_left, x_right, y_bottom, y_top);
        }
    }

    public void addGraph(Graph graph) {
        this.graphs.add(graph);
    }

    public void addGraphs(ArrayList<Graph> graphs) {
        this.graphs.addAll(graphs);
    }

    public ArrayList<Graph> getGraphs() {
        return this.graphs;
    }

    public void onResume() {
        synchronized(stateChanged) {
            state = GLGameState.Running;
        }
    }

    public void onPause(AppCompatActivity activity) {
        synchronized(stateChanged) {

            state = GLGameState.Paused;
         /*   if (activity.isFinishing()) {
                state = GLGameState.Finished;
            } else {
                state = GLGameState.Paused;
            }

            while(true) {
                try {
                    stateChanged.wait();
                    break;
                } catch (InterruptedException e) {

                }
            }*/
        }

      //  super.onPause();
    }

    public void makeScreenShot() {
        this.makeScreenShot = true;
    }

    public void setPGrid(boolean p_grid) {
        this.p_grid = p_grid;
    }

}