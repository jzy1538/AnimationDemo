package com.jzy.animationdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity{

    private Button mBtnAdd;
    private ImageView mIvCart;
    private ImageView mIvBall;
    private int[] mBallArrs;
    private int[] mIvArrs;
    private MainActivity context;
    public static final String TAG = "MainActivity";
    private float[] mValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mIvCart = (ImageView) findViewById(R.id.iv_cart);
        mIvBall = (ImageView) findViewById(R.id.iv_ball);

        initListener();
    }


    private void initListener() {

        mBtnAdd.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initData();
                mBtnAdd.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAnimation();
            }
        });
    }

    private void initData() {
        /**
         * 获取起点和终点的坐标,相对于整个屏幕
         */
        mBallArrs = new int[2];
        mIvBall.getLocationOnScreen(mBallArrs);
        mIvArrs = new int[2];
        mIvCart.getLocationOnScreen(mIvArrs);

        Log.d(TAG, "ball = " + mBallArrs[0] + ", iv = " + mIvArrs[0]);

        /**
         * 计算抛物线公式 y = a*x*x + b*x + c
         */
        final float[][] points = {
                {Float.valueOf(mBallArrs[0]), Float.valueOf(mBallArrs[1])},//第一个点坐标
                {Float.valueOf(mIvArrs[0]), Float.valueOf(mBallArrs[1])},//第二个点坐标
                {Float.valueOf((mBallArrs[0] + mIvArrs[0]) / 2), Float.valueOf(mBallArrs[1] - 200)}};//第三个点坐标
        mValue = calculate(points);
    }

    private void playAnimation() {
        //根据可变数组获取属性动画
        ValueAnimator viAnimator = ValueAnimator.ofFloat(mBallArrs[0], mIvArrs[0]);
        viAnimator.setDuration(1000);
        viAnimator.setInterpolator(new AccelerateInterpolator());

        viAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float x = (float) animation.getAnimatedValue();
                /**
                 *  坐标 y = a*x*x + b*x + c
                 */
                float y = mValue[0] * x * x + mValue[1] * x + mValue[2];
//                Log.d(TAG, "x = " + x + ",y = " + y);

                /**
                 * 这里需要注意的是view的坐标设置只能用setX()函数,不能用setLeft()
                 * setX():设置坐标,从而改变view的位置 setLeft():设置左边距,从而改变view的大小不改变位置
                 * 而且getLeft()的返回值是原始左上角的位置信息,其值不会发生改变
                 */
                mIvBall.setX(x);
                mIvBall.setY(y);
                int left = mIvBall.getLeft();
                int top = mIvBall.getTop();
                float x1 = mIvBall.getX();
                float y1 = mIvBall.getY();
                Log.d(TAG, "onAnimationUpdate left = " + left + ",top = " + top + ",x = " + x1 + ",y = " + y1);
            }
        });

        viAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mIvBall.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mIvBall.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        viAnimator.start();
    }


    /**
     * 计算抛物线公式参数
     * a = (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2)) / (x1 * x1 * (x2 -
     * x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2))
     * b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
     * c = y1 - (x1 * x1) * a - x1 * b;
     */
    public static float[] calculate(float[][] points) {
        float x1 = points[0][0];
        float y1 = points[0][1];
        float x2 = points[1][0];
        float y2 = points[1][1];
        float x3 = points[2][0];
        float y3 = points[2][1];

        final float a = (y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2))
                / (x1 * x1 * (x2 - x3) + x2 * x2 * (x3 - x1) + x3 * x3 * (x1 - x2));
        final float b = (y1 - y2) / (x1 - x2) - a * (x1 + x2);
        final float c = y1 - (x1 * x1) * a - x1 * b;

        System.out.println("-a->" + a + " b->" + b + " c->" + c);

        return new float[]{a, b, c};
    }
}
