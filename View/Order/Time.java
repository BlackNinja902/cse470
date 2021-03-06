package com.dineout.code.view.order;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dineout.R;
import com.dineout.code.view.billing.ConfirmPayment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.dineout.code.model.order.OrderDetails;
import java.util.ArrayList;
import java.util.Locale;


public class Time extends AppCompatActivity {
    private static long START_TIME_IN_MILLIS;  //take from kitchen - this is  30 seconds - 10 minutes
    private TextView mTextViewCountDown;
    private TextView mTextViewNotif;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    ArrayList<OrderDetails>orderDetail=new ArrayList<OrderDetails>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_time);
        //********get value through shared preference for orderid here

        final String orderid="1";
        //get all dishes estimated time corresponding to orderid given and add up and convert to milisecs
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("OrderDetails");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderDetail.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    OrderDetails obj = child.getValue(OrderDetails.class);
                    if(obj.orderid.equals(orderid)) {
                        orderDetail.add(obj);
                    }

                    //cad.notifyDataSetChanged();
                }
                int sizeo=orderDetail.size();
                int timeCount=0;
                for(int i=0;i<sizeo;i++)
                {
                    timeCount=timeCount+orderDetail.get(i).getEstimatedtime();
                }
                Toast.makeText(getApplicationContext(), "total estimated time= "+timeCount+"minutes", Toast.LENGTH_LONG).show();
                //Timer Settings
                int minutes = 1;
                long milliseconds = timeCount * 60000;
                START_TIME_IN_MILLIS=milliseconds;
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                mTextViewCountDown = findViewById(R.id.countdown);
                mTextViewNotif = findViewById(R.id.eTime);
                startTimer();//start count down
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();

            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mTextViewNotif.setText("It's Time!");
            }
        }.start();

        mTimerRunning = true;
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    //Update
    public void onClickReg100(View v)
    {
        Toast.makeText(getApplicationContext(), "Redirect to Update Order", Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(Time.this,Time.class));  // changed to that interface
    }

    //Cancel
    public void onClickReg101(View v)
    {
        Toast.makeText(getApplicationContext(), "Redirect to Cancel Order", Toast.LENGTH_SHORT).show();
        //startActivity(new Intent(Time.this,Time.class));  // changed to that interface
    }

    //ViewBill
    public void onClickReg102(View v)
    {
        Intent i = new Intent(this, ConfirmPayment.class);
        i.putExtra("customerview", "true");
        startActivity(i);
    }
}