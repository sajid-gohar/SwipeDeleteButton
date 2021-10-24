package com.check.swipe_delete.SwipeHelperClass;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class SwipeHelperClass extends ItemTouchHelper.SimpleCallback {
    int buttonWidth;
    RecyclerView recyclerView;
    private List<MyButton> myButtonList;
    private GestureDetector gestureDetector;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer, List<MyButton>> myLstBuffer;
    private Queue<Integer> removeQueue;
    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (MyButton button : myButtonList) {
                if (button.onClick(e.getX(), e.getY())) {
                    break;
                }
            }
            return true;
        }
    };
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (swipePosition < 0) return false;
            Point point = new Point((int) event.getRawX(), (int) event.getRawY());
            RecyclerView.ViewHolder swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition);
            View swipedItem = swipeViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);
            if (event.getAction() == event.ACTION_DOWN || event.getAction() == event.ACTION_UP || event.getAction() == event.ACTION_MOVE) {
                if (rect.top < point.y && rect.bottom > point.y)
                    gestureDetector.onTouchEvent(event);
                else {
                    removeQueue.add(swipePosition);
                    swipePosition = -1;
                    recoverSwipedItem();
                }
            }
            return false;
        }
    };

    private synchronized void recoverSwipedItem() {
        for (int i=0;i<removeQueue.size();i++)
        {
            if (!removeQueue.isEmpty())
            {
                int pos = removeQueue.poll();
                if (pos > -1)
                    recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
//        while (!removeQueue.isEmpty()) {
//
//
//        }
    }

    public SwipeHelperClass(Context context, RecyclerView recyclerView, int buttonWidth) {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.myButtonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.myLstBuffer = new HashMap<>();
        this.buttonWidth = buttonWidth;

        removeQueue = new LinkedList<Integer>() {
            @Override
            public boolean add(Integer integer) {
                if (contains(integer))
                    return false;
                else
                    return super.add(integer);
            }
        };
        attachSwipe();
    }

    private void attachSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public class MyButton {
        private Context context;
        private String myText;
        private int imageResId, textSize, color, pos;
        private RectF clickRegion;
        private BtnDeleteClickListner btnDeleteClickListner;
        private Resources resources;

        public MyButton(Context context, String myText, int imageResId, int textSize, int color, BtnDeleteClickListner btnDeleteClickListner) {
            this.context = context;
            this.myText = myText;
            this.imageResId = imageResId;
            this.textSize = textSize;
            this.color = color;
            this.btnDeleteClickListner = btnDeleteClickListner;
            resources = context.getResources();
        }

        public boolean onClick(float x, float y) {
            if (clickRegion != null && clickRegion.contains(x, y)) {
                btnDeleteClickListner.onClick(pos);
                return true;
            }
            return false;
        }

        public void onDraw(Canvas c, RectF rectF, int pos) {

            Paint p = new Paint();
            p.setColor(color);
            p.setTextSize(textSize);
            final Path path = new Path();
            float radius = 12;
            final float[] radii = new float[8];

            radii[0] = radius;
            radii[1] = radius;
            radii[6] = radius;
            radii[7] = radius;

            path.addRoundRect(rectF, radii, Path.Direction.CW);

            Matrix scaleMatrix = new Matrix();
            path.computeBounds(rectF, true);
            scaleMatrix.setScale(1f, 1f, rectF.centerX(), rectF.centerY());
            path.transform(scaleMatrix);
            c.drawPath(path, p);

            Rect r = new Rect();
            float cHeight = rectF.height();
            float cWidth = rectF.width();
//            p.setTextAlign(Paint.Align.CENTER);
//            p.setColor(Color.WHITE);
//            p.getTextBounds(myText, 0, myText.length(), r);
            float x = 0, y = 0;
            if (imageResId == 0) {
                x = cWidth / 2f - r.width() / 2f - r.left;
                y = cHeight / 2f - r.height() / 2f - r.bottom;
                c.drawText(myText, rectF.left + x, rectF.top + y, p);
            } else {
                //Bitmap Alignment center...
                Drawable d = ContextCompat.getDrawable(context, imageResId);
                Bitmap bitmap = drawableToBitmap(d);
                float bw = bitmap.getWidth() / 2;
                float bh = bitmap.getHeight() / 2;
                c.drawBitmap(bitmap, ((rectF.left + rectF.right) / 2) - bw, ((rectF.top + rectF.bottom) / 2 - bh), p);

//                //Text Alignment below image...
//                x = cWidth / 2f - r.width() / 2f - r.left;
//                y = cHeight / 2f - r.height() / 2f - r.bottom;
//                c.rotate(-90,x,y);
//                c.drawTextOnPath(myText,path, x ,y,  p);
            }
            clickRegion = rectF;
            this.pos = pos;
        }
    }

    private Bitmap drawableToBitmap(Drawable d) {
        if (d instanceof BitmapDrawable)
            return ((BitmapDrawable) d).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        if (swipePosition != pos)
            removeQueue.add(swipePosition);
            swipePosition = pos;
        if (myLstBuffer.containsKey(swipePosition))
            myButtonList = myLstBuffer.get(swipePosition);
        else
            myButtonList.clear();
        myLstBuffer.clear();

        swipeThreshold = 0.5f * myButtonList.size() * buttonWidth;
        //for not overlapping the views...
        recoverSwipedItem();
    }

    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return 1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue) {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;
        if (pos < 0) {
            swipePosition = pos;
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE ) {
            if (dX < 0) {
                List<MyButton> buffer = new ArrayList<>();
                if (!myLstBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer);
                    myLstBuffer.put(pos, buffer);
                } else {
                    buffer = myLstBuffer.get(pos);
                }
                translationX = dX * buffer.size() * buttonWidth / itemView.getWidth();
                drawButton(c, itemView, buffer, pos, translationX);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<MyButton> buffer, int pos, float translationX) {
        float right = itemView.getRight();
        float dButtonWidth = -1 * translationX / buffer.size();
        for (MyButton button : buffer) {
            float left = right - dButtonWidth;
            button.onDraw(c, new RectF(left, itemView.getTop(), right, itemView.getBottom()), pos);
            right = left;
        }
    }

    public abstract void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer);

}
