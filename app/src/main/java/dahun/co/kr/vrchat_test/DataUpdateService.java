package dahun.co.kr.vrchat_test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dahun.co.kr.vrchat_test.API.VRCUser;

public class DataUpdateService extends Service {

    //static ArrayList<UserInfomation> friendsInfomation = new ArrayList<>();
    static Context context;
    static DataUpdateThread updater = new DataUpdateThread();
    static String id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("Service", "onBind() 호출");
        return null;
    }

    @Override
    public void onCreate() {    // 처음 생성될 떄 호출
        super.onCreate();



        //friendsInfomation.clear();
        //Log.i("1.friendsInfomation", "초기화 완료 : " + (friendsInfomation == null?"null":"not null"));
        context = this;

        SharedPreferences autoLoginShared = getSharedPreferences("AutoLogin", MODE_PRIVATE);
        if (autoLoginShared.contains("id") && autoLoginShared.contains("pwd")) {
            id = autoLoginShared.getString("id", null);
            String pwd = autoLoginShared.getString("pwd", null);
            if (!login(id, pwd))
                this.stopSelf();
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {      //  startService 하면 호출
        //Toast.makeText(this, "서비스를 시작합니다.", Toast.LENGTH_SHORT).show();
        Log.i("Thread run", "call run()");
        Log.i("LoginResult", "" + VRCUser.isLoggedIn());



        if (updater.isAlive() == false){
            Log.i("updaterThread", "재시작");
            updater = new DataUpdateThread();
            updater.start();
        }
/*
        while(updater.isAlive()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
*/
        return super.onStartCommand(intent, flags, startId);

    }



    public boolean login(final String id, final String pwd) {   // 로그인 해주는 함수
        Thread t = new Thread() {
            @Override
            public void run() {
                VRCUser.login(id, pwd);
            }
        };
        t.start();
        try {
            t.join();
        } catch (Exception e) {

        }
        return VRCUser.isLoggedIn();
    }

    static public void showNotification(Context context, String comment, int id) {  // Notification 띄워주는 함수

        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM월 dd일 HH시 mm분");
        // nowDate 변수에 값을 저장한다.
        String formatDate = sdfNow.format(date);

        Notification n = new Notification.Builder(context)
                .setContentTitle(comment)
                .setContentText("접속한 시간 : " + formatDate)
                .setSmallIcon(R.drawable.vrc_icon4)
                .setAutoCancel(true).build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(id, n);

    }

}
