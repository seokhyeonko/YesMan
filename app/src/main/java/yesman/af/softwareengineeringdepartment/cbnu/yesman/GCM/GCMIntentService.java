package yesman.af.softwareengineeringdepartment.cbnu.yesman.GCM;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import yesman.af.softwareengineeringdepartment.cbnu.yesman.R;
import yesman.af.softwareengineeringdepartment.cbnu.yesman.View.GCMtestActivity;
//으범수정
/**
 * Created by seokhyeon on 2016-06-26.
 */
public class GCMIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    // web server 에서 받을 extra key (web server 와 동일해야 함)
    static final String TITLE_EXTRA_KEY = "TITLE";
    static final String MSG_EXTRA_KEY = "MSG";
    static final String TYPE_EXTRA_CODE = "TYPE_CODE";
    // web server 에서 받을 extras key
    //다시
    public GCMIntentService() {
        super("");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GCMIntentService(String name) {
        super(name);
        System.out.println("************************************************* GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);


        System.out.println("************************************************* messageType : " + messageType);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                // 메시지를 받은 후 작업 시작
                System.out.println("************************************************* Working........................... ");

                // Post notification of received message.
                System.out.println("************************************************* 상태바 알림 호출");
                sendNotification(extras);
                System.out.println("************************************************* Received toString : " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    // 상태바에 공지
    private void sendNotification(Bundle extras) {
        // 혹시 모를 사용가능한 코드
        String jobjstring = extras.getString(TYPE_EXTRA_CODE);
        System.out.println("넘어온 제이슨 : "+jobjstring);
        JSONObject jboj = null;
        try {
            jboj = new JSONObject(jobjstring);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GCMtestActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                null;
        try {
            mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher) // 도메인에 맡게 코딩하면 될듯
                    .setContentTitle(URLDecoder.decode(extras.getString(TITLE_EXTRA_KEY), "UTF-8"))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8")))
                    .setContentText(URLDecoder.decode(extras.getString(MSG_EXTRA_KEY), "UTF-8"))
                    .setGroup(URLDecoder.decode(jboj.getString("userid"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mBuilder.setVibrate(new long[]{0,1500}); // 진동 효과 (퍼미션 필요)
        mBuilder.setAutoCancel(true); // 클릭하면 삭제

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}

