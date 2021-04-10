package kr.ac.sejong.gameapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {

    private int axeLevel = 1;
    private int numOfWood = 0;
    private int WoodSellerEfficiency = 1;
    private int numOfMoney = 0;
    private TextView numOfWoodText;
    private TextView numOfMoneyText;
    private Button cutDownButton;
    private Button upgradeAxe;
    private Button WoodSeller;
    private ImageView axeImage;

    private static Semaphore sem = new Semaphore(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        numOfWoodText = findViewById(R.id.numOfWood);
        cutDownButton = findViewById(R.id.cutDownButton);
        upgradeAxe = findViewById(R.id.upgradeButton);
        axeImage = findViewById(R.id.imageView);
        WoodSeller = findViewById(R.id.woodSeller);
        numOfMoneyText = findViewById(R.id.numOfMoney);
        WoodSeller = findViewById(R.id.woodSeller);

        cutDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sem.acquire();
                    numOfWood+=axeLevel;
                    numOfWoodText.setText("#Wood: "+numOfWood);
                    sem.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        if(numOfWood>=(50*axeLevel))
        {
            upgradeAxe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //data 업그레이드
                    numOfWood-=50*axeLevel;
                    axeLevel++;
                    //ui 업그레이드
                    numOfWoodText.setText("#Wood: "+numOfWood);
                    if(axeLevel == 2){
                        Resources res = getResources();
                        Drawable drawable = ResourcesCompat.getDrawable(res,R.drawable.axe2,null);
                        axeImage.setImageDrawable(drawable);
                    }
                    else if(axeLevel == 3){
                        Resources res = getResources();
                        Drawable drawable = ResourcesCompat.getDrawable(res,R.drawable.axe3,null);
                        axeImage.setImageDrawable(drawable);
                    }
                    else if(axeLevel == 4){
                        Resources res = getResources();
                        Drawable drawable = ResourcesCompat.getDrawable(res,R.drawable.axe4,null);
                        axeImage.setImageDrawable(drawable);
                    }
                    else if(axeLevel == 5){
                        Resources res = getResources();
                        Drawable drawable = ResourcesCompat.getDrawable(res,R.drawable.axe5,null);
                        axeImage.setImageDrawable(drawable);
                    }
                    else{
                        Resources res = getResources();
                        Drawable drawable = ResourcesCompat.getDrawable(res,R.drawable.axe6,null);
                        axeImage.setImageDrawable(drawable);
                    }
                }
            });
        }else{
            Toast.makeText(MainActivity.this,"Not Enough Wood",Toast.LENGTH_SHORT).show();
        }
        WoodSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WoodSellerEfficiency == 0){
                    //아직 나무를 파는 사람이 한명도 없을 때
                    if (numOfWood >= 100) {
                        numOfWood -= 100;
                        WoodSellerEfficiency++;

                        // Timer가 시작된다.
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (numOfWood > 0) {
                                    try {
                                        sem.acquire();
                                        numOfWood--;
                                        numOfMoney += WoodSellerEfficiency;
                                        sem.release();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                sem.acquire();
                                                numOfWoodText.setText("#Wood: " + numOfWood);
                                                numOfMoneyText.setText("#Money: " + numOfMoney);
                                                sem.release();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                }
                            }
                        }, 0, 1000);
                        numOfWoodText.setText("#Wood: " + numOfWood);
                        WoodSeller.setText("UPGRADE SELLER");
                    } else {
                        Toast.makeText(MainActivity.this, "Not enough", Toast.LENGTH_SHORT).show();
                    }
            }
                else{
                    //업그레이드가 필요한 상황
                    if (numOfWood >= (100*(WoodSellerEfficiency+1))) {
                        numOfWood -= (100*(WoodSellerEfficiency+1));
                        WoodSellerEfficiency++;

                        // Timer가 시작된다.
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (numOfWood > 0) {
                                    numOfWood--;
                                    numOfMoney += WoodSellerEfficiency;

                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            numOfWoodText.setText("#Wood: " + numOfWood);
                                            numOfMoneyText.setText("#Money: " + numOfMoney);
                                        }
                                    });
                                }
                            }
                        }, 0, 1000);
                        numOfWoodText.setText("#Wood: " + numOfWood);
                        WoodSeller.setText("UPGRADE SELLER");
                    } else {
                        Toast.makeText(MainActivity.this, "Not enough", Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });
    }
}
