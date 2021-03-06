package com.martin.postermaster;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.MotionEvent;

/**
 * Created by Martin on 2016/7/23 0023.
 */
public class Layer {

    private Bitmap layer;
    private Bitmap drawLayer;//提供绘图的原图
    private Paint paint;

    private float x, y;
    private int width, height;
    private RectF layerRectF;//Layer的表框

    private int degree = 0;//layer在cover中的旋转角度，tip：这里的旋转角度以在cover标注的layer的中心点的旋转角度为基准
    private float scale = 1;//以在视图中的宽度计算出来的缩放比例。

    public Layer(Bitmap layer, RectF layerRectF, int degree) {
        this.layer = layer;
        this.layerRectF = layerRectF;
        this.degree = degree;

        width = layer.getWidth();
        height = layer.getHeight();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
    }

    /**
     * 计算出 真实起点坐标，和结束坐标，可以确定出显示矩形框
     *
     * @param coverScale 遮盖层图片的缩放比例
     * @return
     */
    public void caculateDrawLayer(float coverScale) {
        layerRectF = scaleRect(coverScale, layerRectF);
        scale = calculateFitScale(width, height, (int) layerRectF.width(), (int) layerRectF.height());
        drawLayer = BitmapUtils.scaleBitmap(layer, scale);
        // 更新Layer的坐标
        setLayerX(layerRectF.left - ((drawLayer.getWidth() - layerRectF.width()) / 2));
        setLayerY(layerRectF.top - ((drawLayer.getHeight() - layerRectF.height()) / 2));
    }

    public void draw(Canvas canvas) {
        int a = canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.rotate(degree, layerRectF.left, layerRectF.top);
        canvas.clipRect(layerRectF, Region.Op.INTERSECT);
        canvas.drawBitmap(drawLayer, x, y, paint);

        //这里是通过计算的出来的起始坐标点，图片绘制的大小是和图片大小一致的
        // 所以当进行缩放是图片本身要对应生成对应比例的图片，移动图片则是修改X,Y的值
        canvas.restoreToCount(a);
    }

    /**
     * 触摸事件处理
     *
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 缩放图层
     *
     * @param toSacle
     */
    protected void scaleLayer(float toSacle) {
        scale = scale * toSacle;
        drawLayer = BitmapUtils.scaleBitmap(layer, scale);
        // 更新Layer的坐标
        setLayerX(x - (drawLayer.getWidth() - width) / 2);
        setLayerY(y - (drawLayer.getHeight() - height) / 2);
    }

    protected RectF scaleRect(float scale, RectF layerRectF) {
        return new RectF(layerRectF.left * 1.0f * scale, layerRectF.top * 1.0f * scale, layerRectF.right * 1.0f * scale, layerRectF.bottom * 1.0f * scale);
    }

    /**
     * 移动图层
     *
     * @param disX
     * @param disY
     */
    protected void moveLayer(float disX, float disY) {
        x = x + disX;
        x = disY + y;
    }

    protected void setLayerX(float x) {
        this.x = x;
    }

    protected void setLayerY(float y) {
        this.y = y;

    }

    /**
     * 清除layer内存
     */
    public void destroyLayer() {

        BitmapUtils.destroyBitmap(layer);
        BitmapUtils.destroyBitmap(drawLayer);

    }


    public Bitmap getLayer() {
        return layer;
    }

    public void setLayer(Bitmap layer) {
        this.layer = layer;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public float getScale() {
        return scale;
    }

    /**
     * 计算缩放比例
     *
     * @param bW
     * @param bH
     * @param cW
     * @param cH
     * @return
     */
    public float calculateFitScale(int bW, int bH, int cW, int cH) {
        float sw = 1;
        float sh = 1;
        if (bW > 0) {
            sw = (float) cW / bW;
        }
        if (bH > 0) {
            sh = (float) cH / bH;
        }
        if (sw < sh) {
            return sh;
        } else {
            return sw;
        }
    }

}
